package dev.goblingroup.uzworks.fragments.main.profile.admin.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.networking.DistrictService
import dev.goblingroup.uzworks.utils.NetworkHelper

class DistrictViewModelFactory(
    private val districtService: DistrictService,
    private val networkHelper: NetworkHelper,
    var districtId: String,
    var regionId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DistrictViewModel::class.java)) {
            return DistrictViewModel(
                districtService = districtService,
                networkHelper = networkHelper,
                districtId = districtId,
                regionId = regionId
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}