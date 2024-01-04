package dev.goblingroup.uzworks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.UserEntity

@Database(entities = [UserEntity::class, JobEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context, AppDatabase::class.java, "uz_works_database")
                        .addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE!!
        }

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `jobs_table` (" +
                            "`job_id` TEXT NOT NULL PRIMARY KEY," +
                            "`benefit` TEXT," +
                            "`category_id` TEXT," +
                            "`deadline` TEXT," +
                            "`district_id` TEXT," +
                            "`gender` TEXT," +
                            "`instagram_link` TEXT," +
                            "`latitude` REAL," +
                            "`longitude` REAL," +
                            "`max_age` INTEGER," +
                            "`min_age` INTEGER," +
                            "`phone_number` TEXT," +
                            "`requirement` TEXT," +
                            "`salary` INTEGER," +
                            "`telegram_link` TEXT," +
                            "`tg_username` TEXT," +
                            "`title` TEXT," +
                            "`working_schedule` TEXT," +
                            "`working_time` TEXT," +
                            "`is_saved` INTEGER NOT NULL)"
                )
            }
        }
    }
}