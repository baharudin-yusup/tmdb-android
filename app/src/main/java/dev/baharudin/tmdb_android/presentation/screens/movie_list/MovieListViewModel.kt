package dev.baharudin.tmdb_android.presentation.screens.movie_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.baharudin.tmdb_android.core.domain.entities.Genre
import dev.baharudin.tmdb_android.core.domain.entities.Movie
import dev.baharudin.tmdb_android.core.domain.entities.Resource
import dev.baharudin.tmdb_android.core.domain.usecases.DiscoverMovies
import dev.baharudin.tmdb_android.core.domain.usecases.GetGenreById
import dev.baharudin.tmdb_android.presentation.common.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val discoverMovies: DiscoverMovies,
    private val getGenreById: GetGenreById,
) : ViewModel() {
    companion object {
        const val TAG = "(VM) MovieListViewModel"
    }

    private val genreId: Int = checkNotNull(savedStateHandle["genreId"])
    private val genreName: String = checkNotNull(savedStateHandle["genreName"])

    private val _genre = MutableStateFlow<DataState<Genre>>(DataState())
    val genreState: StateFlow<DataState<Genre>> get() = _genre.asStateFlow()

    private val _moviesState: MutableStateFlow<PagingData<Movie>> =
        MutableStateFlow(value = PagingData.empty())
    val moviesState: StateFlow<PagingData<Movie>> get() = _moviesState

    init {
        onEvent(MovieListEvent.Init)
    }

    fun onEvent(event: MovieListEvent) {
        viewModelScope.launch {
            when (event) {
                MovieListEvent.Init -> {
                    getGenre()
                    getMovieList()
                }
            }
        }
    }

    private suspend fun getGenre() {
        getGenreById(genreId)
            .distinctUntilChanged()
            .collect { resource ->
                when (resource) {
                    is Resource.Error -> {
                        _genre.value = DataState(errorMessage = resource.message)
                    }

                    is Resource.Loading -> {
                        _genre.value = DataState(isLoading = true)
                    }

                    is Resource.Success -> {
                        _genre.value = DataState(data = resource.data)
                    }
                }
            }
    }

    private suspend fun getMovieList() {
        genreState.value.data?.let { genre ->
            discoverMovies(genre)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _moviesState.value = it
                }
        }
    }
}