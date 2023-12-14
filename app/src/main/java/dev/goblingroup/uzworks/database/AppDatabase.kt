package dev.goblingroup.uzworks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [], version = 1)
abstract class AppDatabase : RoomDatabase() {

//    abstract fun modelDao(): ModelDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context, AppDatabase::class.java, "uz_works_database")
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE!!
        }
    }
}