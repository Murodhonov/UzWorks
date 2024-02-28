package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkerDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val workerLiveData = MutableLiveData<ApiStatus<WorkerResponse>>(ApiStatus.Loading())

    fun fetchJob(workerId: String): LiveData<ApiStatus<WorkerResponse>> {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val workerByIdResponse = announcementRepository.getWorkerById(workerId)
                if (workerByIdResponse.isSuccessful) {
                    workerLiveData.postValue(ApiStatus.Success(workerByIdResponse.body()))
                } else {
                    workerLiveData.postValue(ApiStatus.Error(Throwable(workerByIdResponse.message())))
                }
            } else {
                workerLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return workerLiveData
    }

}