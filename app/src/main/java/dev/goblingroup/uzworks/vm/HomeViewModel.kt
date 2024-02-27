package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.models.response.WorkerResponse
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.repository.WorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val jobRepository: JobRepository,
    private val workerRepository: WorkerRepository,
    private val userDao: UserDao,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _workerCountLivedata = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val workerCountLivedata get() =  _workerCountLivedata

    private val _jobCountLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val jobCountLiveData get() =  _jobCountLiveData

    private val _workersLiveData = MutableLiveData<ApiStatus<List<WorkerResponse>>>(ApiStatus.Loading())
    val workersLiveData get() = _workersLiveData

    private val _jobsLiveData = MutableLiveData<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())
    val jobsLiveData get() = _jobsLiveData

    init {
        countJobs()
        countWorkers()
        fetchAnnouncements()
    }

    private fun fetchAnnouncements() {
        if (securityRepository.isEmployer()) {
            fetchWorkers()
        } else if (securityRepository.isEmployee()) {
            fetchJobs()
        }
    }

    private fun fetchWorkers() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val workersResponse = workerRepository.getAllWorkers()
                if (workersResponse.isSuccessful) {
                    _workersLiveData.postValue(ApiStatus.Success(workersResponse.body()))
                } else {
                    Log.e(TAG, "fetchWorkers: ${workersResponse.code()}")
                    Log.e(TAG, "fetchWorkers: ${workersResponse.message()}")
                    Log.e(TAG, "fetchWorkers: ${workersResponse.errorBody()}")
                    _workersLiveData.postValue(ApiStatus.Error(Throwable(workersResponse.message())))
                }
            } else {
                _workersLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    private fun fetchJobs() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val jobsResponse = jobRepository.getAllJobs()
                if (jobsResponse.isSuccessful) {
                    _jobsLiveData.postValue(ApiStatus.Success(jobsResponse.body()))
                } else {
                    Log.e(TAG, "fetchJobs: ${jobsResponse.code()}")
                    Log.e(TAG, "fetchJobs: ${jobsResponse.message()}")
                    Log.e(TAG, "fetchJobs: ${jobsResponse.errorBody()}")
                    _jobsLiveData.postValue(ApiStatus.Error(Throwable(jobsResponse.message())))
                }
            } else {
                _jobsLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    private fun countJobs() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val countJobsResponse = jobRepository.countJobs()
                if (countJobsResponse.isSuccessful) {
                    _jobCountLiveData.postValue(ApiStatus.Success(countJobsResponse.body()))
                } else {
                    _jobCountLiveData.postValue(ApiStatus.Error(Throwable(countJobsResponse.message())))
                }
            } else {
                _jobCountLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    private fun countWorkers() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val countWorkersResponse = workerRepository.countWorkers()
                if (countWorkersResponse.isSuccessful) {
                    _workerCountLivedata.postValue(ApiStatus.Success(countWorkersResponse.body()))
                } else {
                    _workerCountLivedata.postValue(ApiStatus.Error(Throwable(countWorkersResponse.message())))
                }
            } else {
                _workerCountLivedata.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    fun getFullName(): String {
        val userData = userDao.getUser()
        return "${userData?.firstname} ${userData?.lastName}"
    }

}