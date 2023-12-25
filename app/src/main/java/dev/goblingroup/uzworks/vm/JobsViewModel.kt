package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import dev.goblingroup.uzworks.networking.JobService
import dev.goblingroup.uzworks.utils.NetworkHelper

class JobsViewModel(
    jobService: JobService,
    private val networkHelper: NetworkHelper
) : ViewModel() {


}