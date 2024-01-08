package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.networking.RegionService
import dev.goblingroup.uzworks.utils.NetworkHelper

class RegionViewModelFactory(
    private val appDatabase: AppDatabase,
    private val regionService: RegionService,
    var regionId: String,
    var districtId: String,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegionViewModel::class.java)) {
            return RegionViewModel(
                appDatabase,
                regionService,
                regionId,
                districtId,
                networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}