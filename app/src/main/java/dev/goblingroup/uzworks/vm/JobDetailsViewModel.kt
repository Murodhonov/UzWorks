package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val jobLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    fun fetchJob(jobId: String): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
                val jobByIdResponse = announcementRepository.getJobById(jobId)
                if (jobByIdResponse.isSuccessful) {
                    jobLiveData.postValue(ApiStatus.Success(jobByIdResponse.body()))
                } else {
                    jobLiveData.postValue(ApiStatus.Error(Throwable(jobByIdResponse.message())))
                }
        }
        return jobLiveData
    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

}