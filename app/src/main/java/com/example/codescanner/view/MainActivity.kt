package com.example.codescanner.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.GravityEnum
import com.afollestad.materialdialogs.MaterialDialog
import com.example.codescanner.R
import com.example.codescanner.data.AppDatabase
import com.example.codescanner.databinding.ActivityMainBinding
import com.example.codescanner.model.File
import com.opencsv.CSVWriter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.io.FileWriter
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), OnItemClick {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: MaterialDialog
    private var _data: MutableLiveData<ArrayList<File>> = MutableLiveData()
    private var data: LiveData<ArrayList<File>> = _data
    private var adapter: FilesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        data.observe(this, { d: ArrayList<File> -> adapter?.submitData(d) })
        init()
    }

    private fun init() {
        setRv()
        setToolBar()
        getData()
        initDialog()
        binding.createNewFile.setOnClickListener {
            startActivity(Intent(this, CreateFileActivity::class.java))
            finish()
        }
        binding.profileImage.setOnClickListener {
            val dialog = UserInfoDialog()
            dialog.show(supportFragmentManager, dialog.tag)
        }
    }

    private fun initDialog() {
        try {
            dialog = MaterialDialog.Builder(this)
                //.title(getString(R.string.str_dialog_waiting_msg))
                .content("Processing ...")
                .contentColor(Color.parseColor("#023B78"))
                .contentGravity(GravityEnum.CENTER)
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 100)
                .progressIndeterminateStyle(true)
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            dialog = MaterialDialog.Builder(this)
                //.title(getString(R.string.str_dialog_waiting_msg))
                .content("Processing ...")
                .contentGravity(GravityEnum.CENTER)
                .cancelable(false)
                .autoDismiss(false)
                .progress(true, 100)
                .progressIndeterminateStyle(true)
                .build()
        }
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun getData() = GlobalScope.launch(Dispatchers.IO) {
        AppDatabase.getDatabase(this@MainActivity)
            .fileDao().getAll().collect {
                _data.postValue(ArrayList(it.asReversed()))
            }
    }

    private fun setRv() {
        adapter = FilesAdapter(this, arrayListOf(), this)
        binding.filesRv.adapter = adapter
        adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                val itemCount: Int = adapter!!.itemCount
                if (itemCount == 0) {
                    Log.i(TAG, "onChanged: count = 0")
                    binding.animator.displayedChild = 1
                } else {
                    Log.i(TAG, "onChanged: count = $itemCount")
                    binding.animator.displayedChild = 2
                }
            }
        })
        binding.filesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val b = dy <= 0
                if (b) binding.createNewFile.extend()
                else binding.createNewFile.shrink()

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_action_edit_info -> {
                val dialog = UserInfoDialog()
                dialog.show(supportFragmentManager, dialog.tag)
            }
            R.id.menu_action_delete_all -> if (data.value!!.isNotEmpty()) showDeleteAllDialog()
            R.id.menu_action_log_out -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val pref = getSharedPreferences("", Context.MODE_PRIVATE) ?: return
        with(pref.edit()) {
            putBoolean(getString(R.string.remember_me_key), false)
                .apply()
        }
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun showDeleteAllDialog() {
        MaterialDialog.Builder(this)
            .autoDismiss(false)
            .cancelable(true)
            .positiveText("Yes")
            .negativeText("NO")
            .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                deleteAllFiles()
                dialog.dismiss()
            }
            .onNegative { d: MaterialDialog, _: DialogAction? -> d.dismiss() }
            .content(getString(R.string.delete_all_files_warning))
            .positiveColorRes(R.color.color_app_primary)
            .negativeColorRes(R.color.color_app_primary)
            .build()
            .show()
    }

    private fun deleteAllFiles() {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch { db.fileDao().clearTable() }
    }

    private fun createFile(): java.io.File? {
        val folderName = "Excel_Sheets"
        return try {
            val folderRoot = java.io.File(
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                ), folderName
            )
            if (!folderRoot.exists()) {
                return if (!folderRoot.mkdir()) null
                else folderRoot
            }
            folderRoot
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createCSV(file: File, share: Boolean) {
        val fileDr = createFile()
        if (fileDr == null) {
            if (dialog.isShowing) dialog.dismiss()
            Toast.makeText(this, "can't create file ", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val path = "${fileDr.path}/${file.fileName}.csv"
            val writer = CSVWriter(FileWriter(path))

            val data: MutableList<Array<String>> = ArrayList()
            data.add(arrayOf("User Name", "Serial Number", "Date"))
            file.data.forEach {
                data.add(arrayOf(it.userName, it.itemSerial, it.date))
            }
            writer.writeAll(data) // data is adding to csv
            writer.close()
            showDialog(path, share)

        } catch (e: IOException) {
            e.printStackTrace()
            if (dialog.isShowing) dialog.dismiss()
        }
    }

    private fun showDialog(path: String, share: Boolean) {
        MainScope().launch {
            delay(2000)
            if (dialog.isShowing) dialog.dismiss()
            MaterialDialog.Builder(this@MainActivity)
                .autoDismiss(false)
                .cancelable(true)
                .positiveText("OK")
                .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                    if (share) shareViewWhatsApp(path)
                    dialog.dismiss()
                }
                .content("File Saved At Location $path")
                .positiveColorRes(R.color.color_app_primary)
                .build()
                .show()
        }
    }

    private fun shareViewWhatsApp(path: String) {
        try {
            val imageUri = FileProvider.getUriForFile(
                this, "com.example.codescanner.provider", java.io.File(path)
            )
            val share = Intent()
            share.action = Intent.ACTION_SEND
            share.type = "text/csv"
            share.putExtra(Intent.EXTRA_STREAM, imageUri)
            share.setPackage("com.whatsapp")
            startActivity(share)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                this,
                "Couldn't Find Whats App .. Please Install Whats App First",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showConfirmationDialog(pos: Int) {
        MaterialDialog.Builder(this)
            .autoDismiss(false)
            .cancelable(true)
            .positiveText("Delete")
            .negativeText("Cancel")
            .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                deleteFile(pos)
                dialog.dismiss()
            }
            .onNegative { dialog: MaterialDialog, _: DialogAction? -> dialog.dismiss() }
            .content(getString(R.string.delete_file_warning))
            .positiveColorRes(R.color.color_app_primary)
            .negativeColorRes(R.color.color_app_primary)
            .build()
            .show()
    }

    private fun deleteFile(pos:Int) {
        val db = AppDatabase.getDatabase(this)
        GlobalScope.launch {
            db.fileDao().deleteFile(data.value!![pos])
            withContext(Dispatchers.Main){ adapter?.setLastPosition(pos-1)}
        }
    }

    private fun checkPermissions(): Boolean =
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                0
            )
            false
        } else true

    override fun onItemClick(position: Int, view: View) {
        when (view.id) {
            R.id.export -> {
                if (checkPermissions()) {
                    dialog.show()
                    createCSV(data.value!![position], false)
                }
            }
            R.id.share -> {
                if (checkPermissions()) {
                    dialog.show()
                    createCSV(data.value!![position], true)
                }
            }
            R.id.delete_item -> showConfirmationDialog(position)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}