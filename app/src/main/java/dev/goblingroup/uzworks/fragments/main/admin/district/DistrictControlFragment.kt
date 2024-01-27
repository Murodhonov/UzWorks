package dev.goblingroup.uzworks.fragments.main.admin.district

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.goblingroup.uzworks.adapters.rv_adapters.DistrictAdapter
import dev.goblingroup.uzworks.databinding.FragmentDistrictControlBinding
import dev.goblingroup.uzworks.vm.DistrictViewModel
import dev.goblingroup.uzworks.models.request.DistrictEditRequest
import dev.goblingroup.uzworks.models.request.DistrictRequest
import dev.goblingroup.uzworks.models.response.DistrictResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.SecuredDistrictViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DistrictControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentDistrictControlBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkHelper: NetworkHelper

    private lateinit var viewModel: DistrictViewModel
    private lateinit var securedViewModel: SecuredDistrictViewModel

    private lateinit var viewModelFactory: DistrictViewModelFactory
    private lateinit var securedViewModelFactory: SecuredDistrictViewModelFactory

    private lateinit var districtList: ArrayList<DistrictResponse>
    private lateinit var districtAdapter: DistrictAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDistrictControlBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
//            networkHelper = NetworkHelper(requireContext())
//            viewModelFactory = DistrictViewModelFactory(
//                districtService = ApiClient.districtService,
//                networkHelper = networkHelper,
//                districtId = "",
//                regionId = ""
//            )
//            viewModel = ViewModelProvider(
//                owner = this@DistrictControlFragment,
//                factory = viewModelFactory
//            )[DistrictViewModel::class.java]
//            launch {
//                viewModel.getDistrictStateFlow()
//                    .collect {
//                        when (it) {
//                            is ApiStatus.Error -> {
//                                progressBar.visibility = View.INVISIBLE
//                            }
//
//                            is ApiStatus.Loading -> {
//                                progressBar.visibility = View.VISIBLE
//                            }
//
//                            is ApiStatus.Success -> {
//                                progressBar.visibility = View.INVISIBLE
//                                districtList = ArrayList(it.response as List<DistrictResponse>)
//                                success(districtList)
//                            }
//                        }
//                    }
//            }
        }
    }

    private fun success(districtList: List<DistrictResponse>) {
        binding.apply {
            districtAdapter = DistrictAdapter(
                districtList,
                {
                    Toast.makeText(
                        requireContext(),
                        "editing $it is in progress",
                        Toast.LENGTH_SHORT
                    ).show()
                }, { districtResponse, position, deleteProgress, deleteButton ->
                    delete(districtResponse, position, deleteProgress, deleteButton)
                }
            )
            rv.adapter = districtAdapter
        }
    }

    private fun delete(
        districtResponse: DistrictResponse,
        position: Int,
        deleteProgressBar: ProgressBar,
        deleteButton: ImageView
    ) {
        securedViewModelFactory = SecuredDistrictViewModelFactory(
            securedDistrictService = ApiClient.securedDistrictService,
            districtRequest = DistrictRequest("", ""),
            districtId = districtResponse.id,
            districtEditRequest = DistrictEditRequest("", "", ""),
            networkHelper = networkHelper
        )

        securedViewModel = ViewModelProvider(
            owner = this@DistrictControlFragment,
            factory = securedViewModelFactory
        )[SecuredDistrictViewModel::class.java]

        launch {
            securedViewModel.deleteDistrict()
                .collect {
                    when (it) {
                        is ApiStatus.Error -> {
                            deleteButton.visibility = View.VISIBLE
                            deleteProgressBar.visibility = View.INVISIBLE
                            Toast.makeText(
                                requireContext(),
                                "error on deleting",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is ApiStatus.Loading -> {
                            deleteButton.visibility = View.INVISIBLE
                            deleteProgressBar.visibility = View.VISIBLE
                        }

                        is ApiStatus.Success -> {
//                            deleteProgressBar.visibility = View.GONE
                            deleteSuccess(position)
                        }
                    }
                }
        }
    }

    private fun deleteSuccess(position: Int) {
        binding.apply {
            Log.d(TAG, "deleteSuccess: deleted successfully")
            Log.d(TAG, "deleteSuccess: ${districtList.size}")
            districtList.removeAt(position)
            districtAdapter.notifyItemChanged(position)
            districtAdapter.notifyItemRangeChanged(position, districtList.size - position)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val TAG = "DistrictControlFragment"

        @JvmStatic
        fun newInstance() =
            DistrictControlFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}