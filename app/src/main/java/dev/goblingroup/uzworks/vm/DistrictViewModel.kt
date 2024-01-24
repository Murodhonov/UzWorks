package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.repository.DistrictRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class DistrictViewModel(
    appDatabase: AppDatabase,
    districtService: DistrictService,
    private val networkHelper: NetworkHelper,
    districtId: String,
    regionId: String
) : ViewModel() {

    private val districtRepository =
        DistrictRepository(
            districtService = districtService,
            districtDao = appDatabase.districtDao(),
            districtId = districtId,
            regionId = regionId
        )

    private val districtStateFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val districtByIdFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    private val districtByRegionIdFlow =
        MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    init {
        fetchDistricts()
    }

    private fun fetchDistricts() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    districtRepository.getAllDistricts()
                        .catch {
                            districtStateFlow.emit(ApiStatus.Error(it))
                        }
                        .flatMapConcat { districtResponseList ->
                            val emptyDistrictList = ArrayList<DistrictEntity>()
                            districtResponseList.forEach { districtResponse ->
                                emptyDistrictList.add(districtResponse.mapToEntity())
                            }
                            districtRepository.addDistricts(emptyDistrictList)
                        }
                        .collect {
                            districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                        }
                }
            } else {
                if (districtRepository.listDistricts().isNotEmpty()) {
                    districtStateFlow.emit(ApiStatus.Success(districtRepository.listDistricts()))
                } else {
                    districtStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

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

    fun getDistrictStateFlow(): StateFlow<ApiStatus<Unit>> = districtStateFlow

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

    fun findDistrict(districtId: String) = districtRepository.findDistrict(districtId)

    fun listDistrictsByRegionId(regionId: String) =
        districtRepository.listDistrictsByRegionId(regionId)

    fun getRegionByDistrictId(districtId: String) =
        districtRepository.getRegionByDistrictId(districtId)

}