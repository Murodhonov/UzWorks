package dev.goblingroup.uzworks.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.repository.secured.SecuredDistrictRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredDistrictViewModel @Inject constructor(
    private val securedDistrictRepository: SecuredDistrictRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createStateFlow =
        MutableStateFlow<ApiStatus<DistrictResponse>>(ApiStatus.Loading())

    private val deleteStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createDistrict(districtRequest: DistrictRequest): StateFlow<ApiStatus<DistrictResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.createDistrict(districtRequest)
                    .catch {
                        createStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        createStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                createStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(districtId: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.deleteDistrict(districtId)
                    .catch {
                        deleteStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        if (it.isSuccessful) {
                            deleteStateFlow.emit(ApiStatus.Success(null))
                        } else {
                            Log.e(TAG, "deleteDistrict: ${it.body()}")
                            Log.e(TAG, "deleteDistrict: ${it.errorBody()}")
                            Log.e(TAG, "deleteDistrict: ${it.code()}")
                            Log.e(TAG, "deleteDistrict: ${it.message()}")
                            Log.e(TAG, "deleteDistrict: ${it.headers()}")
                            Log.e(TAG, "deleteDistrict: ${it.raw()}")
                        }
                    }
            } else {
                deleteStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(districtEditRequest: DistrictEditRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedDistrictRepository.editDistrict(districtEditRequest)
                    .catch {
                        editStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        editStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                editStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return editStateFlow
    }
}