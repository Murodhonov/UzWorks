package dev.goblingroup.uzworks.fragments.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentSignUpBinding
import dev.goblingroup.uzworks.utils.extensions.showHidePassword
import dev.goblingroup.uzworks.utils.getNavOptions

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val TAG = "SignUpFragment"

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
                nextPage()
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
                }
            }

            usernameEt.addTextChangedListener {
                if (usernameErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    usernameErrorLayout.visibility = View.GONE
                }
            }

            passwordEt.addTextChangedListener {
                if (passwordErrorLayout.visibility == View.VISIBLE && it.toString().isNotEmpty()) {
                    passwordErrorLayout.visibility = View.GONE
                }
            }

            confirmPasswordEt.addTextChangedListener {
                if (confirmPasswordErrorLayout.visibility == View.VISIBLE && it.toString()
                        .isNotEmpty()
                ) {
                    confirmPasswordErrorLayout.visibility = View.GONE
                    confirmPasswordErrorTv.text = "Confirm password should enter"
                }
            }

            passwordEyeIv.setOnClickListener {
                passwordEt.showHidePassword(requireContext(), passwordEyeIv)
            }

            confirmPasswordEyeIv.setOnClickListener {
                confirmPasswordEt.showHidePassword(requireContext(), confirmPasswordEyeIv)
            }
        }
    }

    private fun nextPage() {
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
                } else {
                    findNavController().navigate(
                        resId = R.id.selectRoleFragment,
                        args = null,
                        navOptions = getNavOptions()
                    )
                }
            } else {
                if (fullNameEt.text.toString().isEmpty()) {
                    fullNameErrorLayout.visibility = View.VISIBLE
                }
                if (usernameEt.text.toString().isEmpty()) {
                    usernameErrorLayout.visibility = View.VISIBLE
                }
                if (passwordEt.text.toString().isEmpty()) {
                    passwordErrorLayout.visibility = View.VISIBLE
                }
                if (confirmPasswordEt.text.toString().isEmpty()) {
                    confirmPasswordErrorLayout.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}