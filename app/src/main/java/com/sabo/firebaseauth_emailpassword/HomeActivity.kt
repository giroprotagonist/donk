package com.rideyrbike0.app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rideyrbike0.app.Auth.SignIn
import com.rideyrbike0.app.Auth.SignUp
import com.rideyrbike0.app.Helpers.EventIntent
import com.rideyrbike0.app.databinding.ActivityHomeBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        if (auth.currentUser != null) EventBus.getDefault().postSticky(EventIntent(isIntent = true))

        binding.btnLogin.setOnClickListener { SignIn(this).runUI() }
        binding.btnRegister.setOnClickListener { SignUp(this).runUI() }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    fun onEventIntent(event: EventIntent){
        if (event.isIntent){
            startActivity(Intent(this, MainActivity::class.java))
            finish()

            event.isIntent = false
        }
    }
}