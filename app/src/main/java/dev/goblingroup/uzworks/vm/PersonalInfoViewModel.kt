package dev.goblingroup.uzworks.vm

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.dao.UserDao
import dev.goblingroup.uzworks.databinding.GenderChoiceLayoutBinding
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.models.response.UserUpdateResponse
import dev.goblingroup.uzworks.repository.ProfileRepository
import dev.goblingroup.uzworks.repository.SecurityRepository
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PersonalInfoViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securityRepository: SecurityRepository,
    private val userDao: UserDao
) : ViewModel() {

    private val _userLiveData = MutableLiveData<ApiStatus<UserResponse>>(ApiStatus.Loading())
    val userLiveData = _userLiveData

    private val updateUserLiveData =
        MutableLiveData<ApiStatus<UserUpdateResponse>>(ApiStatus.Loading())

    private var selectedGender = userDao.getUser()?.gender

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            val userResponse = profileRepository.getUserById(securityRepository.getUserId())
            if (userResponse.isSuccessful) {
                _userLiveData.postValue(ApiStatus.Success(userResponse.body()))
            } else {
                _userLiveData.postValue(ApiStatus.Error(Throwable(userResponse.message())))
                Log.e(TAG, "fetchUserData: ${userResponse.code()}")
                Log.e(TAG, "fetchUserData: ${userResponse.message()}")
                Log.e(TAG, "fetchUserData: ${userResponse.errorBody()}")
            }
        }
    }

    fun updateUser(userUpdateRequest: UserUpdateRequest): LiveData<ApiStatus<UserUpdateResponse>> {
        viewModelScope.launch {
            Log.d(TAG, "updateUser: updating user $userUpdateRequest")
            val updateUserResponse = profileRepository.updateUser(userUpdateRequest)
            if (updateUserResponse.isSuccessful) {
                updateUserLiveData.postValue(ApiStatus.Success(updateUserResponse.body()))
            } else {
                updateUserLiveData.postValue(ApiStatus.Error(Throwable(updateUserResponse.message())))
                Log.e(TAG, "updateUser: ${updateUserResponse.code()}")
                Log.e(TAG, "updateUser: ${updateUserResponse.message()}")
            }
        }
        return updateUserLiveData
    }

    fun getUserId() = securityRepository.getUserId()

    fun getUsername(): String {
        return userDao.getUser()?.username.toString()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun controlInput(
        context: Context,
        resources: Resources,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        genderLayout: GenderChoiceLayoutBinding,
        birthdayEt: TextInputLayout
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
            when (selectedGender) {
                GenderEnum.MALE.label -> {
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    maleCircle.visibility = View.VISIBLE
                    femaleCircle.visibility = View.GONE
                    maleTv.setTextColor(resources.getColor(R.color.black_blue))
                    femaleTv.setTextColor(resources.getColor(R.color.text_color))
                    maleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    femaleBtn.strokeColor = resources.getColor(R.color.text_color)
                }

                GenderEnum.FEMALE.label -> {
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    femaleCircle.visibility = View.VISIBLE
                    maleCircle.visibility = View.GONE
                    femaleTv.setTextColor(resources.getColor(R.color.black_blue))
                    maleTv.setTextColor(resources.getColor(R.color.text_color))
                    femaleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    maleBtn.strokeColor = resources.getColor(R.color.text_color)
                }

            }

            maleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.FEMALE.label || selectedGender?.isEmpty() == true) {
                    selectedGender = GenderEnum.MALE.label
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    maleCircle.visibility = View.VISIBLE
                    femaleCircle.visibility = View.GONE
                    maleTv.setTextColor(resources.getColor(R.color.black_blue))
                    femaleTv.setTextColor(resources.getColor(R.color.text_color))
                    maleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    femaleBtn.strokeColor = resources.getColor(R.color.text_color)
                }
            }
            femaleBtn.setOnClickListener {
                if (selectedGender == GenderEnum.MALE.label || selectedGender?.isEmpty() == true) {
                    selectedGender = GenderEnum.FEMALE.label
                    femaleStroke.setBackgroundResource(R.drawable.gender_stroke_selected)
                    maleStroke.setBackgroundResource(R.drawable.gender_stroke_unselected)
                    femaleCircle.visibility = View.VISIBLE
                    maleCircle.visibility = View.GONE
                    femaleTv.setTextColor(resources.getColor(R.color.black_blue))
                    maleTv.setTextColor(resources.getColor(R.color.text_color))
                    femaleBtn.strokeColor = resources.getColor(R.color.black_blue)
                    maleBtn.strokeColor = resources.getColor(R.color.text_color)
                }
            }
        }

        phoneNumberEt.editText?.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) {
                    return
                }

                isFormatting = true
                val newText = s.toString().filter { !it.isWhitespace() }
                val oldText =
                    phoneNumberEt.editText?.tag.toString().filter { !it.isWhitespace() }
                val formattedPhone =
                    s?.filter { !it.isWhitespace() }.toString()
                        .formatPhoneNumber(newText.length < oldText.length)
                phoneNumberEt.editText?.setText(formattedPhone)
                phoneNumberEt.editText?.setSelection(formattedPhone.length)
                phoneNumberEt.tag = formattedPhone

                isFormatting = false
            }
        })

        birthdayEt.editText?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val datePickerDialog = DatePickerDialog(
                    context,
                    R.style.DatePickerDialogTheme,
                    { _, year, month, dayOfMonth ->
                        val selectedCalendar = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth)
                        }

                        val currentCalendar = Calendar.getInstance()

                        if (selectedCalendar.after(currentCalendar)) {
                            Toast.makeText(
                                context,
                                "Cannot select date after current date",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            birthdayEt.editText?.setText(formatter.format(selectedCalendar.time))
                        }
                    },
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.YEAR.dateLabel),
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.MONTH.dateLabel),
                    birthdayEt.editText?.text.toString()
                        .extractDateValue(DateEnum.DATE.dateLabel),
                )

                datePickerDialog.show()
            }
            true
        }
    }

    fun isFormValid(
        resources: Resources,
        firstNameEt: TextInputLayout,
        lastNameEt: TextInputLayout,
        emailEt: TextInputLayout,
        phoneNumberEt: TextInputLayout,
        birthdayEt: TextInputLayout
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
        if (emailEt.editText?.text.toString().isEmpty()) {
            result = false
            emailEt.isErrorEnabled = true
            emailEt.error = resources.getString(R.string.enter_valid_email)
        }
        if (phoneNumberEt.editText?.text.toString().trim().filter { !it.isWhitespace() }.length != 13) {
            result = false
            phoneNumberEt.isErrorEnabled = true
            phoneNumberEt.error = resources.getString(R.string.phone_number_error)
        }
        if (birthdayEt.editText?.text.toString().isEmpty()) {
            result = false
            birthdayEt.isErrorEnabled = true
            birthdayEt.error = resources.getString(R.string.enter_firstname)
        }
        return result
    }

}