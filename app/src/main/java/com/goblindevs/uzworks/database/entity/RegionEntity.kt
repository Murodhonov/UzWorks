package com.goblindevs.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "region_table")
data class RegionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "region_id")
    val id: String,
    val name: String
)