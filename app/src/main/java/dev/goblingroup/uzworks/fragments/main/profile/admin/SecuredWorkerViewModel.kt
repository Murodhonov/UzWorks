package dev.goblingroup.uzworks.fragments.main.profile.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.repository.secured.SecuredWorkerRepository
import dev.goblingroup.uzworks.resource.secured_resource.job.CreateJobResource
import dev.goblingroup.uzworks.resource.secured_resource.job.DeleteJobResource
import dev.goblingroup.uzworks.resource.secured_resource.job.EditJobResource
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SecuredWorkerViewModel(
    securedWorkerService: SecuredWorkerService,
    workerRequest: WorkerRequest,
    workerId: String,
    workerEditRequest: WorkerEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private var securedWorkerRepository =
        SecuredWorkerRepository(
            securedWorkerService = securedWorkerService,
            workerRequest = workerRequest,
            workerId = workerId,
            workerEditRequest = workerEditRequest
        )

    private val createStateFlow =
        MutableStateFlow<CreateJobResource<Unit>>(CreateJobResource.CreateLoading())

    private val deleteStateFlow =
        MutableStateFlow<DeleteJobResource<Unit>>(DeleteJobResource.DeleteLoading())

    private val editStateFlow =
        MutableStateFlow<EditJobResource<Unit>>(EditJobResource.EditLoading())

    fun createDistrict(): StateFlow<CreateJobResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedWorkerRepository.createWorker()
                    .catch {
                        createStateFlow.emit(CreateJobResource.CreateError(it))
                    }
                    .collect {
                        createStateFlow.emit(CreateJobResource.CreateSuccess())
                    }
            } else {
                createStateFlow.emit(CreateJobResource.CreateError(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(): StateFlow<DeleteJobResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedWorkerRepository.deleteWorker()
                    .catch {
                        deleteStateFlow.emit(DeleteJobResource.DeleteError(it))
                    }
                    .collect {
                        deleteStateFlow.emit(DeleteJobResource.DeleteSuccess())
                    }
            } else {
                deleteStateFlow.emit(DeleteJobResource.DeleteError(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(): StateFlow<EditJobResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedWorkerRepository.editWorker()
                    .catch {
                        editStateFlow.emit(EditJobResource.EditError(it))
                    }
                    .collect {
                        editStateFlow.emit(EditJobResource.EditSuccess())
                    }
            } else {
                editStateFlow.emit(EditJobResource.EditError(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}