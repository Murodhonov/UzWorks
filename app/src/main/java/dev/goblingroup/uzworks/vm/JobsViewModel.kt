package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
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

    private val jobByIdStateFlow = MutableStateFlow<ApiStatus<JobResponse>>(ApiStatus.Loading())

    private val _jobsStateFlow = MutableStateFlow<ApiStatus<List<JobEntity>>>(ApiStatus.Loading())
    val jobsStateFlow get() = _jobsStateFlow

    private val countStateFlow = MutableStateFlow<ApiStatus<Int>>(ApiStatus.Loading())

    private val jobsByIdStateFlow =
        MutableStateFlow<ApiStatus<List<JobResponse>>>(ApiStatus.Loading())

    fun getJobById(jobId: String): StateFlow<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getJobById(jobId)
                    .catch {
                        jobByIdStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        jobByIdStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                jobByIdStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobByIdStateFlow
    }

    fun getAllJobs() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getAllJobs()
                    .catch {
                        _jobsStateFlow.emit(ApiStatus.Error(it))
                    }
                    .flatMapConcat {
                        jobRepository.addJobs(it)
                    }
                    .collect {
                        _jobsStateFlow.emit(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                    }
            } else {
                if (jobRepository.countDatabaseJobs() > 0) {
                    _jobsStateFlow.emit(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                } else {
                    _jobsStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun countJobs(): StateFlow<ApiStatus<Int>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.countJobs()
                    .catch {
                        countStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        countStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                countStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return countStateFlow
    }

    fun getJobsByUserId(userId: String): StateFlow<ApiStatus<List<JobResponse>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getJobByUserId(userId)
                    .catch {
                        jobsByIdStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        jobsByIdStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                jobsByIdStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobsByIdStateFlow
    }

    fun addJob(jobEntity: JobEntity) = jobRepository.addJob(jobEntity)

    fun saveJob(jobId: String) = jobRepository.saveJob(jobId)

    fun unSaveJob(jobId: String) = jobRepository.unSaveJob(jobId)

    fun isJobSaved(jobId: String) = jobRepository.isJobSaved(jobId)

    fun getJob(jobId: String) = jobRepository.getJob(jobId)

    fun listDatabaseJobs() = jobRepository.listDatabaseJobs()

    fun listSavedJobs() = jobRepository.listSavedJobs()

    fun countDatabaseJobs() = jobRepository.countDatabaseJobs()

    fun countSavedJobs() = jobRepository.countSavedJobs()

}