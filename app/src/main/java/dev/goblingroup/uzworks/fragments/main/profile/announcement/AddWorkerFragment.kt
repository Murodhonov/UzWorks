package dev.goblingroup.uzworks.fragments.main.profile.announcement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.database.entity.RegionEntity
import dev.goblingroup.uzworks.databinding.FragmentAddWorkerBinding
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.DistrictViewModel
import dev.goblingroup.uzworks.vm.DistrictViewModelFactory
import dev.goblingroup.uzworks.vm.RegionViewModel
import dev.goblingroup.uzworks.vm.RegionViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class AddWorkerFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentAddWorkerBinding? = null
    private val binding get() = _binding!!

    private lateinit var appDatabase: AppDatabase
    private lateinit var networkHelper: NetworkHelper

    private lateinit var regionViewModel: RegionViewModel
    private lateinit var regionViewModelFactory: RegionViewModelFactory

    private lateinit var districtViewModel: DistrictViewModel
    private lateinit var districtViewModelFactory: DistrictViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddWorkerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            appDatabase = AppDatabase.getInstance(requireContext())
            networkHelper = NetworkHelper(requireContext())

            loadRegions()
            loadDistricts()

            regionChoice.setOnItemClickListener { parent, view, position, id ->

            }
        }
    }

    private fun loadRegions() {
        launch {
            regionViewModelFactory = RegionViewModelFactory(
                appDatabase = appDatabase,
                regionService = ApiClient.regionService,
                regionId = "",
                districtId = "",
                networkHelper = networkHelper
            )
            regionViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = regionViewModelFactory
            )[RegionViewModel::class.java]

            regionViewModel.getRegionStateFlow()
                .collect {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error while loading regions",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "loadRegions: ${it.error}")
                            Log.e(TAG, "loadRegions: ${it.error.message}")
                            Log.e(TAG, "loadRegions: ${it.error.printStackTrace()}")
                            Log.e(TAG, "loadRegions: ${it.error.stackTrace}")
                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            setRegions(it.response as List<RegionEntity>)
                        }
                    }
                }
        }
    }

    private fun setRegions(regionList: List<RegionEntity>) {
        binding.apply {
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                regionList
            )
            regionChoice.setAdapter(adapter)
        }
    }

    private fun loadDistricts() {
        launch {
            districtViewModelFactory = DistrictViewModelFactory(
                appDatabase = appDatabase,
                districtService = ApiClient.districtService,
                networkHelper = networkHelper,
                districtId = "",
                regionId = ""
            )

            districtViewModel = ViewModelProvider(
                owner = this@AddWorkerFragment,
                factory = districtViewModelFactory
            )[DistrictViewModel::class.java]

            districtViewModel.getDistrictStateFlow()
                .collect {
                    when (it) {
                        is ApiStatus.Error -> {
                            Toast.makeText(
                                requireContext(),
                                "some error while loading districts",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.e(TAG, "loadRegions: ${it.error}")
                            Log.e(TAG, "loadRegions: ${it.error.message}")
                            Log.e(TAG, "loadRegions: ${it.error.printStackTrace()}")
                            Log.e(TAG, "loadRegions: ${it.error.stackTrace}")
                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            (it.response as List<DistrictEntity>).forEach {
                                Log.d(TAG, "loadDistricts: succeeded $it")
                            }
                        }
                    }
                }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}