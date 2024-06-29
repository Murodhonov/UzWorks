package com.goblindevs.uzworks.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.goblindevs.uzworks.repository.SecurityRepository
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.isoToDmy
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val securityRepository: SecurityRepository
) : ViewModel() {

    private val _splashLiveData = MutableLiveData<ApiStatus<Boolean>>(ApiStatus.Loading())
    val splashLiveData get() = _splashLiveData

    init {
        login()
    }

    private fun login() {
        try {
            val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val expirationDate =
                dateFormat.parse(securityRepository.getExpirationDate().isoToDmy().toString())
            val currentDate = dateFormat.parse(dateFormat.format(Calendar.getInstance().time))
            val calendar = Calendar.getInstance()
            calendar.time = currentDate!!
            Log.d(TAG, "login: expirationDate: $expirationDate")
            Log.d(TAG, "login: currentDate: $currentDate")
            splashLiveData.postValue(ApiStatus.Success(calendar.time.before(expirationDate)))
        } catch (e: Exception) {
            Log.e(TAG, "login: ${e.message}")
            splashLiveData.postValue(ApiStatus.Error(Throwable("Token expired")))
        }
    }

}