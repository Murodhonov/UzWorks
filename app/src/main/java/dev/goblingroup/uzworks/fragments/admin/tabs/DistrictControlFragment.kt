package dev.goblingroup.uzworks.fragments.admin.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapters.rv_adapters.DistrictAdapter
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.databinding.FragmentDistrictControlBinding
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.DistrictViewModel
import dev.goblingroup.uzworks.vm.SecuredDistrictViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class DistrictControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentDistrictControlBinding? = null
    private val binding get() = _binding!!

    private val districtViewModel: DistrictViewModel by viewModels()
    private val securedDistrictViewModel: SecuredDistrictViewModel by viewModels()

    private lateinit var districtList: ArrayList<DistrictEntity>
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
            launch {
                districtViewModel.districtStateFlow
                    .collect {
                        when (it) {
                            is ApiStatus.Error -> {
                                progressBar.visibility = View.INVISIBLE
                            }

                            is ApiStatus.Loading -> {
                                progressBar.visibility = View.VISIBLE
                            }

                            is ApiStatus.Success -> {
                                progressBar.visibility = View.INVISIBLE
                                districtList = ArrayList(it.response!!)
                                success(districtList)
                            }
                        }
                    }
            }
        }
    }

    private fun success(districtList: List<DistrictEntity>) {
        binding.apply {
            districtAdapter = DistrictAdapter(
                districtList,
                {
                    Toast.makeText(
                        requireContext(),
                        "editing $it is in progress",
                        Toast.LENGTH_SHORT
                    ).show()
                }, { districtEntity, position, deleteProgress, deleteButton ->
                    delete(districtEntity, position, deleteProgress, deleteButton)
                }
            )
            rv.adapter = districtAdapter
        }
    }

    private fun delete(
        districtEntity: DistrictEntity,
        position: Int,
        deleteProgressBar: ProgressBar,
        deleteButton: ImageView
    ) {
        launch {
            securedDistrictViewModel.deleteDistrict(districtEntity.id)
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
                            deleteProgressBar.visibility = View.GONE
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