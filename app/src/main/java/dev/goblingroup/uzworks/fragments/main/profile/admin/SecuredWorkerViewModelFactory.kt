package dev.goblingroup.uzworks.fragments.main.profile.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredWorkerViewModelFactory(
    private val securedWorkerService: SecuredWorkerService,
    var workerRequest: WorkerRequest,
    private val workerId: String,
    var workerEditRequest: WorkerEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredWorkerViewModel::class.java)) {
            return SecuredWorkerViewModel(
                securedWorkerService = securedWorkerService,
                workerRequest = workerRequest,
                workerId = workerId,
                workerEditRequest = workerEditRequest,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}