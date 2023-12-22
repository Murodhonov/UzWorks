package dev.goblingroup.uzworks.repository.secured

import dev.goblingroup.uzworks.models.request.JobCategoryEditRequest
import dev.goblingroup.uzworks.models.request.JobCategoryRequest
import dev.goblingroup.uzworks.networking.SecuredJobCategoryService

class SecuredJobCategoryRepository(
    private val securedJobCategoryService: SecuredJobCategoryService,
    private val jobCategoryRequest: JobCategoryRequest,
    private val jobCategoryId: String,
    private val jobCategoryEditRequest: JobCategoryEditRequest
) {

    fun createJobCategory() =
        securedJobCategoryService.createJobCategory(jobCategoryRequest = jobCategoryRequest)

    fun deleteJobCategory() =
        securedJobCategoryService.deleteJobCategory(jobCategoryId = jobCategoryId)

    fun editJobCategory() =
        securedJobCategoryService.editJobCategory(jobCategoryEditRequest = jobCategoryEditRequest)

}