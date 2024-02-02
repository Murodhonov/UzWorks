package dev.goblingroup.uzworks.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideMigration(): Migration {
        return object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `workers_table` (" +
                            "`worker_id` TEXT NOT NULL PRIMARY KEY," +
                            "`birth_date` TEXT," +
                            "`category_id` TEXT," +
                            "`create_date` TEXT," +
                            "`created_by` TEXT," +
                            "`deadline` TEXT," +
                            "`district_id` TEXT," +
                            "`full_name` TEXT," +
                            "`gender` TEXT," +
                            "`instagram_link` TEXT," +
                            "`location` TEXT," +
                            "`phone_number` TEXT," +
                            "`salary` INTEGER," +
                            "`telegram_link` TEXT," +
                            "`tg_user_name` TEXT," +
                            "`title` TEXT," +
                            "`user_name` TEXT," +
                            "`working_schedule` TEXT," +
                            "`working_time` TEXT NOT NULL," +
                            "`is_saved` INTEGER NOT NULL)"
                )
            }

        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        migration: Migration,
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "uz_works_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .addMigrations(migration)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(
        appDatabase: AppDatabase
    ): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideJobDao(
        appDatabase: AppDatabase
    ): JobDao {
        return appDatabase.jobDao()
    }

    @Provides
    @Singleton
    fun provideJobCategoryDao(
        appDatabase: AppDatabase
    ): JobCategoryDao {
        return appDatabase.jobCategoryDao()
    }

    @Provides
    @Singleton
    fun provideRegionDao(
        appDatabase: AppDatabase
    ): RegionDao {
        return appDatabase.regionDao()
    }

    @Provides
    @Singleton
    fun provideDistrictDao(
        appDatabase: AppDatabase
    ): DistrictDao {
        return appDatabase.districtDao()
    }

    @Provides
    @Singleton
    fun provideWorkerDao(
        appDatabase: AppDatabase
    ): WorkerDao {
        return appDatabase.workerDao()
    }

}