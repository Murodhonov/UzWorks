package dev.goblingroup.uzworks.fragments.main.profile.admin.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.repository.DistrictRepository
import dev.goblingroup.uzworks.resource.district.DistrictByIdResource
import dev.goblingroup.uzworks.resource.district.DistrictByRegionIdResource
import dev.goblingroup.uzworks.resource.district.DistrictResource
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
        MutableStateFlow<DistrictResource<Unit>>(DistrictResource.DistrictLoading())

    private val districtByIdFlow =
        MutableStateFlow<DistrictByIdResource<Unit>>(DistrictByIdResource.Loading())

    private val districtByRegionIdFlow =
        MutableStateFlow<DistrictByRegionIdResource<Unit>>(DistrictByRegionIdResource.Loading())

    fun getDistrictById(): StateFlow<DistrictByIdResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictById()
                    .catch {
                        districtByIdFlow.emit(DistrictByIdResource.Error(it))
                    }
                    .collect {
                        districtByIdFlow.emit(DistrictByIdResource.Success(it))
                    }
            } else {
                districtByIdFlow.emit(DistrictByIdResource.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByIdFlow
    }

    fun loadDistricts(): StateFlow<DistrictResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getAllDistricts()
                    .catch {
                        getStateFlow.emit(DistrictResource.DistrictError(it))
                    }
                    .collect {
                        getStateFlow.emit(DistrictResource.DistrictSuccess(it))
                    }
            } else {
                getStateFlow.emit(DistrictResource.DistrictError(Throwable(NO_INTERNET)))
            }
        }
        return getStateFlow
    }

    fun getDistrictByRegionId(): StateFlow<DistrictByRegionIdResource<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                districtRepository.getDistrictByRegionId()
                    .catch {
                        districtByRegionIdFlow.emit(DistrictByRegionIdResource.Error(it))
                    }
                    .collect {
                        districtByRegionIdFlow.emit(DistrictByRegionIdResource.Success(it))
                    }
            } else {
                districtByRegionIdFlow.emit(DistrictByRegionIdResource.Error(Throwable(NO_INTERNET)))
            }
        }
        return districtByRegionIdFlow
    }

}