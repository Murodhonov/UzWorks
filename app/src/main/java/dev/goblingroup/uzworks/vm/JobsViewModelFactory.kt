package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.utils.NetworkHelper

class JobsViewModelFactory(
    private val appDatabase: AppDatabase,
    private val jobService: JobService,
    private val networkHelper: NetworkHelper,
    var jobId: String,
    var userId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobsViewModel::class.java)) {
            return JobsViewModel(
                appDatabase = appDatabase,
                jobService = jobService,
                networkHelper = networkHelper,
                jobId = jobId,
                userId = userId
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}