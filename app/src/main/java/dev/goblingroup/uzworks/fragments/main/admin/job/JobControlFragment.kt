package dev.goblingroup.uzworks.fragments.main.admin.job

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.goblingroup.uzworks.databinding.FragmentJobControlBinding
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.vm.SecuredJobViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class JobControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentJobControlBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkHelper: NetworkHelper

    private lateinit var jobViewModel: SecuredJobViewModel
    private lateinit var jobViewModelFactory: SecuredJobViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJobControlBinding.inflate(layoutInflater)
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
            JobControlFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}