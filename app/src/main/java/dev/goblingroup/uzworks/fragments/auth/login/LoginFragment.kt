package dev.goblingroup.uzworks.fragments.auth.login

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.ErrorDialogItemBinding
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.models.request.LoginRequest
import dev.goblingroup.uzworks.models.response.LoginResponse
import dev.goblingroup.uzworks.networking.ApiClient
import dev.goblingroup.uzworks.networking.NetworkHelper
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

    private lateinit var errorDialog: AlertDialog
    private lateinit var errorBinding: ErrorDialogItemBinding

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
            loginViewModelFactory = LoginViewModelFactory(
                ApiClient.authService,
                networkHelper,
                LoginRequest(usernameEt.text.toString(), passwordEt.text.toString())
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
            loginViewModelFactory.loginRequest.username = usernameEt.text.toString()
            loginViewModelFactory.loginRequest.password = passwordEt.text.toString()
            loginViewModel = ViewModelProvider(
                this@LoginFragment,
                loginViewModelFactory
            )[LoginViewModel::class.java]
            Log.d(
                TAG,
                "login: updateCredentials loginRequest in ${this@LoginFragment::class.java.simpleName} ${loginViewModelFactory.loginRequest}"
            )
            launch {
                loginViewModel.login()
                    .observe(viewLifecycleOwner) {
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
            progressBar.visibility = View.INVISIBLE
            val expandAnim =
                AnimationUtils.loadAnimation(requireContext(), R.anim.expand_anim)
            loginBtn.startAnimation(expandAnim)
            loginTv.startAnimation(expandAnim)
            expandAnim.setAnimationListener(object : AnimationListener {
                override fun onAnimationStart(p0: Animation?) {

                }

                override fun onAnimationEnd(p0: Animation?) {
                    loginBtn.visibility = View.VISIBLE
                    loginTv.visibility = View.VISIBLE
                    if (loginResponse == null) {
                        errorDialog = AlertDialog.Builder(requireContext()).create()
                        errorBinding = ErrorDialogItemBinding.inflate(layoutInflater)
                        errorDialog.setView(errorBinding.root)
                        errorDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        errorDialog.show()
                        Log.e(TAG, "login: error $loginError")
                    } else {
                        try {
                            if (errorDialog.isShowing) errorDialog.dismiss()
                        } catch (e: Exception) {
                            Log.e(
                                TAG,
                                "onAnimationEnd: error dialog hasn't been shown yet",
                            )
                        }
                        findNavController().navigate(
                            resId = R.id.homeFragment,
                            args = null,
                            navOptions = getNavOptions()
                        )
                        Log.d(TAG, "loginSuccess: $loginResponse")
                    }
                }

                override fun onAnimationRepeat(p0: Animation?) {

                }

            })
        }
    }

    private fun loginLoading() {
        binding.apply {
            val scaleXLoginBtn = ObjectAnimator.ofFloat(
                loginBtn, View.SCALE_X, resources.getDimension(
                    com.intuit.sdp.R.dimen._34sdp
                ) / loginBtn.width
            )

            val collapseXTv = ObjectAnimator.ofFloat(loginTv, View.SCALE_X, 0f)
            val collapseYTv = ObjectAnimator.ofFloat(loginTv, View.SCALE_Y, 0f)
            loginTv.pivotX = loginTv.width * 0.5f
            loginTv.pivotY = loginTv.height * 0.5f

            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleXLoginBtn, collapseXTv, collapseYTv)
            animatorSet.duration = 400
            animatorSet.start()
            animatorSet.addListener(object : AnimatorListener {
                override fun onAnimationStart(p0: Animator) {

                }

                override fun onAnimationEnd(p0: Animator) {
                    val collapseAnim =
                        AnimationUtils.loadAnimation(requireContext(), R.anim.collapse_anim)
                    loginBtn.startAnimation(collapseAnim)
                    collapseAnim.setAnimationListener(object : AnimationListener {
                        override fun onAnimationStart(p0: Animation?) {

                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            loginBtn.visibility = View.INVISIBLE
                            progressBar.visibility = View.VISIBLE
                        }

                        override fun onAnimationRepeat(p0: Animation?) {

                        }

                    })
                }

                override fun onAnimationCancel(p0: Animator) {

                }

                override fun onAnimationRepeat(p0: Animator) {

                }

            })
        }
    }

    private fun loginSuccess(loginResponse: LoginResponse) {
        stopLoading(loginResponse = loginResponse, loginError = null)
    }

    private fun stopLoading(loginResponse: LoginResponse? = null, loginError: Throwable? = null) {

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