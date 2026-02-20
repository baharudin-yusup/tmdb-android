package dev.baharudin.tmdb_android.core.data.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.baharudin.tmdb_android.core.data.models.remote.get_movie_detail.MovieDetailResponse
import dev.baharudin.tmdb_android.core.data.models.toDbEntity
import dev.baharudin.tmdb_android.core.data.models.toEntity
import dev.baharudin.tmdb_android.core.data.sources.MovieListSource
import dev.baharudin.tmdb_android.core.data.sources.ReviewListSource
import dev.baharudin.tmdb_android.core.data.sources.local.db.GenreDao
import dev.baharudin.tmdb_android.core.data.sources.local.db.MovieDao
import dev.baharudin.tmdb_android.core.data.sources.remote.TheMovieDBApi
import dev.baharudin.tmdb_android.core.domain.entities.Genre
import dev.baharudin.tmdb_android.core.domain.entities.Movie
import dev.baharudin.tmdb_android.core.domain.entities.Review
import dev.baharudin.tmdb_android.core.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val genreDao: GenreDao,
    private val movieDao: MovieDao,
    private val theMovieDBApi: TheMovieDBApi,
) : MovieRepository {

    companion object {
        private const val TAG = "(RP) MovieRepository"
    }

    override suspend fun getMovieGenres(): List<Genre> {
        val response = theMovieDBApi.getGenreList()
        val genreResponseList = response.genres
        genreDao.insertAll(*genreResponseList.map { it.toDbEntity() }.toTypedArray())
        return genreResponseList.map { it.toEntity() }
    }

    override suspend fun getMovieGenreById(genreId: Int): Genre {
        return genreDao.loadAllById(genreId).toEntity()
    }

    override suspend fun getMovieDetail(movie: Movie): Movie {
        val savedMovie = movieDao.loadMovieById(movie.id)
        return movie.copy(isFavorite = savedMovie.isFavorite)
    }

    override suspend fun getMovieDetail(movieId: Int): Movie {
        Log.d(TAG, "getMovieDetail: started...")
        val savedMovie = movieDao.loadMovieById(movieId)
        Log.d(TAG, "getMovieDetail: get saved movie success!")

        Log.d(TAG, "getMovieDetail: get movie detail from remote API started...")
        val movieDetailResponse: MovieDetailResponse
        try {
            movieDetailResponse = theMovieDBApi.getMovieDetail(movieId)
            Log.d(TAG, "getMovieDetail: get movie detail from remote API success!")
        } catch (e: Exception) {
            Log.e(TAG, "getMovieDetail: get movie detail from remote API error!", e)
            throw e
        }

        return movieDetailResponse.toEntity(savedMovie.isFavorite)
    }

    override fun getFavoriteMovies(): Flow<List<Movie>> {
        val genres = genreDao
            .loadAll()
            .map { genre -> genre.toEntity() }
        return movieDao.loadAllFavoriteMovie()
            .map {
                it.map { movie -> movie.toEntity(genres) }
            }
    }

    override fun discoverMoviesByGenre(genre: Genre): Flow<PagingData<Movie>> {
        Log.d(TAG, "discoverMoviesByGenre: $genre started...")
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MovieListSource(
                    theMovieDBApi, MovieListSource.QueryParams(listOf(genre.id))
                )
            }
        ).flow.map { pagingData ->
            pagingData.map {
                val selectedMovie = movieDao.loadMovieByIds(intArrayOf(it.id))
                val isFavorite = if (selectedMovie.isEmpty()) {
                    movieDao.insertAll(it.toDbEntity())
                    false
                } else {
                    selectedMovie.first().isFavorite
                }

                val genres =
                    genreDao.loadAllByIds(it.genreIds.toIntArray())
                        .map { data -> data.toEntity() }

                it.toEntity(genres = genres, isFavorite = isFavorite)
            }
        }
    }

    override fun discoverMoviesByGenre(genreIds: List<Int>): Flow<PagingData<Movie>> {
        Log.d(TAG, "discoverMoviesByGenreId: $genreIds started...")
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MovieListSource(
                    theMovieDBApi, MovieListSource.QueryParams(genreIds)
                )
            }
        ).flow.map { pagingData ->
            pagingData.map {
                val selectedMovie = movieDao.loadMovieByIds(intArrayOf(it.id))
                val isFavorite = if (selectedMovie.isEmpty()) {
                    movieDao.insertAll(it.toDbEntity())
                    false
                } else {
                    selectedMovie.first().isFavorite
                }

                val genres =
                    genreDao.loadAllByIds(it.genreIds.toIntArray())
                        .map { data -> data.toEntity() }

                it.toEntity(genres = genres, isFavorite = isFavorite)
            }
        }
    }

    override fun getMovieReviews(movie: Movie): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewListSource(
                    theMovieDBApi, movie.id
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toEntity() }
        }
    }

    override fun getMovieReviews(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(
                pageSize = 2,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ReviewListSource(
                    theMovieDBApi, movieId
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toEntity() }
        }
    }

    override fun addToFavoriteMovie(movie: Movie): Movie {
        Log.d(TAG, "addToFavoriteMovie: add ${movie.title} started...")
        val updatedMovie = movie.copy(isFavorite = true)
        val isSuccess = movieDao.updateMovie(updatedMovie.toDbEntity()) == 1

        if (!isSuccess) {
            Log.d(TAG, "addToFavoriteMovie: add ${movie.title} error!")
            throw Exception()
        }

        Log.d(TAG, "addToFavoriteMovie: add ${movie.title} success!")
        return updatedMovie
    }

    override fun removeFromFavoriteMovie(movie: Movie): Movie {
        val updatedMovie = movie.copy(isFavorite = false)
        val isSuccess = movieDao.updateMovie(updatedMovie.toDbEntity()) == 1

        if (!isSuccess) {
            throw Exception()
        }

        return updatedMovie
    }
}