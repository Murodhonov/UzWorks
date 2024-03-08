package dev.goblingroup.uzworks.fragments.announcement

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentJobAddressBinding
import dev.goblingroup.uzworks.databinding.JobAddressDialogBinding
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LATITUDE
import dev.goblingroup.uzworks.utils.ConstValues.DEFAULT_LONGITUDE
import dev.goblingroup.uzworks.utils.ConstValues.TAG

class JobAddressFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentJobAddressBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    private var state = false

    private var selectedLocation: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobAddressBinding.inflate(layoutInflater)
        binding.apply {
            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@JobAddressFragment)
            state = arguments?.getBoolean("job_creating") ?: false
            if (arguments?.getDouble("latitude") != null && arguments?.getDouble("longitude") != null) {
                selectedLocation = LatLng(
                    arguments?.getDouble("latitude") ?: DEFAULT_LATITUDE,
                    arguments?.getDouble("longitude") ?: DEFAULT_LONGITUDE
                )
                Log.d(
                    TAG,
                    "onViewCreated: map testing $selectedLocation received in ${this@JobAddressFragment::class.java.simpleName}"
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
        }
    }

    private fun setLocation() {
        val bundle = Bundle()
        Log.d(
            TAG,
            "onViewCreated: map testing $selectedLocation passed from ${this@JobAddressFragment::class.java.simpleName}"
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
        if (state) {
            displayDialog()
        }

        var previousMarker: Marker? = null
        googleMap = map
        if (selectedLocation?.latitude != DEFAULT_LATITUDE && selectedLocation?.longitude != DEFAULT_LONGITUDE) {
            previousMarker = googleMap.addMarker(MarkerOptions().position(selectedLocation!!))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 12f))

        googleMap.setOnMapClickListener {
            if (state) {
                binding.setLocationBtn.visibility = View.VISIBLE

                previousMarker?.remove()

                val newMarker = googleMap.addMarker(MarkerOptions().position(it))

                previousMarker = newMarker

                selectedLocation = it
            }
        }
    }
}