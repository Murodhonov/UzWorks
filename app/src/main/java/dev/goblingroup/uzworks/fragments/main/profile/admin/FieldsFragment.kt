package dev.goblingroup.uzworks.fragments.main.profile.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.goblingroup.uzworks.databinding.FragmentFieldsBinding
import dev.goblingroup.uzworks.utils.NetworkHelper

class FieldsFragment : Fragment() {

    private var _binding: FragmentFieldsBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkHelper: NetworkHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFieldsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            networkHelper = NetworkHelper(requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(tabTitle: String) =
            FieldsFragment().apply {
                arguments = Bundle().apply {
                    putString("tab_title", tabTitle)
                }
            }
    }
}