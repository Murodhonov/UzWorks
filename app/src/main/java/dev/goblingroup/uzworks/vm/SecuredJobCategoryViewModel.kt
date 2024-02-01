package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.JobCategoryCreateRequest
import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.response.JobCategoryCreateResponse
import dev.goblingroup.uzworks.repository.secured.SecuredJobCategoryRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredJobCategoryViewModel @Inject constructor(
    private val securedJobCategoryRepository: SecuredJobCategoryRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<JobCategoryCreateResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createJobCategory(jobCategoryCreateRequest: JobCategoryCreateRequest): LiveData<ApiStatus<JobCategoryCreateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response =
                    securedJobCategoryRepository.createJobCategory(jobCategoryCreateRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(null))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createLiveData
    }

    fun deleteJobCategory(jobCategoryId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response =
                    securedJobCategoryRepository.deleteJobCategory(jobCategoryId)
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

    fun editJobCategory(jobCategoryEditRequest: JobCategoryEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response =
                    securedJobCategoryRepository.editJobCategory(jobCategoryEditRequest)
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