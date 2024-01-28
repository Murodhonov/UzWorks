package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import javax.inject.Inject

class SecuredWorkerRepository @Inject constructor(
    private val securedWorkerService: SecuredWorkerService
) {

    suspend fun createWorker(workerRequest: WorkerRequest) = securedWorkerService.createWorker(workerRequest = workerRequest)

    suspend fun deleteWorker(workerId: String) = securedWorkerService.deleteWorker(workerId = workerId)

    suspend fun editWorker(workerEditRequest: WorkerEditRequest) =
        securedWorkerService.editWorker(workerEditRequest = workerEditRequest)

}