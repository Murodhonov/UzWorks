package dev.goblingroup.uzworks.fragments.auth.login

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import dev.goblingroup.uzworks.database.AppDatabase
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.resource.LoginResource
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginFragment : Fragment(), CoroutineScope {

    private val TAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginViewModelFactory: LoginViewModelFactory
    private lateinit var networkHelper: NetworkHelper
    private lateinit var appDatabase: AppDatabase

    private lateinit var authDialog: AlertDialog
    private lateinit var authDialogBinding: AuthDialogItemBinding

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
                    resId = R.id.selectRoleFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            networkHelper = NetworkHelper(requireContext())
            appDatabase = AppDatabase.getInstance(requireContext())
            loginViewModelFactory = LoginViewModelFactory(
                appDatabase = appDatabase,
                authService = ApiClient.authService,
                networkHelper = networkHelper,
                loginRequest = LoginRequest(usernameEt.text.toString(), passwordEt.text.toString()),
                context = requireContext()
            )

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
        }
    }

    private fun login() {
        binding.apply {
            loginViewModelFactory.loginRequest?.username = usernameEt.text.toString()
            loginViewModelFactory.loginRequest?.password = passwordEt.text.toString()
            loginViewModel = ViewModelProvider(
                owner = this@LoginFragment,
                factory = loginViewModelFactory
            )[LoginViewModel::class.java]
            Log.d(
                TAG,
                "login: updateCredentials loginRequest in ${this@LoginFragment::class.java.simpleName} ${loginViewModelFactory.loginRequest}"
            )
            launch {
                loginViewModel.login()
                    .collect {
                        when (it) {
                            is LoginResource.LoginError -> {
                                loginError(it.loginError)
                            }

                            is LoginResource.LoginLoading -> {
                                loginLoading()
                            }

                            is LoginResource.LoginSuccess -> {
                                loginSuccess(it.loginResponse)
                            }
                        }
                    }
            }
        }
    }

    private fun loginError(loginError: Throwable) {
        binding.apply {
            authDialogBinding.apply {
                progressBar.visibility = View.INVISIBLE
                errorIv.visibility = View.VISIBLE
                dialogTv.text = "Incorrect username or password"
                closeDialog.visibility = View.VISIBLE
                closeDialog.setOnClickListener {
                    authDialog.dismiss()
                }
                Log.e(TAG, "loginError: login error $loginError")
                Log.e(TAG, "loginError: login error ${loginError.stackTrace}")
                Log.e(TAG, "loginError: login error ${loginError.printStackTrace()}")
                Log.e(TAG, "loginError: login error ${loginError.message}")
            }
        }
    }

    private fun loginLoading() {
        binding.apply {
            authDialog = AlertDialog.Builder(requireContext()).create()
            authDialogBinding = AuthDialogItemBinding.inflate(layoutInflater)
            authDialog.setView(authDialogBinding.root)
            authDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            authDialogBinding.apply {
                progressBar.visibility = View.VISIBLE
                errorIv.visibility = View.INVISIBLE
                dialogTv.text = "Loading"
                closeDialog.visibility = View.INVISIBLE
            }
            authDialog.show()
        }
    }

    private fun loginSuccess(loginResponse: LoginResponse) {
        authDialog.dismiss()
        findNavController().navigate(
            resId = R.id.homeFragment,
            args = null,
            navOptions = getNavOptions()
        )
        Log.d(TAG, "loginSuccess: login success $loginResponse")
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