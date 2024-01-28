package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.secured.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredWorkerViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createStateFlow =
        MutableStateFlow<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    private val deleteStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createWorker(workerRequest: WorkerRequest): StateFlow<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(
                    TAG,
                    "createWorker: starting create worker $workerRequest in view model"
                )
                securedWorkerRepository.createWorker(workerRequest)
                    .catch {
                        createStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        createStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                createStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteWorker(workerId: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedWorkerRepository.deleteWorker(workerId)
                    .catch {
                        deleteStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        deleteStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                deleteStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editWorker(workerEditRequest: WorkerEditRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedWorkerRepository.editWorker(workerEditRequest)
                    .catch {
                        editStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        editStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                editStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}