package com.example.offlineaudioplayer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.offlineaudioplayer.R
import com.example.offlineaudioplayer.databinding.ItemSoundButtonBinding

class SoundButtonAdapter(
    private val onClick: (ButtonUiModel) -> Unit
) : RecyclerView.Adapter<SoundButtonAdapter.SoundButtonVH>() {

    private val items = mutableListOf<ButtonUiModel>()
    private var activeId: Int? = null

    fun submitList(newItems: List<ButtonUiModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setActive(buttonId: Int?) {
        activeId = buttonId
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundButtonVH {
        val binding = ItemSoundButtonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundButtonVH(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SoundButtonVH, position: Int) {
        holder.bind(items[position], items[position].buttonId == activeId)
    }

    inner class SoundButtonVH(private val binding: ItemSoundButtonBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ButtonUiModel, active: Boolean) {
            binding.buttonLabel.text = item.label
            binding.card.strokeWidth = if (active) 4 else 1
            binding.card.strokeColor = if (active) {
                binding.root.context.getColor(R.color.accent_active)
            } else {
                binding.root.context.getColor(R.color.highlight_dark)
            }
            binding.root.setOnClickListener { onClick(item) }
        }
    }
}
