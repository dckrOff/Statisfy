package uz.dckroff.statisfy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.statisfy.databinding.ItemFavoriteFactBinding
import uz.dckroff.statisfy.databinding.ItemFavoriteNewsBinding
import uz.dckroff.statisfy.databinding.ItemFavoriteStatisticBinding
import uz.dckroff.statisfy.domain.model.ContentType
import uz.dckroff.statisfy.domain.model.FavoriteItem

/**
 * Адаптер для отображения избранного контента
 */
class FavoritesAdapter(
    private val onItemClick: (FavoriteItem) -> Unit,
    private val onRemoveClick: (FavoriteItem) -> Unit,
    private val onShareClick: (FavoriteItem) -> Unit,
    private val onLongClick: (FavoriteItem) -> Boolean = { false }
) : ListAdapter<FavoriteItem, RecyclerView.ViewHolder>(FavoriteItemDiffCallback()) {

    companion object {
        private const val TYPE_FACT = 1
        private const val TYPE_NEWS = 2
        private const val TYPE_STATISTIC = 3
    }

    var isSelectionMode = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var selectedItems = setOf<String>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).contentType) {
            ContentType.FACT -> TYPE_FACT
            ContentType.NEWS -> TYPE_NEWS
            ContentType.STATISTIC -> TYPE_STATISTIC
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_FACT -> {
                val binding = ItemFavoriteFactBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                FactViewHolder(binding)
            }
            TYPE_NEWS -> {
                val binding = ItemFavoriteNewsBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                NewsViewHolder(binding)
            }
            TYPE_STATISTIC -> {
                val binding = ItemFavoriteStatisticBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                StatisticViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is FactViewHolder -> holder.bind(item)
            is NewsViewHolder -> holder.bind(item)
            is StatisticViewHolder -> holder.bind(item)
        }
    }

    // ViewHolder для фактов
    inner class FactViewHolder(
        private val binding: ItemFavoriteFactBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    val item = getItem(adapterPosition)
                    onLongClick(item)
                } else {
                    onItemClick(getItem(adapterPosition))
                }
            }
            
            binding.root.setOnLongClickListener {
                onLongClick(getItem(adapterPosition))
            }
            
            binding.btnRemove.setOnClickListener {
                onRemoveClick(getItem(adapterPosition))
            }
            
            binding.btnShare.setOnClickListener {
                onShareClick(getItem(adapterPosition))
            }
        }

        fun bind(item: FavoriteItem) {
            binding.apply {
                tvFactTitle.text = item.title
                tvFactContent.text = item.summary
                tvFactCategory.text = item.category?.name ?: "Без категории"
                tvFactDate.text = formatDate(item.addedAt)
                
                // Показываем/скрываем чекбокс в режиме выбора
                checkboxSelect.visibility = if (isSelectionMode) 
                    android.view.View.VISIBLE else android.view.View.GONE
                checkboxSelect.isChecked = selectedItems.contains(item.id)
                
                // Обновляем состояние выбора
                root.isSelected = selectedItems.contains(item.id)
                
                // TODO: Загрузка изображения с помощью Glide
                // Glide.with(imgFactImage)
                //     .load(item.imageUrl)
                //     .centerCrop()
                //     .placeholder(R.drawable.img_placeholder)
                //     .into(imgFactImage)
            }
        }
    }

    // ViewHolder для новостей
    inner class NewsViewHolder(
        private val binding: ItemFavoriteNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    val item = getItem(adapterPosition)
                    onLongClick(item)
                } else {
                    onItemClick(getItem(adapterPosition))
                }
            }
            
            binding.root.setOnLongClickListener {
                onLongClick(getItem(adapterPosition))
            }
            
            binding.btnRemove.setOnClickListener {
                onRemoveClick(getItem(adapterPosition))
            }
            
            binding.btnShare.setOnClickListener {
                onShareClick(getItem(adapterPosition))
            }
        }

        fun bind(item: FavoriteItem) {
            binding.apply {
                tvNewsTitle.text = item.title
                tvNewsContent.text = item.summary
                tvNewsSource.text = item.source ?: "Неизвестный источник"
                tvNewsDate.text = formatDate(item.addedAt)
                
                // Показываем/скрываем чекбокс в режиме выбора
                checkboxSelect.visibility = if (isSelectionMode) 
                    android.view.View.VISIBLE else android.view.View.GONE
                checkboxSelect.isChecked = selectedItems.contains(item.id)
                
                // Обновляем состояние выбора
                root.isSelected = selectedItems.contains(item.id)
                
                // TODO: Загрузка изображения
            }
        }
    }

    // ViewHolder для статистики
    inner class StatisticViewHolder(
        private val binding: ItemFavoriteStatisticBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    val item = getItem(adapterPosition)
                    onLongClick(item)
                } else {
                    onItemClick(getItem(adapterPosition))
                }
            }
            
            binding.root.setOnLongClickListener {
                onLongClick(getItem(adapterPosition))
            }
            
            binding.btnRemove.setOnClickListener {
                onRemoveClick(getItem(adapterPosition))
            }
            
            binding.btnShare.setOnClickListener {
                onShareClick(getItem(adapterPosition))
            }
        }

        fun bind(item: FavoriteItem) {
            binding.apply {
                tvStatisticTitle.text = item.title
                tvStatisticContent.text = item.summary
                tvStatisticCategory.text = item.category?.name ?: "Статистика"
                tvStatisticDate.text = formatDate(item.addedAt)
                
                // Показываем/скрываем чекбокс в режиме выбора
                checkboxSelect.visibility = if (isSelectionMode) 
                    android.view.View.VISIBLE else android.view.View.GONE
                checkboxSelect.isChecked = selectedItems.contains(item.id)
                
                // Обновляем состояние выбора
                root.isSelected = selectedItems.contains(item.id)
            }
        }
    }

    private fun formatDate(dateString: String): String {
        // TODO: Реализовать форматирование даты
        return dateString
    }

    /**
     * DiffUtil для оптимизации обновлений списка
     */
    class FavoriteItemDiffCallback : DiffUtil.ItemCallback<FavoriteItem>() {
        override fun areItemsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FavoriteItem, newItem: FavoriteItem): Boolean {
            return oldItem == newItem
        }
    }
}