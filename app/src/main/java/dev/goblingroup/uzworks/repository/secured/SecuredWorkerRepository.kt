package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService

class SecuredWorkerRepository(
    private val securedWorkerService: SecuredWorkerService,
    val workerRequest: WorkerRequest,
    private val workerId: String,
    private val workerEditRequest: WorkerEditRequest
) {

    fun createWorker() = securedWorkerService.createWorker(workerRequest = workerRequest)

    fun deleteWorker() = securedWorkerService.deleteWorker(workerId = workerId)

    fun editWorker() =
        securedWorkerService.editWorker(workerEditRequest = workerEditRequest)

}