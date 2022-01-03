package com.rideyrbike0.app.Auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.rideyrbike0.app.Helpers.EventIntent
import com.rideyrbike0.app.R
import org.greenrobot.eventbus.EventBus
import java.util.*

class SignIn(private val context: Context) {

    private var auth: FirebaseAuth = Firebase.auth

    fun runUI() {
        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = "Sign In")
            customView(R.layout.bottom_sheet_sign_in, scrollable = true, horizontalPadding = true)

            positiveButton(text = "Sign In") { dialog ->
                val user = hashMapOf<String, Any>()
                user["email"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etEmail).text.toString()
                user["password"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etPassword).text.toString()

                if (checkValues(user, dialog)) {
                    val sweetLoading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    sweetLoading.progressHelper.barColor =
                        context.resources.getColor(R.color.amber_600, context.theme)
                    sweetLoading.titleText = "Please wait..."
                    sweetLoading.setCancelable(false)
                    sweetLoading.show()

                    auth.signInWithEmailAndPassword(
                        user["email"].toString(),
                        user["password"].toString()
                    ).addOnSuccessListener {
                        sweetLoading.dismissWithAnimation()
                        onClearText(dialog)
                        dismiss()

                        Toast.makeText(context, "Login successfully!", Toast.LENGTH_SHORT).show()
                        /** If Success
                         * go to MainClass */
                        EventBus.getDefault().postSticky(EventIntent(isIntent = true))

                        Log.d("Login", "Login successfully!")
                    }.addOnFailureListener { err ->
                        sweetLoading.titleText = "Oops!"
                        sweetLoading.contentText = err.message!!
                        sweetLoading.setCanceledOnTouchOutside(true)
                        sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                        sweetLoading.findViewById<Button>(R.id.confirm_button).isVisible = false

                        Log.w("Login", "${err.message}")
                    }
                }
            }
            negativeButton { dismiss() }
            noAutoDismiss()
        }
        onTextWatcher(dialog)
    }

    private fun onClearText(dialog: MaterialDialog) {
        dialog.getCustomView().findViewById<EditText>(R.id.etEmail).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etPassword).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etPassword).clearFocus()
    }

    private fun onTextWatcher(dialog: MaterialDialog) {
        dialog.getCustomView().findViewById<EditText>(R.id.etEmail)
            .doOnTextChanged { text, _, _, _ ->
                if (text!!.isNotEmpty()) dialog.getCustomView()
                    .findViewById<TextInputLayout>(R.id.tilEmail).error = null
            }
        dialog.getCustomView().findViewById<EditText>(R.id.etPassword)
            .doOnTextChanged { text, _, _, _ ->
                if (text!!.isNotEmpty()) dialog.getCustomView()
                    .findViewById<TextInputLayout>(R.id.tilPassword).error = null
            }
    }

    private fun checkValues(user: HashMap<String, Any>, dialog: MaterialDialog): Boolean {
        if (user["email"].toString().isEmpty()) {
            dialog.getCustomView().findViewById<TextInputLayout>(R.id.tilEmail).error =
                "Email is required!"
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(user["email"].toString()).matches()) {
            dialog.getCustomView().findViewById<TextInputLayout>(R.id.tilEmail).error =
                "Invalid email format!"
            return false
        }
        if (user["password"].toString().length < 6) {
            dialog.getCustomView().findViewById<TextInputLayout>(R.id.tilPassword).error =
                "Password is required min. 6 character!"
            return false
        }
        return true
    }


}