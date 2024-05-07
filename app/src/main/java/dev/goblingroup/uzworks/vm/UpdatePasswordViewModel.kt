package dev.goblingroup.uzworks.vm

import android.content.res.Resources
import android.util.Log
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.models.request.UpdatePasswordRequest
import dev.goblingroup.uzworks.models.response.UpdatePasswordResponse
import dev.goblingroup.uzworks.networking.UserService
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.isStrongPassword
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val userService: UserService,
    private val securityRepository: SecurityRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val resetPasswordLiveData = MutableLiveData<ApiStatus<Unit>>()

    fun updatePassword(updatePasswordRequest: UpdatePasswordRequest): LiveData<ApiStatus<Unit>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                resetPasswordLiveData.postValue(ApiStatus.Loading())
                val resetPasswordResponse = userService.resetPassword(securityRepository.getToken(), updatePasswordRequest)
                if (resetPasswordResponse.isSuccessful) {
                    resetPasswordLiveData.postValue(ApiStatus.Success(resetPasswordResponse.body()))
                } else {
                    resetPasswordLiveData.postValue(ApiStatus.Error(Throwable(resetPasswordResponse.message())))
                }
            }
        }
        return resetPasswordLiveData
    }

    fun isFormValid(
        resources: Resources,
        oldPasswordEt: TextInputLayout,
        newPasswordEt: TextInputLayout,
        confirmNewPasswordEt: TextInputLayout
    ): Boolean {
        if (oldPasswordEt.editText?.text.toString().isEmpty()) {
            oldPasswordEt.isErrorEnabled = true
            oldPasswordEt.error = resources.getString(R.string.enter_password)
        }
        if (newPasswordEt.editText?.text.toString().isEmpty()) {
            newPasswordEt.isErrorEnabled = true
            newPasswordEt.error = resources.getString(R.string.enter_new_password)
        } else if (!newPasswordEt.editText?.text.toString().isStrongPassword()) {
            newPasswordEt.isErrorEnabled = true
            newPasswordEt.error = resources.getString(R.string.password_requirements)
        }
        if (confirmNewPasswordEt.editText?.text.toString() != newPasswordEt.editText?.text.toString()) {
            confirmNewPasswordEt.isErrorEnabled = true
            confirmNewPasswordEt.error = resources.getString(R.string.confirm_new_password)
        }
        return !oldPasswordEt.isErrorEnabled &&
                !newPasswordEt.isErrorEnabled &&
                !confirmNewPasswordEt.isErrorEnabled
    }

    fun getUserId() = securityRepository.getUserId()

    fun controlInput(
        oldPasswordEt: TextInputLayout,
        newPasswordEt: TextInputLayout,
        confirmNewPasswordEt: TextInputLayout,
        motionLayout: MotionLayout
    ) {
        oldPasswordEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty() && oldPasswordEt.isErrorEnabled) {
                oldPasswordEt.isErrorEnabled = false
            }
        }
        newPasswordEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty() && newPasswordEt.isErrorEnabled) {
                newPasswordEt.isErrorEnabled = false
            }
        }
        confirmNewPasswordEt.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty() && confirmNewPasswordEt.isErrorEnabled) {
                confirmNewPasswordEt.isErrorEnabled = false
            }
        }

        oldPasswordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
        newPasswordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
        confirmNewPasswordEt.editText?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                motionLayout.setTransitionDuration(500)
                motionLayout.transitionToEnd()
                motionLayout.setTransitionDuration(1000)
            }
        }
    }

}