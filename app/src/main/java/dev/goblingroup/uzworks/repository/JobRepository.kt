package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.JobDao
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.networking.JobService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val jobService: JobService
) {

    suspend fun getJobById(jobId: String) = jobService.getJobById(jobId)

    suspend fun getAllJobs() = jobService.getAllJobs()

    suspend fun countJobs() = jobService.countJobs()

    suspend fun getJobByUserId(userId: String) = jobService.getJobsByUserId(userId = userId)

    fun addJob(jobEntity: JobEntity) = jobDao.addJob(jobEntity)

    fun addJobs(jobList: List<JobResponse>) = flow {
        val existingJobs = jobDao.listAllJobs()
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
        emit(jobDao.addJobs(newJobs))
    }

    fun saveJob(jobId: String) = jobDao.saveJob(jobId)

    fun unSaveJob(jobId: String) = jobDao.unSaveJob(jobId)

    fun isJobSaved(jobId: String) = jobDao.isJobSaved(jobId)

    fun getJob(jobId: String) = jobDao.getJob(jobId)

    fun listDatabaseJobs() = jobDao.listAllJobs()

    fun listSavedJobs() = jobDao.listSavedJobs()

    fun countDatabaseJobs() = jobDao.countJobs()

    fun countSavedJobs() = jobDao.countSavedJobs()

}