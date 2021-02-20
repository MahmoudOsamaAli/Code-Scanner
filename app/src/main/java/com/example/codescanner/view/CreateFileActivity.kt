package com.example.codescanner.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codescanner.R
import com.example.codescanner.data.AppDatabase
import com.example.codescanner.databinding.ActivityCreateFileBinding
import com.example.codescanner.model.File
import com.example.codescanner.model.FileRow
import com.honeywell.aidc.*
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CreateFileActivity : AppCompatActivity(), BarcodeReader.BarcodeListener,
    BarcodeReader.TriggerListener, onItemRemovedListener, OnDialogDismiss {

    private lateinit var binding: ActivityCreateFileBinding
    private val data: ArrayList<String> = arrayListOf()
    private var barcodeReader: BarcodeReader? = null
    private var manager: AidcManager? = null
    private var adapter: RowItemAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_file)
        init()
    }

    private fun init() {
        setToolBar()
        initQRCodeScanner()
        initRv()
    }

    private fun initRv() {
        adapter = RowItemAdapter(this, data, this)
        binding.rowItemsRv.adapter = adapter
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                val itemCount: Int = adapter!!.itemCount
                if (itemCount == 0) {
                    Log.i(TAG, "onChanged: count = 0")
                    binding.animator.displayedChild = 0
                } else {
                    Log.i(TAG, "onChanged: count = $itemCount")
                    binding.animator.displayedChild = 1
                }
            }
        })
    }

    private fun initQRCodeScanner() {
        try {
            AidcManager.create(this) { aidcManager: AidcManager ->
                manager = aidcManager
                try {
                    barcodeReader = manager!!.createBarcodeReader()
                    Log.e(TAG, (barcodeReader == null).toString())
                    if (barcodeReader != null) {
                        Log.e(TAG, "inside if condition")
                        // register bar code event listener
                        barcodeReader!!.addBarcodeListener(this)
                        initBarCode()
                        barcodeReader!!.claim()
                        // set the trigger mode to client control
                    } else {
                        Toast.makeText(this, "can't create bar core reader ", Toast.LENGTH_SHORT)
                            .show()
                    }
                } catch (e: InvalidScannerNameException) {
                    Toast.makeText(
                        this,
                        "Invalid Scanner Name Exception: " + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: Exception) {
                    Toast.makeText(this, "Exception: " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.i(TAG, "initQRCodeScanner: ${e.message}")
        }
    }

    private fun initBarCode() {
        try {
            barcodeReader!!.setProperty(
                BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL
            )
        } catch (e: UnsupportedPropertyException) {
            Toast.makeText(this, "Failed to apply properties", Toast.LENGTH_SHORT).show()
        }
        // register trigger state change listener
        try {
            barcodeReader!!.addTriggerListener(this)
            val properties: MutableMap<String, Any> = HashMap()
            // Set Symbologies On/Off
            properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_GS1_128_ENABLED] = true
            properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = false


            //enable to get all the bar code digit 8 number
            properties[BarcodeReader.PROPERTY_EAN_8_CHECK_DIGIT_TRANSMIT_ENABLED] = true
            properties[BarcodeReader.PROPERTY_DATAMATRIX_ENABLED] = true
            properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = true
            properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = true
            //enable to get all the bar code digit 13 number
            properties[BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED] = true
            properties[BarcodeReader.PROPERTY_AZTEC_ENABLED] = true
            properties[BarcodeReader.PROPERTY_CODABAR_ENABLED] = true
            properties[BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED] = true
            properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = true
            // Set Max Code 39 barcode length
            properties[BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH] = 50
            // Turn on center decoding
            properties[BarcodeReader.PROPERTY_CENTER_DECODE] = true
            // Enable bad read response
            properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] = true
            // Apply the settings
            barcodeReader!!.setProperties(properties)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            exit()
        } else if (item.itemId == R.id.menu_action_save) {
            if (data.isNotEmpty()) {
                val dialog = FileNameDialog()
                dialog.setListener(this)
                dialog.show(supportFragmentManager, dialog.tag)
            } else {
                Toast.makeText(this, getString(R.string.no_items_found), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = exit()

    private fun save(fName: String) {
        if (data.isNotEmpty()) {
            val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.ROOT)
            val date = Date()
            val name: String? = getSharedPreferences(
                "",
                Context.MODE_PRIVATE
            ).getString(getString(R.string.user_name), "")
            val db = AppDatabase.getDatabase(this)
            val fileRows: ArrayList<FileRow> = arrayListOf()
            GlobalScope.launch(Dispatchers.IO) {
                data.forEach {
                    fileRows.add(FileRow(name!!, it, sdf.format(date)))
                }
                db.fileDao().insertFile(File(sdf.format(date), fileRows, fName))
                withContext(Dispatchers.Main) { exit() }
            }

        } else {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun exit() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onBarcodeEvent(event: BarcodeReadEvent) {
        runOnUiThread {
            Log.e(TAG, "onBarcodeEvent: $event")
            Log.e(TAG, "onBarcodeEvent: " + event.barcodeData.trim())
            data.add(event.barcodeData.toString().trim())
            adapter?.submitData(data)
        }
    }

    override fun onFailureEvent(p0: BarcodeFailureEvent?) {
        runOnUiThread {
            Log.e(TAG, "onFailureEvent: $p0")
        }
    }

    companion object {
        private const val TAG = "CreateFileActivity"
    }

    override fun onTriggerEvent(p0: TriggerStateChangeEvent?) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.e(TAG, "onTriggerEvent: $p0")
        }
    }

    override fun onItemRemoved(position: Int) {
        data.removeAt(position)
        adapter?.setLastPosition(position-1)
        adapter?.submitData(data)
    }

    override fun onDialogDismiss(fileName: String) {
        save(fileName)
    }
}