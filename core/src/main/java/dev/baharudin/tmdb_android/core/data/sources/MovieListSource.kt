package dev.baharudin.tmdb_android.core.data.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.baharudin.tmdb_android.core.data.models.remote.get_movie_list.MovieResponse
import dev.baharudin.tmdb_android.core.data.sources.remote.TheMovieDBApi
import retrofit2.HttpException
import java.io.IOException

class MovieListSource(
    private val theMovieDBApi: TheMovieDBApi,
    private val query: QueryParams,
) : PagingSource<Int, MovieResponse>() {

    data class QueryParams(val genreIds: List<Int>)

    companion object {
        const val TAG = "(DS) MovieListSource"
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieResponse> {
        Log.d(TAG, "load: params = $params")
        val currentPage = params.key ?: 1
        return try {
            val response = theMovieDBApi.getMovieList(
                withGenres = query.genreIds.map { it.toString() }
                    .reduce { acc, string -> "$acc|$string" },
                page = currentPage
            )
            val movies = response.results

            val prevKey = if (currentPage == 1) null else currentPage - 1
            val nextKey = if (movies.isEmpty()) null else currentPage + 1

            LoadResult.Page(
                data = movies,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            return LoadResult.Error(
                Exception(
                    e.localizedMessage ?: "Couldn't reach server. Check your internet connection."
                )
            )
        } catch (e: HttpException) {
            return LoadResult.Error(
                Exception(e.message())
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MovieResponse>): Int? {
        return state.anchorPosition
    }
}