package com.goblindevs.uzworks.fragments.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import com.goblindevs.uzworks.adapter.ChatUsersAdapter
import com.goblindevs.uzworks.databinding.FragmentChatsListBinding

@AndroidEntryPoint
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
        Log.d(TAG, "onCreateView: checking UzWorks")
        _binding = FragmentChatsListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: checking UzWorks")
        binding.apply {
            /*adapter = ChatUsersAdapter(
                getList(),
                { position ->
                    *//**
                     * on user click
                     *//*
                    findNavController().navigate(
                        resId = R.id.chatFragment,
                        args = null
                    )

                }, { position ->
                    delete(position)
                }
            )
            chatUsersRv.adapter = adapter*/
        }
    }

    private fun delete(position: Int) {
        Toast.makeText(requireContext(), "$position delete clicked", Toast.LENGTH_SHORT).show()
    }

    private fun getList(): MutableList<Int> {
        userList = MutableList(15) { 1 }
        return userList
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: checking UzWorks")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: checking UzWorks")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: checking UzWorks")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: checking UzWorks")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: checking UzWorks")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: checking UzWorks")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach: checking UzWorks")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: checking UzWorks")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        fun newInstance() =
            ChatsListFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}