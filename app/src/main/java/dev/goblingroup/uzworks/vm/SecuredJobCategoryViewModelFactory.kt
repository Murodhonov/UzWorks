package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.networking.SecuredJobCategoryService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredJobCategoryViewModelFactory(
    private val securedJobCategoryService: SecuredJobCategoryService,
    var jobCategoryRequest: JobCategoryRequest,
    private val jobCategoryId: String,
    var jobCategoryEditRequest: JobCategoryEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredJobCategoryViewModel::class.java)) {
            return SecuredJobCategoryViewModel(
                securedJobCategoryService = securedJobCategoryService,
                jobCategoryRequest = jobCategoryRequest,
                jobCategoryId = jobCategoryId,
                jobCategoryEditRequest = jobCategoryEditRequest,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}