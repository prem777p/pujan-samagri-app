package com.prem.pujansamagri.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.prem.pujansamagri.R
import com.prem.pujansamagri.adapter.ParcheListAdapter
import com.prem.pujansamagri.databinding.ActivityNewParcheBinding
import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.PredefineParche
import com.prem.pujansamagri.models.UserInfo
import com.prem.pujansamagri.service.LocalParcheStorageImpl
import com.prem.pujansamagri.service.VibrationHaptics

class NewParche : AppCompatActivity() {
    private lateinit var binding: ActivityNewParcheBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = getColor(R.color.jacarta_matte_black)
        binding = ActivityNewParcheBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().

            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            )
            {
                windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }


        val selectedFlag = intent.getBooleanExtra("FLAG_EDIT", false)

        val samanList = LocalParcheStorageImpl(this,"Parche")
            .getParcheList(PredefineParche.SAMAN_LIST.name.plus(".txt"))

        if (selectedFlag) {
            val selectedItemList = intent.getParcelableArrayListExtra("SELECT_ITEM", Item::class.java)!!
            samanList.forEach { samanItem ->
                val selectedItem = selectedItemList.find { it.name == samanItem.name }
                if (selectedItem != null) {
                    samanItem.isChecked = selectedItem.isChecked
                    samanItem.quantity = selectedItem.quantity
                    samanItem.unit = selectedItem.unit
                }
            }

            selectedItemList.forEach { samanItem ->
                val existItem = samanList.find { it.name == samanItem.name }
                if (existItem == null) {
                    samanList.add(samanItem)
                }
            }
        }

        val userInfo = intent.getSerializableExtra("userinfo", UserInfo::class.java)

        val adapter = ParcheListAdapter(this, samanList) { isAnyItemChecked ->
            binding.nextBtn.visibility = if (isAnyItemChecked) View.VISIBLE else View.INVISIBLE
        }

        binding.samagriListRv.adapter = adapter
        binding.samagriListRv.layoutManager = LinearLayoutManager(this)

        binding.nextBtn.setOnClickListener {
            VibrationHaptics.vibration(this)

            if (selectedFlag) {
                val intent = Intent()
                intent.putParcelableArrayListExtra("SELECT_ITEM", adapter.getCheckedItemList())
                setResult(Activity.RESULT_OK, intent)
                finish() // Close this activity and return to CanvasActivity
            } else {
                val intent = Intent(this, SelectedItemEdit::class.java).apply {
                    putParcelableArrayListExtra(
                        "selectedItemList",
                        adapter.getCheckedItemList()
                    )
                    putExtra("userinfo", userInfo)
                }
                startActivity(intent)
            }
        }

        binding.backBtn.setOnClickListener {
            VibrationHaptics.vibration(this)
            onBackPressedDispatcher.onBackPressed()
        }

        binding.addNewItemFloatingBtn.setOnClickListener {
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setView(R.layout.item_dialog)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            val addBtn = dialog.findViewById<MaterialButton>(R.id.add_btn)
            dialog.findViewById<TextView>(R.id.textView1)?.visibility = View.GONE
            dialog.findViewById<TextInputLayout>(R.id.textInputLayout1)?.visibility = View.GONE
            dialog.findViewById<LinearLayout>(R.id.unit_ll)?.visibility = View.GONE
            val textInputLayout = dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
            textInputLayout?.boxStrokeColor =getColor(R.color.jacarta_matte_black)

            val itemEdt =  dialog.findViewById<EditText>(R.id.item_edtv)
            itemEdt?.addTextChangedListener(object: TextWatcher{
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayout?.isErrorEnabled = false
                }
                override fun afterTextChanged(s: Editable?) {}
            })


            addBtn?.setBackgroundColor(getColor(R.color.jacarta_matte_black))
            addBtn?.setOnClickListener {
                val item: String = itemEdt?.text.toString()
                if (item.isNotEmpty()) {
                    if (samanList.find { it.name == item } == null){
                        samanList.add(Item(item,true))
                        adapter.notifyItemChanged(samanList.size - 1)
                        adapter.onCheckBoxChange()

                        LocalParcheStorageImpl(this, "Parche").saveSamanListItem(item)

                        dialog.cancel()
                    }else{
                        textInputLayout?.error= "Item Already Exist!"
                    }
                } else {
                    Toast.makeText(this, "Enter Item", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Add SearchView Listener
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter((newText ?: "").toString())
                return false
            }

        })
    }
}