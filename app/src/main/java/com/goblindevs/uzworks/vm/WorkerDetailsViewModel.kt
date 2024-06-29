package com.goblindevs.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.repository.AnnouncementRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val workerLiveData = MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    fun fetchWorker(workerId: String): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val workerByIdResponse = announcementRepository.getWorkerById(workerId)
                if (workerByIdResponse.isSuccessful) {
                    workerLiveData.postValue(ApiStatus.Success(workerByIdResponse.body()))
                } else {
                    workerLiveData.postValue(ApiStatus.Error(Throwable(workerByIdResponse.message())))
                    Log.e(TAG, "fetchWorker: ${workerByIdResponse.code()}")
                    Log.e(TAG, "fetchWorker: ${workerByIdResponse.message()}")
                    Log.e(TAG, "fetchWorker: ${workerByIdResponse.errorBody()}")
                }
            }
        }
        return workerLiveData
    }

}