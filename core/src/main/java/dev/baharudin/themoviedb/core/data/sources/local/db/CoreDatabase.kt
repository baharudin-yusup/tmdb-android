package dev.baharudin.themoviedb.core.data.sources.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.baharudin.themoviedb.core.data.models.ArrayListOfIntConverter
import dev.baharudin.themoviedb.core.data.models.local.Genre
import dev.baharudin.themoviedb.core.data.models.local.Movie
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

@Database(
    entities = [Genre::class, Movie::class],
    version = 1,
)
@TypeConverters(ArrayListOfIntConverter::class)
abstract class CoreDatabase : RoomDatabase() {
    abstract fun genreDao(): GenreDao
    abstract fun movieDao(): MovieDao

    companion object {
        private const val passphrase = "baharudin"

        fun create(context: Context): CoreDatabase {
            val passphraseByteArray = SQLiteDatabase.getBytes(passphrase.toCharArray())
            val factory = SupportFactory(passphraseByteArray)
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