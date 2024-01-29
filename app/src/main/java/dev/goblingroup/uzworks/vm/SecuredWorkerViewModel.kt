package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.WorkerEditRequest
import dev.goblingroup.uzworks.models.request.WorkerRequest
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.secured.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredWorkerViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createWorker(workerRequest: WorkerRequest): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(
                    TAG,
                    "createWorker: starting create worker $workerRequest in view model"
                )
                val response = securedWorkerRepository.createWorker(workerRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createLiveData
    }

    fun deleteWorker(workerId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedWorkerRepository.deleteWorker(workerId)
                if (response.isSuccessful) {
                    deleteLiveData.postValue(ApiStatus.Success(null))
                } else {
                    deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                deleteLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteLiveData
    }

    fun editWorker(workerEditRequest: WorkerEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedWorkerRepository.editWorker(workerEditRequest)
                if (response.isSuccessful) {
                    editLiveData.postValue(ApiStatus.Success(null))
                } else {
                    editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editLiveData
    }

}