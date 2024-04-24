package dev.goblingroup.uzworks.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.adapter.view_pager_adapters.StartPagerAdapter
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
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
                userLiveData.postValue(ApiStatus.Success(response.body()))
            } else {
                userLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
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