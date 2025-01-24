package com.prem.pujansamagri.activity

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.prem.pujansamagri.databinding.ActivityHeaderFooterBinding
import com.prem.pujansamagri.models.UserInfo
import com.prem.pujansamagri.service.VibrationHaptics

class HeaderFooter : AppCompatActivity() {

    private lateinit var binding: ActivityHeaderFooterBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = ActivityHeaderFooterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.saveBtn.setOnClickListener {
            VibrationHaptics.vibration(this)

            val title = binding.titleEdtv.text.toString()
            val name = binding.nameEdtv.text.toString()
            val phone = binding.phoneNoEdtv.text.toString()
            val userInfo = UserInfo(name, phone, title)

            val intent = Intent(this, NewParche::class.java)
            intent.putExtra("userinfo", userInfo)
            startActivity(intent)

        }
    }
}