package dev.baharudin.themoviedb.data.models.remote.get_genre_list

import com.google.gson.annotations.SerializedName

data class GetGenreListResponse(
    @SerializedName("genres") var genres: ArrayList<GenreResponse> = arrayListOf()
)