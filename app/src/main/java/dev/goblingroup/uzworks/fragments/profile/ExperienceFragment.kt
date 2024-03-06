package dev.goblingroup.uzworks.fragments.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import dev.goblingroup.uzworks.models.response.ExperienceResponse
import dev.goblingroup.uzworks.utils.clear
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ExperienceViewModel

@AndroidEntryPoint
class ExperienceFragment : Fragment() {

    private var _binding: FragmentExperienceBinding? = null
    private val binding get() = _binding!!

    private val experienceViewModel: ExperienceViewModel by viewModels()

    private lateinit var dialog: AlertDialog
    private lateinit var dialogItemBinding: AddEditExperienceDialogItemBinding

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

            dialog = AlertDialog.Builder(requireContext()).create()
            dialogItemBinding = AddEditExperienceDialogItemBinding.inflate(layoutInflater)
            dialog.setView(dialogItemBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            addExperienceBtn.setOnClickListener {
                addEditExperience(null)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun addEditExperience(experienceResponse: ExperienceResponse?) {
        dialogItemBinding.apply {
            dialog.show()
            startDateEt.clear()
            endDateEt.clear()
            startDateEt.editText?.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {

                }
                true
            }
        }
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