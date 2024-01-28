package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.secured.SecuredJobRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredJobViewModel @Inject constructor(
    private val securedJobRepository: SecuredJobRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createStateFlow =
        MutableStateFlow<ApiStatus<JobResponse>>(ApiStatus.Loading())

    private val deleteStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createJob(jobRequest: JobRequest): StateFlow<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(
                    TAG,
                    "createJob: creating job for $jobRequest object ${this@SecuredJobViewModel::class.java.simpleName}"
                )
                securedJobRepository.createJob(jobRequest)
                    .catch {
                        createStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        createStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                createStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteJob(jobId: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobRepository.deleteJob(jobId)
                    .catch {
                        deleteStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        deleteStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                deleteStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editJob(jobEditRequest: JobEditRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobRepository.editJob(jobEditRequest)
                    .catch {
                        editStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        editStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                editStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}