package com.example.offlineaudioplayer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.offlineaudioplayer.databinding.ItemAdminMappingBinding

class AdminMappingAdapter(
    private val onSetClicked: (position: Int) -> Unit
) : RecyclerView.Adapter<AdminMappingAdapter.AdminVH>() {
    private val items = mutableListOf<ButtonUiModel>()

    fun submitList(models: List<ButtonUiModel>) {
        items.clear()
        items.addAll(models.map { it.copy() })
        notifyDataSetChanged()
    }

    fun getItems(): List<ButtonUiModel> = items.map { it.copy() }

    fun updateUri(position: Int, uri: String) {
        items[position].uri = uri
        notifyItemChanged(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminVH {
        val binding = ItemAdminMappingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AdminVH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: AdminVH, position: Int) {
        holder.bind(items[position], position)
    }

    inner class AdminVH(private val binding: ItemAdminMappingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ButtonUiModel, position: Int) {
            binding.buttonIdLabel.text = "Button ${item.buttonId}"
            binding.customLabelInput.setText(item.label)
            binding.customLabelInput.doAfterTextChanged {
                items[position].label = it?.toString().orEmpty().ifBlank { "Button ${item.buttonId}" }
            }
            binding.uriText.text = item.uri ?: "No file selected"
            binding.setButton.setOnClickListener { onSetClicked(position) }
        }
    }
}
