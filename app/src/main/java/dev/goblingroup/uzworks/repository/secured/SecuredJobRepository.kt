package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.networking.SecuredJobService

class SecuredJobRepository(
    private val securedJobService: SecuredJobService
) {

    fun createJob(jobRequest: JobRequest) = securedJobService.createJob(jobRequest = jobRequest)

    fun deleteJob(jobId: String) = securedJobService.deleteJob(jobId = jobId)

    fun editJob(jobEditRequest: JobEditRequest) =
        securedJobService.editJob(jobEditRequest = jobEditRequest)

}