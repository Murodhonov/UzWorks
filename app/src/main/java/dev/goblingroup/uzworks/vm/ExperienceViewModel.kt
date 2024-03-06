package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceCreateResponse
import dev.goblingroup.uzworks.models.response.ExperienceEditResponse
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.repository.secured.SecuredWorkerRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperienceViewModel @Inject constructor(
    private val securedWorkerRepository: SecuredWorkerRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _experienceLiveData = MutableLiveData<ApiStatus<List<ExperienceResponse>>>(ApiStatus.Loading())
    val experienceLiveData get() = _experienceLiveData

    private val createExperienceLiveData = MutableLiveData<ApiStatus<ExperienceCreateResponse>>(ApiStatus.Loading())

    private val editExperienceLiveData = MutableLiveData<ApiStatus<ExperienceEditResponse>>(ApiStatus.Loading())

    init {
        fetchExperiences()
    }

    private fun fetchExperiences() {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val experienceResponse =
                    securedWorkerRepository.getExperiencesByUserId(securityRepository.getUserId())
                if (experienceResponse.isSuccessful) {
                    _experienceLiveData.postValue(ApiStatus.Success(experienceResponse.body()))
                } else {
                    _experienceLiveData.postValue(ApiStatus.Error(Throwable(experienceResponse.message())))
                }
            } else {
                _experienceLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    fun createExperience(experienceCreateRequest: ExperienceCreateRequest): LiveData<ApiStatus<ExperienceCreateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val experienceCreateResponse =
                    securedWorkerRepository.createExperience(experienceCreateRequest)
                if (experienceCreateResponse.isSuccessful) {
                    createExperienceLiveData.postValue(ApiStatus.Success(experienceCreateResponse.body()))
                } else {
                    createExperienceLiveData.postValue(ApiStatus.Error(Throwable(experienceCreateResponse.message())))
                }
            } else {
                createExperienceLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createExperienceLiveData
    }

    fun editExperience(experienceEditRequest: ExperienceEditRequest): LiveData<ApiStatus<ExperienceEditResponse>> {
        viewModelScope.launch {
            if (networkHelper.isConnected()) {
                val editExperienceResponse = securedWorkerRepository.editExperience(experienceEditRequest)
                if (editExperienceResponse.isSuccessful) {
                    editExperienceLiveData.postValue(ApiStatus.Success(editExperienceResponse.body()))
                } else {
                    editExperienceLiveData.postValue(ApiStatus.Error(Throwable(editExperienceResponse.message())))
                }
            } else {
                editExperienceLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editExperienceLiveData
    }

}