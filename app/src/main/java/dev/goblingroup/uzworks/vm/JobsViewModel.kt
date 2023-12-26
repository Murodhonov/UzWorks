package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.utils.NetworkHelper

class JobsViewModel(
    jobService: JobService,
    private val networkHelper: NetworkHelper,
    jobId: String,
    userId: String
) : ViewModel() {

    private val jobRepository = JobRepository(jobService, jobId, userId)

}