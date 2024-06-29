package com.goblindevs.uzworks.fragments.announcement

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentWorkerDetailsBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.models.response.WorkerResponse
import com.goblindevs.uzworks.utils.ConstValues
import com.goblindevs.uzworks.utils.ConstValues.DEFAULT_BIRTHDAY
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.formatSalary
import com.goblindevs.uzworks.utils.isoToDmy
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.WorkerDetailsViewModel

@AndroidEntryPoint
class WorkerDetailsFragment : Fragment() {

    private var _binding: FragmentWorkerDetailsBinding? = null
    private val binding get() = _binding!!

    private val workerDetailsViewModel: WorkerDetailsViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            loadWorker()
        }
    }

    private fun loadWorker() {
        workerDetailsViewModel.fetchWorker(arguments?.getString("announcement_id").toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(
                            requireContext(),
                            resources.getString(R.string.fetch_worker_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                        loadingDialog.dismiss()
                        findNavController().popBackStack()
                    }
                    is ApiStatus.Loading -> {
                        loading()
                    }
                    is ApiStatus.Success -> {
                        loadingDialog.dismiss()
                        setWorkerDetails(it.response)
                    }
                }
            }
    }

    private fun loading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        val loadingBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setView(loadingBinding.root)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun setWorkerDetails(workerResponse: WorkerResponse?) {
        binding.apply {
            titleTv.isSelected = true
            jobCategoryTv.isSelected = true


            titleTv.text = workerResponse?.title
            jobCategoryTv.text = workerResponse?.jobCategory?.title
            genderTv.text =
                when (workerResponse?.gender) {
                    GenderEnum.MALE.label -> {
                        resources.getString(R.string.male)
                    }

                    GenderEnum.FEMALE.label -> {
                        resources.getString(R.string.female)
                    }

                    GenderEnum.UNKNOWN.label -> {
                        resources.getString(R.string.unknown)
                    }

                    else -> {
                        ""
                    }
                }
            fullNameTv.text = workerResponse?.fullName
            when (workerResponse?.birthDate) {
                DEFAULT_BIRTHDAY -> {
                    birthdateTv.text = resources.getString(R.string.unknown)
                }

                else -> {
                    birthdateTv.text = workerResponse?.birthDate?.isoToDmy()
                }
            }
            addressTv.text = "${workerResponse?.district?.region?.name}, ${workerResponse?.district?.name}"
            salaryTv.text = workerResponse?.salary.toString().formatSalary()
            workingTimeTv.text = workerResponse?.workingTime
            workingScheduleTv.text = workerResponse?.workingSchedule

            contactTgBtn.setOnClickListener {
                openLink("https://t.me/${workerResponse?.tgUserName}")
            }

            contactCallBtn.setOnClickListener {
                call(workerResponse?.phoneNumber)
            }

            if (workerResponse?.telegramLink.toString()
                    .isEmpty() && workerResponse?.instagramLink.toString().isEmpty()
            ) {
                socialsDivider.visibility = View.GONE
                socialsTv.visibility = View.GONE
                tgIv.visibility = View.GONE
                tgLinkTv.visibility = View.GONE
                igIv.visibility = View.GONE
                igLinkTv.visibility = View.GONE
            }

            tgIv.setOnClickListener {
                openLink("https://t.me/${workerResponse?.telegramLink}")
            }

            tgLinkTv.setOnClickListener {
                openLink("https://t.me/${workerResponse?.telegramLink}")
            }

            igIv.setOnClickListener {
                openLink("https://www.instagram.com/${workerResponse?.instagramLink}")
            }

            igLinkTv.setOnClickListener {
                openLink("https://www.instagram.com/${workerResponse?.instagramLink}")
            }
        }
    }

    private fun call(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(ConstValues.TAG, "call: $e")
            Log.e(ConstValues.TAG, "call: ${e.message}")
            Log.e(ConstValues.TAG, "call: ${e.printStackTrace()}")
        }
    }

    private fun openLink(link: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(ConstValues.TAG, "openLink: $e")
            Log.e(ConstValues.TAG, "openLink: ${e.message}")
            Log.e(ConstValues.TAG, "openLink: ${e.printStackTrace()}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}