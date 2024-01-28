package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.repository.secured.SecuredRegionRepository
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuredRegionViewModel @Inject constructor(
    private val securedRegionRepository: SecuredRegionRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val TAG = "SecuredDistrictViewMode"

    private val createStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val deleteStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val editStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun createDistrict(regionName: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.createRegion(regionName)
                    .catch {
                        createStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        createStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                createStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return createStateFlow
    }

    fun deleteDistrict(regionId: String): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.deleteRegion(regionId)
                    .catch {
                        deleteStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        deleteStateFlow.emit(ApiStatus.Success(null))
                    }
            } else {
                deleteStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return deleteStateFlow
    }

    fun editDistrict(regionEditRequest: RegionEditRequest): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                securedRegionRepository.editRegion(regionEditRequest)
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