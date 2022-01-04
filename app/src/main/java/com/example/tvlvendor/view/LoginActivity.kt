package com.rideyrbike1.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.rideyrbike1.app.R
import com.rideyrbike1.app.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var loginViewModel: LoginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(FirebaseAuth.getInstance().currentUser!=null){
            val value  = Intent(this, MainActivity::class.java)
            value.putExtra("user", FirebaseAuth.getInstance().currentUser?.uid)
            value.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(value)
        }

        loginViewModel.data.observe(this, Observer {
            if(it != null) {
                val value  = Intent(this, MainActivity::class.java)
                value.putExtra("user", FirebaseAuth.getInstance().currentUser?.uid)
                value.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(value)
            }
            else{
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                val email = findViewById<EditText>(R.id.email);
                val pass = findViewById<EditText>(R.id.pass);
                email.isEnabled = true
                pass.isEnabled = true
            }
        })

        findViewById<View>(R.id.login).setOnClickListener {
            SignIn(it)
        }

        findViewById<View>(R.id.signUp).setOnClickListener {
            onSignup(it)
        }
    }

    fun SignIn(view: View){
        val email = findViewById<EditText>(R.id.email);
        val pass = findViewById<EditText>(R.id.pass);
        if(email.text.contains("@") && pass.text.length >=8){
            email.isEnabled = false
            pass.isEnabled = false
            loginViewModel.login(email.text.toString(), pass.text.toString())
            Log.d ("Main ",this.loginViewModel.data.value?.uid.toString())

        }
        else{
            if(!email.text.contains("@"))
                email.error = "Incorrect email format"
            if(pass.text.length < 8)
                pass.error = "Password can not be less than 8 characters"
        }
    }



    fun onSignup(view: View) {
        val value  = Intent(this, SignupActivity::class.java)
        startActivity(value)
    }
}