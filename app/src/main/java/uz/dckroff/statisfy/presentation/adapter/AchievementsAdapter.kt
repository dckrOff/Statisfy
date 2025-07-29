package uz.dckroff.statisfy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.dckroff.statisfy.databinding.ItemAchievementSmallBinding
import uz.dckroff.statisfy.domain.model.Achievement

/**
 * Адаптер для отображения достижений пользователя
 */
class AchievementsAdapter(
    private val onAchievementClick: (Achievement) -> Unit = {}
) : ListAdapter<Achievement, AchievementsAdapter.AchievementViewHolder>(AchievementDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementViewHolder {
        val binding = ItemAchievementSmallBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AchievementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AchievementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AchievementViewHolder(
        private val binding: ItemAchievementSmallBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAchievementClick(getItem(position))
                }
            }
        }

        fun bind(achievement: Achievement) {
            binding.apply {
                tvAchievementTitle.text = achievement.title
                tvAchievementDescription.text = achievement.description
                
                // Устанавливаем иконку в зависимости от типа достижения
                when (achievement.type) {
                    "reading" -> ivAchievementIcon.setImageResource(uz.dckroff.statisfy.R.drawable.ic_facts)
                    "streak" -> ivAchievementIcon.setImageResource(uz.dckroff.statisfy.R.drawable.ic_mark)
                    "level" -> ivAchievementIcon.setImageResource(uz.dckroff.statisfy.R.drawable.ic_achievement)
                    "collection" -> ivAchievementIcon.setImageResource(uz.dckroff.statisfy.R.drawable.ic_favorites)
                    else -> ivAchievementIcon.setImageResource(uz.dckroff.statisfy.R.drawable.ic_achievement)
                }

                // Применяем стиль в зависимости от статуса
                if (achievement.isUnlocked) {
                    root.alpha = 1.0f
                    ivAchievementIcon.alpha = 1.0f
                } else {
                    root.alpha = 0.6f
                    ivAchievementIcon.alpha = 0.6f
                }
            }
        }
    }

    /**
     * DiffUtil для оптимизации обновлений списка
     */
    class AchievementDiffCallback : DiffUtil.ItemCallback<Achievement>() {
        override fun areItemsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Achievement, newItem: Achievement): Boolean {
            return oldItem == newItem
        }
    }
}