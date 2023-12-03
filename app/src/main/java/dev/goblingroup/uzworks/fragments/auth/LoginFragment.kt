package dev.goblingroup.uzworks.fragments.auth

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentLoginBinding
import dev.goblingroup.uzworks.utils.LanguageSelectionListener
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions
import dev.goblingroup.uzworks.utils.languageDialog

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

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
                    findNavController().navigate(
                        resId = R.id.homeFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )
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
        }
    }

    private fun chooseLanguage() {
        languageDialog(requireContext(), layoutInflater, object : LanguageSelectionListener {
            override fun onLanguageSelected(language: String?) {
                binding.languageTv.text = language
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}