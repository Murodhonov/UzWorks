package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _workerCountLivedata = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val workerCountLivedata get() =  _workerCountLivedata

    private val _jobCountLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val jobCountLiveData get() =  _jobCountLiveData

    init {
        countJobs()
        countWorkers()
    }

    private fun countJobs() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val countJobsResponse = announcementRepository.countJobs()
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
                val countWorkersResponse = announcementRepository.countWorkers()
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
        return "${securityRepository.getFirstName()} ${securityRepository.getLastName()}"
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}