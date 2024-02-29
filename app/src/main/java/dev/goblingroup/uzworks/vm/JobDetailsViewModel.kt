package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.response.JobCategoryResponse
import dev.goblingroup.uzworks.models.response.JobResponse
import dev.goblingroup.uzworks.repository.AnnouncementRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailsViewModel @Inject constructor(
    private val announcementRepository: AnnouncementRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val jobLiveData = MutableLiveData<ApiStatus<JobResponse>>(ApiStatus.Loading())

    fun fetchJob(jobId: String): LiveData<ApiStatus<JobResponse>> {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val jobByIdResponse = announcementRepository.getJobById(jobId)
                if (jobByIdResponse.isSuccessful) {
                    jobLiveData.postValue(ApiStatus.Success(jobByIdResponse.body()))
                } else {
                    jobLiveData.postValue(ApiStatus.Error(Throwable(jobByIdResponse.message())))
                }
            } else {
                jobLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return jobLiveData
    }

    fun getLanguageCode() = securityRepository.getLanguageCode()

}