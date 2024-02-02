package dev.goblingroup.uzworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.dao.WorkerDao
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.database.entity.UserEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity

@Database(
    entities = [UserEntity::class, JobEntity::class, JobCategoryEntity::class, RegionEntity::class, DistrictEntity::class, WorkerEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun jobCategoryDao(): JobCategoryDao
    abstract fun regionDao(): RegionDao
    abstract fun districtDao(): DistrictDao
    abstract fun workerDao(): WorkerDao
}