package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.CombinedData
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.repository.WorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.UserRole
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnnouncementViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val workerRepository: WorkerRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _combinedLiveData = MutableLiveData<ApiStatus<CombinedData>>(ApiStatus.Loading())
    val combinedLiveData get() = _combinedLiveData

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
                val response = jobRepository.getAllJobs()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadJobs: ${response.body()?.size} jobs got")
                    jobRepository.addJobs(response.body()!!)
                    _combinedLiveData.value =
                        ApiStatus.Success(CombinedData(jobs = ArrayList(jobRepository.listDatabaseJobs())))
                } else {
                    _combinedLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            } else {
                if (jobRepository.countDatabaseJobs() > 0) {
                    _combinedLiveData.value =
                        ApiStatus.Success(CombinedData(jobs = ArrayList(jobRepository.listDatabaseJobs())))
                } else {
                    _combinedLiveData.value = ApiStatus.Error(Throwable(ConstValues.NO_INTERNET))
                }
            }
        }
    }

    private fun loadWorkers() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val response = workerRepository.getAllWorkers()
                if (response.isSuccessful) {
                    Log.d(ConstValues.TAG, "loadWorkers: ${response.body()?.size} workers got")
                    workerRepository.addWorkers(response.body()!!)
                    _combinedLiveData.value =
                        ApiStatus.Success(CombinedData(workers = ArrayList(workerRepository.listDatabaseWorkers())))
                } else {
                    _combinedLiveData.value = ApiStatus.Error(Throwable(response.message()))
                }
            } else {
                if (workerRepository.countDatabaseWorkers() > 0) {
                    _combinedLiveData.value =
                        ApiStatus.Success(CombinedData(workers = ArrayList(workerRepository.listDatabaseWorkers())))
                } else {
                    _combinedLiveData.value = ApiStatus.Error(Throwable(ConstValues.NO_INTERNET))
                }
            }
        }
    }

    fun saveAnnouncement(announcementId: String) {
        if (securityRepository.isEmployee()) {
            jobRepository.saveJob(announcementId)
        } else if (securityRepository.isEmployer()) {
            workerRepository.saveWorker(announcementId)
        }
    }

    fun unSaveAnnouncement(announcementId: String): Boolean? {
        return if (securityRepository.isEmployee()) {
            jobRepository.unSaveJob(announcementId)
        } else if (securityRepository.isEmployer()) {
            workerRepository.unSaveWorker(announcementId)
        } else null
    }

    fun listDatabaseAnnouncements(): CombinedData {
        return if (securityRepository.isEmployee()) {
            CombinedData(jobs = ArrayList(jobRepository.listDatabaseJobs()))
        } else if (securityRepository.isEmployer()) {
            CombinedData(workers = ArrayList(workerRepository.listDatabaseWorkers()))
        } else CombinedData()
    }

    fun listSavedAnnouncements(): CombinedData? {
        return if (securityRepository.isEmployee()) {
            CombinedData(jobs = ArrayList(jobRepository.listSavedJobs()))
        } else if (securityRepository.isEmployer()) {
            CombinedData(workers = ArrayList(workerRepository.listSavedWorkers()))
        } else null
    }

    fun countSavedAnnouncements(): Int {
        return if (securityRepository.isEmployee()) {
            jobRepository.countSavedJobs()
        } else if (securityRepository.isEmployer()) {
            workerRepository.countSavedWorkers()
        } else 0
    }
}