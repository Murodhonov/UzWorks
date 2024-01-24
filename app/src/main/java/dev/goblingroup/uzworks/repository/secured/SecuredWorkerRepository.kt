package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService

class SecuredWorkerRepository(
    private val securedWorkerService: SecuredWorkerService
) {

    fun createWorker(workerRequest: WorkerRequest) = securedWorkerService.createWorker(workerRequest = workerRequest)

    fun deleteWorker(workerId: String) = securedWorkerService.deleteWorker(workerId = workerId)

    fun editWorker(workerEditRequest: WorkerEditRequest) =
        securedWorkerService.editWorker(workerEditRequest = workerEditRequest)

}