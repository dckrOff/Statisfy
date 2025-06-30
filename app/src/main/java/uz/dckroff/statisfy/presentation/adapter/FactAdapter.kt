package uz.dckroff.statisfy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.statisfy.databinding.ItemFactBinding
import uz.dckroff.statisfy.domain.model.Fact

/**
 * Адаптер для отображения списка фактов
 */
class FactAdapter(
    private val onFactClick: (Fact) -> Unit,
    private val onShareClick: (Fact) -> Unit,
    private val onFavoriteClick: (Fact) -> Unit
) : ListAdapter<Fact, FactAdapter.FactViewHolder>(FactDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactViewHolder {
        val binding = ItemFactBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FactViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: FactViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class FactViewHolder(
        private val binding: ItemFactBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFactClick(getItem(position))
                }
            }
            
            binding.btnShare.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onShareClick(getItem(position))
                }
            }
            
            binding.btnFavorite.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onFavoriteClick(getItem(position))
                }
            }
        }
        
        fun bind(fact: Fact) {
            binding.apply {
                tvFactTitle.text = fact.title
                tvFactContent.text = fact.content
                tvFactCategory.text = fact.category.name
                
                // TODO: Загрузка изображения с помощью Glide
                // Glide.with(imgFactImage)
                //     .load(fact.imageUrl)
                //     .centerCrop()
                //     .placeholder(R.drawable.img)
                //     .into(imgFactImage)
            }
        }
    }
    
    /**
     * DiffUtil для оптимизации обновлений списка
     */
    class FactDiffCallback : DiffUtil.ItemCallback<Fact>() {
        override fun areItemsTheSame(oldItem: Fact, newItem: Fact): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: Fact, newItem: Fact): Boolean {
            return oldItem == newItem
        }
    }
} 