package com.goblindevs.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.database.entity.JobCategoryEntity
import com.goblindevs.uzworks.mapper.mapToEntity
import com.goblindevs.uzworks.repository.JobCategoryRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobCategoryViewModel @Inject constructor(
    private val jobCategoryRepository: JobCategoryRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _jobCategoriesLiveData = MutableLiveData<ApiStatus<List<JobCategoryEntity>>>()
    val jobCategoriesLiveData get() = _jobCategoriesLiveData

    init {
        fetchJobCategories()
    }

    private fun fetchJobCategories() {
        viewModelScope.launch {
            if (jobCategoryRepository.listCategories().isEmpty()) {
                if (networkHelper.isNetworkConnected()) {
                    _jobCategoriesLiveData.postValue(ApiStatus.Loading())
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
            } else {
                _jobCategoriesLiveData.postValue(ApiStatus.Success(jobCategoryRepository.listCategories()))
            }
        }
    }

    fun findJobCategory(jobCategoryId: String) =
        jobCategoryRepository.findJobCategory(jobCategoryId)

    fun listJobCategories() = jobCategoryRepository.listCategories()

}