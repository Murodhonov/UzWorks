package com.goblindevs.uzworks.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.goblindevs.uzworks.database.AppDatabase
import com.goblindevs.uzworks.database.dao.AnnouncementDao
import com.goblindevs.uzworks.database.dao.DistrictDao
import com.goblindevs.uzworks.database.dao.JobCategoryDao
import com.goblindevs.uzworks.database.dao.RegionDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideMigration(): Migration {
        return object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE announcement_table ADD COLUMN is_top BOOLEAN")
                db.execSQL("ALTER TABLE announcement_table ADD COLUMN picture_res_id INTEGER")
            }
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        migration: Migration
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
    fun provideRegionDao(
        appDatabase: AppDatabase
    ): RegionDao {
        return appDatabase.regionDao()
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
    fun provideDistrictDao(
        appDatabase: AppDatabase
    ): DistrictDao {
        return appDatabase.districtDao()
    }

    @Provides
    @Singleton
    fun provideAnnouncementDao(
        appDatabase: AppDatabase
    ): AnnouncementDao {
        return appDatabase.announcementDao()
    }

}