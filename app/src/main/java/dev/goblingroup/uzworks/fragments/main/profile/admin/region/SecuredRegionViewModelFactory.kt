package dev.goblingroup.uzworks.fragments.main.profile.admin.region

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.models.request.RegionEditRequest
import dev.goblingroup.uzworks.networking.SecuredRegionService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredRegionViewModelFactory(
    private val securedRegionService: SecuredRegionService,
    var regionName: String,
    private val regionId: String,
    var regionEditRequest: RegionEditRequest,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredRegionViewModel::class.java)) {
            return SecuredRegionViewModel(
                securedRegionService = securedRegionService,
                regionName = regionName,
                regionId = regionId,
                regionEditRequest = regionEditRequest,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}