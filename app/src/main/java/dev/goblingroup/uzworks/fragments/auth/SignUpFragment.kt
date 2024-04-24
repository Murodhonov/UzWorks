package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.request.SignUpRequest
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.utils.splitFullName
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import dev.goblingroup.uzworks.vm.SharedSignUpViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingBinding: LoadingDialogBinding

    private lateinit var userRole: String

    private val sharedSignUpViewModel: SharedSignUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            userRole = arguments?.getString("user role")!!
            sharedSignUpViewModel.controlInput(
                fullNameEt,
                usernameEt,
                passwordEt,
                confirmPasswordEt,
                motionLayout
            )

            continueBtn.setOnClickListener {
                if (sharedSignUpViewModel.isFormValid(
                        fullNameEt,
                        usernameEt,
                        passwordEt,
                        confirmPasswordEt,
                        resources
                    )
                ) {
                    signUp()
                }
            }

            signInTv.setOnClickListener {
                findNavController().popBackStack(
                    R.id.loginFragment,
                    false
                )
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.setView(loadingBinding.root)
        }
    }

    private fun signUp() {
        binding.apply {
            lifecycleScope.launch {
                val (firstName, lastName) = fullNameEt.splitFullName()
                sharedSignUpViewModel.signup(
                    signupRequest = SignUpRequest(
                        username = usernameEt.editText?.text.toString(),
                        password = passwordEt.editText?.text.toString(),
                        confirmPassword = confirmPasswordEt.editText?.text.toString(),
                        firstName = firstName.toString(),
                        lastName = lastName.toString(),
                        role = userRole
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {
                            signupError(it.error)
                        }

                        is ApiStatus.Loading -> {
                            signupLoading()
                        }

                        is ApiStatus.Success -> {
                            signupSuccess()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun signupError(signUpError: Throwable) {
        binding.apply {
            loadingBinding.apply {
                progress.visibility = View.INVISIBLE
                resultIv.setImageResource(R.drawable.ic_error)
                resultIv.visibility = View.VISIBLE
                dialogMessageTv.text = resources.getString(R.string.username_exist)
                close.text = resources.getString(R.string.close)
                close.visibility = View.VISIBLE
                close.setOnClickListener {
                    loadingDialog.dismiss()
                }
            }
        }
    }

    private fun signupLoading() {
        loadingBinding.apply {
            resultIv.visibility = View.GONE
            progress.visibility = View.VISIBLE
            dialogMessageTv.text = resources.getString(R.string.loading)
            close.visibility = View.GONE
            if (!loadingDialog.isShowing) {
                loadingDialog.show()
            }
        }
    }

    private fun signupSuccess() {
        binding.apply {
            lifecycleScope.launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        usernameEt.editText?.text.toString(),
                        passwordEt.editText?.text.toString()
                    )
                ).observe(viewLifecycleOwner) {
                    when (it) {
                        is ApiStatus.Error -> {

                        }

                        is ApiStatus.Loading -> {

                        }

                        is ApiStatus.Success -> {
                            loadingDialog.dismiss()
                            findNavController().navigate(
                                resId = R.id.action_signUpFragment_to_succeedFragment,
                                args = null
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            sharedSignUpViewModel.fullName.observe(viewLifecycleOwner) {
                fullNameEt.editText?.setText(it)
            }
            sharedSignUpViewModel.username.observe(viewLifecycleOwner) {
                usernameEt.editText?.setText(it)
            }
            sharedSignUpViewModel.password.observe(viewLifecycleOwner) {
                passwordEt.editText?.setText(it)
            }
            sharedSignUpViewModel.confirmPassword.observe(viewLifecycleOwner) {
                confirmPasswordEt.editText?.setText(it)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        binding.apply {
            sharedSignUpViewModel.setFullName(fullNameEt.editText?.text.toString())
            sharedSignUpViewModel.setUsername(usernameEt.editText?.text.toString())
            sharedSignUpViewModel.setPassword(passwordEt.editText?.text.toString())
            sharedSignUpViewModel.setConfirmPassword(confirmPasswordEt.editText?.text.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}