package dev.goblingroup.uzworks.fragments.announcement

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.ExplainLocationPermissionBinding
import dev.goblingroup.uzworks.databinding.FragmentSelectJobLocationBinding
import dev.goblingroup.uzworks.databinding.JobAddressDialogBinding
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.AddJobViewModel

@AndroidEntryPoint
class SelectJobLocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSelectJobLocationBinding? = null
    private val binding get() = _binding!!

    private val addJobViewModel: AddJobViewModel by activityViewModels()

    private lateinit var googleMap: GoogleMap

    private var selectedLocation = LatLng(0.0, 0.0)

    private var previousMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectJobLocationBinding.inflate(layoutInflater)
        binding.apply {
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.select_job_map) as SupportMapFragment
            mapFragment.getMapAsync(this@SelectJobLocationFragment)

            setLocationBtn.setOnClickListener {
                setLocation()
            }

            toolbar.setNavigationOnClickListener {
                setLocation()
            }

            updateFindBtn()
            findMeBtn.setOnClickListener {
                if (checkLocationPermission()) {
                    findUser()
                } else {
                    explain()
                }
            }
        }
    }

    private fun explain() {
        val explanationDialog = AlertDialog.Builder(requireContext()).create()
        val explanationBinding = ExplainLocationPermissionBinding.inflate(layoutInflater)
        explanationDialog.setView(explanationBinding.root)
        explanationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        explanationBinding.apply {
            allowBtn.setOnClickListener {
                explanationDialog.dismiss()
                requestLocationPermission()
            }
            denyBtn.setOnClickListener {
                explanationDialog.dismiss()
            }
        }
        explanationDialog.show()
    }

    private fun findUser() {
        if (!checkLocationPermission())
            return
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    selectedLocation = LatLng(it.latitude, it.longitude)
                    previousMarker?.remove()
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f)
                    googleMap.animateCamera(cameraUpdate, 1000, null)
                    previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation))
                    binding.setLocationBtn.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), resources.getString(R.string.get_location_failed), Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "findUser: ${it.message}")
            }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findUser()
                updateFindBtn()
            }
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun updateFindBtn() {
        if (checkLocationPermission()) {
            binding.findMeBtn.setImageResource(R.drawable.ic_find_me)
        } else {
            binding.findMeBtn.setImageResource(R.drawable.ic_location_permission_required)
        }
    }

    private fun setLocation() {
        addJobViewModel.setLatitude(selectedLocation.latitude)
        addJobViewModel.setLongitude(selectedLocation.longitude)
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        displayDialog()

        googleMap = map

        if (addJobViewModel.latitude.value == 0.0 && addJobViewModel.longitude.value == 0.0) {
            selectedLocation = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 4f))
        } else {
            selectedLocation = LatLng(
                addJobViewModel.latitude.value!!.toDouble(),
                addJobViewModel.longitude.value!!.toDouble()
            )
            previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation))
            binding.setLocationBtn.visibility = View.VISIBLE
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f))
        }

        googleMap.setOnMapClickListener {
            binding.setLocationBtn.visibility = View.VISIBLE
            previousMarker?.remove()
            val newMarker = googleMap.addMarker(MarkerOptions().position(it))
            previousMarker = newMarker
            selectedLocation = it
        }
    }

    private fun displayDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val addressDialogBinding = JobAddressDialogBinding.inflate(layoutInflater)
        alertDialog.setView(addressDialogBinding.root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
}