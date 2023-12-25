package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.JobService

class JobRepository(
    private val jobService: JobService,
    private val jobId: String
) {

    fun getJobById() = jobService.getJobById(jobId)

    fun getAllJobs() = jobService.getAllJobs()

}