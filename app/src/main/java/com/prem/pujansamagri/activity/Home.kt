package com.prem.pujansamagri.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.prem.pujansamagri.databinding.ActivityHomeBinding
import com.prem.pujansamagri.service.LocalParcheStorageImpl

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val localParcheStorageImpl = LocalParcheStorageImpl(this, "Parche")
        localParcheStorageImpl.initializePreDefineParche()


        binding.newparchaBtn.setOnClickListener {
            startActivity(Intent(this, HeaderFooter::class.java))
        }

        binding.selectparchaBtn.setOnClickListener {
            startActivity(Intent(this, SamagriParcheList::class.java))
        }
    }
}