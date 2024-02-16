package dev.goblingroup.uzworks.models

import dev.goblingroup.uzworks.database.entity.JobEntity
import dev.goblingroup.uzworks.database.entity.WorkerEntity

data class CombinedData(
    val workers: ArrayList<WorkerEntity>? = null,
    val jobs: ArrayList<JobEntity>? = null
)