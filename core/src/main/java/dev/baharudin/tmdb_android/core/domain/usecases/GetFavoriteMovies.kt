package dev.baharudin.tmdb_android.core.domain.usecases

import dev.baharudin.tmdb_android.core.domain.entities.Movie
import dev.baharudin.tmdb_android.core.domain.entities.Resource
import dev.baharudin.tmdb_android.core.domain.repositories.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetFavoriteMovies @Inject constructor(
    private val movieRepository: MovieRepository
) {
    operator fun invoke(): Flow<Resource<List<Movie>>> =
        movieRepository.getFavoriteMovies().map {
            Resource.Success(it)
        }
}