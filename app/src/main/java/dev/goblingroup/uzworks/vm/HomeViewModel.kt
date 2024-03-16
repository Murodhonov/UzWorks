package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _workerCountLivedata = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val workerCountLivedata get() =  _workerCountLivedata

    private val _jobCountLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val jobCountLiveData get() =  _jobCountLiveData

    private val _announcementLiveData =
        MutableLiveData<ApiStatus<List<AnnouncementEntity>>>(ApiStatus.Loading())
    val announcementLiveData get() = _announcementLiveData

    init {
        countJobs()
        countWorkers()

        if (securityRepository.isEmployee()) {
            fetchTopJobs()
        } else if (securityRepository.isEmployer()) {
            fetchTopWorkers()
        }
    }

    private fun fetchTopJobs() {
        viewModelScope.launch {
            val response = announcementRepository.getTopJobs()
            if (response.isSuccessful) {
                Log.d(ConstValues.TAG, "loadJobs: ${response.body()?.size} jobs got")
                announcementRepository.addJobs(response.body()!!, true)
                _announcementLiveData.postValue(ApiStatus.Success(announcementRepository.listDatabaseAnnouncements()))
            } else {
                _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
            }
        }
    }

    private fun fetchTopWorkers() {
        viewModelScope.launch {
            val response = announcementRepository.getTopWorkers()
            if (response.isSuccessful) {
                Log.d(ConstValues.TAG, "loadWorkers: ${response.body()?.size} workers got")
                announcementRepository.addWorkers(response.body()!!, true)
                _announcementLiveData.postValue(ApiStatus.Success(announcementRepository.listDatabaseAnnouncements()))
            } else {
                _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
            }
        }
    }

    private fun countJobs() {
        viewModelScope.launch {
            val countJobsResponse = announcementRepository.countJobs()
            if (countJobsResponse.isSuccessful) {
                _jobCountLiveData.postValue(ApiStatus.Success(countJobsResponse.body()))
            } else {
                _jobCountLiveData.postValue(ApiStatus.Error(Throwable(countJobsResponse.message())))
            }
        }
    }

    private fun countWorkers() {
        viewModelScope.launch {
                val countWorkersResponse = announcementRepository.countWorkers()
                if (countWorkersResponse.isSuccessful) {
                    _workerCountLivedata.postValue(ApiStatus.Success(countWorkersResponse.body()))
                } else {
                    _workerCountLivedata.postValue(ApiStatus.Error(Throwable(countWorkersResponse.message())))
                }
        }
    }

    fun getFullName(): String {
        return "${securityRepository.getFirstName()} ${securityRepository.getLastName()}"
    }

}