package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AddEditExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentExperienceBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.utils.DateEnum
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.extractDateValue
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ExperienceViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class ExperienceFragment : Fragment() {

    private val TAG = "ExperienceFragment"

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!

    private val experienceViewModel: ExperienceViewModel by activityViewModels()

    private lateinit var addEditExperienceDialog: AlertDialog
    private lateinit var addEditExperienceDialogBinding: AddEditExperienceDialogItemBinding

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    private lateinit var experienceList: ArrayList<ExperienceResponse>
    private lateinit var experienceAdapter: ExperienceAdapter

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
                        experienceList = ArrayList()
                        experienceList.addAll(it.response ?: emptyList())
                        setExperiences()
                    }
                }
            }

            addEditExperienceDialog = AlertDialog.Builder(requireContext()).create()
            addEditExperienceDialogBinding =
                AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            addEditExperienceDialog.setView(addEditExperienceDialogBinding.root)
            addEditExperienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addEditExperienceDialog.setCancelable(false)

            addExperienceBtn.setOnClickListener {
                addExperience()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        addEditExperienceDialog.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addExperience() {
        addEditExperienceDialogBinding.apply {
            addEditExperienceDialog.show()
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

            positionEt.editText?.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    positionEt.isErrorEnabled = false
                }
            }

            companyNameEt.editText?.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    companyNameEt.isErrorEnabled = false
                }
            }

            cancelBtn.setOnClickListener {
                addEditExperienceDialog.dismiss()
            }

            positionEt.editText?.setText("")
            companyNameEt.editText?.setText("")
            startDateEt.editText?.setText("")
            endDateEt.editText?.setText("")

            saveBtn.setOnClickListener {
                if (isFormValid(addEditExperienceDialogBinding)) {
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
                                Toast.makeText(
                                    requireContext(),
                                    "add experience loading",
                                    Toast.LENGTH_SHORT
                                ).show()
                                addEditExperienceLoading()
                            }

                            is ApiStatus.Success -> {
                                Toast.makeText(
                                    requireContext(),
                                    "add experience succeeded",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadingDialog.dismiss()
                                experienceList.add(
                                    ExperienceResponse(
                                        companyName = it.response?.companyName.toString(),
                                        description = it.response?.description.toString(),
                                        endDate = it.response?.endDate.toString(),
                                        id = it.response?.id.toString(),
                                        position = it.response?.position.toString(),
                                        startDate = it.response?.startDate.toString()
                                    )
                                )
                                experienceAdapter.notifyItemInserted(experienceList.size - 1)
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateExperience(experience: ExperienceResponse, position: Int) {
        addEditExperienceDialogBinding.apply {
            addEditExperienceDialog.show()
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

            positionEt.editText?.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    positionEt.isErrorEnabled = false
                }
            }

            companyNameEt.editText?.doAfterTextChanged {
                if (it.toString().isNotEmpty()) {
                    companyNameEt.isErrorEnabled = false
                }
            }

            cancelBtn.setOnClickListener {
                addEditExperienceDialog.dismiss()
            }

            positionEt.editText?.setText(experience.position)
            companyNameEt.editText?.setText(experience.companyName)
            startDateEt.editText?.setText(experience.startDate.isoToDmy())
            endDateEt.editText?.setText(experience.endDate.isoToDmy())

            saveBtn.setOnClickListener {
                if (isFormValid(addEditExperienceDialogBinding)) {
                    experienceViewModel.editExperience(
                        ExperienceEditRequest(
                            companyName = companyNameEt.editText?.text.toString(),
                            description = "fjsdkfj",
                            endDate = endDateEt.editText?.text.toString().dmyToIso().toString(),
                            id = experience.id,
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
                                experienceList[position] = ExperienceResponse(
                                    companyName = it.response?.companyName.toString(),
                                    description = it.response?.description.toString(),
                                    endDate = it.response?.endDate.toString(),
                                    id = it.response?.id.toString(),
                                    position = it.response?.position.toString(),
                                    startDate = it.response?.startDate.toString()
                                )
                                experienceAdapter.notifyItemChanged(position)
                            }
                        }
                    }
                }
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

    private fun addEditExperienceLoading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setView(loadingDialogBinding.root)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
    }

    private fun setExperiences() {
        binding.apply {
            experienceAdapter =
                ExperienceAdapter(experienceList) { experienceResponse, position ->
                    updateExperience(experienceResponse, position)
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