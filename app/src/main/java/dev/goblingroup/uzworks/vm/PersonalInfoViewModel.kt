package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.models.response.UserUpdateResponse
import dev.goblingroup.uzworks.repository.AddressRepository
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.extractErrorMessage
import dev.goblingroup.uzworks.utils.selectFemale
import dev.goblingroup.uzworks.utils.selectMale
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository,
    private val addressRepository: AddressRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())
    val userLiveData = _userLiveData

    private val updateUserLiveData =
        MutableLiveData<ApiStatus<UserUpdateResponse>>(ApiStatus.Loading())

    private val _regionLiveData =
        MutableLiveData<ApiStatus<List<RegionEntity>>>()

    private val _districtLiveData =
        MutableLiveData<ApiStatus<List<DistrictEntity>>>()

    private lateinit var userResponse: UserResponse
    var selectedGender: Int = GenderEnum.UNKNOWN.code
    var districtId: String? = null
    var regionId: String? = null

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                val response = profileRepository.getUserById(securityRepository.getUserId())
                if (response.isSuccessful) {
                    userResponse = response.body()!!
                    selectedGender = userResponse.gender
                    districtId = userResponse.districtId
                    Log.d(TAG, "fetchUserData: ${response.body()}")
                    _userLiveData.postValue(ApiStatus.Success(userResponse))
                } else {
                    _userLiveData.postValue(ApiStatus.Error(Throwable(response.message())))
                    Log.e(TAG, "fetchUserData: ${response.code()}")
                    Log.e(TAG, "fetchUserData: ${response.message()}")
                    Log.e(TAG, "fetchUserData: ${response.errorBody()}")
                    Log.e(
                        TAG,
                        "fetchUserData: ${
                            response.errorBody()?.extractErrorMessage().toString()
                        }"
                    )
                }
            }
        }
    }

    fun updateUser(userUpdateRequest: UserUpdateRequest): LiveData<ApiStatus<UserUpdateResponse>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                Log.d(TAG, "updateUser: updating user $userUpdateRequest")
                val updateUserResponse = profileRepository.updateUser(userUpdateRequest)
                if (updateUserResponse.isSuccessful) {
                    if (updateInfo(updateUserResponse.body()!!)) {
                        updateUserLiveData.postValue(ApiStatus.Success(updateUserResponse.body()))
                    }
                } else {
                    updateUserLiveData.postValue(ApiStatus.Error(Throwable(updateUserResponse.message())))
                    Log.e(
                        TAG,
                        "updateUser: ${updateUserResponse.errorBody()?.extractErrorMessage()}",
                    )
                }
            }
        }
        return updateUserLiveData
    }

    fun fetchRegions(): LiveData<ApiStatus<List<RegionEntity>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (addressRepository.listRegions().isEmpty()) {
                    _regionLiveData.postValue(ApiStatus.Loading())
                    val regionResponse = addressRepository.getAllRegions()
                    if (regionResponse.isSuccessful) {
                        if (addressRepository.addRegions(regionResponse.body()!!)) {
                            if (userResponse.regionName != null) {
                                regionId = addressRepository.listRegions()
                                    .find { it.name == userResponse.regionName }?.id
                            }
                            _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                        }
                    } else {
                        _regionLiveData.postValue(ApiStatus.Error(Throwable(regionResponse.message())))
                    }
                } else {
                    if (userResponse.regionName != null) {
                        regionId = addressRepository.listRegions()
                            .find { it.name == userResponse.regionName }?.id
                    }
                    _regionLiveData.postValue(ApiStatus.Success(addressRepository.listRegions()))
                }
            }
        }
        return _regionLiveData
    }

    fun fetchDistrictsByRegionId(): LiveData<ApiStatus<List<DistrictEntity>>> {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                if (regionId != null) {
                    if (addressRepository.listDistrictsByRegionId(regionId.toString())
                            .isNotEmpty()
                    ) {
                        _districtLiveData.postValue(
                            ApiStatus.Success(
                                addressRepository.listDistrictsByRegionId(
                                    regionId.toString()
                                )
                            )
                        )
                    } else {
                        _districtLiveData.postValue(ApiStatus.Loading())
                        val districtResponse =
                            addressRepository.getDistrictsByRegionId(regionId.toString())
                        if (districtResponse.isSuccessful) {
                            if (addressRepository.addDistricts(
                                    districtResponse.body()!!,
                                    regionId.toString()
                                )
                            ) {
                                _districtLiveData.postValue(
                                    ApiStatus.Success(
                                        addressRepository.listDistrictsByRegionId(
                                            regionId.toString()
                                        )
                                    )
                                )
                            }
                        } else {
                            _districtLiveData.postValue(ApiStatus.Error(Throwable(districtResponse.message())))
                        }
                    }
                } else {
                    val regionResponse = addressRepository.getAllRegions()
                    if (regionResponse.isSuccessful) {
                        if (addressRepository.addRegions(regionResponse.body()!!)) {
                            regionId =
                                regionResponse.body()?.find { it.name == userResponse.regionName }?.id
                            val districtResponse =
                                addressRepository.getDistrictsByRegionId(regionId.toString())
                            if (districtResponse.isSuccessful) {
                                if (addressRepository.addDistricts(
                                        districtResponse.body()!!,
                                        regionId.toString()
                                    )
                                ) {
                                    _districtLiveData.postValue(
                                        ApiStatus.Success(
                                            addressRepository.listDistrictsByRegionId(
                                                regionId.toString()
                                            )
                                        )
                                    )
                                }
                            } else {
                                _districtLiveData.postValue(ApiStatus.Error(Throwable(districtResponse.message())))
                            }
                        }
                    } else {
                        _districtLiveData.postValue(ApiStatus.Error(Throwable(regionResponse.message())))
                    }
                }
            }
        }
        return _districtLiveData
    }

    private fun updateInfo(userUpdateResponse: UserUpdateResponse): Boolean {
        return securityRepository.setGender(userUpdateResponse.gender) && securityRepository.setBirthdate(
            userUpdateResponse.birthDate
        )
    }

    fun getUserId() = securityRepository.getUserId()

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        fragmentActivity: FragmentActivity,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout,
        phoneNumber: TextView,
        genderLayout: GenderChoiceLayoutBinding,
        birthday: TextView
    ) {
        firstNameEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                firstNameEt.isErrorEnabled = false
            }
        }

        lastNameEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                lastNameEt.isErrorEnabled = false
            }
        }

        emailEt.editText?.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                emailEt.isErrorEnabled = false
            }
        }

        genderLayout.apply {
            maleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.FEMALE.code || selectedGender == GenderEnum.UNKNOWN.code) {
                    selectedGender = GenderEnum.MALE.code
                    selectMale(fragmentActivity.resources)
                }
            }
            femaleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.MALE.code || selectedGender == GenderEnum.UNKNOWN.code) {
                    selectedGender = GenderEnum.FEMALE.code
                    selectFemale(fragmentActivity.resources)
                }
            }
        }

        phoneNumber.setOnClickListener {
            val clipboardManager =
                fragmentActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData =
                ClipData.newPlainText("label", phoneNumber.text.toString())
            clipboardManager.setPrimaryClip(clipData)
            Toast.makeText(
                fragmentActivity,
                fragmentActivity.resources.getString(R.string.phone_number_copied),
                Toast.LENGTH_SHORT
            ).show()
        }

        birthday.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                fragmentActivity,
                R.style.DatePickerDialogTheme,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    val minimumCalendar =
                        Calendar.getInstance().apply { add(Calendar.YEAR, -16) }

                    if (selectedCalendar.after(minimumCalendar)) {
                        Toast.makeText(
                            fragmentActivity,
                            fragmentActivity.resources.getString(R.string.birthdate_requirement),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val formatter = SimpleDateFormat(
                            "dd.MM.yyyy", Locale.getDefault()
                        )
                        birthday.text = formatter.format(selectedCalendar.time)
                    }
                },
                birthday.text.toString()
                    .extractDateValue(DateEnum.YEAR.dateLabel),
                birthday.text.toString()
                    .extractDateValue(DateEnum.MONTH.dateLabel) - 1,
                birthday.text.toString()
                    .extractDateValue(DateEnum.DATE.dateLabel),
            )

            datePickerDialog.show()

            datePickerDialog.setOnDismissListener {
                birthday.clearFocus()
            }
        }
    }

    fun isFormValid(
        resources: Resources,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout
    ): Boolean {
        var result = true
        if (firstNameEt.editText?.text.toString().isEmpty()) {
            result = false
            firstNameEt.isErrorEnabled = true
            firstNameEt.error = resources.getString(R.string.enter_firstname)
        }
        if (lastNameEt.editText?.text.toString().isEmpty()) {
            result = false
            lastNameEt.isErrorEnabled = true
            lastNameEt.error = resources.getString(R.string.enter_lastname)
        }
        if (!isEmailValid(emailEt.editText?.text.toString())) {
            result = false
            emailEt.isErrorEnabled = true
            emailEt.error = resources.getString(R.string.enter_valid_email)
        }
        return result
    }

    private fun isEmailValid(email: String): Boolean {
        if (email.isEmpty()) return true
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

}