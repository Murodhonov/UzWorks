package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class JobsViewModel(
    appDatabase: AppDatabase,
    jobService: JobService,
    private val networkHelper: NetworkHelper,
    jobId: String,
    userId: String
) : ViewModel() {

    private val jobRepository = JobRepository(appDatabase.jobDao(), jobService, jobId, userId)

    private val jobByIdStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val jobsStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val countStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val jobsByIdStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun getJobById(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getJobById()
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

    fun getAllJobs(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getAllJobs()
                    .catch {
                        jobsStateFlow.emit(ApiStatus.Error(it))
                    }
                    .flatMapConcat {
                        jobRepository.addJobs(it)
                    }
                    .collect {
                        jobsStateFlow.emit(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                    }
            } else {
                if (jobRepository.countDatabaseJobs() > 0) {
                    jobsStateFlow.emit(ApiStatus.Success(jobRepository.listDatabaseJobs()))
                } else {
                    jobsStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
        return jobsStateFlow
    }

    fun countJobs(): StateFlow<ApiStatus<Unit>> {
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

    fun getJobsByUserId(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getJobByUserId()
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