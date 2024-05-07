package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.AnnouncementEntity
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _announcementLiveData = MutableLiveData<ApiStatus<List<Any>>>(ApiStatus.Loading())
    val announcementLiveData get() = _announcementLiveData

    init {
        fetchData()
    }

    fun fetchData() {
        if (securityRepository.isEmployer()) {
            loadWorkers()
        } else if (securityRepository.isEmployee()) {
            loadJobs()
        }
    }

    private fun loadWorkers() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _announcementLiveData.postValue(ApiStatus.Loading())
                val response = announcementRepository.getAllWorkers()
                if (response.isSuccessful) {
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    _announcementLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
    }

    private fun loadJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                _announcementLiveData.postValue(ApiStatus.Loading())
                val response = announcementRepository.getAllJobs()
                if (response.isSuccessful) {
                    _announcementLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    _announcementLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            }
        }
    }

    fun isSaved(announcementId: String) = announcementRepository.isAnnouncementSaved(announcementId)

    fun save(announcementEntity: AnnouncementEntity) =
        announcementRepository.saveAnnouncement(announcementEntity)

    fun unSave(announcementId: String) = announcementRepository.unSaveAnnouncement(announcementId)

}