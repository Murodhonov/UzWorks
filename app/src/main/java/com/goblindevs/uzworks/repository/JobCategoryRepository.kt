package com.goblindevs.uzworks.repository

import com.goblindevs.uzworks.database.dao.JobCategoryDao
import com.goblindevs.uzworks.database.entity.JobCategoryEntity
import com.goblindevs.uzworks.networking.JobCategoryService
import javax.inject.Inject

class JobCategoryRepository @Inject constructor(
    private val jobCategoryService: JobCategoryService,
    private val jobCategoryDao: JobCategoryDao
) {

    suspend fun getJobCategoryById(categoryId: String) = jobCategoryService.getJobCategoryById(categoryId)

    suspend fun getAllJobCategories() = jobCategoryService.getAllJobCategories()

    fun addJobCategories(categoryList: List<JobCategoryEntity>) = jobCategoryDao.addJobCategories(categoryList)

    fun findJobCategory(categoryId: String) = jobCategoryDao.findJobCategory(categoryId)

    fun listCategories() = jobCategoryDao.listJobCategories()

}