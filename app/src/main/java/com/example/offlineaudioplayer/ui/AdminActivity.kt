package com.example.offlineaudioplayer.ui

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.offlineaudioplayer.data.AppDatabase
import com.example.offlineaudioplayer.databinding.ActivityAdminBinding
import com.example.offlineaudioplayer.repo.AudioRepository
import com.example.offlineaudioplayer.util.AdminPrefs

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminBinding
    private lateinit var adminPrefs: AdminPrefs
    private var pickerPosition: Int = -1

    private val viewModel: AdminViewModel by viewModels {
        AdminViewModel.Factory(AudioRepository(AppDatabase.get(this).buttonMappingDao()))
    }

    private val adapter: AdminMappingAdapter = AdminMappingAdapter { position ->
        pickerPosition = position
        filePickerLauncher.launch(arrayOf("audio/mpeg", "audio/mp3", "audio/*"))
    }

    private val filePickerLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null && pickerPosition >= 0) {
            takePersistablePermission(uri)
            adapter.updateUri(pickerPosition, uri.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adminPrefs = AdminPrefs(this)

        binding.adminList.layoutManager = LinearLayoutManager(this)
        binding.adminList.adapter = adapter

        viewModel.mappings.observe(this) {
            adapter.submitList(it)
        }

        binding.saveButton.setOnClickListener {
            viewModel.save(adapter.getItems())
            Toast.makeText(this, "SAVING...", Toast.LENGTH_SHORT).show()
        }

        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun takePersistablePermission(uri: Uri) {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, flags)
    }

    private fun showChangePasswordDialog() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newPassword = input.text.toString()
                if (newPassword.isBlank()) {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                } else {
                    adminPrefs.setPassword(newPassword)
                    Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
