package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.rv_adapters.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AddEditExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentExperienceBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ExperienceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ExperienceFragment : Fragment() {

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!

    private val experienceViewModel: ExperienceViewModel by viewModels()

    private lateinit var experienceDialog: AlertDialog
    private lateinit var addEditExperienceDialogItemBinding: AddEditExperienceDialogItemBinding

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExperienceBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            experienceViewModel.experienceLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            "failed to load experiences",
                            Toast.LENGTH_SHORT
                        ).show()
                        progressBar.visibility = View.GONE
                        noExperienceTv.text = it.error.message
                        noExperienceTv.visibility = View.VISIBLE
                    }

                    is ApiStatus.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }

                    is ApiStatus.Success -> {
                        progressBar.visibility = View.GONE
                        setExperiences(it.response ?: emptyList())
                    }
                }
            }

            experienceDialog = AlertDialog.Builder(requireContext()).create()
            addEditExperienceDialogItemBinding =
                AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            experienceDialog.setView(addEditExperienceDialogItemBinding.root)
            experienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            addExperienceBtn.setOnClickListener {
                addEditExperience(null)
            }
        }
    }

    private fun isFormValid(dialogItemBinding: AddEditExperienceDialogItemBinding): Boolean {
        var result = true
        dialogItemBinding.apply {
            if (companyNameEt.editText?.text.toString().isEmpty()) {
                result = false
                companyNameEt.isErrorEnabled = true
                companyNameEt.error = resources.getString(R.string.company_name_error)
            }
            if (positionEt.editText?.text.toString().isEmpty()) {
                result = false
                positionEt.isErrorEnabled = true
                positionEt.error = resources.getString(R.string.position_error)
            }
            if (startDateEt.editText?.text.toString().isEmpty()) {
                result = false
                startDateEt.isErrorEnabled = true
                startDateEt.error = resources.getString(R.string.start_date_error)
            }
            if (endDateEt.editText?.text.toString().isEmpty()) {
                result = false
                endDateEt.isErrorEnabled = true
                endDateEt.error = resources.getString(R.string.end_date_error)
            }
            return result
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addEditExperience(addEditExperience: ExperienceResponse?) {
        addEditExperienceDialogItemBinding.apply {
            experienceDialog.show()
            startDateEt.clear()
            endDateEt.clear()
            startDateEt.editText?.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }

                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            startDateEt.isErrorEnabled = false
                            startDateEt.editText?.setText(formatter.format(selectedCalendar.time))
                        },
                        startDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.YEAR.dateLabel),
                        startDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.MONTH.dateLabel),
                        startDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.DATE.dateLabel)
                    )

                    datePickerDialog.show()
                }
                true
            }

            endDateEt.editText?.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val datePickerDialog = DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            val selectedCalendar = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth)
                            }

                            val formatter = SimpleDateFormat(
                                "dd.MM.yyyy", Locale.getDefault()
                            )
                            endDateEt.isErrorEnabled = false
                            endDateEt.editText?.setText(formatter.format(selectedCalendar.time))
                        },
                        endDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.YEAR.dateLabel),
                        endDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.MONTH.dateLabel),
                        endDateEt.editText?.text.toString()
                            .extractDateValue(DateEnum.DATE.dateLabel)
                    )

                    datePickerDialog.show()
                }
                true
            }

            cancelBtn.setOnClickListener {
                experienceDialog.dismiss()
            }

            saveBtn.setOnClickListener {
                if (isFormValid(addEditExperienceDialogItemBinding)) {
                    if (addEditExperience != null) {
                        experienceViewModel.editExperience(
                            ExperienceEditRequest(
                                companyName = companyNameEt.editText?.text.toString(),
                                description = "fjsdkfj",
                                endDate = endDateEt.editText?.text.toString().dmyToIso().toString(),
                                id = addEditExperience.id,
                                position = positionEt.editText?.text.toString(),
                                startDate = startDateEt.editText?.text.toString().dmyToIso().toString()
                            )
                        ).observe(viewLifecycleOwner) {
                            when (it) {
                                is ApiStatus.Error -> {
                                    Toast.makeText(
                                        requireContext(),
                                        "something went wrong while editing experience",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                is ApiStatus.Loading -> {
                                    addEditExperienceLoading()
                                }
                                is ApiStatus.Success -> {
                                    loadingDialog.dismiss()
                                    Toast.makeText(
                                        requireContext(),
                                        "successfully edited",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                    experienceViewModel.createExperience(
                        ExperienceCreateRequest(
                            companyName = companyNameEt.editText?.text.toString(),
                            description = "some description",
                            endDate = endDateEt.editText?.text.toString().dmyToIso().toString(),
                            position = positionEt.editText?.text.toString(),
                            startDate = startDateEt.editText?.text.toString().dmyToIso().toString()
                        )
                    ).observe(viewLifecycleOwner) {
                        when (it) {
                            is ApiStatus.Error -> {
                                Toast.makeText(
                                    requireContext(),
                                    "something went wrong while creating experience",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is ApiStatus.Loading -> {
                                addEditExperienceLoading()
                            }

                            is ApiStatus.Success -> {
                                loadingDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "successfully created",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addEditExperienceLoading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
        loadingDialog.setView(loadingDialogItemBinding.root)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
    }

    private fun setExperiences(experienceList: List<ExperienceResponse>) {
        binding.apply {
            val experienceAdapter = ExperienceAdapter(experienceList) { experienceResponse ->
                addEditExperience(experienceResponse)
            }
            experienceRv.adapter = experienceAdapter
            if (experienceAdapter.itemCount == 0) {
                noExperienceTv.text = resources.getString(R.string.no_experience)
                noExperienceTv.visibility = View.VISIBLE
            } else {
                noExperienceTv.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}