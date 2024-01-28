package dev.goblingroup.uzworks.fragments.admin.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.FragmentWorkerControlBinding
import dev.goblingroup.uzworks.vm.SecuredWorkerViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class WorkerControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentWorkerControlBinding? = null
    private val binding get() = _binding!!

    private val workerViewModel: SecuredWorkerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerControlBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {

        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WorkerControlFragment().apply {
                arguments = Bundle().apply {
                    
                }
            }
    }
}