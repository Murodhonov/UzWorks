package dev.goblingroup.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workers_table")
data class WorkerEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "worker_id")
    val id: String,
    @ColumnInfo(name = "birth_date")
    val birthDate: String? = null,
    @ColumnInfo(name = "category_id")
    val categoryId: String? = null,
    @ColumnInfo(name = "create_date")
    val createDate: String? = null,
    @ColumnInfo(name = "created_by")
    val createdBy: String? = null,
    val deadline: String? = null,
    @ColumnInfo(name = "district_id")
    val districtId: String? = null,
    @ColumnInfo(name = "full_name")
    val fullName: String? = null,
    val gender: String? = null,
    @ColumnInfo(name = "instagram_link")
    val instagramLink: String? = null,
    val location: String? = null,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null,
    val salary: Int? = null,
    @ColumnInfo(name = "telegram_link")
    val telegramLink: String? = null,
    @ColumnInfo(name = "tg_user_name")
    val tgUserName: String? = null,
    val title: String? = null,
    @ColumnInfo(name = "user_name")
    val userName: String? = null,
    @ColumnInfo(name = "working_schedule")
    val workingSchedule: String? = null,
    @ColumnInfo(name = "working_time")
    val workingTime: String,
    @ColumnInfo(name = "is_saved")
    val isSaved: Boolean
)