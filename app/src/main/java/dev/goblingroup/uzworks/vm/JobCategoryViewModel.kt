package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.JobCategoryRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobCategoryViewModel @Inject constructor(
    private val jobCategoryRepository: JobCategoryRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _jobCategoriesStateFlow = MutableStateFlow<ApiStatus<List<JobCategoryEntity>>>(
        ApiStatus.Loading())
    val jobCategoriesStateFlow get() = _jobCategoriesStateFlow

    init {
        fetchJobCategories()
    }

    private fun fetchJobCategories() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                jobCategoryRepository.getAllJobCategories()
                    .catch {
                        _jobCategoriesStateFlow.emit(ApiStatus.Error(it))
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
                        _jobCategoriesStateFlow.emit(ApiStatus.Success(jobCategoryRepository.listCategories()))
                    }
            } else {
                if (jobCategoryRepository.listCategories().isNotEmpty()) {
                    _jobCategoriesStateFlow.emit(ApiStatus.Success(jobCategoryRepository.listCategories()))
                } else {
                    _jobCategoriesStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun findJobCategory(jobCategoryId: String) =
        jobCategoryRepository.findJobCategory(jobCategoryId)

    fun listJobCategories() = jobCategoryRepository.listCategories()

}