package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.networking.SecuredJobCategoryService
import javax.inject.Inject

class SecuredJobCategoryRepository @Inject constructor(
    private val securedJobCategoryService: SecuredJobCategoryService
) {

    suspend fun createJobCategory(jobCategoryRequest: JobCategoryRequest) =
        securedJobCategoryService.createJobCategory(jobCategoryRequest = jobCategoryRequest)

    suspend fun deleteJobCategory(jobCategoryId: String) =
        securedJobCategoryService.deleteJobCategory(jobCategoryId = jobCategoryId)

    suspend fun editJobCategory(jobCategoryEditRequest: JobCategoryEditRequest) =
        securedJobCategoryService.editJobCategory(jobCategoryEditRequest = jobCategoryEditRequest)

}