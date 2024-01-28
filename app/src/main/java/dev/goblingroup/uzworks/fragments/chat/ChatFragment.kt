package dev.goblingroup.uzworks.fragments.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.adapters.rv_adapters.MessagesAdapter
import dev.goblingroup.uzworks.databinding.FragmentChatBinding

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val TAG = "ChatFragment"

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            backBtn.setOnClickListener {
                findNavController().popBackStack()
            }

            messagesRv.adapter = MessagesAdapter()

            messageLayout.setOnClickListener {
                messageEt.requestFocus()
                val inputMethodManager =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.showSoftInput(messageEt, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}