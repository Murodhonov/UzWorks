package com.goblindevs.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "district_table",
    foreignKeys = [ForeignKey(
        entity = RegionEntity::class,
        parentColumns = ["region_id"],
        childColumns = ["region_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class DistrictEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "district_id")
    val id: String,
    val name: String,
    @ColumnInfo(name = "region_id")
    val regionId: String
)