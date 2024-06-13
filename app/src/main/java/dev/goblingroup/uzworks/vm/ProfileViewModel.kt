package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())

    val districtLiveData = MutableLiveData<String>()
    val regionLiveData = MutableLiveData<String>()

    fun fetchUserData(): LiveData<ApiStatus<UserResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val userResponse = profileRepository.getUserById(securityRepository.getUserId())
                if (userResponse.isSuccessful) {
                    userLiveData.postValue(ApiStatus.Success(userResponse.body()))
                } else {
                    userLiveData.postValue(ApiStatus.Error(Throwable(userResponse.message())))
                    Log.e(TAG, "fetchUserData: ${userResponse.code()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.message()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.errorBody()}")
                }
            }
        }
        return userLiveData
    }

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}