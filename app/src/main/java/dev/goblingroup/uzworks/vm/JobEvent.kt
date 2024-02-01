package dev.goblingroup.uzworks.vm

sealed class JobEvent {

    data class JobSaved(val jobId: String) : JobEvent()

    data class JobUnsaved(val jobId: String) : JobEvent()

}
