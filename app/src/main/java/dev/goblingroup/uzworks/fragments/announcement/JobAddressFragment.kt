package dev.goblingroup.uzworks.fragments.announcement

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentJobAddressBinding
import dev.goblingroup.uzworks.databinding.JobAddressDialogBinding

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
            if (state) {
                displayDialog()
            }

            setLocation.setOnClickListener {
                val bundle = Bundle()
                bundle.putDouble("latitude", selectedLocation?.latitude ?: 0.0)
                bundle.putDouble("longitude", selectedLocation?.longitude ?: 0.0)
                setFragmentResult("lat_lng", bundle)
                findNavController().popBackStack()
            }
        }
    }

    private fun displayDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).create()
        val addressDialogBinding = JobAddressDialogBinding.inflate(layoutInflater)
        alertDialog.setView(addressDialogBinding.root)
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        addressDialogBinding.closeTv.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val sydney = LatLng(41.3409, 69.2867)
        googleMap.addMarker(MarkerOptions().position(sydney))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f ))

        googleMap.setOnMapClickListener {
            if (state) {
                binding.setLocation.visibility = View.VISIBLE
                googleMap.addMarker(MarkerOptions().position(it))
                selectedLocation = it
            }
        }
    }
}