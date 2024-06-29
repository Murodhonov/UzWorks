package com.goblindevs.uzworks.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.goblindevs.uzworks.database.dao.AnnouncementDao
import com.goblindevs.uzworks.database.dao.DistrictDao
import com.goblindevs.uzworks.database.dao.JobCategoryDao
import com.goblindevs.uzworks.database.dao.RegionDao
import com.goblindevs.uzworks.database.entity.AnnouncementEntity
import com.goblindevs.uzworks.database.entity.DistrictEntity
import com.goblindevs.uzworks.database.entity.JobCategoryEntity
import com.goblindevs.uzworks.database.entity.RegionEntity

@Database(
    entities = [RegionEntity::class, JobCategoryEntity::class, DistrictEntity::class, AnnouncementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun regionDao(): RegionDao
    abstract fun jobCategoryDao(): JobCategoryDao
    abstract fun districtDao(): DistrictDao
    abstract fun announcementDao(): AnnouncementDao
}