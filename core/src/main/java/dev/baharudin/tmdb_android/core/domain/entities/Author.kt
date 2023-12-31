package dev.baharudin.tmdb_android.core.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Author(
    val name: String,
    val username: String,
    val avatarPath: String?,
): Parcelable
