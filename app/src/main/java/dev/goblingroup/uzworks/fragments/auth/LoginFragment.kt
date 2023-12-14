package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogItemBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.networking.NetworkHelper
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog
import dev.goblingroup.uzworks.vm.AuthResource
import dev.goblingroup.uzworks.vm.AuthViewModel
import dev.goblingroup.uzworks.vm.AuthViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginFragment : Fragment(), CoroutineScope {

    private val TAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var authViewModel: AuthViewModel
    private lateinit var networkHelper: NetworkHelper

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogItemBinding: LoadingDialogItemBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            passwordEt.transformationMethod = PasswordTransformationMethod.getInstance()

            signUpTv.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.signUpFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            loginBtn.setOnClickListener {
                if (usernameEt.text.toString().isNotEmpty() && passwordEt.text.toString()
                        .isNotEmpty()
                ) {
                    login()
                } else {
                    if (usernameEt.text.toString().isEmpty()) {
                        usernameErrorLayout.visibility = View.VISIBLE
                        usernameEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    }
                    if (passwordEt.text.toString().isEmpty()) {
                        passwordErrorLayout.visibility = View.VISIBLE
                        passwordEt.setBackgroundResource(R.drawable.error_edit_text_background)
                    }
                }
            }

            signInWithGoogleBtn.setOnClickListener {
                Toast.makeText(
                    requireContext(),
                    "In this stage should be google sign in",
                    Toast.LENGTH_SHORT
                ).show()
            }

            eyeIv.setOnClickListener {
                passwordEt.showHidePassword(requireContext(), eyeIv)
            }

            languageBtn.setOnClickListener {
                chooseLanguage()
            }

            languageTv.setOnClickListener {
                chooseLanguage()
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

            networkHelper = NetworkHelper(requireContext())

            authViewModel = ViewModelProvider(
                requireActivity(),
                AuthViewModelFactory(
                    authService = ApiClient.authService,
                    networkHelper = networkHelper,
                    loginRequest = LoginRequest(
                        username = usernameEt.text.toString(),
                        password = passwordEt.text.toString()
                    )
                )
            )[AuthViewModel::class.java]
        }
    }

    private fun login() {
        launch {
            authViewModel.login()
                .collect {
                    when (it) {
                        is AuthResource.AuthError -> {
                            authError(it)
                        }

                        is AuthResource.AuthLoading -> {
                            authLoading()
                        }

                        is AuthResource.AuthSuccess -> {
                            authSuccess()
                        }
                    }
                }
        }
    }

    private fun authError(authError: AuthResource.AuthError<Unit>) {
        Log.e(TAG, "login: error ${authError.authError}")
        Log.e(TAG, "login: error $authError")
        loadingDialogItemBinding.apply {
            progressBar.visibility = View.INVISIBLE
            errorIv.visibility = View.VISIBLE
            dialogTv.text = "Username yoki password xato"
            closeDialog.visibility = View.VISIBLE
            closeDialog.setOnClickListener {
                loadingDialog.dismiss()
            }
        }
    }

    private fun authLoading() {
        loadingDialog = AlertDialog.Builder(requireContext()).create()
        loadingDialogItemBinding = LoadingDialogItemBinding.inflate(layoutInflater)
        loadingDialog.setView(loadingDialogItemBinding.root)
        loadingDialog.setCancelable(false)
        loadingDialog.show()
    }

    private fun authSuccess() {
        loadingDialog.dismiss()
        findNavController().navigate(
            resId = R.id.homeFragment,
            args = null,
            navOptions = getNavOptions()
        )
    }

    private fun chooseLanguage() {
        languageDialog(requireContext(), layoutInflater, object : LanguageSelectionListener {
            override fun onLanguageSelected(language: String?) {
                binding.languageTv.text = language
            }
        })
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}