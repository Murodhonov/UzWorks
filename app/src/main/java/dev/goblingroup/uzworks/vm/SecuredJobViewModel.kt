package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.JobCreateRequest
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.response.JobCreateResponse
import dev.goblingroup.uzworks.repository.SecuredJobRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredJobViewModel @Inject constructor(
    private val securedJobRepository: SecuredJobRepository
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<JobCreateResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createJob(jobCreateRequest: JobCreateRequest): LiveData<ApiStatus<JobCreateResponse>> {
        viewModelScope.launch {
            val response = securedJobRepository.createJob(jobCreateRequest)
            if (response.isSuccessful) {
                createLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return createLiveData
    }

    fun deleteJob(jobId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            val response = securedJobRepository.deleteJob(jobId)
            if (response.isSuccessful) {
                deleteLiveData.postValue(ApiStatus.Success(null))
            } else {
                deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return deleteLiveData
    }

    fun editJob(jobEditRequest: JobEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            val response = securedJobRepository.editJob(jobEditRequest)
            if (response.isSuccessful) {
                editLiveData.postValue(ApiStatus.Success(null))
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
            }
        }
        return editLiveData
    }

}