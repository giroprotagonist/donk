package com.rideyrbike0.app.Auth

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog
import com.rideyrbike0.app.Helpers.Constants
import com.rideyrbike0.app.Helpers.EventIntent
import com.rideyrbike0.app.MainActivity
import com.rideyrbike0.app.R
import org.greenrobot.eventbus.EventBus
import java.text.SimpleDateFormat
import java.util.*

class SignUp(private val context: Context) {

    private var auth: FirebaseAuth = Firebase.auth

    fun runUI() {
        val dialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = "Sign Up")
            customView(R.layout.bottom_sheet_sign_up, scrollable = true, horizontalPadding = true)

            positiveButton(text = "Sign Up") { dialog ->
                val user = hashMapOf<String, Any>()
                user["username"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etUsername).text.toString()
                user["email"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etEmail).text.toString()
                user["password"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etPassword).text.toString()
                user["cPassword"] =
                    dialog.getCustomView().findViewById<EditText>(R.id.etCPassword).text.toString()
                user["createdAt"] =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                user["updatedAt"] = user["createdAt"] as Any

                if (checkValues(user, dialog)) {
                    val sweetLoading = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                    sweetLoading.progressHelper.barColor =
                        context.resources.getColor(R.color.amber_600, context.theme)
                    sweetLoading.titleText = "Please wait..."
                    sweetLoading.setCancelable(false)
                    sweetLoading.show()

                    auth.createUserWithEmailAndPassword(
                        user["email"].toString(),
                        user["password"].toString()
                    ).addOnSuccessListener {
                        /** Remove Password
                         *  Not Save to Database Realtime or FireStore
                         */
                        user.remove("cPassword")
                        user.remove("password")

                        /** Save To Realtime Database */
                        onSaveToRealtimeDatabase(user, dialog, sweetLoading)
                        /** End Realtime Database */

                        Log.d("Create-User", "Success")
                    }.addOnFailureListener { err ->
                        sweetLoading.titleText = "Oops!"
                        sweetLoading.contentText = err.message!!
                        sweetLoading.setCanceledOnTouchOutside(true)
                        sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                        sweetLoading.findViewById<Button>(R.id.confirm_button).isVisible = false

                        Log.w("Create-User", "${err.message}")
                    }
                }
            }
            negativeButton { dismiss() }
            noAutoDismiss()
        }

        onTextWatcher(dialog)
    }

    private fun onSaveToRealtimeDatabase(
        user: HashMap<String, Any>,
        dialog: MaterialDialog,
        sweetLoading: SweetAlertDialog
    ) {
        val userRef = Firebase.database.getReference(Constants.USER_REFERENCE)
        auth.currentUser?.let {
            userRef.child(it.uid).setValue(user)
                .addOnSuccessListener {
                    /** Save To FireStore */
                    onSaveToFireStoreDatabase(user, dialog, sweetLoading)
                    /** End FireStore */

                    Log.d("Create-User Database-RealTime", "Success")
                }
                .addOnFailureListener { err ->
                    sweetLoading.titleText = "Oops!"
                    sweetLoading.contentText = err.message!!
                    sweetLoading.setCanceledOnTouchOutside(true)
                    sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    sweetLoading.findViewById<Button>(R.id.confirm_button).isVisible = false

                    Log.w("Create-User Database-RealTime", "${err.message}")
                }
        }
    }

    private fun onSaveToFireStoreDatabase(
        user: HashMap<String, Any>,
        dialog: MaterialDialog,
        sweetLoading: SweetAlertDialog
    ) {
        val collection = Firebase.firestore.collection(Constants.USER_COLLECTION)
        auth.currentUser?.let {
            collection.document(it.uid).set(user)
                .addOnSuccessListener {
                    sweetLoading.dismissWithAnimation()
                    onClearText(dialog)

                    Toast.makeText(
                        context,
                        "Register has been successfully!",
                        Toast.LENGTH_SHORT
                    ).show()

                    /** If Success
                     * - insert to Realtime Database
                     * - insert to FireStore Collection
                     * go to MainClass */
                    EventBus.getDefault().postSticky(EventIntent(isIntent = true))

                    Log.d("Create-User Collection-FireStore", "Success")
                }
                .addOnFailureListener { err ->
                    sweetLoading.titleText = "Oops!"
                    sweetLoading.contentText = err.message!!
                    sweetLoading.setCanceledOnTouchOutside(true)
                    sweetLoading.changeAlertType(SweetAlertDialog.WARNING_TYPE)
                    sweetLoading.findViewById<Button>(R.id.confirm_button).isVisible = false

                    Log.w("Create-User Collection-FireStore", "${err.message}")
                }
        }
    }

    private fun onClearText(dialog: MaterialDialog) {
        dialog.getCustomView().findViewById<EditText>(R.id.etUsername).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etEmail).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etPassword).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etCPassword).text = null
        dialog.getCustomView().findViewById<EditText>(R.id.etCPassword).clearFocus()
    }

    private fun onTextWatcher(dialog: MaterialDialog) {
        dialog.getCustomView().findViewById<EditText>(R.id.etUsername)
            .doOnTextChanged { text, _, _, _ ->
                if (text!!.isNotEmpty()) dialog.getCustomView()
                    .findViewById<TextInputLayout>(R.id.tilUsername).error = null
            }
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
        dialog.getCustomView().findViewById<EditText>(R.id.etCPassword)
            .doOnTextChanged { text, _, _, _ ->
                if (text!!.isNotEmpty()) dialog.getCustomView()
                    .findViewById<TextInputLayout>(R.id.tilCPassword).error = null
            }
    }

    private fun checkValues(user: HashMap<String, Any>, dialog: MaterialDialog): Boolean {
        if (user["username"].toString().isEmpty()) {
            dialog.getCustomView().findViewById<TextInputLayout>(R.id.tilUsername).error =
                "Username is required!"
            return false
        }
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

        if (user["cPassword"].toString() != user["password"].toString()) {
            dialog.getCustomView().findViewById<TextInputLayout>(R.id.tilCPassword).error =
                "Confirm password is incorrect!"
            return false
        }

        return true
    }
}