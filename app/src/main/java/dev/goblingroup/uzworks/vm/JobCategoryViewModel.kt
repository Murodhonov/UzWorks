package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.networking.JobCategoryService
import dev.goblingroup.uzworks.repository.JobCategoryRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class JobCategoryViewModel(
    private val appDatabase: AppDatabase,
    jobCategoryService: JobCategoryService,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "JobCategoryViewModel"

    private val jobCategoryRepository =
        JobCategoryRepository(jobCategoryService, appDatabase.jobCategoryDao())

    private val jobCategoriesStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    init {
        fetchJobCategories()
    }

    private fun fetchJobCategories() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobCategoryRepository.getAllJobCategories()
                    .catch {
                        jobCategoriesStateFlow.emit(ApiStatus.Error(it))
                    }
                    .flatMapConcat { responseList ->
                        val emptyList = ArrayList<JobCategoryEntity>()
                        responseList.forEach {
                            emptyList.add(it.mapToEntity())
                        }
                        jobCategoryRepository.addJobCategories(emptyList)
                    }
                    .collect {
                        Log.d(
                            TAG,
                            "fetchJobCategories: ${jobCategoryRepository.listCategories().size} categories set"
                        )
                        jobCategoriesStateFlow.emit(ApiStatus.Success(jobCategoryRepository.listCategories()))
                    }
            } else {
                if (jobCategoryRepository.listCategories().isNotEmpty()) {
                    jobCategoriesStateFlow.emit(ApiStatus.Success(jobCategoryRepository.listCategories()))
                } else {
                    jobCategoriesStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun getJobCategories(): StateFlow<ApiStatus<Unit>> {
        return jobCategoriesStateFlow
    }

    fun findJobCategory(jobCategoryId: String) =
        jobCategoryRepository.findJobCategory(jobCategoryId)

}