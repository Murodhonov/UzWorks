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
import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.dao.UserDao
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
    fun provideUserDao(
        appDatabase: AppDatabase
    ): UserDao {
        return appDatabase.userDao()
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