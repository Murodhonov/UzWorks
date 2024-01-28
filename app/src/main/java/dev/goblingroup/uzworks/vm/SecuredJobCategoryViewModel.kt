package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.repository.secured.SecuredJobCategoryRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredJobCategoryViewModel @Inject constructor(
    private val securedJobCategoryRepository: SecuredJobCategoryRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createStateFlow =
        MutableStateFlow<ApiStatus<JobCategoryResponse>>(ApiStatus.Loading())

    private val deleteStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createJobCategory(jobCategoryRequest: JobCategoryRequest): StateFlow<ApiStatus<JobCategoryResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.createJobCategory(jobCategoryRequest)
                    .catch {
                        createStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        createStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                createStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteJobCategory(jobCategoryId: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.deleteJobCategory(jobCategoryId)
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

    fun editJobCategory(jobCategoryEditRequest: JobCategoryEditRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.editJobCategory(jobCategoryEditRequest)
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