package dev.goblingroup.uzworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity

@Database(
    entities = [JobEntity::class, WorkerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun jobDao(): JobDao
    abstract fun workerDao(): WorkerDao
}