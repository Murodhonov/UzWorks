package dev.goblingroup.uzworks.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users_table")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "username")
    val username: String,
    @ColumnInfo(name = "password")
    val password: String,
    @ColumnInfo(name = "birth_date")
    val birthDate: String,
    @ColumnInfo(name = "email")
    val email: String? = null,
    @ColumnInfo(name = "first_name")
    val firstname: String,
    @ColumnInfo(name = "last_name")
    val lastName: String,
    @ColumnInfo(name = "gender")
    val gender: String? = null,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String? = null
)