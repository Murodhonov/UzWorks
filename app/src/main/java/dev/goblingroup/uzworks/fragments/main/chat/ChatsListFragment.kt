package dev.goblingroup.uzworks.fragments.main.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.adapters.rv_adapters.ChatUsersAdapter
import dev.goblingroup.uzworks.databinding.FragmentChatsListBinding
import dev.goblingroup.uzworks.utils.getNavOptions


class ChatsListFragment : Fragment() {

    private val TAG = "ChatFragment"

    private var _binding: FragmentChatsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            val adapter = ChatUsersAdapter(requireContext()) {
                findNavController().navigate(
                    resId = R.id.chatFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            chatUsersRv.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}