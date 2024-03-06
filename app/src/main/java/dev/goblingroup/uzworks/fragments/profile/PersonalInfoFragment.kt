package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentPersonalInfoBinding
import dev.goblingroup.uzworks.models.request.UserUpdateRequest
import dev.goblingroup.uzworks.models.response.UserResponse
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.GenderEnum
import dev.goblingroup.uzworks.utils.convertPhoneNumber
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ProfileViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern

@AndroidEntryPoint
class PersonalInfoFragment : Fragment() {

    private var _binding: FragmentPersonalInfoBinding? = null
    private val binding get() = _binding!!

    private var userResponse: UserResponse? = null
    private var selectedGender = ""

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalInfoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                userResponse = arguments?.getParcelable("user_response", UserResponse::class.java)
            }
            if (userResponse != null) {
                setData()
            } else {
                profileViewModel.userLiveData.observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            firstNameEt.visibility = View.GONE
                            lastNameEt.visibility = View.GONE
                            birthdayEt.visibility = View.GONE
                            genderLayout.apply {
                                root.visibility = View.GONE
                            }
                            emailEt.visibility = View.GONE
                            phoneNumberEt.visibility = View.GONE
                            saveBtn.visibility = View.GONE
                            cancelBtn.visibility = View.GONE
                        }

                        is ApiStatus.Success -> {
                            userResponse = it.response
                            setData()
                        }
                    }
                }
            }

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
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setData() {
        binding.apply {
            selectedGender = userResponse?.gender ?: ""
            progressBar.visibility = View.GONE
            firstNameEt.visibility = View.VISIBLE
            lastNameEt.visibility = View.VISIBLE
            birthdayEt.visibility = View.VISIBLE
            genderLayout.apply {
                root.visibility = View.VISIBLE
            }
            emailEt.visibility = View.VISIBLE
            phoneNumberEt.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            cancelBtn.visibility = View.VISIBLE

            firstNameEt.editText?.setText(userResponse?.firstName ?: "")
            lastNameEt.editText?.setText(userResponse?.lastName ?: "")
            birthdayEt.editText?.setText(
                if (userResponse?.birthDate?.isoToDmy() == null) resources.getString(R.string.birth_date) else userResponse?.birthDate?.isoToDmy()
            )
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
                    if (selectedGender == GenderEnum.FEMALE.label || selectedGender.isEmpty()) {
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
                    if (selectedGender == GenderEnum.MALE.label || selectedGender.isEmpty()) {
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

            emailEt.editText?.setText(userResponse?.email ?: "")
            phoneNumberEt.editText?.setText(
                userResponse?.phoneNumber?.convertPhoneNumber()
                    ?: resources.getString(R.string.phone_number_prefix)
            )

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
                    Log.d(
                        TAG,
                        "afterTextChanged: s -> $s\tformattedPhone -> $formattedPhone\tet -> ${phoneNumberEt.editText?.text.toString()}"
                    )
                    phoneNumberEt.editText?.setText(formattedPhone)
                    phoneNumberEt.editText?.setSelection(formattedPhone.length)
                    phoneNumberEt.tag = formattedPhone

                    isFormatting = false
                }
            })

            birthdayEt.editText?.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }

                            val currentCalendar = Calendar.getInstance()

                            if (selectedCalendar.after(currentCalendar)) {
                                Toast.makeText(
                                    requireContext(),
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

            saveBtn.setOnClickListener {
                if (isFormValid()) {
                    lifecycleScope.launch {
                        profileViewModel.updateUser(
                            UserUpdateRequest(
                                birthDate = birthdayEt.editText?.text.toString().dmyToIso()
                                    .toString(),
                                email = emailEt.editText?.text.toString(),
                                firstName = firstNameEt.editText?.text.toString(),
                                gender = selectedGender,
                                id = profileViewModel.getUserId(),
                                lastName = lastNameEt.editText?.text.toString(),
                                mobileId = Settings.Secure.getString(
                                    requireContext().contentResolver,
                                    Settings.Secure.ANDROID_ID
                                ),
                                phoneNumber = phoneNumberEt.editText?.text.toString()
                                    .filter { !it.isWhitespace() },
                                userName = profileViewModel.getUsername()
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "something went wrong while updating",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                is ApiStatus.Loading -> {
                                    Toast.makeText(requireContext(), "loading", Toast.LENGTH_SHORT)
                                        .show()
                                }

                                is ApiStatus.Success -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "successfully updated",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun isFormValid(): Boolean {
        binding.apply {
            var result = true
            if (firstNameEt.editText?.text.toString().isEmpty()) {
                firstNameEt.error = resources.getString(R.string.enter_firstname)
                result = false
            }
            if (lastNameEt.editText?.text.toString().isEmpty()) {
                lastNameEt.error = resources.getString(R.string.enter_lastname)
                result = false
            }
            if (selectedGender.isEmpty()) {
                result = false
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.confirm_gender),
                    Toast.LENGTH_SHORT
                ).show()
            }
            if (emailEt.editText?.text.toString()
                    .isNotEmpty() && !isEmailValid(emailEt.editText?.text.toString())
            ) {
                emailEt.error = resources.getString(R.string.enter_valid_email)
                result = false
            }
            return result
        }
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
        val pattern = Pattern.compile(emailPattern)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}