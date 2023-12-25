package dev.goblingroup.uzworks.fragments.main.profile.admin.job_category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.networking.SecuredJobCategoryService
import dev.goblingroup.uzworks.repository.secured.SecuredJobCategoryRepository
import dev.goblingroup.uzworks.resource.secured_resource.job_category.CreateJobCategoryResource
import dev.goblingroup.uzworks.resource.secured_resource.job_category.DeleteJobCategoryResource
import dev.goblingroup.uzworks.resource.secured_resource.job_category.EditJobCategoryResource
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SecuredJobCategoryViewModel(
    securedJobCategoryService: SecuredJobCategoryService,
    jobCategoryRequest: JobCategoryRequest,
    jobCategoryId: String,
    jobCategoryEditRequest: JobCategoryEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private var securedJobCategoryRepository =
        SecuredJobCategoryRepository(
            securedJobCategoryService = securedJobCategoryService,
            jobCategoryRequest = jobCategoryRequest,
            jobCategoryId = jobCategoryId,
            jobCategoryEditRequest = jobCategoryEditRequest
        )

    private val createStateFlow =
        MutableStateFlow<CreateJobCategoryResource<Unit>>(CreateJobCategoryResource.CreateLoading())

    private val deleteStateFlow =
        MutableStateFlow<DeleteJobCategoryResource<Unit>>(DeleteJobCategoryResource.DeleteLoading())

    private val editStateFlow =
        MutableStateFlow<EditJobCategoryResource<Unit>>(EditJobCategoryResource.EditLoading())

    fun createDistrict(): StateFlow<CreateJobCategoryResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.createJobCategory()
                    .catch {
                        createStateFlow.emit(CreateJobCategoryResource.CreateError(it))
                    }
                    .collect {
                        createStateFlow.emit(CreateJobCategoryResource.CreateSuccess())
                    }
            } else {
                createStateFlow.emit(CreateJobCategoryResource.CreateError(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(): StateFlow<DeleteJobCategoryResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.deleteJobCategory()
                    .catch {
                        deleteStateFlow.emit(DeleteJobCategoryResource.DeleteError(it))
                    }
                    .collect {
                        deleteStateFlow.emit(DeleteJobCategoryResource.DeleteSuccess())
                    }
            } else {
                deleteStateFlow.emit(DeleteJobCategoryResource.DeleteError(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(): StateFlow<EditJobCategoryResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedJobCategoryRepository.editJobCategory()
                    .catch {
                        editStateFlow.emit(EditJobCategoryResource.EditError(it))
                    }
                    .collect {
                        editStateFlow.emit(EditJobCategoryResource.EditSuccess())
                    }
            } else {
                editStateFlow.emit(EditJobCategoryResource.EditError(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }

}