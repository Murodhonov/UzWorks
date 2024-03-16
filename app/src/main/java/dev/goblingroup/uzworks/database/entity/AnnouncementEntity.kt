package dev.goblingroup.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcement_table")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "announcement_id")
    val id: String,
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
    @ColumnInfo(name = "district_id")
    val districtId: String? = null,
    val gender: String? = null,
    val salary: Int,
    val title: String? = null,
    @ColumnInfo(name = "announcement_type")
    val announcementType: String,
    @ColumnInfo(name = "is_top")
    val isTop: Boolean,
    @ColumnInfo(name = "picture_res_id")
    val pictureResId: Int,
    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean
)