package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.networking.JobService
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val jobService: JobService
) {

    suspend fun getJobById(jobId: String) = jobService.getJobById(jobId)

    suspend fun getAllJobs() = jobService.getAllJobs()

    suspend fun countJobs() = jobService.countJobs()

    suspend fun getJobByUserId(userId: String) = jobService.getJobsByUserId(userId = userId)

    suspend fun addJob(jobEntity: JobEntity) = jobDao.addJob(jobEntity)

    suspend fun addJobs(jobList: List<JobResponse>) {
        val existingJobs = try {
            jobDao.listAllJobs()
        } catch (e: Exception) {
            ArrayList()
        }
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
    }

    fun saveJob(jobId: String) = jobDao.saveJob(jobId)

    suspend fun unSaveJob(jobId: String): Boolean {
        jobDao.unSaveJob(jobId)
        return jobDao.countSavedJobs() != 0
    }

    suspend fun isJobSaved(jobId: String) = jobDao.isJobSaved(jobId)

    suspend fun getJob(jobId: String) = jobDao.getJob(jobId)

    suspend fun listDatabaseJobs() = jobDao.listAllJobs()

    fun listSavedJobs() = jobDao.listSavedJobs()

    suspend fun countDatabaseJobs() = jobDao.countJobs()

    suspend fun countSavedJobs() = jobDao.countSavedJobs()

}