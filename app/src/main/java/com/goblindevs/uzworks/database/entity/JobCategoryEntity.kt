package com.goblindevs.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_category_table")
data class JobCategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "job_category_id")
    val id: String,
    val description: String,
    val title: String
)