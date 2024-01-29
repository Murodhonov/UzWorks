package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobsViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    init {
        getAllJobs()
    }

    private val jobByIdLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    private val _jobsLiveData = MutableLiveData<ApiStatus<List<JobEntity>>>(ApiStatus.Loading())
    val jobsLiveData get() = _jobsLiveData

    private val countLiveData = MutableLiveData<ApiStatus<Int>>(ApiStatus.Loading())

    private val jobsByIdLiveData =
        MutableLiveData<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())

    fun getJobById(jobId: String): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = jobRepository.getJobById(jobId)
                if (response.isSuccessful) {
                    jobByIdLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    jobByIdLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                jobByIdLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobByIdLiveData
    }

    private fun getAllJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = jobRepository.getAllJobs()
                if (response.isSuccessful) {
                    jobRepository.addJobs(response.body()!!)
                    _jobsLiveData.postValue(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                } else {
                    _jobsLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                if (jobRepository.countDatabaseJobs() > 0) {
                    _jobsLiveData.postValue(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                } else {
                    _jobsLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun countJobs(): LiveData<ApiStatus<Int>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = jobRepository.countJobs()
                if (response.isSuccessful) {
                    countLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    countLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                countLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return countLiveData
    }

    fun getJobsByUserId(userId: String): LiveData<ApiStatus<List<JobResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = jobRepository.getJobByUserId(userId)
                if (response.isSuccessful) {
                    jobsByIdLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    jobsByIdLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                jobsByIdLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobsByIdLiveData
    }

    suspend fun addJob(jobEntity: JobEntity) = jobRepository.addJob(jobEntity)

    fun saveJob(jobId: String) = jobRepository.saveJob(jobId)

    suspend fun unSaveJob(jobId: String) = jobRepository.unSaveJob(jobId)

    suspend fun isJobSaved(jobId: String) = jobRepository.isJobSaved(jobId)

    suspend fun getJob(jobId: String) = jobRepository.getJob(jobId)

    suspend fun listDatabaseJobs() = jobRepository.listDatabaseJobs()

    fun listSavedJobs() = jobRepository.listSavedJobs()

    suspend fun countDatabaseJobs() = jobRepository.countDatabaseJobs()

    suspend fun countSavedJobs() = jobRepository.countSavedJobs()

}