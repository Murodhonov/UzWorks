package dev.goblingroup.uzworks.fragments.admin.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.FragmentRegionControlBinding
import dev.goblingroup.uzworks.vm.SecuredRegionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class RegionControlFragment : Fragment(), CoroutineScope {

    private var _binding: FragmentRegionControlBinding? = null
    private val binding get() = _binding!!

    private val regionViewModel: SecuredRegionViewModel by  viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegionControlBinding.inflate(layoutInflater)
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
            RegionControlFragment().apply {
                arguments = Bundle().apply {
//                    putString("ARG_PARAM1", param1)
//                    putString("ARG_PARAM2", param2)
                }
            }
    }
}