package dev.goblingroup.uzworks.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, JobEntity::class, JobCategoryEntity::class, RegionEntity::class, DistrictEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun jobCategoryDao(): JobCategoryDao
    abstract fun regionDao(): RegionDao
    abstract fun districtDao(): DistrictDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE =
                    Room.databaseBuilder(context, AppDatabase::class.java, "uz_works_database")
                        .addMigrations(MIGRATION_3_4)
                        .allowMainThreadQueries()
                        .build()
            }
            return INSTANCE!!
        }

        private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE `region_table` (" +
                            "`region_id` TEXT NOT NULL PRIMARY KEY," +
                            "`name` TEXT NOT NULL)"
                )

                database.execSQL(
                    "CREATE TABLE `district_table` (" +
                            "`district_id` TEXT NOT NULL PRIMARY KEY," +
                            "`name` TEXT NOT NULL," +
                            "`region_id` TEXT NOT NULL," +
                            "FOREIGN KEY (`region_id`) REFERENCES `region_table`(`region_id`) ON DELETE CASCADE)"
                )
            }
        }
    }
}