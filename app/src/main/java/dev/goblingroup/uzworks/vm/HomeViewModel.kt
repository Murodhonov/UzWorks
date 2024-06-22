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

    val TAG = "HomeViewModel"

    private val TOTAL_COUNT = 5
    private var responseCount = 0

    private val _allResponseReceived = MutableLiveData(false)
    val allResponseReceived get() = _allResponseReceived

    private val _workerCountLivedata = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val workerCountLivedata get() =  _workerCountLivedata

    private val _jobCountLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val jobCountLiveData get() =  _jobCountLiveData

    private val _userCountLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())
    val userCountLiveData get() = _userCountLiveData

    private val _announcementLiveData =
        MutableLiveData<ApiStatus<List<Any>>>(ApiStatus.Loading())
    val announcementLiveData get() = _announcementLiveData

    private val _fullNameLiveData = MutableLiveData<ApiStatus<String>>(ApiStatus.Loading())
    val fullNameLiveData get() = _fullNameLiveData

    fun fetchData() {
        responseCount = 0
        loadUser()
        countJobs()
        countWorkers()
        countUsers()

        if (securityRepository.isEmployee()) {
            fetchTopJobs()
        } else if (securityRepository.isEmployer()) {
            fetchTopWorkers()
        }
    }

    init {
        fetchData()
    }

    private fun loadUser() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _fullNameLiveData.postValue(ApiStatus.Loading())
                val response = securityRepository.getUser()
                if (response.isSuccessful) {
                    _fullNameLiveData.postValue(ApiStatus.Success("${response.body()?.firstName} ${response.body()?.lastName}"))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _fullNameLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
    }

    private fun fetchTopJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _announcementLiveData.postValue(ApiStatus.Loading())
                val response = announcementRepository.getTopJobs()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadJobs: ${response.body()?.size} jobs got")
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            }
        }
    }

    private fun fetchTopWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _announcementLiveData.postValue(ApiStatus.Loading())
                val response = announcementRepository.getTopWorkers()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadWorkers: ${response.body()?.size} workers got")
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            }
        }
    }

    private fun countJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _jobCountLiveData.postValue(ApiStatus.Loading())
                val countJobsResponse = announcementRepository.countJobs()
                if (countJobsResponse.isSuccessful) {
                    _jobCountLiveData.postValue(ApiStatus.Success(countJobsResponse.body()))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _jobCountLiveData.postValue(ApiStatus.Error(Throwable(countJobsResponse.message())))
                }
            }
        }
    }

    private fun countWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _workerCountLivedata.postValue(ApiStatus.Loading())
                val countWorkersResponse = announcementRepository.countWorkers()
                if (countWorkersResponse.isSuccessful) {
                    _workerCountLivedata.postValue(ApiStatus.Success(countWorkersResponse.body()))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _workerCountLivedata.postValue(ApiStatus.Error(Throwable(countWorkersResponse.message())))
                }
            }
        }
    }

    private fun countUsers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _userCountLiveData.postValue(ApiStatus.Loading())
                val countUsersResponse = announcementRepository.countUsers()
                if (countUsersResponse.isSuccessful) {
                    _userCountLiveData.postValue(ApiStatus.Success(countUsersResponse.body()))
                    responseCount++
                    if (responseCount == TOTAL_COUNT) {
                        _allResponseReceived.postValue(true)
                    }
                } else {
                    _userCountLiveData.postValue(ApiStatus.Error(Throwable(countUsersResponse.message())))
                }
            }
        }
    }

    fun isSaved(announcementId: String): Boolean =
        announcementRepository.isAnnouncementSaved(announcementId)

    fun unSave(announcementId: String) {
        announcementRepository.unSaveAnnouncement(announcementId)
    }

    fun save(announcement: AnnouncementEntity) {
        announcementRepository.saveAnnouncement(announcement)
    }
}