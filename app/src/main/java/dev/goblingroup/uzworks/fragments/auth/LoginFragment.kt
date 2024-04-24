package dev.goblingroup.uzworks.fragments.auth

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.LoginViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

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
                    resId = R.id.action_loginFragment_to_selectRoleFragment,
                    args = null
                )
            }

            loginBtn.setOnClickListener {
                if (loginViewModel.isFormValid(usernameEt, passwordEt, resources)) {
                    login()
                }
            }

            languageBtn.setOnClickListener {
                chooseLanguage()
            }

            languageTv.setOnClickListener {
                chooseLanguage()
            }

            loginViewModel.controlInput(usernameEt, passwordEt)

            languageTv.text = loginViewModel.getLanguageName()

            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.setView(loadingDialogBinding.root)
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
                                loginError()
                            }

                            is ApiStatus.Loading -> {
                                loginLoading()
                            }

                            is ApiStatus.Success -> {
                                loginSuccess()
                            }
                        }
                    }
            }
        }
    }

    private fun loginError() {
        loadingDialogBinding.apply {
            progress.visibility = View.INVISIBLE
            resultIv.setImageResource(R.drawable.ic_error)
            resultIv.visibility = View.VISIBLE
            dialogMessageTv.text = resources.getString(R.string.incorrect_username_password)
            close.text = resources.getString(R.string.close)
            close.visibility = View.VISIBLE
            close.setOnClickListener {
                loadingDialog.dismiss()
            }
        }
    }

    private fun loginLoading() {
        loadingDialogBinding.apply {
            resultIv.visibility = View.GONE
            progress.visibility = View.VISIBLE
            dialogMessageTv.text = resources.getString(R.string.loading)
            close.visibility = View.GONE
            if (!loadingDialog.isShowing) {
                loadingDialog.show()
            }
        }
    }

    private fun loginSuccess() {
        if (loadingDialog.isShowing)
            loadingDialog.dismiss()
        findNavController().navigate(
            resId = R.id.action_loginFragment_to_startFragment,
            args = null
        )
    }

    private fun chooseLanguage() {
        loginViewModel.chooseLanguage(
            requireContext(),
            layoutInflater,
            object : LanguageSelectionListener {
                override fun onLanguageSelected(languageCode: String?, languageName: String?) {
                    binding.languageTv.text = languageName
                    updateTexts()
                }

                override fun onCanceled() {

                }

            }
        )
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