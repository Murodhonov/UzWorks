package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity

@Dao
interface JobCategoryDao {

    @Insert(entity = JobCategoryEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addJobCategory(jobCategoryEntity: JobCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addJobCategories(jobCategoryList: List<JobCategoryEntity>)

    @Query("SELECT * FROM job_category_table WHERE job_category_id = :categoryId")
    suspend fun findJobCategory(categoryId: String): JobCategoryEntity

    @Query("SELECT * FROM job_category_table")
    suspend fun listJobCategories(): List<JobCategoryEntity>

}