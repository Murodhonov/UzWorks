package dev.goblingroup.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "district_table")
data class DistrictEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "district_id")
    val id: String,
    val name: String,
    @ColumnInfo(name = "region_id")
    val regionId: String
)