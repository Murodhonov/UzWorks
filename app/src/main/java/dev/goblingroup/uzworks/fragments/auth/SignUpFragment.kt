package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.SignupRequest
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.vm.AuthViewModel
import dev.goblingroup.uzworks.vm.AuthViewModelFactory

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SignUpFragment"

    private lateinit var authViewModel: AuthViewModel
    private lateinit var networkHelper: NetworkHelper

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            continueBtn.setOnClickListener {
                continueClicked()

            }

            signUpWithGoogleBtn.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "In this stage should be google sign in",
                    Toast.LENGTH_SHORT
                ).show()
            }

            signInTv.setOnClickListener {
                findNavController().popBackStack()
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            fullNameEt.addTextChangedListener {
                if (fullNameErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    fullNameErrorLayout.visibility = View.GONE
                    fullNameEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            usernameEt.addTextChangedListener {
                if (usernameErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    usernameErrorLayout.visibility = View.GONE
                    usernameEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            passwordEt.addTextChangedListener {
                if (passwordErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    passwordErrorLayout.visibility = View.GONE
                    passwordEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                }
            }

            confirmPasswordEt.addTextChangedListener {
                if (confirmPasswordErrorLayout.visibility == View.VISIBLE && it.toString()
                        .isNotEmpty()
                ) {
                    confirmPasswordErrorLayout.visibility = View.GONE
                    confirmPasswordEt.setBackgroundResource(R.drawable.registration_edit_text_background)
                    confirmPasswordErrorTv.text = "Confirm password should enter"
                }
            }

            passwordEyeIv.setOnClickListener {
                passwordEt.showHidePassword(requireContext(), passwordEyeIv)
            }

            confirmPasswordEyeIv.setOnClickListener {
                confirmPasswordEt.showHidePassword(requireContext(), confirmPasswordEyeIv)
            }

            fullNameEt.onFocusChangeListener =
                OnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        motionLayout.setTransitionDuration(500)
                        motionLayout.transitionToEnd()
                        motionLayout.setTransitionDuration(1000)
                    }
                }
        }
    }

    private fun continueClicked() {
        binding.apply {
            if (
                fullNameEt.text.toString().isNotEmpty() &&
                usernameEt.text.toString().isNotEmpty() &&
                passwordEt.text.toString().isNotEmpty() &&
                confirmPasswordEt.text.toString().isNotEmpty()
            ) {
                /**
                 * check if password and confirm passwords are equal
                 */
                if (confirmPasswordEt.text.toString() != passwordEt.text.toString()) {
                    confirmPasswordErrorTv.text = "Please confirm the password"
                    confirmPasswordErrorLayout.visibility = View.VISIBLE
                    confirmPasswordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                } else {
                    signUp()
                    /*findNavController().navigate(
                        resId = R.id.selectRoleFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )*/
                }
            } else {
                if (fullNameEt.text.toString().isEmpty()) {
                    fullNameErrorLayout.visibility = View.VISIBLE
                    fullNameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                }
                if (usernameEt.text.toString().isEmpty()) {
                    usernameErrorLayout.visibility = View.VISIBLE
                    usernameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                }
                if (passwordEt.text.toString().isEmpty()) {
                    passwordErrorLayout.visibility = View.VISIBLE
                    passwordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                }
                if (confirmPasswordEt.text.toString().isEmpty()) {
                    confirmPasswordErrorLayout.visibility = View.VISIBLE
                    confirmPasswordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                }
            }
        }
    }

    private fun signUp() {
        networkHelper = NetworkHelper(requireContext())
        authViewModel = ViewModelProvider(
            requireActivity(),
            AuthViewModelFactory(
                authService = ApiClient.authService,
                networkHelper = networkHelper,
                signupRequest = SignupRequest()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}