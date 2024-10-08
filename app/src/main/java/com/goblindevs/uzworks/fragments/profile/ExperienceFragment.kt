package com.goblindevs.uzworks.fragments.profile

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
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.adapter.ExperienceAdapter
import com.goblindevs.uzworks.databinding.AddEditExperienceDialogItemBinding
import com.goblindevs.uzworks.databinding.DeleteExperienceDialogBinding
import com.goblindevs.uzworks.databinding.FragmentExperienceBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.models.request.ExperienceCreateRequest
import com.goblindevs.uzworks.models.request.ExperienceEditRequest
import com.goblindevs.uzworks.models.response.ExperienceResponse
import com.goblindevs.uzworks.utils.dmyToIso
import com.goblindevs.uzworks.utils.isoToDmy
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.ExperienceViewModel

@AndroidEntryPoint
class ExperienceFragment : Fragment() {

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!

    private val experienceViewModel: ExperienceViewModel by activityViewModels()

    private lateinit var addEditExperienceDialog: AlertDialog
    private lateinit var addEditExperienceDialogBinding: AddEditExperienceDialogItemBinding

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    private lateinit var deleteDialog: BottomSheetDialog
    private lateinit var deleteExperienceDialogBinding: DeleteExperienceDialogBinding

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
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            toolbar.setOnMenuItemClickListener {
                if (it.itemId == R.id.add_experience)
                    addExperience()
                true
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
                loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
                loadingDialog.setView(loadingDialogBinding.root)
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
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
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
                loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
                loadingDialog.setView(loadingDialogBinding.root)
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.setCancelable(false)
            }

            experienceAdapter = ExperienceAdapter(
                experienceViewModel.experienceList,
                resources,
                { experienceResponse, position ->
                    updateExperience(experienceResponse, position)
                }, { experienceId, position ->
                    deleteExperience(experienceId, position)
                }
            )
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

    private fun deleteExperience(experienceId: String, position: Int) {
        try {
            deleteDialog.show()
        } catch (e: Exception) {
            deleteDialog = BottomSheetDialog(requireContext())
            deleteExperienceDialogBinding = DeleteExperienceDialogBinding.inflate(layoutInflater)
            deleteDialog.setContentView(deleteExperienceDialogBinding.root)
            deleteDialog.show()
        }
        deleteExperienceDialogBinding.apply {
            yesBtn.setOnClickListener {
                experienceViewModel.deleteExperience(experienceId).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            deleteDialog.dismiss()
                            loadingDialog.dismiss()
                        }

                        is ApiStatus.Loading -> {
                            loading()
                        }

                        is ApiStatus.Success -> {
                            deleteDialog.dismiss()
                            loadingDialog.dismiss()
                            experienceViewModel.experienceList.removeAt(position)
                            experienceAdapter.notifyItemRemoved(position)
                            if (experienceAdapter.itemCount == 0) {
                                binding.noExperienceTv.text =
                                    resources.getString(R.string.no_experience)
                                binding.noExperienceTv.visibility = View.VISIBLE
                            } else {
                                binding.noExperienceTv.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addError() {
        Toast.makeText(requireContext(), resources.getString(R.string.add_experience_failed), Toast.LENGTH_SHORT).show()
    }

    private fun editError() {
        Toast.makeText(requireContext(), resources.getString(R.string.edit_experience_failed), Toast.LENGTH_SHORT).show()
    }

    private fun deleteError() {
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.delete_experience_failed),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}