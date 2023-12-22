package dev.goblingroup.uzworks.resource.secured_resource.job_category

sealed class DeleteJobCategoryResource<T> {

    class DeleteLoading<T> : DeleteJobCategoryResource<T>()

    class DeleteSuccess<T> : DeleteJobCategoryResource<T>()

    class DeleteError<T : Any>(val deleteError: Throwable) : DeleteJobCategoryResource<T>()

}