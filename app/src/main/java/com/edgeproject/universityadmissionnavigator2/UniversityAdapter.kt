package com.edgeproject.universityadmissionnavigator2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edgeproject.universityadmissionnavigator2.databinding.ItemUniversitiesBinding
import kotlin.Unit

class UnitAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<UnitWithUniversity, UnitAdapter.UnitViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UnitViewHolder {
        val binding = ItemUniversitiesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UnitViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: UnitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class UnitViewHolder(
        private val binding: ItemUniversitiesBinding,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(unitWithUniversity: UnitWithUniversity) {
            binding.tvUniversityName.text = unitWithUniversity.universityName
            binding.tvUnitTitle.text = unitWithUniversity.unitTitle
            binding.tvUnitName.text = unitWithUniversity.unitName
            binding.btnApply.setOnClickListener {
                onItemClick(unitWithUniversity.applicationLink)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<UnitWithUniversity>() {
        override fun areItemsTheSame(oldItem: UnitWithUniversity, newItem: UnitWithUniversity): Boolean {
            return oldItem.unitName == newItem.unitName &&
                    oldItem.universityName == newItem.universityName
        }

        override fun areContentsTheSame(oldItem: UnitWithUniversity, newItem: UnitWithUniversity): Boolean {
            return oldItem == newItem
        }
    }
}