package dev.goblingroup.uzworks.fragments.admin.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.databinding.FragmentRegionControlBinding

@AndroidEntryPoint
class RegionControlFragment : Fragment() {

    private var _binding: FragmentRegionControlBinding? = null
    private val binding get() = _binding!!

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