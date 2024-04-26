package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentForgotPasswordBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.databinding.PhoneVerificationDialogBinding
import dev.goblingroup.uzworks.databinding.SetPasswordDialogBinding
import dev.goblingroup.uzworks.models.request.ForgotPasswordRequest
import dev.goblingroup.uzworks.utils.addCodeTextWatcher
import dev.goblingroup.uzworks.utils.formatPhoneNumber
import dev.goblingroup.uzworks.utils.setFocus
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.ForgotPasswordViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private val TAG = "ForgotPasswordFragment"

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private val forgotPasswordViewModel: ForgotPasswordViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    private lateinit var phoneVerificationDialog: BottomSheetDialog
    private lateinit var phoneVerificationDialogBinding: PhoneVerificationDialogBinding

    private lateinit var setPasswordDialog: BottomSheetDialog
    private lateinit var setPasswordDialogBinding: SetPasswordDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            submitBtn.setOnClickListener {
                if (phoneNumberEt.editText?.text.toString().trim()
                        .filter { !it.isWhitespace() }.length != 13
                ) {
                    phoneNumberEt.isErrorEnabled = true
                    phoneNumberEt.error = resources.getString(R.string.phone_number_error)
                } else {
                    lifecycleScope.launch {
                        forgotPasswordViewModel.forgotPassword(
                            ForgotPasswordRequest(
                                phoneNumberEt.editText?.text.toString().trim()
                                    .filter { !it.isWhitespace() }.substring(1)
                            )
                        )
                    }
                }
                verifyPhone()
            }

            phoneNumberEt.editText?.addTextChangedListener(object : TextWatcher {
                private var isFormatting = false

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if (isFormatting) {
                        return
                    }

                    isFormatting = true
                    val newText = s.toString().filter { !it.isWhitespace() }
                    val oldText =
                        phoneNumberEt.editText?.tag.toString().filter { !it.isWhitespace() }
                    val formattedPhone =
                        s?.filter { !it.isWhitespace() }.toString()
                            .formatPhoneNumber(newText.length < oldText.length)
                    phoneNumberEt.editText?.setText(formattedPhone)
                    phoneNumberEt.editText?.setSelection(formattedPhone.length)
                    phoneNumberEt.tag = formattedPhone

                    isFormatting = false
                }
            })

            forgotPasswordViewModel.forgotPasswordLiveData.observe(viewLifecycleOwner) {
                when (it) {
                    is ApiStatus.Error -> {
                        loadingDialog.dismiss()
                        Toast.makeText(requireContext(), "something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }

                    is ApiStatus.Loading -> {
                        loading()
                    }

                    is ApiStatus.Success -> {
                        loadingDialog.dismiss()
                        verifyPhone()
                    }
                }
            }
        }
    }

    private fun verifyPhone() {
        try {
            phoneVerificationDialog.show()
        } catch (e: Exception) {
            phoneVerificationDialog = BottomSheetDialog(requireContext())
            phoneVerificationDialogBinding = PhoneVerificationDialogBinding.inflate(layoutInflater)
            phoneVerificationDialog.setContentView(phoneVerificationDialogBinding.root)
            phoneVerificationDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            phoneVerificationDialog.show()
        }

        phoneVerificationDialogBinding.apply {
            code1.setFocus(requireActivity())
            code1.addCodeTextWatcher(requireActivity(), null, code2)
            code2.addCodeTextWatcher(requireActivity(), code1, code3)
            code3.addCodeTextWatcher(requireActivity(), code2, code4)
            code4.addCodeTextWatcher(requireActivity(), code3, null)
        }

        forgotPasswordViewModel.verifyPhoneLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ApiStatus.Error -> {

                }

                is ApiStatus.Loading -> {

                }

                is ApiStatus.Success -> {

                }
            }
        }
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}