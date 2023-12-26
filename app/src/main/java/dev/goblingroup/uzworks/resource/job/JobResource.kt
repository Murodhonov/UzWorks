package dev.goblingroup.uzworks.resource.job

import dev.goblingroup.uzworks.models.response.JobResponse

sealed class JobResource<T> {

    class Loading<T> : JobResource<T>()

    class Success<T : Any>(val jobList: List<JobResponse>) : JobResource<T>()

    class Error<T : Any>(val jobError: Throwable) : JobResource<T>()

}