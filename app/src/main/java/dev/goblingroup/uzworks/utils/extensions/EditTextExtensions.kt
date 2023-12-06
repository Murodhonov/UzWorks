package dev.goblingroup.uzworks.utils.extensions

import android.content.Context
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.imageview.ShapeableImageView
import dev.goblingroup.uzworks.R

fun EditText.showHidePassword(
    context: Context,
    eyeIcon: ImageView
) {
    if (this.transformationMethod == PasswordTransformationMethod.getInstance()) {
        /**
         * from password mode to simple mode
         * should open eyes
         */
        this.transformationMethod = HideReturnsTransformationMethod.getInstance()
        this.setSelection(this.text.toString().length)
        this.setTextColor(context.getColor(R.color.black_blue))
        eyeIcon.setImageResource(R.drawable.ic_open_eye)
    } else {
        /**
         * from simple mode to password mode
         * should close eyes
         */
        this.transformationMethod = PasswordTransformationMethod.getInstance()
        this.setSelection(this.text.toString().length)
        this.setTextColor(context.getColor(R.color.black_blue_60))
        eyeIcon.setImageResource(R.drawable.ic_closed_eye)
    }
}