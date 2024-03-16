package dev.goblingroup.uzworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.goblingroup.uzworks.database.dao.AnnouncementDao
import dev.goblingroup.uzworks.database.dao.DistrictDao
import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.dao.RegionDao
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, RegionEntity::class, JobCategoryEntity::class, DistrictEntity::class, AnnouncementEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun regionDao(): RegionDao
    abstract fun jobCategoryDao(): JobCategoryDao
    abstract fun districtDao(): DistrictDao
    abstract fun announcementDao(): AnnouncementDao
}