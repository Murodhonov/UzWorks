package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class JobsViewModel(
    jobService: JobService,
    private val networkHelper: NetworkHelper,
    jobId: String,
    userId: String
) : ViewModel() {

    private val jobRepository = JobRepository(jobService, jobId, userId)

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
                    .collect {
                        jobsStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                jobsStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
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

}