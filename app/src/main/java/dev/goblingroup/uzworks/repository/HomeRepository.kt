package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.networking.WorkerService
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val workerService: WorkerService,
    private val jobService: JobService
) {

    suspend fun countWorkers() = workerService.countWorkers()

    suspend fun countJobs() = jobService.countJobs()

}