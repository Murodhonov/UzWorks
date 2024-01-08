package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.utils.NetworkHelper

class DistrictViewModelFactory(
    private val appDatabase: AppDatabase,
    private val districtService: DistrictService,
    private val networkHelper: NetworkHelper,
    var districtId: String,
    var regionId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistrictViewModel::class.java)) {
            return DistrictViewModel(
                appDatabase = appDatabase,
                districtService = districtService,
                networkHelper = networkHelper,
                districtId = districtId,
                regionId = regionId
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}