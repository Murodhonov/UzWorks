package dev.goblingroup.uzworks.fragments.auth.login

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.utils.ApiStatus
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.NetworkHelper
import dev.goblingroup.uzworks.utils.UserRole
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class LoginFragment : Fragment(), CoroutineScope {

    private val TAG = "LoginFragment"

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

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
            signUpTv.setOnClickListener {
                findNavController().navigate(
                    resId = R.id.selectRoleFragment,
                    args = null,
                    navOptions = getNavOptions()
                )
            }

            loginBtn.setOnClickListener {
                if (usernameEt.editText?.text.toString()
                        .isNotEmpty() && passwordEt.editText?.text.toString()
                        .isNotEmpty()
                ) {
                    login()
                } else {
                    if (usernameEt.editText?.text.toString().isEmpty()) {
                        usernameEt.error = "Username should be entered"
                        usernameEt.isErrorEnabled = true
                    }
                    if (passwordEt.editText?.text.toString().isEmpty()) {
                        passwordEt.error = "Password should be entered"
                        passwordEt.isErrorEnabled = true
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

            languageBtn.setOnClickListener {
                chooseLanguage()
            }

            languageTv.setOnClickListener {
                chooseLanguage()
            }

            usernameEt.editText?.addTextChangedListener {
                if (usernameEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    usernameEt.isErrorEnabled = false
                }
            }

            passwordEt.editText?.addTextChangedListener {
                if (passwordEt.isErrorEnabled && it.toString().isNotEmpty()) {
                    passwordEt.isErrorEnabled = false
                }
            }
        }
    }

    private fun login() {
        binding.apply {
            launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        username = usernameEt.editText?.text.toString(),
                        password = passwordEt.editText?.text.toString()
                    )
                )
                    .collect {
                        when (it) {
                            is ApiStatus.Error -> {
                                loginError(it.error)
                            }

                            is ApiStatus.Loading -> {
                                loginLoading()
                            }

                            is ApiStatus.Success -> {
                                loginSuccess(it.response as LoginResponse)
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
            authDialog.setCancelable(false)
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
            resId = if (loginResponse.access.contains(UserRole.SUPER_ADMIN.roleName)) R.id.adminPanelFragment else R.id.homeFragment,
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