package dev.goblingroup.uzworks.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.repository.secured.SecuredDistrictRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredDistrictViewModel @Inject constructor(
    private val securedDistrictRepository: SecuredDistrictRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createLiveData =
        MutableLiveData<ApiStatus<DistrictResponse>>(ApiStatus.Loading())

    private val deleteLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editLiveData =
        MutableLiveData<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createDistrict(districtRequest: DistrictRequest): LiveData<ApiStatus<DistrictResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedDistrictRepository.createDistrict(districtRequest)
                if (response.isSuccessful) {
                    createLiveData.postValue(ApiStatus.Success(response.body()))
                } else {
                    createLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                createLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createLiveData
    }

    fun deleteDistrict(districtId: String): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedDistrictRepository.deleteDistrict(districtId)
                if (response.isSuccessful) {
                    deleteLiveData.postValue(ApiStatus.Success(null))
                } else {
                    deleteLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                deleteLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteLiveData
    }

    fun editDistrict(districtEditRequest: DistrictEditRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = securedDistrictRepository.editDistrict(districtEditRequest)
                if (response.isSuccessful) {
                    editLiveData.postValue(ApiStatus.Success(null))
                } else {
                    editLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                }
            } else {
                editLiveData.postValue(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editLiveData
    }
}