package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.WorkerCreateRequest
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.SecuredWorkerRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredWorkerViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createWorker(workerCreateRequest: WorkerCreateRequest): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
                Log.d(
                    TAG,
                    "createWorker: starting create worker $workerCreateRequest in view model"
                )
                val response = securedWorkerRepository.createWorker(workerCreateRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
        }
        return createLiveData
    }

    fun deleteWorker(workerId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
                val response = securedWorkerRepository.deleteWorker(workerId)
                if (response.isSuccessful) {
                    deleteLiveData.postValue(ApiStatus.Success(null))
                } else {
                    deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
        }
        return deleteLiveData
    }

    fun editWorker(workerEditRequest: WorkerEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
                val response = securedWorkerRepository.editWorker(workerEditRequest)
                if (response.isSuccessful) {
                    editLiveData.postValue(ApiStatus.Success(null))
                } else {
                    editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
        }
        return editLiveData
    }

}