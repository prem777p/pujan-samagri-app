package com.prem.pujansamagri.activity

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.prem.pujansamagri.R
import com.prem.pujansamagri.adapter.PageAdapter
import com.prem.pujansamagri.databinding.ActivityCanvasBinding
import com.prem.pujansamagri.models.CanvasStackItem
import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.UserInfo
import com.prem.pujansamagri.service.LocalParcheStorageImpl
import com.prem.pujansamagri.service.PDFDocument
import com.prem.pujansamagri.service.UndoRedoService
import com.prem.pujansamagri.service.VibrationHaptics
import java.io.File


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class ParchaCanvas : AppCompatActivity() {

    private lateinit var binding: ActivityCanvasBinding
    private lateinit var itemList: ArrayList<Item>
    private lateinit var pageAdapter: PageAdapter
    private var userInfo: UserInfo? = null
    private val undoRedoService = UndoRedoService()

    private val editItemLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val editedItemList = result.data?.getParcelableArrayListExtra("EDIT_ITEMS", Item::class.java)

                if (editedItemList != null) {
                    itemList.clear()
                    itemList.addAll(editedItemList)
                    pageAdapter.updatePageContent()

                    undoRedoService.pushUndoStack(CanvasStackItem(ArrayList(editedItemList), UserInfo(userInfo!!)))
                    undoRedoService.clearRedoStack()
                    binding.undoBtn.isEnabled = undoRedoService.undoStackSize() > 1
                    binding.redoBtn.isEnabled = undoRedoService.redoStackSize() > 0
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCanvasBinding.inflate(layoutInflater)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        window.statusBarColor = getColor(R.color.x11_gray)
        setContentView(binding.root)


        val parcheName: String? = intent.getStringExtra("PARCHA_NAME")
        itemList = intent.extras!!.getParcelableArrayList("selectedItemList", Item::class.java)!!
            .filterNotNull() as ArrayList<Item>

        userInfo = intent.getSerializableExtra("userinfo", UserInfo::class.java)


        binding.canvasContainerRv.setLayoutManager(LinearLayoutManager(this))
        binding.toolbar.setNavigationOnClickListener {
            VibrationHaptics.vibration(this)
            onBackPressedDispatcher.onBackPressed()
        }

        binding.undoBtn.isEnabled = undoRedoService.undoStackSize() > 1
        binding.redoBtn.isEnabled = undoRedoService.redoStackSize() > 0
        undoRedoService.pushUndoStack(CanvasStackItem(ArrayList(itemList), UserInfo(userInfo!!)))

        // Set the adapter to the RecyclerView
        pageAdapter = PageAdapter(this, userInfo, itemList)
        binding.canvasContainerRv.setAdapter(pageAdapter)


        binding.headerFooterEditBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(this)
                .setView(R.layout.user_info_dialog)
                .create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()

            dialog.findViewById<TextInputLayout>(R.id.textInputLayout)?.boxStrokeColor = getColor(R.color.davy_grey_light_hint_x11_gray)
            dialog.findViewById<TextInputLayout>(R.id.textInputLayout1)?.boxStrokeColor = getColor(R.color.davy_grey_light_hint_x11_gray)
            dialog.findViewById<TextInputLayout>(R.id.textInputLayout2)?.boxStrokeColor = getColor(R.color.davy_grey_light_hint_x11_gray)

            val title = dialog.findViewById<EditText>(R.id.title_edtv)
            val name = dialog.findViewById<EditText>(R.id.name_edtv)
            val phone = dialog.findViewById<EditText>(R.id.phone_no_edtv)

            title!!.setText(userInfo?.title)
            name!!.setText(userInfo?.name)
            phone!!.setText(userInfo?.phoneNo)

            dialog.findViewById<Button>(R.id.save_btn)?.setOnClickListener {

                val editUserInfo = UserInfo(name.text.toString(), phone.text.toString(), title.text.toString())

                userInfo?.title = title.text.toString()
                userInfo?.name = name.text.toString()
                userInfo?.phoneNo = phone.text.toString()

                pageAdapter.editUserInfo(editUserInfo)
                pageAdapter.notifyItemRangeChanged(0, pageAdapter.itemCount)

                undoRedoService.pushUndoStack(CanvasStackItem(ArrayList(itemList), UserInfo(userInfo!!)))
                undoRedoService.clearRedoStack()
                binding.undoBtn.isEnabled = undoRedoService.undoStackSize() > 1
                binding.redoBtn.isEnabled = undoRedoService.redoStackSize() > 0
                dialog.dismiss()
            }
        }

        binding.editBtn.setOnClickListener {
            val intent = Intent(this, SelectedItemEdit::class.java)
            intent.putParcelableArrayListExtra("EDIT_ITEMS", itemList)
            intent.putExtra("FLAG_CANVAS", true)

            editItemLauncher.launch(intent)
        }

        binding.undoBtn.setOnClickListener{
            VibrationHaptics.vibration(this)

            val stackItem = undoRedoService.undo()
            itemList.clear()
            pageAdapter.editUserInfo(stackItem.userInfo)
            itemList.addAll(stackItem.contentList)
            pageAdapter.updatePageContent()

            it.isEnabled = undoRedoService.undoStackSize() > 1
            binding.redoBtn.isEnabled = undoRedoService.redoStackSize() > 0
        }

        binding.redoBtn.setOnClickListener {
            VibrationHaptics.vibration(this)
            val stackItem = undoRedoService.redo()
            if (stackItem != null) {

                itemList.clear()
                pageAdapter.editUserInfo(stackItem.userInfo)
                itemList.addAll(stackItem.contentList)
                pageAdapter.updatePageContent()

            }
            it.isEnabled = undoRedoService.redoStackSize() > 0
            binding.undoBtn.isEnabled = undoRedoService.undoStackSize() > 1
        }

        binding.toolbar.overflowIcon?.setTint(getColor(R.color.white))
        binding.toolbar.setOnMenuItemClickListener {

            when (it.itemId) {
                R.id.save_btn -> {
                    val dialog = nameDialog()
                    val parcheNameEdt = dialog.findViewById<EditText>(R.id.item_edtv)
                    parcheNameEdt!!.setText(parcheName)

                    dialog.findViewById<MaterialButton>(R.id.add_btn).setOnClickListener {

                        val pName = parcheNameEdt.text.toString()
                        if (pName.isNotEmpty()) {
                            val parchaAck = LocalParcheStorageImpl(this, "Parche")
                                .savedParcha(pName, userInfo!!, itemList)
                            if (parchaAck) {
                                Toast.makeText(this, "Parcha Saved!", Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            }
                        } else {
                            Toast.makeText(this, "Enter Parcha Name!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }

                R.id.download_btn -> {
                    val dialog = nameDialog()
                    val parcheNameEdt = dialog.findViewById<EditText>(R.id.item_edtv)
                    parcheNameEdt!!.setText(parcheName)

                    dialog.findViewById<MaterialButton>(R.id.add_btn).setOnClickListener {

                        val pName = parcheNameEdt.text.toString()
                        if (pName.isNotEmpty()) {
                            val pdfFile = generatePdf(pName)
                            if (pdfFile != null) {
                                Toast.makeText(this, "Downloaded!", Toast.LENGTH_SHORT).show()
                                dialog.cancel()
                            } else {
                                val textInputLayout =
                                    dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
                                textInputLayout.error = "File Already Exist!"
                            }
                        } else {
                            Toast.makeText(this, "Enter Parcha Name!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }

                R.id.share_btn -> {
                    val dialog = nameDialog()
                    val parcheNameEdt = dialog.findViewById<EditText>(R.id.item_edtv)
                    parcheNameEdt!!.setText(parcheName)

                    dialog.findViewById<MaterialButton>(R.id.add_btn).setOnClickListener {

                        val pName = parcheNameEdt.text.toString()
                        if (pName.isNotEmpty()) {
                            val pdfFile = generatePdf(pName)

                            if (pdfFile != null && pdfFile.exists()) {
                                val pdfUri: Uri = FileProvider.getUriForFile(
                                    this,
                                    "${this.packageName}.fileprovider",
                                    pdfFile
                                )

                                val intent = Intent(Intent.ACTION_SEND)
                                intent.type = "application/pdf"
                                intent.putExtra(Intent.EXTRA_SUBJECT, "PDF File")
                                intent.putExtra(Intent.EXTRA_STREAM, pdfUri)
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                                // Use chooser to let user select the preferred app for sharing
                                startActivity(Intent.createChooser(intent, "Send PDF via"))
                                dialog.cancel()
                            } else {
                                val textInputLayout =
                                    dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
                                textInputLayout.error = "File Already Exist!"
                            }

                        } else {
                            Toast.makeText(this, "Enter Parcha Name!", Toast.LENGTH_SHORT).show()
                        }

                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun generatePdf(parchaName: String): File? {
        val pdfDocument = PDFDocument(this)
        val pdfFile = pdfDocument.generateMultiPagePdf(
            pageAdapter.getPagesContent().second,
            pageAdapter.userInfo!!.title,
            "${pageAdapter.userInfo!!.name} - ${pageAdapter.userInfo!!.phoneNo}",
            parchaName
        )
        return pdfFile
    }

    private fun nameDialog(): Dialog {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(R.layout.item_dialog)
            .create()
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val saveBtn = dialog.findViewById<MaterialButton>(R.id.add_btn)
        saveBtn!!.text = resources.getString(R.string.save)
        saveBtn.setBackgroundColor(getColor(R.color.davy_grey_light_hint_x11_gray))

        dialog.findViewById<TextView>(R.id.textView1)?.visibility = View.GONE
        dialog.findViewById<TextInputLayout>(R.id.textInputLayout1)?.visibility =
            View.GONE
        dialog.findViewById<LinearLayout>(R.id.unit_ll)?.visibility = View.GONE
        dialog.findViewById<TextView>(R.id.title)?.text =
            resources.getString(R.string.parcha_name)

        val textInputLayout = dialog.findViewById<TextInputLayout>(R.id.textInputLayout)
        textInputLayout!!.boxStrokeColor =
            getColor(R.color.davy_grey_light_hint_x11_gray)
        textInputLayout.hint = resources.getString(R.string.enter_name)

        dialog.findViewById<EditText>(R.id.item_edtv)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                textInputLayout.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return dialog
    }
}