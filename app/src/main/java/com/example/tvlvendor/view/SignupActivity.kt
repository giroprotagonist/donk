package com.rideyrbike1.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.rideyrbike1.app.R
import com.rideyrbike1.app.viewmodel.SignUpViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {

    private var viewModel: SignUpViewModel = SignUpViewModel()
    private lateinit var email: TextView
    private lateinit var name: TextView
    private lateinit var password: TextView
    private lateinit var repassword: TextView
    private lateinit var cnic: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        FirebaseApp.initializeApp(this)
        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        password = findViewById(R.id.pass)
        repassword = findViewById(R.id.cpass)
        cnic = findViewById(R.id.cnic)
        phoneNumber = findViewById(R.id.phone)
        progressBar = findViewById(R.id.progressBar)
        btn= findViewById(R.id.signUp)
        val login = findViewById<View>(R.id.login)

        viewModel.result.observe(this, Observer {
            progressBar.visibility = View.GONE
            name.isEnabled = true
            email.isEnabled = true
            password.isEnabled = true
            repassword.isEnabled = true
            cnic.isEnabled = true
            phoneNumber.isEnabled = true
            progressBar.isEnabled = true
            btn.isEnabled = true
            val value  = Intent(this, LoginActivity::class.java)
            value.putExtra("user", FirebaseAuth.getInstance().currentUser?.uid)
            value.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(value)
        })

        btn.setOnClickListener{
            onSignUp(it)
        }
        login.setOnClickListener{
            onSignIn(it)
        }
    }

    fun onSignUp(view: View) {

        if (!email.text.contains("@"))
            email.error = "Enter valid email"
        else if (password.text.length < 8 && !password.text.equals(repassword.text))
            password.error = "Invalid Passwords"
        else {
            progressBar.visibility = View.VISIBLE
            name.isEnabled = false
            email.isEnabled = false
            password.isEnabled = false
            repassword.isEnabled = false
            cnic.isEnabled = false
            phoneNumber.isEnabled = false
            progressBar.isEnabled = false
            btn.isEnabled = false
            viewModel.createAccount(
                email.text.toString(), password.text.toString(), name.text.toString(),
                cnic.text.toString(), phoneNumber.text.toString(), this
            )
        }
    }

    fun onSignIn(view: View) {
        val value  = Intent(this, LoginActivity::class.java)
        startActivity(value)
    }
}