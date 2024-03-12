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
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSelectJobAddressBinding
import dev.goblingroup.uzworks.databinding.JobAddressDialogBinding
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import dev.goblingroup.uzworks.utils.ConstValues.TAG

class SelectJobAddressFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSelectJobAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    private var selectedLocation: LatLng? = null

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var previousMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectJobAddressBinding.inflate(layoutInflater)
        binding.apply {
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@SelectJobAddressFragment)
            if (arguments?.getDouble("latitude") != null && arguments?.getDouble("longitude") != null) {
                selectedLocation = LatLng(
                    arguments?.getDouble("latitude") ?: DEFAULT_LATITUDE,
                    arguments?.getDouble("longitude") ?: DEFAULT_LONGITUDE
                )
                Log.d(
                    TAG,
                    "onViewCreated: map testing $selectedLocation received in ${this@SelectJobAddressFragment::class.java.simpleName}"
                )
            }

            setLocationBtn.setOnClickListener {
                setLocation()
            }

            backBtn.setOnClickListener {
                setLocation()
            }

            requireActivity().onBackPressedDispatcher.addCallback(
                viewLifecycleOwner,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        setLocation()
                    }
                })

            updateFindBtn()
            findMeBtn.setOnClickListener {
                if (checkLocationPermission()) {
                    findUser()
                } else {
                    requestLocationPermission()
                }
            }
        }
    }

    private fun findUser() {
        if (!checkLocationPermission())
            return
        LocationServices.getFusedLocationProviderClient(requireContext()).lastLocation
            .addOnSuccessListener {
                if (it != null) {
                    selectedLocation = LatLng(it.latitude, it.longitude)
                    previousMarker?.remove()
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 20f)
                    googleMap.animateCamera(cameraUpdate, 1000, null)
                    previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation!!))
                } else {
                    Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "failed to get location", Toast.LENGTH_SHORT)
                    .show()
                Log.e(TAG, "findUser: ${it.message}")
            }
    }

    private fun requestLocationPermission() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findUser()
                updateFindBtn()
            } else {
                Toast.makeText(requireContext(), "denied", Toast.LENGTH_SHORT).show()
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
        val bundle = Bundle()
        Log.d(
            TAG,
            "onViewCreated: map testing $selectedLocation passed from ${this@SelectJobAddressFragment::class.java.simpleName}"
        )
        bundle.putDouble("latitude", selectedLocation?.latitude ?: 0.0)
        bundle.putDouble("longitude", selectedLocation?.longitude ?: 0.0)
        setFragmentResult("lat_lng", bundle)
        findNavController().popBackStack()
    }

    private fun displayDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val addressDialogBinding = JobAddressDialogBinding.inflate(layoutInflater)
        alertDialog.setView(addressDialogBinding.root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        displayDialog()
        googleMap = map
        Log.d(TAG, "onMapReady: $previousMarker")
        if (selectedLocation?.latitude != DEFAULT_LATITUDE && selectedLocation?.longitude != DEFAULT_LONGITUDE) {
            previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation!!))
        }
        Log.d(TAG, "onMapReady: $previousMarker")
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 12f))

        googleMap.setOnMapClickListener {
            binding.setLocationBtn.visibility = View.VISIBLE
            previousMarker?.remove()
            val newMarker = googleMap.addMarker(MarkerOptions().position(it))
            previousMarker = newMarker
            selectedLocation = it
        }
    }
}