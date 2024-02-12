package dev.goblingroup.uzworks.fragments.workers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.goblingroup.uzworks.adapter.rv_adapters.WorkerAdapter
import dev.goblingroup.uzworks.databinding.FragmentAllWorkersBinding
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.WorkersViewModel
import kotlinx.coroutines.launch

class AllWorkersFragment : Fragment() {

    private val TAG = "AllWorkersFragment"

    private var _binding: FragmentAllWorkersBinding? = null
    private val binding get() = _binding!!

    private var workerClickListener: OnAllWorkerClickListener? = null

    private lateinit var workerAdapter: WorkerAdapter

    private val workersViewModel: WorkersViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllWorkersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: lifecycle checking $TAG")
            loadWorkers()
        }
    }

    private fun loadWorkers() {
        lifecycleScope.launch {
            workersViewModel.workersLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        Toast.makeText(requireContext(), "some error", Toast.LENGTH_SHORT)
                            .show()
                        Log.e(TAG, "loadWorkers: ${it.error}")
                        Log.e(TAG, "loadWorkers: ${it.error.printStackTrace()}")
                        Log.e(TAG, "loadWorkers: ${it.error.stackTrace}")
                        Log.e(TAG, "loadWorkers: ${it.error.message}")
                    }

                    is ApiStatus.Loading -> {
                        binding.progress.visibility = View.GONE
                    }

                    is ApiStatus.Success -> {
                        Log.d(TAG, "loadWorkers: ${it.response}")
                        success()
                    }
                }
            }
        }
    }

    private fun success() {
        if (_binding != null) {
            lifecycleScope.launch {
                binding.apply {
                    workerAdapter = WorkerAdapter(
                        workersViewModel.listDatabaseWorkers(),
                        jobCategoryViewModel.listJobCategories(),
                        { workerId ->
                            workerClickListener?.onAllWorkerClick(workerId)
                        },
                        { state, workerId ->
                            saveUnSave(state, workerId)
                        }
                    )
                    recommendedWorkAnnouncementsRv.adapter = workerAdapter
                }
            }
        }
    }

    private fun saveUnSave(state: Boolean, workerId: String) {
        lifecycleScope.launch {
            if (state) {
                workersViewModel.saveWorker(workerId)
            } else {
                workersViewModel.unSaveWorker(workerId)
            }
        }
    }

    interface OnAllWorkerClickListener {
        fun onAllWorkerClick(workerId: String)
    }

    fun setOnWorkerClickListener(listener: OnAllWorkerClickListener) {
        workerClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking $TAG")
        loadWorkers()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking $TAG")
        loadWorkers()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AllWorkersFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}