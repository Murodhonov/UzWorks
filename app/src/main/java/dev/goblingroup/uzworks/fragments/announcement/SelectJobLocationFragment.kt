package dev.goblingroup.uzworks.fragments.announcement

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
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
import dev.goblingroup.uzworks.databinding.FragmentSelectJobLocationBinding
import dev.goblingroup.uzworks.databinding.JobAddressDialogBinding
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import dev.goblingroup.uzworks.utils.ConstValues.TAG

class SelectJobLocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentSelectJobLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    private var selectedLocation = LatLng(0.0, 0.0)

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

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
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@SelectJobLocationFragment)

            setLocationBtn.setOnClickListener {
                setLocation()
            }

            backBtn.setOnClickListener {
                setLocation()
            }

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
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f)
                    googleMap.animateCamera(cameraUpdate, 1000, null)
                    previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation))
                    binding.setLocationBtn.visibility = View.VISIBLE
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
        bundle.putDouble("latitude", if (selectedLocation.latitude == DEFAULT_LATITUDE) 0.0 else selectedLocation.latitude)
        bundle.putDouble("longitude", if (selectedLocation.longitude == DEFAULT_LONGITUDE) 0.0 else selectedLocation.longitude)
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

        if (arguments?.getDouble("latitude") != null && arguments?.getDouble("longitude") != null) {
            selectedLocation = LatLng(arguments?.getDouble("latitude")!!, arguments?.getDouble("longitude")!!)
        }

        if (selectedLocation.latitude == 0.0 && selectedLocation.longitude == 0.0) {
            selectedLocation = LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 4f))
        } else {
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
}