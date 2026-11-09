package com.example.partymember.ui

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.partymember.R
import com.example.partymember.databinding.ActivityUploadBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private val viewModel: UploadViewModel by viewModels()

    private val excelPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            processExcelFile(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        binding.btnSelectFile.setOnClickListener {
            excelPicker.launch(
                arrayOf(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    "application/vnd.ms-excel"
                )
            )
        }

        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.txtInstruction.setOnClickListener {
            showInstructionDialog()
        }

        binding.btnDownloadTemplate.setOnClickListener {
            showDownloadConfirmDialog()
        }

        binding.btnClearData.setOnClickListener {
            showClearConfirmDialog()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.uploadState.collect { state ->
                when (state) {
                    is UploadState.Loading -> {
                        showLoading(true)
                    }
                    is UploadState.Success -> {
                        showLoading(false)
                        if (state.count > 0) {
                            Toast.makeText(
                                this@UploadActivity,
                                "成功导入 ${state.count} 条数据",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        setResult(RESULT_OK)
                        finish()
                    }
                    is UploadState.Error -> {
                        showLoading(false)
                        Toast.makeText(
                            this@UploadActivity,
                            "操作失败: ${state.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    UploadState.Idle -> {
                        showLoading(false)
                    }
                }
            }
        }
    }

    private fun processExcelFile(uri: Uri) {
        lifecycleScope.launch {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                inputStream?.let {
                    viewModel.parseAndImport(it)
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@UploadActivity,
                    "文件读取失败: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSelectFile.isEnabled = !show
    }

    private fun showInstructionDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.upload_instruction_title)
            .setMessage(R.string.upload_instruction_content)
            .setPositiveButton("知道了") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun showDownloadConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("下载导入模板")
            .setMessage("将下载「党员信息导入模板.xlsx」到手机的 Download 文件夹中。\n\n请将该文件发送到电脑，按模板格式填写数据后，再选择文件导入。")
            .setPositiveButton("下载") { dialog, _ ->
                dialog.dismiss()
                downloadTemplate()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun downloadTemplate() {
        lifecycleScope.launch {
            var success = false
            var filePath = ""
            try {
                withContext(Dispatchers.IO) {
                    val fileName = "党员信息导入模板.xlsx"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val values = ContentValues().apply {
                            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                            put(MediaStore.Downloads.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            put(MediaStore.Downloads.IS_PENDING, 1)
                        }
                        val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                        if (uri != null) {
                            contentResolver.openOutputStream(uri)?.use { outputStream ->
                                assets.open("template.xlsx").use { input ->
                                    input.copyTo(outputStream)
                                }
                            }
                            values.clear()
                            values.put(MediaStore.Downloads.IS_PENDING, 0)
                            contentResolver.update(uri, values, null, null)

                            contentResolver.query(uri, arrayOf(MediaStore.Downloads.DATA), null, null, null)?.use { cursor ->
                                if (cursor.moveToFirst()) {
                                    val colIndex = cursor.getColumnIndex(MediaStore.Downloads.DATA)
                                    if (colIndex >= 0) filePath = cursor.getString(colIndex)
                                }
                            }
                            if (filePath.isBlank()) filePath = "/Download/$fileName"
                            success = true
                        } else {
                            filePath = "无法访问 Downloads 目录"
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        if (!dir.exists()) dir.mkdirs()
                        val file = File(dir, fileName)
                        assets.open("template.xlsx").use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        filePath = file.absolutePath
                        success = true
                    }
                }

                if (success) {
                    Toast.makeText(this@UploadActivity, "模板已下载到：\n$filePath", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@UploadActivity, "下载失败：$filePath", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@UploadActivity, "下载失败: ${e.javaClass.simpleName} - ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showClearConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle("清空数据")
            .setMessage("确定要清空所有党员信息吗？此操作不可恢复。")
            .setPositiveButton("确定") { dialog, _ ->
                dialog.dismiss()
                viewModel.clearAllData()
            }
            .setNegativeButton("取消") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
