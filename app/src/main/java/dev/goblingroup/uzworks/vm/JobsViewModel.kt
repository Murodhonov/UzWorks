package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.repository.JobRepository
import dev.goblingroup.uzworks.resource.JobResource
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

    private val jobStateFlow = MutableStateFlow<JobResource<Unit>>(JobResource.Loading())

    private val countStateFLow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun loadFirstPage(): StateFlow<JobResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getAllJobs(pageNumber = 1)
                    .catch {
                        jobStateFlow.emit(JobResource.Error(it))
                    }
                    .collect {
                        jobStateFlow.emit(JobResource.Success(it))
                    }
            } else {
                jobStateFlow.emit(JobResource.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobStateFlow
    }

    fun loadNextPage(pageNumber: Int): StateFlow<JobResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.getAllJobs(pageNumber)
                    .catch {
                        jobStateFlow.emit(JobResource.Error(it))
                    }
                    .collect {
                        jobStateFlow.emit(JobResource.Success(it))
                    }
            } else {
                jobStateFlow.emit(JobResource.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobStateFlow
    }

    fun countJobs(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobRepository.countJobs()
                    .catch {
                        countStateFLow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        countStateFLow.emit(ApiStatus.Success(it))
                    }
            } else {
                countStateFLow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return countStateFLow
    }

}