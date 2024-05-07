package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AddEditExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentExperienceBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
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
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

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

            addExperienceBtn.setOnClickListener {
                addExperience()
            }

            experienceViewModel.experienceLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        error()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        succeeded()
                    }
                }
            }

            swipeRefresh.setOnRefreshListener {
                experienceViewModel.fetchExperiences()
            }
        }
    }

    private fun error() {
        binding.apply {
            try {
                loadingDialog.hide()
            } catch (e: Exception) {
                loadingDialog = AlertDialog.Builder(requireContext()).create()
                loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
                loadingDialog.setView(loadingDialogItemBinding.root)
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.setCancelable(false)
            }

            noExperienceTv.visibility = View.VISIBLE
            noExperienceTv.text = resources.getString(R.string.load_experience_failed)
        }
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogItemBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun succeeded() {
        binding.apply {
            swipeRefresh.isRefreshing = false
            try {
                loadingDialog.hide()
            } catch (e: Exception) {
                loadingDialog = AlertDialog.Builder(requireContext()).create()
                loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
                loadingDialog.setView(loadingDialogItemBinding.root)
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.setCancelable(false)
            }

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

    @SuppressLint("ClickableViewAccessibility")
    private fun addExperience() {
        try {
            addEditExperienceDialog.show()
        } catch (e: Exception) {
            addEditExperienceDialog = AlertDialog.Builder(requireContext()).create()
            addEditExperienceDialogBinding =
                AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            addEditExperienceDialog.setView(addEditExperienceDialogBinding.root)
            addEditExperienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addEditExperienceDialog.show()
        }

        addEditExperienceDialogBinding.apply {
            try {
                addEditExperienceDialog.show()
            } catch (e: Exception) {
                addEditExperienceDialog = AlertDialog.Builder(requireContext()).create()
                addEditExperienceDialogBinding =
                    AddEditExperienceDialogItemBinding.inflate(layoutInflater)
                addEditExperienceDialog.setView(addEditExperienceDialogBinding.root)
                addEditExperienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                addEditExperienceDialog.show()
            }

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
                                addError()
                            }

                            is ApiStatus.Loading -> {
                                loading()
                            }

                            is ApiStatus.Success -> {
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
                                    binding.noExperienceTv.visibility = View.GONE
                                }
                            }
                        }
                    }
                }
            }

            cancelBtn.setOnClickListener {
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    addEditExperienceDialog.window?.decorView?.windowToken,
                    0
                )
                addEditExperienceDialog.dismiss()
            }
        }

        addEditExperienceDialog.setOnDismissListener {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                addEditExperienceDialog.window?.decorView?.windowToken,
                0
            )
            addEditExperienceDialog.dismiss()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun updateExperience(experience: ExperienceResponse, position: Int) {
        try {
            addEditExperienceDialog.show()
        } catch (e: Exception) {
            addEditExperienceDialog = AlertDialog.Builder(requireContext()).create()
            addEditExperienceDialogBinding =
                AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            addEditExperienceDialog.setView(addEditExperienceDialogBinding.root)
            addEditExperienceDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            addEditExperienceDialog.show()
        }

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
                if (experienceViewModel.isFormValid(
                        resources,
                        positionEt,
                        companyNameEt,
                        startDateEt,
                        endDateEt
                    )
                ) {
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
                                editError()
                            }

                            is ApiStatus.Loading -> {
                                loading()
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
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    addEditExperienceDialog.window?.decorView?.windowToken,
                    0
                )
                addEditExperienceDialog.dismiss()
            }
        }

        addEditExperienceDialog.setOnDismissListener {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                addEditExperienceDialog.window?.decorView?.windowToken,
                0
            )
            addEditExperienceDialog.dismiss()
        }
    }

    private fun addError() {
        Toast.makeText(requireContext(), "failed to add experience", Toast.LENGTH_SHORT).show()
    }

    private fun editError() {
        Toast.makeText(requireContext(), "failed to edit experience", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}