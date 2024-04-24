package dev.goblingroup.uzworks.vm

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.goblingroup.uzworks.databinding.NoInternetDialogBinding
import dev.goblingroup.uzworks.utils.ConstValues.TAG
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private lateinit var noInternetDialog: AlertDialog
    private lateinit var noInternetBinding: NoInternetDialogBinding

    fun disconnected(context: Context) {
        Log.d(TAG, "disconnected: ")
        try {
            if (!noInternetDialog.isShowing) {
                noInternetDialog.show()
            }
        } catch (e: Exception) {
            noInternetDialog = AlertDialog.Builder(context).create()
            noInternetBinding = NoInternetDialogBinding.inflate(LayoutInflater.from(context))
            noInternetDialog.setView(noInternetBinding.root)
            noInternetDialog.setCancelable(false)
            noInternetDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            noInternetDialog.show()
        }
    }

    fun connected() {
        Log.d(TAG, "connected: ")
        try {
            if (noInternetDialog.isShowing) {
                noInternetDialog.dismiss()
            }
        } catch (e: Exception) {
            Log.e(TAG, "connected: ${e.message}")
        }
    }
}