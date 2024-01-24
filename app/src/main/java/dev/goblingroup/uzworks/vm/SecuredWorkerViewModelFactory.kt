package dev.goblingroup.uzworks.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.networking.SecuredWorkerService
import dev.goblingroup.uzworks.utils.NetworkHelper

class SecuredWorkerViewModelFactory(
    private val securedWorkerService: SecuredWorkerService,
    private val networkHelper: NetworkHelper
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SecuredWorkerViewModel::class.java)) {
            return SecuredWorkerViewModel(
                securedWorkerService = securedWorkerService,
                networkHelper = networkHelper
            ) as T
        }
        return throw Exception("Some error in ${this::class.java.simpleName}")
    }

}