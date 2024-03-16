package dev.goblingroup.uzworks.vm

import android.util.Log
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
import dev.goblingroup.uzworks.repository.ExperienceRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExperienceViewModel @Inject constructor(
    private val experienceRepository: ExperienceRepository,
    private val securityRepository: SecurityRepository
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
                val experienceResponse =
                    experienceRepository.getExperiencesByUserId(securityRepository.getUserId())
                if (experienceResponse.isSuccessful) {
                    _experienceLiveData.postValue(ApiStatus.Success(experienceResponse.body()))
                } else {
                    _experienceLiveData.postValue(ApiStatus.Error(Throwable(experienceResponse.message())))
                }
        }
    }

    fun createExperience(experienceCreateRequest: ExperienceCreateRequest): LiveData<ApiStatus<ExperienceCreateResponse>> {
        viewModelScope.launch {
            Log.d(TAG, "createExperience: creating $experienceCreateRequest")
            val experienceCreateResponse =
                experienceRepository.createExperience(experienceCreateRequest)
            if (experienceCreateResponse.isSuccessful) {
                createExperienceLiveData.postValue(ApiStatus.Success(experienceCreateResponse.body()))
            } else {
                createExperienceLiveData.postValue(
                    ApiStatus.Error(
                        Throwable(
                            experienceCreateResponse.message()
                        )
                    )
                )
                Log.e(TAG, "createExperience: ${experienceCreateResponse.errorBody()}")
                Log.e(TAG, "createExperience: ${experienceCreateResponse.code()}")
            }
        }
        return createExperienceLiveData
    }

    fun editExperience(experienceEditRequest: ExperienceEditRequest): LiveData<ApiStatus<ExperienceEditResponse>> {
        viewModelScope.launch {
                val editExperienceResponse = experienceRepository.editExperience(experienceEditRequest)
                if (editExperienceResponse.isSuccessful) {
                    editExperienceLiveData.postValue(ApiStatus.Success(editExperienceResponse.body()))
                } else {
                    editExperienceLiveData.postValue(ApiStatus.Error(Throwable(editExperienceResponse.message())))
                    Log.e(TAG, "editExperience: ${editExperienceResponse.errorBody()}")
                    Log.e(TAG, "editExperience: ${editExperienceResponse.code()}")
                }
        }
        return editExperienceLiveData
    }

}