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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.R
import com.goblindevs.uzworks.databinding.FragmentJobDetailsBinding
import com.goblindevs.uzworks.databinding.LoadingDialogBinding
import com.goblindevs.uzworks.models.response.JobResponse
import com.goblindevs.uzworks.utils.ConstValues.TAG
import com.goblindevs.uzworks.utils.GenderEnum
import com.goblindevs.uzworks.utils.LanguageEnum
import com.goblindevs.uzworks.utils.isoToDmy
import com.goblindevs.uzworks.vm.ApiStatus
import com.goblindevs.uzworks.vm.JobDetailsViewModel

@AndroidEntryPoint
class JobDetailsFragment : Fragment() {

    private var _binding: FragmentJobDetailsBinding? = null

    private val binding get() = _binding!!

    private val jobDetailsViewModel: JobDetailsViewModel by viewModels()

    private lateinit var googleMap: GoogleMap

    private var latitude = 0.0
    private var longitude = 0.0
    private var jobTitle = ""

    private lateinit var loadingDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            loadJob()

            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun loadJob() {
        jobDetailsViewModel.fetchJob(arguments?.getString("announcement_id").toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), resources.getString(R.string.fetch_job_failed), Toast.LENGTH_SHORT)
                            .show()
                        loadingDialog.dismiss()
                        findNavController().popBackStack()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        loadingDialog.dismiss()
                        setJobDetails(it.response)
                    }
                }
            }
    }

    private fun loading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        val dialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingDialog.setView(dialogBinding.root)
        loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun setJobDetails(jobResponse: JobResponse?) {
        binding.apply {
            titleTv.isSelected = true
            jobCategoryTv.isSelected = true

            titleTv.text = jobResponse?.title
            jobCategoryTv.text = jobResponse?.jobCategory?.title
            genderTv.text =
                when (jobResponse?.gender) {
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
            addressTv.text =
                "${jobResponse?.district?.region?.name}, ${jobResponse?.district?.name}"
            salaryTv.text = jobResponse?.salary.toString()
            workingTimeTv.text = jobResponse?.workingTime
            workingScheduleTv.text = jobResponse?.workingSchedule
            benefitTv.text = jobResponse?.benefit
            ageLimitTv.text =
                if (jobDetailsViewModel.getLanguageCode() == LanguageEnum.ENGLISH.code ||
                    jobDetailsViewModel.getLanguageCode() == LanguageEnum.RUSSIAN.code
                )
                    "${resources.getString(R.string.from)} ${jobResponse?.minAge} ${
                        resources.getString(
                            R.string.until
                        )
                    } ${jobResponse?.maxAge}"
                else "${jobResponse?.minAge} ${resources.getString(R.string.from)} ${jobResponse?.maxAge} ${
                    resources.getString(
                        R.string.until
                    )
                }"
            requirementTv.text = jobResponse?.requirement
            latitude = jobResponse?.latitude ?: 0.0
            longitude = jobResponse?.longitude ?: 0.0
//            dateTv.text = jobDetailsViewModel.getTimeAgo(jobResponse?.createDate.toString(), resources)
            dateTv.text = jobResponse?.createDate?.isoToDmy()

            contactTgBtn.setOnClickListener {
                openLink("https://t.me/${jobResponse?.tgUserName}")
            }

            contactCallBtn.setOnClickListener {
                call(jobResponse?.phoneNumber)
            }

            if (jobResponse?.telegramLink.toString()
                    .isEmpty() && jobResponse?.instagramLink.toString().isEmpty()
            ) {
                socialsDivider.visibility = View.GONE
                socialsTv.visibility = View.GONE
                tgIv.visibility = View.GONE
                tgLinkTv.visibility = View.GONE
                igIv.visibility = View.GONE
                igLinkTv.visibility = View.GONE
            }

            tgIv.setOnClickListener {
                openLink("https://t.me/${jobResponse?.telegramLink}")
            }

            tgLinkTv.setOnClickListener {
                openLink("https://t.me/${jobResponse?.telegramLink}")
            }

            igIv.setOnClickListener {
                openLink("https://www.instagram.com/${jobResponse?.instagramLink}")
            }

            igLinkTv.setOnClickListener {
                openLink("https://www.instagram.com/${jobResponse?.instagramLink}")
            }

            try {
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.job_details_map) as SupportMapFragment
                mapFragment.getMapAsync { map ->
                    googleMap = map
                    val latLng = LatLng(latitude, longitude)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                        1000,
                        null
                    )
                    googleMap.addMarker(MarkerOptions().position(latLng))
                    googleMap.setOnMapClickListener {
                        val geoUri =
                            Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${binding.titleTv.text})")
                        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
                        mapIntent.setPackage("com.google.android.apps.maps")

                        try {
                            startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                resources.getString(R.string.open_location_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "setJobDetails: $e")
                Log.e(TAG, "setJobDetails: ${e.message}")
                Log.e(TAG, "setJobDetails: ${e.printStackTrace()}")
                mapAddressTv.text = resources.getString(R.string.open_map_failed)
                mapIv.visibility = View.VISIBLE
            }
        }
    }

    private fun call(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "call: $e")
            Log.e(TAG, "call: ${e.message}")
            Log.e(TAG, "call: ${e.printStackTrace()}")
        }
    }

    private fun openLink(link: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "openLink: $e")
            Log.e(TAG, "openLink: ${e.message}")
            Log.e(TAG, "openLink: ${e.printStackTrace()}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}