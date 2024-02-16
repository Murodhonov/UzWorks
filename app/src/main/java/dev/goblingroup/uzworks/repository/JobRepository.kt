package dev.goblingroup.uzworks.repository

import android.util.Log
import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val jobService: JobService
) {

    suspend fun getAllJobs() = jobService.getAllJobs()

    fun addJobs(jobList: List<JobResponse>) {
        val existingJobs = try {
            jobDao.listAllJobs()
        } catch (e: Exception) {
            ArrayList()
        }
        Log.d(TAG, "addJobs: existingJobs -> $existingJobs")
        val newJobs = mutableListOf<JobEntity>()


        jobList.forEach { apiJob ->
            val existingJob = existingJobs.find { it.id == apiJob.id }
            if (existingJob != null) {
                /**
                 * job exists on table
                 */
                newJobs.add(apiJob.mapToEntity(existingJob.isSaved))
            } else {
                /**
                 * it's new item, so it should be directly added
                 */
                newJobs.add(apiJob.mapToEntity(false))
            }
        }
        jobDao.addJobs(newJobs)
    }

    fun saveJob(jobId: String) = jobDao.saveJob(jobId)

    fun unSaveJob(jobId: String): Boolean {
        jobDao.unSaveJob(jobId)
        return jobDao.countSavedJobs() != 0
    }

    fun listDatabaseJobs() = jobDao.listAllJobs()

    fun listSavedJobs() = jobDao.listSavedJobs()

    fun countDatabaseJobs() = jobDao.countJobs()

    fun countSavedJobs() = jobDao.countSavedJobs()

}