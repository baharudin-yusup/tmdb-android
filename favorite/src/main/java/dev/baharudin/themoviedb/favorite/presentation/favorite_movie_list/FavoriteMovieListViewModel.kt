package dev.baharudin.themoviedb.favorite.presentation.favorite_movie_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.baharudin.themoviedb.core.domain.entities.Movie
import dev.baharudin.themoviedb.core.domain.entities.Resource
import dev.baharudin.themoviedb.core.domain.usecases.GetFavoriteMovies
import dev.baharudin.themoviedb.presentation.common.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class FavoriteMovieListViewModel @Inject constructor(
    private val getFavoriteMovies: GetFavoriteMovies
) : ViewModel() {
    companion object {
        private const val TAG = "FavoriteMovieListViewModel"
    }

    private val _favoriteMovieStateFlow: MutableStateFlow<DataState<List<Movie>>> =
        MutableStateFlow(DataState())
    val favoriteMovieStateFlow: StateFlow<DataState<List<Movie>>> = _favoriteMovieStateFlow

    init {
        fetchData()
    }

    private fun fetchData() {
        viewModelScope.launch {
            getFavoriteMovies()
                .flowOn(Dispatchers.IO)
                .catch { error ->
                    Log.e(TAG, "fetchData: ${error.message}")
                }
                .collect { resource ->
                    val newState = when (resource) {
                        is Resource.Error -> DataState(errorMessage = resource.message)
                        is Resource.Loading -> DataState(isLoading = true)
                        is Resource.Success -> DataState(resource.data)
                    }

                    _favoriteMovieStateFlow.update { newState }
                }
        }
    }
}