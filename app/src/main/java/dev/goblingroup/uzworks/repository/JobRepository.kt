package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.JobService

class JobRepository(
    private val jobService: JobService,
    private val jobId: String,
    private val userId: String
) {

    fun getJobById() = jobService.getJobById(jobId)

    fun getAllJobs(pageNumber: Int) = jobService.getAllJobs(pageNumber = pageNumber)

    fun countJobs() = jobService.countJobs()

    fun getJobByUserId() = jobService.getJobsByUserId(userId = userId)

}