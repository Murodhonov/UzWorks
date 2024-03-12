package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.JobCategoryRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobCategoryViewModel @Inject constructor(
    private val jobCategoryRepository: JobCategoryRepository
) : ViewModel() {

    private val _jobCategoriesLiveData = MutableLiveData<ApiStatus<List<JobCategoryEntity>>>(
        ApiStatus.Loading())
    val jobCategoriesLiveData get() = _jobCategoriesLiveData

    init {
        fetchJobCategories()
    }

    private fun fetchJobCategories() {
        viewModelScope.launch {
            val response = jobCategoryRepository.getAllJobCategories()
            if (response.isSuccessful) {
                val emptyList = ArrayList<JobCategoryEntity>()
                response.body()?.forEach {
                    emptyList.add(it.mapToEntity())
                }
                Log.d(TAG, "fetchJobCategories: ${response.body()}")
                Log.d(TAG, "fetchJobCategories: ${response.body()?.size}")
                jobCategoryRepository.addJobCategories(emptyList)
                Log.d(
                    TAG,
                    "fetchJobCategories: ${jobCategoryRepository.listCategories().size} categories set"
                )
                _jobCategoriesLiveData.postValue(ApiStatus.Success(jobCategoryRepository.listCategories()))
            }
        }
    }

    fun findJobCategory(jobCategoryId: String) =
        jobCategoryRepository.findJobCategory(jobCategoryId)

    fun listJobCategories() = jobCategoryRepository.listCategories()

}