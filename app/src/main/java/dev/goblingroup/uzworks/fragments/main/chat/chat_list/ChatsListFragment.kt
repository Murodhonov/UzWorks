package dev.goblingroup.uzworks.fragments.main.chat.chat_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

    private lateinit var adapter: ChatUsersAdapter
    private lateinit var userList: MutableList<Int>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            adapter = ChatUsersAdapter(
                getList(),
                { position ->
                    /**
                     * on user click
                     */
                    findNavController().navigate(
                        resId = R.id.chatFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )

                }, { position ->
                    delete(position)
                }
            )
            chatUsersRv.adapter = adapter
        }
    }

    private fun delete(position: Int) {
        Toast.makeText(requireContext(), "$position delete clicked", Toast.LENGTH_SHORT).show()
    }

    private fun getList(): MutableList<Int> {
        userList = MutableList(15) { 1 }
        return userList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}