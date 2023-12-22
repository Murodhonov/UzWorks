package dev.goblingroup.uzworks.resource.secured_resource.job_category

sealed class EditJobCategoryResource<T> {

    class EditLoading<T> : EditJobCategoryResource<T>()

    class EditSuccess<T> : EditJobCategoryResource<T>()

    class EditError<T : Any>(val createError: Throwable) : EditJobCategoryResource<T>()

}