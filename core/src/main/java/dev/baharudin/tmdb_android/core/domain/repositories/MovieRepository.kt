package dev.baharudin.tmdb_android.core.domain.repositories

import androidx.paging.PagingData
import dev.baharudin.tmdb_android.core.domain.entities.Genre
import dev.baharudin.tmdb_android.core.domain.entities.Movie
import dev.baharudin.tmdb_android.core.domain.entities.Review
import kotlinx.coroutines.flow.Flow


interface MovieRepository {
    suspend fun getMovieGenres(): List<Genre>

    suspend fun getMovieGenreById(genreId: Int): Genre
    suspend fun getMovieDetail(movie: Movie): Movie
    suspend fun getMovieDetail(movieId: Int): Movie
    fun getFavoriteMovies(): Flow<List<Movie>>
    fun discoverMoviesByGenre(genre: Genre): Flow<PagingData<Movie>>
    fun discoverMoviesByGenre(genreIds: List<Int>): Flow<PagingData<Movie>>
    fun getMovieReviews(movie: Movie): Flow<PagingData<Review>>
    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>
    fun addToFavoriteMovie(movie: Movie): Movie
    fun removeFromFavoriteMovie(movie: Movie): Movie
}