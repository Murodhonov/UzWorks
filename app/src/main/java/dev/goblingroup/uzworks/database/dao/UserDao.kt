package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.goblingroup.uzworks.database.entity.UserEntity
import retrofit2.http.DELETE

@Dao
interface UserDao {

    @Insert(
        entity = UserEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    fun addUser(userEntity: UserEntity)

    @Query("SELECT * FROM users_table LIMIT 1")
    fun getUser(): UserEntity?

    @Update(
        entity = UserEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    fun updateUser(userEntity: UserEntity)

    @DELETE
    fun deleteUser(userEntity: UserEntity)
}