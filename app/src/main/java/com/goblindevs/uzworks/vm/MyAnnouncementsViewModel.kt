package com.goblindevs.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.repository.AnnouncementRepository
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.NetworkHelper
import com.goblindevs.uzworks.utils.extractErrorMessage
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyAnnouncementsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val jobLiveData = MutableLiveData<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())

    private val workerLiveData =
        MutableLiveData<ApiStatus<List<WorkerResponse>>>(ApiStatus.Loading())

    private val deleteLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())

    lateinit var jobList: ArrayList<JobResponse>
    lateinit var workerList: ArrayList<WorkerResponse>

    fun fetchAnnouncements() {
        if (securityRepository.isEmployer()) {
            loadJobs()
        } else if (securityRepository.isEmployee()) {
            loadWorkers()
        }
    }

    fun loadJobs(): LiveData<ApiStatus<List<JobResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val jobsByUserId =
                    announcementRepository.jobsByUserId(securityRepository.getUserId())
                if (jobsByUserId.isSuccessful) {
                    jobList = ArrayList(jobsByUserId.body()!!)
                    jobLiveData.postValue(ApiStatus.Success(jobsByUserId.body()))
                } else {
                    Log.e(TAG, "loadJobs: ${jobsByUserId.code()}")
                    Log.e(TAG, "loadJobs: ${jobsByUserId.errorBody()}")
                    Log.e(TAG, "loadJobs: ${jobsByUserId.message()}")
                    jobLiveData.postValue(ApiStatus.Error(Throwable(jobsByUserId.message())))
                }
            }
        }
        return jobLiveData
    }

    fun loadWorkers(): LiveData<ApiStatus<List<WorkerResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val workersByUserId =
                    announcementRepository.workersByUserId(securityRepository.getUserId())
                if (workersByUserId.isSuccessful) {
                    workerList = ArrayList(workersByUserId.body()!!)
                    workerLiveData.postValue(ApiStatus.Success(workersByUserId.body()))
                } else {
                    Log.e(TAG, "loadWorkers: ${workersByUserId.code()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.errorBody()}")
                    Log.e(TAG, "loadWorkers: ${workersByUserId.message()}")
                    workerLiveData.postValue(ApiStatus.Error(Throwable(workersByUserId.message())))
                }
            }
        }
        return workerLiveData
    }

    fun deleteAnnouncement(announcementId: String): LiveData<ApiStatus<Int>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                deleteLiveData.postValue(ApiStatus.Loading())
                if (securityRepository.isEmployee()) {
                    Log.d(TAG, "deleteAnnouncement: deleting worker with $announcementId")
                    val deleteResponse = announcementRepository.deleteWorker(announcementId)
                    if (deleteResponse.isSuccessful) {
                        val index = workerList.indexOfFirst { it.id == announcementId }
                        workerList.removeAt(index)
                        deleteLiveData.postValue(ApiStatus.Success(index))
                    } else {
                        deleteLiveData.postValue(ApiStatus.Error(Throwable(deleteResponse.message())))
                        Log.e(TAG, "deleteAnnouncement: ${deleteResponse.code()}")
                        Log.e(
                            TAG,
                            "deleteAnnouncement: ${
                                deleteResponse.errorBody()?.extractErrorMessage()
                            }"
                        )
                    }
                } else if (securityRepository.isEmployer()) {
                    Log.d(
                        TAG,
                        "deleteAnnouncement: checking delete job progress id $announcementId"
                    )
                    val deleteResponse = announcementRepository.deleteJob(announcementId)
                    Log.d(
                        TAG,
                        "deleteAnnouncement: checking delete job progress; result of deleting job with id $announcementId position ${deleteResponse.isSuccessful}"
                    )
                    if (deleteResponse.isSuccessful) {
                        Log.d(TAG, "checking list: jobList before deleting")
                        print()
                        val index = jobList.indexOfFirst { it.id == announcementId }
                        jobList.removeAt(index)
                        Log.d(TAG, "checking list: jobList after deleting")
                        print()
                        deleteLiveData.postValue(ApiStatus.Success(index))
                    } else {
                        deleteLiveData.postValue(ApiStatus.Error(Throwable(deleteResponse.message())))
                        Log.e(TAG, "deleteAnnouncement: ${deleteResponse.code()}")
                        Log.e(
                            TAG,
                            "deleteAnnouncement: ${
                                deleteResponse.errorBody()?.extractErrorMessage()
                            }"
                        )
                    }
                }
            }
        }
        return deleteLiveData
    }

    private fun print() {
        for (index in jobList.indices) {
            Log.d(TAG, "checking list: jobList[$index] = ${jobList[index].title}")
        }
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}