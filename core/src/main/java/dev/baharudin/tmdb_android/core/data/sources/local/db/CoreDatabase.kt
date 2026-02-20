package dev.baharudin.tmdb_android.core.data.sources.local.db

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.baharudin.tmdb_android.core.data.models.ArrayListOfIntConverter
import dev.baharudin.tmdb_android.core.data.models.local.Genre
import dev.baharudin.tmdb_android.core.data.models.local.Movie
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(
    entities = [Genre::class, Movie::class],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(ArrayListOfIntConverter::class)
abstract class CoreDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun movieDao(): MovieDao

    companion object {
        private const val passphrase = "baharudin"

        fun create(context: Context): CoreDatabase {
            val passphraseByteArray = passphrase.toByteArray()
            val factory = SupportOpenHelperFactory(passphraseByteArray)
            return Room.databaseBuilder(
                context.applicationContext,
                CoreDatabase::class.java, "core.db"
            ).allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .openHelperFactory(factory)
                .build()
        }
    }
}