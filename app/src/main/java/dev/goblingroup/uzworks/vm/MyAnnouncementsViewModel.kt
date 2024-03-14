package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.repository.SecuredJobRepository
import dev.goblingroup.uzworks.repository.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAnnouncementsViewModel @Inject constructor(
    private val securedJobRepository: SecuredJobRepository,
    private val securedWorkerRepository: SecuredWorkerRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _jobLiveData = MutableLiveData<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())
    val jobLiveData get() = _jobLiveData

    private val _workerLiveData = MutableLiveData<ApiStatus<List<WorkerResponse>>>(ApiStatus.Loading())
    val workerLiveData get() = _workerLiveData

    init {
        if (securityRepository.isEmployer()) {
            loadJobs()
        } else if (securityRepository.isEmployee()) {
            loadWorkers()
        }
    }

    private fun loadJobs() {
        viewModelScope.launch {
                val jobsByUserId = securedJobRepository.getJobsByUserId(securityRepository.getUserId())
                if (jobsByUserId.isSuccessful) {
                    _jobLiveData.postValue(ApiStatus.Success(jobsByUserId.body()))
                } else {
                    Log.e(TAG, "loadJobs: ${jobsByUserId.code()}")
                    Log.e(TAG, "loadJobs: ${jobsByUserId.errorBody()}")
                    Log.e(TAG, "loadJobs: ${jobsByUserId.message()}")
                    _jobLiveData.postValue(ApiStatus.Error(Throwable(jobsByUserId.message())))
                }
        }
    }

    private fun loadWorkers() {
        viewModelScope.launch {
                val workersByUserId =
                    securedWorkerRepository.getWorkersByUserId(securityRepository.getUserId())
                if (workersByUserId.isSuccessful) {
                    _workerLiveData.postValue(ApiStatus.Success(workersByUserId.body()))
                } else {
                    Log.e(TAG, "loadWorkers: ${workersByUserId.code()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.errorBody()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.message()}")
                    _workerLiveData.postValue(ApiStatus.Error(Throwable(workersByUserId.message())))
                }
        }
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}