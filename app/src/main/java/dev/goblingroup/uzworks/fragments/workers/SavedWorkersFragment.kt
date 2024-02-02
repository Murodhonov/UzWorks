package dev.goblingroup.uzworks.fragments.workers

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dev.goblingroup.uzworks.adapters.rv_adapters.SavedWorkersAdapter
import dev.goblingroup.uzworks.database.entity.WorkerEntity
import dev.goblingroup.uzworks.databinding.FragmentSavedWorkersBinding
import dev.goblingroup.uzworks.vm.JobCategoryViewModel
import dev.goblingroup.uzworks.vm.WorkersViewModel
import kotlinx.coroutines.launch

class SavedWorkersFragment : Fragment() {

    private val TAG = "SavedWorkersFragment"

    private var _binding: FragmentSavedWorkersBinding? = null
    private val binding get() = _binding!!

    private var workerClickListener: OnSavedWorkerClickListener? = null
    private var findWorkerClickListener: OnFindWorkerClickListener? = null

    private lateinit var savedWorkersAdapter: SavedWorkersAdapter

    private val workersViewModel: WorkersViewModel by viewModels()
    private val jobCategoryViewModel: JobCategoryViewModel by viewModels()

    private lateinit var savedWorkers: ArrayList<WorkerEntity>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedWorkersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            Log.d(TAG, "onViewCreated: lifecycle checking $TAG")
            loadSavedWorkers()
            findWorkerBtn.setOnClickListener {
                findWorkerClickListener?.onFindWorkerClick()
            }
        }
    }

    private fun loadSavedWorkers() {
        lifecycleScope.launch {
            binding.apply {
                savedWorkers = ArrayList(workersViewModel.listSavedWorkers())
                if (savedWorkers.isNotEmpty()) {
                    emptyLayout.visibility = View.GONE
                    savedWorkersAdapter = SavedWorkersAdapter(
                        savedWorkers,
                        jobCategoryViewModel.listJobCategories(),
                        { clickedWorkerId ->
                            workerClickListener?.onSavedWorkerClick(clickedWorkerId)
                        },
                        { workerId, position ->
                            unSave(workerId, position)
                        }
                    )
                    recommendedWorkAnnouncementsRv.adapter = savedWorkersAdapter
                } else {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun unSave(workerId: String, position: Int) {
        lifecycleScope.launch {
            binding.apply {
                savedWorkers.removeAt(position)
                savedWorkersAdapter.notifyItemRemoved(position)
                savedWorkersAdapter.notifyItemRangeChanged(
                    position,
                    workersViewModel.countSavedWorkers() - position
                )
                if (!workersViewModel.unSaveWorker(workerId)) {
                    emptyLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    interface OnSavedWorkerClickListener {
        fun onSavedWorkerClick(workerId: String)
    }

    interface OnFindWorkerClickListener {
        fun onFindWorkerClick()
    }

    fun setOnFindWorkerClickListener(listener: OnFindWorkerClickListener) {
        findWorkerClickListener = listener
    }

    fun setOnWorkerClickListener(listener: OnSavedWorkerClickListener) {
        workerClickListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: lifecycle checking $TAG")
        loadSavedWorkers()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: lifecycle checking $TAG")
        loadSavedWorkers()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SavedWorkersFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}