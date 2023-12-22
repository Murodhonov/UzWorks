package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.networking.SecuredJobService

class SecuredJobRepository(
    private val securedJobService: SecuredJobService,
    private val jobRequest: JobRequest,
    private val jobId: String,
    private val jobEditRequest: JobEditRequest
) {

    fun createJob() = securedJobService.createJob(jobRequest = jobRequest)

    fun deleteJob() = securedJobService.deleteJob(jobId = jobId)

    fun editJob() =
        securedJobService.editJob(jobEditRequest = jobEditRequest)

}