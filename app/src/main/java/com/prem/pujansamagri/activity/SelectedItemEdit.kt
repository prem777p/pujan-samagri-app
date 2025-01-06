package com.prem.pujansamagri.activity

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.prem.pujansamagri.R
import com.prem.pujansamagri.adapter.SelectedItemEditAdapter
import com.prem.pujansamagri.databinding.ActivitySelectedItemEditBinding
import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.UserInfo
import com.prem.pujansamagri.service.LocalParcheStorageImpl
import com.prem.pujansamagri.service.VibrationHaptics

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class SelectedItemEdit : AppCompatActivity() {

    private lateinit var binding: ActivitySelectedItemEditBinding
    private lateinit var adapter: SelectedItemEditAdapter
    private lateinit var selectedItemList: ArrayList<Item>
    private var isFABVisible = false

    private val selectEditItemLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedEditItemList =
                    result.data?.getParcelableArrayListExtra("SELECT_ITEM", Item::class.java)
                if (selectedEditItemList != null) {
                    selectedItemList.clear()
                    selectedItemList.addAll(selectedEditItemList)
                    adapter.setParchaList(selectedEditItemList)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectedItemEditBinding.inflate(LayoutInflater.from(this))
        window.statusBarColor = TypedValue().data
        setContentView(binding.root)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (resources.configuration.uiMode == Configuration.UI_MODE_NIGHT_YES.plus(1)) {
            // If the background is dark, make the status bar text light
            windowInsetsController.isAppearanceLightStatusBars = false
        } else {
            // If the background is light, make the status bar text dark
            windowInsetsController.isAppearanceLightStatusBars = true
        }

        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            // You can hide the caption bar even when the other system bars are visible.
            // To account for this, explicitly check the visibility of navigationBars()
            // and statusBars() rather than checking the visibility of systemBars().

            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())
            ) {
                windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())
            }

            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }

        showFABs()
        hideFABs()

        val editCanvasActivty: Boolean = intent.getBooleanExtra("FLAG_CANVAS", false)
        selectedItemList = if (editCanvasActivty) {
            intent.getParcelableArrayListExtra("EDIT_ITEMS", Item::class.java)!!
        } else {
            intent.extras!!.getParcelableArrayList("selectedItemList", Item::class.java)!!
        }
        val userInfo = intent.getSerializableExtra("userinfo", UserInfo::class.java)


        adapter = SelectedItemEditAdapter(this, selectedItemList) { isAnyItemChecked ->
            binding.nextBtn.visibility = if (isAnyItemChecked) View.VISIBLE else View.INVISIBLE
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.nextBtn.setOnClickListener {
            VibrationHaptics.vibration(this)
            if (editCanvasActivty) {
                val intent = Intent()
                intent.putParcelableArrayListExtra("EDIT_ITEMS", adapter.getItemList())
                setResult(Activity.RESULT_OK, intent)
                finish() // Close this activity and return to CanvasActivity
            } else {
                val intent = Intent(this, ParchaCanvas::class.java).apply {
                    putParcelableArrayListExtra("selectedItemList", adapter.getItemList())
                    putExtra("userinfo", userInfo)
                }
                startActivity(intent)
            }

        }


        binding.backBtn.setOnClickListener {
            VibrationHaptics.vibration(this)
            onBackPressedDispatcher.onBackPressed()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy > 0 && isFABVisible) {
                    // Scrolling down, hide FABs
                    hideFABs()
                } else if (dy < 0 && isFABVisible) {
                    // Scrolling up, show FABs
                    hideFABs()
                }
            }
        })


        binding.parentEditFab.setOnClickListener {
            if (isFABVisible) {
                hideFABs()
            } else {
                showFABs()
            }
        }

        binding.addItemFab.setOnClickListener {
            hideFABs()
            val intent = Intent(this, NewParche::class.java)
            intent.putParcelableArrayListExtra("SELECT_ITEM", selectedItemList)
            intent.putExtra("FLAG_EDIT", true)
            selectEditItemLauncher.launch(intent)
        }

        binding.addNewItemFloatingBtn.setOnClickListener {
            hideFABs()
            val dialog: AlertDialog = AlertDialog.Builder(this)
                .setView(R.layout.item_dialog)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            val addBtn = dialog.findViewById<MaterialButton>(R.id.add_btn)

            val textInputLayout = dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
            textInputLayout?.boxStrokeColor = getColor(R.color.vivid_auburn_matte_black)
            dialog.findViewById<TextInputLayout>(R.id.textInputLayout1)?.boxStrokeColor = getColor(R.color.vivid_auburn_matte_black)

            val itemEdt =  dialog.findViewById<EditText>(R.id.item_edtv)
            itemEdt?.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int,after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    textInputLayout?.isErrorEnabled = false
                }
                override fun afterTextChanged(s: Editable?) {}
            })

            val spinner = dialog.findViewById<Spinner>(R.id.quantity_value_spinner)
            ArrayAdapter.createFromResource(
                this,
                R.array.quantity_notation,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears.
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner.
                spinner?.adapter = adapter
            }

            addBtn?.setOnClickListener {
                val item: String = itemEdt?.text.toString()
                val itemQuantityEdt = dialog.findViewById<EditText>(R.id.quantity_edtv)
                val itemQuantity: String = itemQuantityEdt?.text.toString()
                val itemUnit: String = dialog.findViewById<Spinner>(R.id.quantity_value_spinner)?.selectedItem.toString()

                if (item.isNotEmpty() && itemQuantity.isNotEmpty()) {
                    if (selectedItemList.find { it.name == item } == null) {
                        selectedItemList.add(Item(item, itemQuantity, itemUnit, true))
                        adapter.setParchaList(selectedItemList)

                        LocalParcheStorageImpl(this, "Parche").saveSamanListItem(item)

                        dialog.cancel()
                    }else{
                        textInputLayout?.error= "Item Already Exist!"
                    }
                } else {
                    Toast.makeText(this, "Enter Item Detail", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun hideFABs() {
        isFABVisible = false

        binding.addItemFab.animate()
            .alpha(0f)
            .translationY(100f)
            .setDuration(200)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .withEndAction { binding.addItemFab.visibility = View.GONE }
            .start()

        binding.addNewItemFloatingBtn.animate()
            .alpha(0f)
            .translationY(100f)
            .setDuration(200)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .withEndAction { binding.addNewItemFloatingBtn.visibility = View.GONE }
            .start()
    }

    private fun showFABs() {
        isFABVisible = true

        binding.addItemFab.visibility = View.VISIBLE
        binding.addNewItemFloatingBtn.visibility = View.VISIBLE

        binding.addItemFab.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(200)
            .start()

        binding.addNewItemFloatingBtn.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(200)
            .start()
    }
}