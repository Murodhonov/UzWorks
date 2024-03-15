package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.AuthDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.utils.LanguageEnum
import dev.goblingroup.uzworks.utils.LanguageManager
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

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
                        usernameEt.error = resources.getString(R.string.enter_username)
                        usernameEt.isErrorEnabled = true
                    }
                    if (passwordEt.editText?.text.toString().isEmpty()) {
                        passwordEt.error = resources.getString(R.string.enter_password)
                        passwordEt.isErrorEnabled = true
                    }
                }
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

            languageTv.text = when (loginViewModel.getLanguageCode()) {
                LanguageEnum.LATIN_UZB.code -> {
                    LanguageEnum.LATIN_UZB.languageName
                }

                LanguageEnum.KIRILL_UZB.code -> {
                    LanguageEnum.KIRILL_UZB.languageName
                }

                LanguageEnum.RUSSIAN.code -> {
                    LanguageEnum.RUSSIAN.languageName
                }

                LanguageEnum.ENGLISH.code -> {
                    LanguageEnum.ENGLISH.languageName
                }

                else -> {
                    ""
                }
            }
        }
    }

    private fun login() {
        binding.apply {
            lifecycleScope.launch {
                loginViewModel.login(
                    loginRequest = LoginRequest(
                        username = usernameEt.editText?.text.toString(),
                        password = passwordEt.editText?.text.toString()
                    )
                )
                    .observe(viewLifecycleOwner
                    ) {
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
                dialogTv.text = resources.getString(R.string.incorrect_username_password)
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
        languageDialog(
            loginViewModel.getLanguageCode(),
            requireContext(),
            layoutInflater,
            object : LanguageSelectionListener {
                override fun onLanguageSelected(languageCode: String?, languageName: String?) {
                    binding.languageTv.text = languageName
                    loginViewModel.setLanguageCode(languageCode)
                    LanguageManager.setLanguage(languageCode.toString(), requireContext())
                    updateTexts()
                }

                override fun onCanceled() {

                }
            })
    }

    private fun updateTexts() {
        binding.apply {
            greetingTv.text = resources.getString(R.string.welcome)
            underGreetingTv.text = resources.getString(R.string.lorem_ipsum)
            usernameEt.hint = resources.getString(R.string.username)
            passwordEt.hint = resources.getString(R.string.password)
            loginBtn.text = resources.getString(R.string.login)
            doNotHaveAccountTv.text = resources.getString(R.string.do_not_have_account)
            signUpTv.text = resources.getString(R.string.create_account)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}