package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.networking.JobCategoryService
import dev.goblingroup.uzworks.utils.NetworkHelper

class JobCategoryViewModelFactory(
    private val appDatabase: AppDatabase,
    private val jobCategoryService: JobCategoryService,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobCategoryViewModel::class.java)) {
            return JobCategoryViewModel(
                appDatabase,
                jobCategoryService,
                networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}