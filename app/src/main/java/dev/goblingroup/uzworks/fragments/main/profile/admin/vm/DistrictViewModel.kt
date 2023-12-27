package dev.goblingroup.uzworks.fragments.main.profile.admin.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.repository.DistrictRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DistrictViewModel(
    districtService: DistrictService,
    private val networkHelper: NetworkHelper,
    districtId: String,
    regionId: String
) : ViewModel() {

    private val districtRepository =
        DistrictRepository(
            districtService = districtService,
            districtId = districtId,
            regionId = regionId
        )

    private val getStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val districtByIdFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val districtByRegionIdFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    fun getDistrictById(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictById()
                    .catch {
                        districtByIdFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        districtByIdFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                districtByIdFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByIdFlow
    }

    fun loadDistricts(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getAllDistricts()
                    .catch {
                        getStateFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        getStateFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                getStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return getStateFlow
    }

    fun getDistrictByRegionId(): StateFlow<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictByRegionId()
                    .catch {
                        districtByRegionIdFlow.emit(ApiStatus.Error(it))
                    }
                    .collect {
                        districtByRegionIdFlow.emit(ApiStatus.Success(it))
                    }
            } else {
                districtByRegionIdFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByRegionIdFlow
    }

}