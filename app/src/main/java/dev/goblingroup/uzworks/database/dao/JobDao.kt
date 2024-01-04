package dev.goblingroup.uzworks.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.goblingroup.uzworks.database.entity.JobEntity

@Dao
interface JobDao {

    @Insert(entity = JobEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun addJob(jobEntity: JobEntity)

    @Insert(entity = JobEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun addJobs(jobList: List<JobEntity>)

    @Query("UPDATE jobs_table SET is_saved = 1 WHERE job_id = :jobId")
    fun saveJob(jobId: String)

    @Query("UPDATE jobs_table SET is_saved = 0 WHERE job_id = :jobId")
    fun unSaveJob(jobId: String)

    @Query("SELECT is_saved FROM jobs_table WHERE job_id = :jobId")
    fun isJobSaved(jobId: String): Boolean

    @Query("SELECT * FROM jobs_table WHERE job_id = :jobId")
    fun getJob(jobId: String): JobEntity

    @Query("SELECT * FROM jobs_table")
    fun listAllJobs(): List<JobEntity>

    @Query("SELECT * FROM jobs_table WHERE is_saved = 1")
    fun listSavedJobs(): List<JobEntity>

    @Query("SELECT COUNT(*) FROM jobs_table")
    fun countJobs(): Int

    @Query("SELECT COUNT(*) FROM jobs_table WHERE is_saved = 1")
    fun countSavedJobs(): Int

}