package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.adapter.view_pager_adapters.StartPagerAdapter
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.extractErrorMessage
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())
    val userLiveData get() = _userLiveData

    init {
        fetchUser()
    }

    private fun fetchUser() {
        viewModelScope.launch {
            val response = profileRepository.getUserById(securityRepository.getUserId())
            if (response.isSuccessful) {
                _userLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                _userLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                Log.e(TAG, "fetchUser: ${response.errorBody()?.extractErrorMessage()}")
                Log.e(TAG, "fetchUser: ${response.code()}")
                Log.e(TAG, "fetchUser: ${response.message()}")
            }
        }
    }

    var startPagerAdapter: StartPagerAdapter? = null

    private val _viewPagerPositionLiveData = MutableLiveData(0)
    val viewPagerPositionLiveData get() = _viewPagerPositionLiveData

    fun setViewPagerPosition(value: Int) {
        _viewPagerPositionLiveData.value = value
    }



}