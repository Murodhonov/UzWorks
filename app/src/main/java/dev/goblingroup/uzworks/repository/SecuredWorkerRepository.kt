package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import javax.inject.Inject

class SecuredWorkerRepository @Inject constructor(
    private val securedWorkerService: SecuredWorkerService
) {

    suspend fun createWorker(workerCreateRequest: WorkerCreateRequest) = securedWorkerService.createWorker(workerCreateRequest = workerCreateRequest)

    suspend fun deleteWorker(workerId: String) = securedWorkerService.deleteWorker(workerId = workerId)

    suspend fun editWorker(workerEditRequest: WorkerEditRequest) =
        securedWorkerService.editWorker(workerEditRequest = workerEditRequest)

    suspend fun getWorkersByUserId(userId: String) = securedWorkerService.getWorkersByUserId(userId)

}