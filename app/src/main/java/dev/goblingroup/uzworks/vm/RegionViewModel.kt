package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.mapper.mapToEntity
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.repository.RegionRepository
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.NO_INTERNET
import dev.goblingroup.uzworks.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch

class RegionViewModel(
    appDatabase: AppDatabase,
    regionService: RegionService,
    regionId: String,
    districtId: String,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private var regionRepository = RegionRepository(
        regionService, appDatabase.regionDao(), regionId, districtId
    )

    private val regionStateFlow = MutableStateFlow<ApiStatus<Unit>>(ApiStatus.Loading())

    init {
        fetchRegions()
    }

    private fun fetchRegions() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                regionRepository.getAllRegions()
                    .catch {
                        regionStateFlow.emit(ApiStatus.Error(it))
                    }
                    .flatMapConcat { regionList ->
                        val emptyRegionList = ArrayList<RegionEntity>()
                        regionList.forEach { regionResponse ->
                            emptyRegionList.add(regionResponse.mapToEntity())
                        }
                        regionRepository.addRegions(emptyRegionList)
                    }
                    .collect {
                        regionStateFlow.emit(ApiStatus.Success(regionRepository.listRegions()))
                    }
            } else {
                if (regionRepository.listRegions().isNotEmpty()) {
                    regionStateFlow.emit(ApiStatus.Success(regionRepository.listRegions()))
                } else {
                    regionStateFlow.emit(ApiStatus.Error(Throwable(NO_INTERNET)))
                }
            }
        }
    }

    fun getRegionStateFlow(): StateFlow<ApiStatus<Unit>> {
        return regionStateFlow
    }

    fun findRegionById(regionId: String) = regionRepository.findRegionById(regionId)

    fun listRegions() = regionRepository.listRegions()

}