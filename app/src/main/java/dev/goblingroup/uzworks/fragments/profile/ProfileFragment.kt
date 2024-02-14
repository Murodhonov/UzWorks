package dev.goblingroup.uzworks.fragments.profile

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapter.rv_adapters.ExperienceAdapter
import dev.goblingroup.uzworks.databinding.AboutDialogItemBinding
import dev.goblingroup.uzworks.databinding.ExperienceDialogItemBinding
import dev.goblingroup.uzworks.databinding.FieldsDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentProfileBinding
import dev.goblingroup.uzworks.databinding.PersonalInfoDialogItemBinding
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.utils.dpToPx
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.SecurityViewModel

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val securityViewModel: SecurityViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            announcementTv.text = if (securityViewModel.getUserRoles()
                    .contains(UserRole.EMPLOYEE.roleName)
            ) "Ishchi e'lon qilish" else "Ish e'lon qilish"

            settings.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.settingsFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            personalInfoBtn.setOnClickListener {
                personalInfoDialog(requireContext(), layoutInflater)
            }

            aboutBtn.setOnClickListener {
                aboutDialog(requireContext(), layoutInflater)
            }

            experienceBtn.setOnClickListener {
                experienceDialog(requireContext(), layoutInflater)
            }

            announcementsBtn.setOnClickListener {
                navigateAnnouncement()
            }
        }
    }

    private fun personalInfoDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        val binding = PersonalInfoDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            alertDialog.show()
        }
    }

    private fun aboutDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        val binding = AboutDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            alertDialog.show()
        }
    }

    private fun fieldsDialog(
        context: Context,
        layoutInflater: LayoutInflater,
        resources: Resources
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        val binding = FieldsDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setChipGroup(chipGroup, context, resources)
            alertDialog.show()
        }
    }

    private fun setChipGroup(
        chipGroup: ChipGroup,
        context: Context,
        resources: Resources
    ) {
        val list = arrayOf(
            "History",
            "Sport",
            "Art",
            "Entertainment",
            "Outdoor",
            "Music",
            "Social",
            "Nightlife",
            "Concerts",
            "Health",
            "Submarine",
            "Shopping",
            "Walking",
            "Museum",
            "Cinema",
            "Adventure",
            "Animals",
            "Food",
            "Party",
            "Nature",
        )
        for (chipText in list) {
            val chip = Chip(context)
            chip.text = chipText
            chip.setChipBackgroundColorResource(R.color.middle_green)
            chip.setChipStrokeColorResource(R.color.middle_green)
            chip.setTextColor(resources.getColor(R.color.text_color))
            chip.typeface = ResourcesCompat.getFont(context, R.font.dmsans_regular)
            chip.chipCornerRadius = 5f.dpToPx()
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            chipGroup.addView(chip)
        }
    }

    private fun experienceDialog(
        context: Context,
        layoutInflater: LayoutInflater
    ) {
        val alertDialog = AlertDialog.Builder(context).create()
        val binding = ExperienceDialogItemBinding.inflate(layoutInflater)
        binding.apply {
            alertDialog.setView(root)
            alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            experienceRv.adapter = ExperienceAdapter()
            alertDialog.show()
        }
    }

    private fun navigateAnnouncement() {
        val direction = if (securityViewModel.getUserRoles()
                .contains(UserRole.EMPLOYEE.roleName)
        ) {
            R.id.addWorkerFragment
        } else {
            R.id.addJobFragment
        }
        findNavController().navigate(
            resId = direction,
            args = null,
            navOptions = getNavOptions()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}