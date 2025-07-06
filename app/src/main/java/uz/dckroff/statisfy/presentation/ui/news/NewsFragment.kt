package uz.dckroff.statisfy.presentation.ui.news

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentNewsBinding
import uz.dckroff.statisfy.domain.model.News
import uz.dckroff.statisfy.presentation.adapter.NewsAdapter
import uz.dckroff.statisfy.presentation.adapter.NewsSearchAdapter
import uz.dckroff.statisfy.presentation.viewmodel.NewsViewModel
import uz.dckroff.statisfy.utils.UiState
import uz.dckroff.statisfy.utils.hide
import uz.dckroff.statisfy.utils.show

@AndroidEntryPoint
class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: NewsViewModel by viewModels()
    
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var searchAdapter: NewsSearchAdapter
    
    private var isSearchMode = false
    private var currentSortBy = "date_desc"
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeViewModel()
    }

    private fun setupUI() {
        // SwipeRefreshLayout
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (isSearchMode) {
                viewModel.searchNews(viewModel.searchQuery.value)
            } else {
                viewModel.refresh()
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
        
        // Кнопка сортировки
        binding.buttonSort.setOnClickListener {
            showSortDialog()
        }
        
        // Кнопка фильтров
        binding.buttonFilter.setOnClickListener {
            showFiltersDialog()
        }
        
        // Кнопка синхронизации
        binding.buttonSync.setOnClickListener {
            viewModel.syncNews()
        }
    }

    private fun setupRecyclerView() {
        // Основной адаптер для новостей
        newsAdapter = NewsAdapter(
            onNewsClick = { news ->
                navigateToNewsDetail(news)
            },
            onFavoriteClick = { news ->
                viewModel.toggleFavorite(news.id)
            },
            onShareClick = { news ->
                shareNews(news)
            }
        )
        
        // Адаптер для поиска
        searchAdapter = NewsSearchAdapter(
            onNewsClick = { news ->
                navigateToNewsDetail(news)
            }
        )
        
        // Настройка RecyclerView
        binding.recyclerViewNews.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = newsAdapter
            
            // Пагинация
            addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    
                    if (!isSearchMode && !viewModel.isLoadingMore.value && viewModel.hasMorePages.value) {
                        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
                            viewModel.loadMoreNews()
                        }
                    }
                }
            })
        }
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotEmpty()) {
                        startSearch(it)
                    } else {
                        exitSearch()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isEmpty()) {
                        exitSearch()
                    } else if (it.length >= 3) {
                        // Поиск после 3 символов
                        startSearch(it)
                    }
                }
                return true
            }
        })
        
        binding.searchView.setOnCloseListener {
            exitSearch()
            false
        }
    }

    private fun setupFilters() {
        // Здесь можно добавить динамически загружаемые категории
        // Пока добавим базовые фильтры
        addFilterChip("Все", null)
        addFilterChip("Технологии", "tech")
        addFilterChip("Наука", "science")
        addFilterChip("Спорт", "sport")
        addFilterChip("Политика", "politics")
        addFilterChip("Релевантное", "relevant")
    }
    
    private fun addFilterChip(text: String, categoryId: String?) {
        val chip = Chip(requireContext())
        chip.text = text
        chip.isCheckable = true
        chip.isChecked = categoryId == null // "Все" выбрано по умолчанию
        
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Снять выбор с других чипов
                for (i in 0 until binding.chipGroupCategories.childCount) {
                    val otherChip = binding.chipGroupCategories.getChildAt(i) as Chip
                    if (otherChip != chip) {
                        otherChip.isChecked = false
                    }
                }
                
                // Применить фильтр
                if (categoryId == "relevant") {
                    viewModel.getRelevantNews()
                } else {
                    viewModel.getNewsByCategory(categoryId)
                }
            }
        }
        
        binding.chipGroupCategories.addView(chip)
    }

    private fun observeViewModel() {
        // Наблюдение за состоянием новостей
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newsState.collect { state ->
                when (state) {
                    is UiState.Idle -> {
                        hideLoading()
                    }
                    is UiState.Loading -> {
                        showLoading()
                    }
                    is UiState.Success -> {
                        hideLoading()
                        hideError()
                        
                        if (!isSearchMode) {
                            val newsList = viewModel.newsList.value
                            newsAdapter.submitList(newsList)
                            
                            // Показать сообщение если список пуст
                            if (newsList.isEmpty()) {
                                showEmptyState()
                            } else {
                                hideEmptyState()
                            }
                        }
                    }
                    is UiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
        
        // Наблюдение за состоянием поиска
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchState.collect { state ->
                when (state) {
                    is UiState.Idle -> {
                        if (isSearchMode) {
                            binding.recyclerViewNews.adapter = newsAdapter
                            isSearchMode = false
                        }
                    }
                    is UiState.Loading -> {
                        showLoading()
                    }
                    is UiState.Success -> {
                        hideLoading()
                        hideError()
                        
                        if (isSearchMode) {
                            searchAdapter.submitList(state.data.news)
                            
                            if (state.data.news.isEmpty()) {
                                showEmptySearchState()
                            } else {
                                hideEmptyState()
                            }
                        }
                    }
                    is UiState.Error -> {
                        hideLoading()
                        showError(state.message)
                    }
                }
            }
        }
        
        // Наблюдение за состоянием загрузки дополнительных данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoadingMore.collect { isLoading ->
                binding.progressBarLoadMore.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun startSearch(query: String) {
        if (!isSearchMode) {
            isSearchMode = true
            binding.recyclerViewNews.adapter = searchAdapter
        }
        viewModel.searchNews(query)
    }

    private fun exitSearch() {
        isSearchMode = false
        binding.recyclerViewNews.adapter = newsAdapter
        viewModel.clearSearch()
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
    }

    private fun navigateToNewsDetail(news: News) {
        // Отметить как прочитанную
        viewModel.markAsRead(news.id)
        
        // Навигация к детальному экрану
        val bundle = Bundle().apply {
            putString("newsId", news.id)
        }
        findNavController().navigate(R.id.action_newsFragment_to_newsDetailFragment, bundle)
    }

    private fun shareNews(news: News) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${news.title}\n\n${news.url}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Поделиться новостью"))
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("По дате (новые)", "По дате (старые)", "По популярности")
        val sortValues = arrayOf("date_desc", "date_asc", "popularity")
        
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Сортировка")
        builder.setSingleChoiceItems(sortOptions, sortValues.indexOf(currentSortBy)) { dialog, which ->
            currentSortBy = sortValues[which]
            viewModel.setSortBy(currentSortBy)
            dialog.dismiss()
        }
        builder.show()
    }

    private fun showFiltersDialog() {
        // Реализация диалога с расширенными фильтрами
        Toast.makeText(requireContext(), "Фильтры в разработке", Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        binding.progressBarMain.show()
        binding.recyclerViewNews.hide()
        binding.layoutError.hide()
        binding.layoutEmpty.hide()
    }

    private fun hideLoading() {
        binding.progressBarMain.hide()
        binding.recyclerViewNews.show()
    }

    private fun showError(message: String) {
        binding.layoutError.show()
        binding.textViewError.text = message
        binding.buttonRetry.setOnClickListener {
            viewModel.refresh()
        }
        binding.recyclerViewNews.hide()
        binding.layoutEmpty.hide()
    }

    private fun hideError() {
        binding.layoutError.hide()
    }

    private fun showEmptyState() {
        binding.layoutEmpty.show()
        binding.textViewEmpty.text = "Нет новостей"
        binding.recyclerViewNews.hide()
    }

    private fun showEmptySearchState() {
        binding.layoutEmpty.show()
        binding.textViewEmpty.text = "Ничего не найдено"
        binding.recyclerViewNews.hide()
    }

    private fun hideEmptyState() {
        binding.layoutEmpty.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 