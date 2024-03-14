package dev.goblingroup.uzworks.fragments.announcement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentViewJobLocationBinding

class ViewJobLocationFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentViewJobLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewJobLocationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val mapFragment =
                childFragmentManager.findFragmentById(R.id.view_job_map) as SupportMapFragment
            mapFragment.getMapAsync(this@ViewJobLocationFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val latitude = arguments?.getDouble("latitude")!!
        val longitude = arguments?.getDouble("longitude")!!
        val latLng = LatLng(latitude, longitude)
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(latLng, 15f),
            1000,
            null
        )
        googleMap.addMarker(MarkerOptions().position(latLng))
    }
}