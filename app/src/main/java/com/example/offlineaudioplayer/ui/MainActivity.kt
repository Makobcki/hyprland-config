package com.example.offlineaudioplayer.ui

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.offlineaudioplayer.data.AppDatabase
import com.example.offlineaudioplayer.databinding.ActivityMainBinding
import com.example.offlineaudioplayer.repo.AudioRepository
import com.example.offlineaudioplayer.util.AdminPrefs

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adminPrefs: AdminPrefs
    private var mediaPlayer: MediaPlayer? = null
    private var activeButtonId: Int? = null
    private var hiddenTapCount = 0
    private var lastHiddenTapTs = 0L

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(AudioRepository(AppDatabase.get(this).buttonMappingDao()))
    }

    private val adapter = SoundButtonAdapter(::onSoundButtonClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adminPrefs = AdminPrefs(this)

        binding.buttonGrid.layoutManager = GridLayoutManager(this, 2)
        binding.buttonGrid.adapter = adapter
        setStatus("READY")

        viewModel.mappings.observe(this) { adapter.submitList(it) }
        binding.hiddenTrigger.setOnClickListener { handleHiddenTap() }
    }

    private fun onSoundButtonClick(item: ButtonUiModel) {
        val uriText = item.uri
        if (uriText.isNullOrBlank()) {
            setStatus("OFFLINE")
            Toast.makeText(this, "No MP3 assigned for ${item.label}", Toast.LENGTH_SHORT).show()
            return
        }
        playAudio(item.buttonId, item.label, Uri.parse(uriText))
    }

    private fun playAudio(buttonId: Int, label: String, uri: Uri) {
        stopPlayback()
        setStatus("PLAYING $label")
        activeButtonId = buttonId
        adapter.setActive(buttonId)

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                start()
                setStatus("PLAYING $label")
            }
            setOnCompletionListener {
                setStatus("READY")
                activeButtonId = null
                adapter.setActive(null)
                releasePlayer()
            }
            setOnErrorListener { _, _, _ ->
                setStatus("OFFLINE")
                activeButtonId = null
                adapter.setActive(null)
                releasePlayer()
                true
            }
            setDataSource(this@MainActivity, uri)
            prepareAsync()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            releasePlayer()
        }
        activeButtonId = null
        adapter.setActive(null)
    }

    private fun releasePlayer() {
        mediaPlayer?.reset()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun handleHiddenTap() {
        val now = SystemClock.elapsedRealtime()
        hiddenTapCount = if (now - lastHiddenTapTs <= 1500) hiddenTapCount + 1 else 1
        lastHiddenTapTs = now
        if (hiddenTapCount >= 8) {
            hiddenTapCount = 0
            showAdminLogin()
        }
    }

    private fun showAdminLogin() {
        val input = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Admin Login")
            .setMessage("Enter password")
            .setView(input)
            .setPositiveButton("Login") { _, _ ->
                if (input.text.toString() == adminPrefs.getPassword()) {
                    startActivity(Intent(this, AdminActivity::class.java))
                } else {
                    Toast.makeText(this, "Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setStatus(value: String) {
        binding.statusPanel.text = value
    }

    override fun onStop() {
        super.onStop()
        stopPlayback()
    }
}
