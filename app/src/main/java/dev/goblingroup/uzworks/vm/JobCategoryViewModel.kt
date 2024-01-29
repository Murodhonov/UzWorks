package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.repository.JobCategoryRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobCategoryViewModel @Inject constructor(
    private val jobCategoryRepository: JobCategoryRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _jobCategoriesLiveData = MutableLiveData<ApiStatus<List<JobCategoryEntity>>>(
        ApiStatus.Loading())
    val jobCategoriesLiveData get() = _jobCategoriesLiveData

    init {
        fetchJobCategories()
    }

    private fun fetchJobCategories() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
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
            } else {
                if (jobCategoryRepository.listCategories().isNotEmpty()) {
                    _jobCategoriesLiveData.postValue(ApiStatus.Success(jobCategoryRepository.listCategories()))
                } else {
                    _jobCategoriesLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    suspend fun findJobCategory(jobCategoryId: String) =
        jobCategoryRepository.findJobCategory(jobCategoryId)

    suspend fun listJobCategories() = jobCategoryRepository.listCategories()

}