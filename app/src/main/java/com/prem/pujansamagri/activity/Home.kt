package com.prem.pujansamagri.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.prem.pujansamagri.databinding.ActivityHomeBinding
import com.prem.pujansamagri.service.LocalParcheStorageImpl

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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