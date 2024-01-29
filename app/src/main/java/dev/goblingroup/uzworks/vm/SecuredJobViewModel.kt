package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.JobEditRequest
import dev.goblingroup.uzworks.models.request.JobRequest
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.secured.SecuredJobRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredJobViewModel @Inject constructor(
    private val securedJobRepository: SecuredJobRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createJob(jobRequest: JobRequest): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(
                    TAG,
                    "createJob: creating job for $jobRequest object ${this@SecuredJobViewModel::class.java.simpleName}"
                )
                val response = securedJobRepository.createJob(jobRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createLiveData
    }

    fun deleteJob(jobId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedJobRepository.deleteJob(jobId)
                if (response.isSuccessful) {
                    deleteLiveData.postValue(ApiStatus.Success(null))
                } else {
                    deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                deleteLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteLiveData
    }

    fun editJob(jobEditRequest: JobEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedJobRepository.editJob(jobEditRequest)
                if (response.isSuccessful) {
                    editLiveData.postValue(ApiStatus.Success(null))
                } else {
                    editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editLiveData
    }

}