package uz.dckroff.statisfy.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.ItemNewsBinding
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.utils.formatDate

/**
 * Адаптер для отображения списка новостей
 */
class NewsAdapter(
    private val onNewsClick: (News) -> Unit,
    private val onFavoriteClick: (News) -> Unit,
    private val onShareClick: (News) -> Unit
) : ListAdapter<News, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NewsViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            with(binding) {
                // Основная информация
                textViewTitle.text = news.title
                textViewSummary.text = news.summary
                textViewSource.text = news.source
                textViewCategory.text = news.category.name

                // Дата публикации
                textViewDate.text = formatDate(news.publishedAt.toString())

                // Загрузка изображения
                if (!news.imageUrl.isNullOrEmpty()) {
                    Glide.with(imageViewNews.context)
                        .load(news.imageUrl)
                        .placeholder(R.drawable.img_2)
                        .error(R.drawable.ic_image_error)
                        .centerCrop()
                        .into(imageViewNews)
                } else {
                    imageViewNews.setImageResource(R.drawable.img_2)
//                    imageViewNews.scaleType = ImageView.ScaleType.CENTER
                }

                // Индикатор релевантности
                textViewRelevant.visibility = if (news.isRelevant) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }

                // Кнопка избранного
                buttonFavorite.setImageResource(
                    if (isFavorite(news)) R.drawable.ic_favorite_filled
                    else R.drawable.ic_favorite_outline
                )

                // Обработчики нажатий
                root.setOnClickListener {
                    onNewsClick(news)
                }

                buttonFavorite.setOnClickListener {
                    onFavoriteClick(news)
                }

                buttonShare.setOnClickListener {
                    onShareClick(news)
                }
            }
        }

        private fun isFavorite(news: News): Boolean {
            // Здесь будет логика проверки избранного
            // Пока возвращаем false, логика будет добавлена позже
            return false
        }
    }

    /**
     * DiffCallback для эффективного обновления списка
     */
    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
}

/**
 * Адаптер для поиска новостей (может иметь другой макет)
 */
class NewsSearchAdapter(
    private val onNewsClick: (News) -> Unit
) : ListAdapter<News, NewsSearchAdapter.SearchViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SearchViewHolder(
        private val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(news: News) {
            with(binding) {
                textViewTitle.text = news.title
                textViewSummary.text = news.summary
                textViewSource.text = news.source
                textViewCategory.text = news.category.name
                textViewDate.text = formatDate(news.publishedAt.toString())

                // Для поиска можем скрыть некоторые элементы
                buttonFavorite.visibility = android.view.View.GONE
                buttonShare.visibility = android.view.View.GONE

                // Загрузка изображения
                if (!news.imageUrl.isNullOrEmpty()) {
                    Glide.with(imageViewNews.context)
                        .load(news.imageUrl)
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .centerCrop()
                        .into(imageViewNews)
                } else {
                    imageViewNews.setImageResource(R.drawable.ic_news_placeholder)
                }

                root.setOnClickListener {
                    onNewsClick(news)
                }
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<News>() {
        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem
        }
    }
}