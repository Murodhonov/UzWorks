package dev.goblingroup.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs_table")
data class JobEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "job_id")
    val id: String,
    val benefit: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
    val deadline: String? = null,
    @ColumnInfo(name = "district_id")
    val districtId: String? = null,
    val gender: String? = null,
    @ColumnInfo(name = "instagram_link")
    val instagramLink: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @ColumnInfo(name = "max_age")
    val maxAge: Int? = null,
    @ColumnInfo(name = "min_age")
    val minAge: Int? = null,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,
    val requirement: String? = null,
    val salary: Int? = null,
    @ColumnInfo(name = "telegram_link")
    val telegramLink: String? = null,
    @ColumnInfo(name = "tg_username")
    val tgUserName: String? = null,
    val title: String? = null,
    @ColumnInfo(name = "working_schedule")
    val workingSchedule: String? = null,
    @ColumnInfo(name = "working_time")
    val workingTime: String? = null,
    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean
)