package dev.goblingroup.uzworks.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "uz_works_database"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
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
    fun provideWorkerDao(
        appDatabase: AppDatabase
    ): WorkerDao {
        return appDatabase.workerDao()
    }

}