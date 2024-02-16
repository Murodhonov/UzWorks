package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.goblingroup.uzworks.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert(
        entity = UserEntity::class,
        onConflict = OnConflictStrategy.REPLACE
    )
    fun addUser(userEntity: UserEntity)

    @Query("SELECT * FROM users_table LIMIT 1")
    fun getUser(): UserEntity?

    @Query("DELETE FROM users_table")
    fun deleteUser()

}