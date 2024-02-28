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
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.UserRole
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _announcementLiveData =
        MutableLiveData<ApiStatus<List<AnnouncementEntity>>>(ApiStatus.Loading())
    val announcementLiveData get() = _announcementLiveData

    init {
        if (securityRepository.isEmployee()) {
            loadJobs()
        } else if (securityRepository.isEmployer()) {
            loadWorkers()
        } else {
            Log.e(
                TAG,
                "${securityRepository.getUserRoles()} user roles don't contain neither ${UserRole.EMPLOYER.roleName} and ${UserRole.EMPLOYEE.roleName}",
            )
        }
    }

    private fun loadJobs() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val response = announcementRepository.getAllJobs()
                if (response.isSuccessful) {
                    Log.d(TAG, "loadJobs: ${response.body()?.size} jobs got")
                    announcementRepository.addJobs(response.body()!!)
                    _announcementLiveData.postValue(ApiStatus.Success(announcementRepository.listDatabaseAnnouncements()))
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            } else {
                _announcementLiveData.value = ApiStatus.Error(Throwable(ConstValues.NO_INTERNET))
            }
        }
    }

    private fun loadWorkers() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val response = announcementRepository.getAllWorkers()
                if (response.isSuccessful) {
                    Log.d(TAG, "loadWorkers: ${response.body()?.size} workers got")
                    announcementRepository.addWorkers(response.body()!!)
                    _announcementLiveData.postValue(ApiStatus.Success(announcementRepository.listDatabaseAnnouncements()))
                } else {
                    _announcementLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            } else {
                _announcementLiveData.postValue(ApiStatus.Error(Throwable(ConstValues.NO_INTERNET)))
            }
        }
    }

    fun saveAnnouncement(announcementId: String) =
        announcementRepository.saveAnnouncement(announcementId)

    fun unSaveAnnouncement(announcementId: String) =
        announcementRepository.unSaveAnnouncement(announcementId)

    fun listAnnouncements() = announcementRepository.listDatabaseAnnouncements()

    fun listSavedAnnouncements() = announcementRepository.listSavedAnnouncements()

    fun isAnnouncementSaved(announcementId: String) =
        announcementRepository.isAnnouncementSaved(announcementId)


}