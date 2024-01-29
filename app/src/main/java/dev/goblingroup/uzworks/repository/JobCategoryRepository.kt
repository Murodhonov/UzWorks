package dev.goblingroup.uzworks.repository

import dev.goblingroup.uzworks.database.dao.JobCategoryDao
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.networking.JobCategoryService
import javax.inject.Inject

class JobCategoryRepository @Inject constructor(
    private val jobCategoryService: JobCategoryService,
    private val jobCategoryDao: JobCategoryDao
) {

    suspend fun getJobCategoryById(categoryId: String) = jobCategoryService.getJobCategoryById(categoryId)

    suspend fun getAllJobCategories() = jobCategoryService.getAllJobCategories()

    suspend fun addJobCategory(jobCategoryEntity: JobCategoryEntity) =
        jobCategoryDao.addJobCategory(jobCategoryEntity)

    suspend fun addJobCategories(categoryList: List<JobCategoryEntity>) = jobCategoryDao.addJobCategories(categoryList)

    suspend fun findJobCategory(categoryId: String) = jobCategoryDao.findJobCategory(categoryId)

    suspend fun listCategories() = jobCategoryDao.listJobCategories()

}