package dev.goblingroup.uzworks.fragments.profile

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.databinding.FragmentUpdatePasswordBinding
import dev.goblingroup.uzworks.databinding.LoadingDialogBinding
import dev.goblingroup.uzworks.models.request.UpdatePasswordRequest
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import dev.goblingroup.uzworks.vm.ApiStatus
import dev.goblingroup.uzworks.vm.UpdatePasswordViewModel

@AndroidEntryPoint
class UpdatePasswordFragment : Fragment() {

    private var _binding: FragmentUpdatePasswordBinding? = null
    private val binding get() = _binding!!

    private val updatePasswordViewModel: UpdatePasswordViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdatePasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            updateBtn.setOnClickListener {
                if (updatePasswordViewModel.isFormValid(
                        resources,
                        oldPasswordEt,
                        newPasswordEt,
                        confirmNewPasswordEt
                    )
                ) {
                    updatePasswordViewModel.updatePassword(
                        UpdatePasswordRequest(
                            confirmPassword = confirmNewPasswordEt.editText?.text.toString(),
                            newPassword = newPasswordEt.editText?.text.toString(),
                            oldPassword = oldPasswordEt.editText?.text.toString(),
                            userId = updatePasswordViewModel.getUserId()
                        )
                    ).observe(viewLifecycleOwner) {
                        when (it) {
                            is ApiStatus.Error -> {
                                hideLoading()
                                oldPasswordEt.isErrorEnabled = true
                                oldPasswordEt.error =
                                    resources.getString(R.string.enter_old_password)
                            }

                            is ApiStatus.Loading -> {
                                loading()
                            }

                            is ApiStatus.Success -> {
                                hideLoading()
                                Toast.makeText(requireContext(), resources.getString(R.string.password_updated), Toast.LENGTH_SHORT).show()
                                findNavController().popBackStack()
                            }
                        }
                    }
                }
            }

            back.setOnClickListener {
                findNavController().popBackStack()
            }

            updatePasswordViewModel.controlInput(
                oldPasswordEt,
                newPasswordEt,
                confirmNewPasswordEt,
                root
            )
        }
    }

    private fun loading() {
        try {
            loadingDialog.show()
        } catch (e: Exception) {
            loadingDialog = AlertDialog.Builder(requireContext()).create()
            loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
            loadingDialog.setView(loadingDialogBinding.root)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.setCancelable(false)
            loadingDialog.show()
        }
    }

    private fun hideLoading() {
        try {
            loadingDialog.dismiss()
        } catch (e: Exception) {
            Log.e(TAG, "hideLoading: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}