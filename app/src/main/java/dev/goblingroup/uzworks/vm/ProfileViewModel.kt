package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.models.response.UserUpdateResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
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

    private val _userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())
    val userLiveData = _userLiveData

    private val updateUserLiveData =
        MutableLiveData<ApiStatus<UserUpdateResponse>>(ApiStatus.Loading())

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val userResponse = profileRepository.getUserById(securityRepository.getUserId())
                if (userResponse.isSuccessful) {
                    _userLiveData.postValue(ApiStatus.Success(userResponse.body()))
                } else {
                    _userLiveData.postValue(ApiStatus.Error(Throwable(userResponse.message())))
                    Log.e(TAG, "fetchUserData: ${userResponse.code()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.message()}")
                    Log.e(TAG, "fetchUserData: ${userResponse.errorBody()}")
                }
            } else {
                _userLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
    }

    fun updateUser(userUpdateRequest: UserUpdateRequest): LiveData<ApiStatus<UserUpdateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "updateUser: updating user $userUpdateRequest")
                val updateUserResponse = profileRepository.updateUser(userUpdateRequest)
                if (updateUserResponse.isSuccessful) {
                    updateUserLiveData.postValue(ApiStatus.Success(updateUserResponse.body()))
                } else {
                    updateUserLiveData.postValue(ApiStatus.Error(Throwable(updateUserResponse.message())))
                    Log.e(TAG, "updateUser: ${updateUserResponse.code()}")
                    Log.e(TAG, "updateUser: ${updateUserResponse.message()}")
                }
            } else {
                updateUserLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return updateUserLiveData
    }

    fun getUserId() = securityRepository.getUserId()

    fun getUsername() = securityRepository.getUsername()

    fun isEmployee() = securityRepository.isEmployee()

    fun isEmployer() = securityRepository.isEmployer()

}