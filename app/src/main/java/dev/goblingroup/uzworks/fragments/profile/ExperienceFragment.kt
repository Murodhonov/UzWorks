package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AddEditExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentExperienceBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.ExperienceCreateRequest
import dev.goblingroup.uzworks.models.request.ExperienceEditRequest
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.utils.dmyToIso
import dev.goblingroup.uzworks.utils.isoToDmy
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ExperienceViewModel

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
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            addEditExperienceDialog = AlertDialog.Builder(requireContext()).create()
            addEditExperienceDialogBinding =
                AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            addEditExperienceDialog.setView(addEditExperienceDialogBinding.root)
            addEditExperienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

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
                        setExperiences()
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addExperience() {
        addEditExperienceDialogBinding.apply {
            addEditExperienceDialog.show()

            positionEt.editText?.setText("")
            companyNameEt.editText?.setText("")
            startDateEt.editText?.setText("")
            endDateEt.editText?.setText("")

            experienceViewModel.controlExperienceInput(
                requireContext(),
                positionEt,
                companyNameEt,
                startDateEt,
                endDateEt
            )

            saveBtn.setOnClickListener {
                if (experienceViewModel.isFormValid(
                        resources,
                        positionEt,
                        companyNameEt,
                        startDateEt,
                        endDateEt
                    )
                ) {
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
                                Toast.makeText(
                                    requireContext(),
                                    "add experience succeeded",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadingDialog.dismiss()
                                experienceViewModel.experienceList.add(
                                    ExperienceResponse(
                                        companyName = it.response?.companyName.toString(),
                                        description = it.response?.description.toString(),
                                        endDate = it.response?.endDate.toString(),
                                        id = it.response?.id.toString(),
                                        position = it.response?.position.toString(),
                                        startDate = it.response?.startDate.toString()
                                    )
                                )
                                experienceAdapter.notifyItemInserted(experienceViewModel.experienceList.size - 1)
                                addEditExperienceDialog.dismiss()
                                if (experienceAdapter.itemCount != 0) {
                                    binding.noExperienceTv.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }

            cancelBtn.setOnClickListener {
                addEditExperienceDialog.dismiss()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateExperience(experience: ExperienceResponse, position: Int) {
        addEditExperienceDialogBinding.apply {
            addEditExperienceDialog.show()

            experienceViewModel.controlExperienceInput(
                requireContext(),
                positionEt,
                companyNameEt,
                startDateEt,
                endDateEt
            )

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
                                experienceViewModel.experienceList[position] = ExperienceResponse(
                                    companyName = it.response?.companyName.toString(),
                                    description = it.response?.description.toString(),
                                    endDate = it.response?.endDate.toString(),
                                    id = it.response?.id.toString(),
                                    position = it.response?.position.toString(),
                                    startDate = it.response?.startDate.toString()
                                )
                                experienceAdapter.notifyItemChanged(position)
                                addEditExperienceDialog.dismiss()
                            }
                        }
                    }
                }
            }

            cancelBtn.setOnClickListener {
                addEditExperienceDialog.dismiss()
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
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun setExperiences() {
        binding.apply {
            experienceAdapter =
                ExperienceAdapter(experienceViewModel.experienceList) { experienceResponse, position ->
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