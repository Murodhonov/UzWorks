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

    private val _announcementLiveData =
        MutableLiveData<ApiStatus<List<Any>>>(ApiStatus.Loading())
    val announcementLiveData get() = _announcementLiveData

    private val _fullNameLiveData = MutableLiveData<ApiStatus<String>>(ApiStatus.Loading())
    val fullNameLiveData get() = _fullNameLiveData

    init {
        loadUser()
        countJobs()
        countWorkers()

        if (securityRepository.isEmployee()) {
            fetchTopJobs()
        } else if (securityRepository.isEmployer()) {
            fetchTopWorkers()
        }
    }

    private fun loadUser() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securityRepository.getUser()
                if (response.isSuccessful) {
                    fullNameLiveData.postValue(ApiStatus.Success("${response.body()?.firstName} ${response.body()?.lastName}"))
                } else {
                    fullNameLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
    }

    private fun fetchTopJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = announcementRepository.getTopJobs()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadJobs: ${response.body()?.size} jobs got")
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            }
        }
    }

    private fun fetchTopWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = announcementRepository.getTopWorkers()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadWorkers: ${response.body()?.size} workers got")
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            }
        }
    }

    private fun countJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val countJobsResponse = announcementRepository.countJobs()
                if (countJobsResponse.isSuccessful) {
                    _jobCountLiveData.postValue(ApiStatus.Success(countJobsResponse.body()))
                } else {
                    _jobCountLiveData.postValue(ApiStatus.Error(Throwable(countJobsResponse.message())))
                }
            }
        }
    }

    private fun countWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val countWorkersResponse = announcementRepository.countWorkers()
                if (countWorkersResponse.isSuccessful) {
                    _workerCountLivedata.postValue(ApiStatus.Success(countWorkersResponse.body()))
                } else {
                    _workerCountLivedata.postValue(ApiStatus.Error(Throwable(countWorkersResponse.message())))
                }
            }
        }
    }

    fun isSaved(announcementId: String): Boolean =
        announcementRepository.isAnnouncementSaved(announcementId)

    fun unSave(announcement: AnnouncementEntity) {
        announcementRepository.saveAnnouncement(announcement)
    }

    fun save(announcement: AnnouncementEntity) {
        announcementRepository.saveAnnouncement(announcement)
    }
}