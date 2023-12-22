package dev.goblingroup.uzworks.resource.secured_resource.job_category

sealed class CreateJobCategoryResource<T> {

    class CreateLoading<T> : CreateJobCategoryResource<T>()

    class CreateSuccess<T> : CreateJobCategoryResource<T>()

    class CreateError<T : Any>(val createError: Throwable) : CreateJobCategoryResource<T>()

}