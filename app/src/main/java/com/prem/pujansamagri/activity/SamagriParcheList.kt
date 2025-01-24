package com.prem.pujansamagri.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.prem.pujansamagri.R
import com.prem.pujansamagri.adapter.ParcheListCardAdapter
import com.prem.pujansamagri.databinding.ActivitySamagriParcheListBinding
import com.prem.pujansamagri.models.PredefineParche
import com.prem.pujansamagri.service.LocalParcheStorageImpl


class SamagriParcheList : AppCompatActivity() {

    private lateinit var binding: ActivitySamagriParcheListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        binding = ActivitySamagriParcheListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val predefineParcheList = HashSet<String>()

        PredefineParche.entries.onEach {
            if (!it.name.equals("SAMAN_LIST", true)) {
                predefineParcheList.add(it.name)
            }
        }

        LocalParcheStorageImpl(this, "Parche").getAllParche()?.onEach {
            val its = it.split(".")[0]
            if (!its.equals("SAMAN_LIST", true)) {
                predefineParcheList.add(its)
            }
        }

        val parcheList = ArrayList<String>(predefineParcheList)
        binding.parcheRv.adapter = ParcheListCardAdapter(this, parcheList)
        binding.parcheRv.layoutManager = LinearLayoutManager(this)


        binding.searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrEmpty()) {
                    val filteredList = parcheList.filter { it.contains(s, ignoreCase = true) }
                    binding.searchRv.layoutManager = LinearLayoutManager(baseContext)
                    binding.searchRv.adapter = ParcheListCardAdapter(this@SamagriParcheList, filteredList as ArrayList<String>)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

}
