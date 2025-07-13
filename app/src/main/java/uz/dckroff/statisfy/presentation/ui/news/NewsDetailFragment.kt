package uz.dckroff.statisfy.presentation.ui.news

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentNewsDetailBinding
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.presentation.viewmodel.NewsViewModel
import uz.dckroff.statisfy.utils.formatDateTime
import uz.dckroff.statisfy.utils.getRelativeTimeString
import uz.dckroff.statisfy.utils.hide
import uz.dckroff.statisfy.utils.show

@AndroidEntryPoint
class NewsDetailFragment : Fragment() {

    private var _binding: FragmentNewsDetailBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NewsViewModel by viewModels()
    
    private var currentNews: News? = null
    private var newsId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            newsId = it.getString("newsId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        loadNews()
    }

    private fun setupUI() {
        // Кнопка назад
        binding.buttonBack.setOnClickListener {
            findNavController().navigateUp()
        }
        
        // Кнопка избранного
        binding.buttonFavorite.setOnClickListener {
            currentNews?.let { news ->
                viewModel.toggleFavorite(news.id)
            }
        }
        
        // Кнопка поделиться
        binding.buttonShare.setOnClickListener {
            currentNews?.let { news ->
                shareNews(news)
            }
        }
        
        // Кнопка открыть в браузере
        binding.buttonOpenBrowser.setOnClickListener {
            currentNews?.let { news ->
                openInBrowser(news.url)
            }
        }
        
        // Кнопка источника
        binding.buttonSource.setOnClickListener {
            currentNews?.let { news ->
                openInBrowser(news.url)
            }
        }
    }

    private fun observeViewModel() {
        // Наблюдение за выбранной новостью
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedNews.collect { news ->
                if (news != null) {
                    currentNews = news
                    displayNews(news)
                }
            }
        }
    }

    private fun loadNews() {
        newsId?.let { id ->
            viewModel.getNewsById(id)
        }
    }

    private fun displayNews(news: News) {
        with(binding) {
            // Основная информация
            textViewTitle.text = news.title
            textViewSummary.text = news.summary
            textViewContent.text = news.content ?: news.summary
            textViewSource.text = news.source
            textViewCategory.text = news.category.name
            
            // Дата публикации
            textViewDate.text = getRelativeTimeString(news.publishedAt.toString())
            textViewDateFull.text = formatDateTime(news.publishedAt.toString())
            
            // Загрузка изображения
            if (!news.imageUrl.isNullOrEmpty()) {
                Glide.with(requireContext())
                    .load(news.imageUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_error)
                    .centerCrop()
                    .into(imageViewNews)
                imageViewNews.show()
            } else {
                imageViewNews.hide()
            }
            
            // Индикатор релевантности
            chipRelevant.visibility = if (news.isRelevant) {
                View.VISIBLE
            } else {
                View.GONE
            }
            
            // Кнопка избранного
            updateFavoriteButton(false) // Пока всегда false, логика будет добавлена позже
            
            // Показать контент
            layoutContent.show()
            progressBar.hide()
        }
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_filled)
            binding.buttonFavorite.setColorFilter(
                requireContext().getColor(R.color.favorite_color)
            )
        } else {
            binding.buttonFavorite.setImageResource(R.drawable.ic_favorite_outline)
            binding.buttonFavorite.clearColorFilter()
        }
    }

    private fun shareNews(news: News) {
        val shareText = buildString {
            append(news.title)
            append("\n\n")
            append(news.summary)
            append("\n\n")
            append("Источник: ${news.source}")
            append("\n")
            append(news.url)
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться новостью"))
    }

    private fun openInBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Не удалось открыть ссылку",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSelectedNews()
        _binding = null
    }
}