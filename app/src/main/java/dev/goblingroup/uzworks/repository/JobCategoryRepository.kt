package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.networking.JobCategoryService
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class JobCategoryRepository @Inject constructor(
    private val jobCategoryService: JobCategoryService,
    private val jobCategoryDao: JobCategoryDao
) {

    suspend fun getJobCategoryById(categoryId: String) = jobCategoryService.getJobCategoryById(categoryId)

    suspend fun getAllJobCategories() = jobCategoryService.getAllJobCategories()

    fun addJobCategory(jobCategoryEntity: JobCategoryEntity) =
        jobCategoryDao.addJobCategory(jobCategoryEntity)

    fun addJobCategories(categoryList: List<JobCategoryEntity>) =
        flow { emit(jobCategoryDao.addJobCategories(categoryList)) }

    fun findJobCategory(categoryId: String) = jobCategoryDao.findJobCategory(categoryId)

    fun listCategories() = jobCategoryDao.listJobCategories()

}