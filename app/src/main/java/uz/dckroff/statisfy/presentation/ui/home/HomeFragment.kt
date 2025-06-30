package uz.dckroff.statisfy.presentation.ui.home

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.databinding.FragmentHomeBinding
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.domain.model.HomeData
import uz.dckroff.statisfy.presentation.adapter.FactAdapter
import uz.dckroff.statisfy.presentation.viewmodel.HomeViewModel
import uz.dckroff.statisfy.utils.UiState
import uz.dckroff.statisfy.utils.gone
import uz.dckroff.statisfy.utils.visible

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
    private lateinit var factAdapter: FactAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupRecyclerView()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Настройка SwipeRefreshLayout
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
        
        // Настройка кнопки "More Facts"
        binding.btnMoreFacts.setOnClickListener {
            // TODO: Навигация к списку всех фактов
            Toast.makeText(requireContext(), "Coming soon in next update", Toast.LENGTH_SHORT).show()
        }
        
        // Настройка кнопки "See All"
        binding.tvSeeAllFacts.setOnClickListener {
            // TODO: Навигация к списку всех фактов
            Toast.makeText(requireContext(), "Coming soon in next update", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun setupRecyclerView() {
        factAdapter = FactAdapter(
            onFactClick = { fact ->
                // TODO: Открыть детальный просмотр факта
                Toast.makeText(requireContext(), "Fact: ${fact.title}", Toast.LENGTH_SHORT).show()
            },
            onShareClick = { fact ->
                shareFact(fact)
            },
            onFavoriteClick = { fact ->
                // TODO: Добавить в избранное
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            }
        )
        
        binding.rvRecentFacts.adapter = factAdapter
    }
    
    private fun observeViewModel() {
        // Наблюдение за состоянием обновления
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
        }
        
        // Наблюдение за состоянием данных
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showContent(state.data)
                    is UiState.Error -> showError(state.message)
                }
            }
        }
    }
    
    private fun showLoading() {
        binding.apply {
            progressBar.visible()
            contentLayout.gone()
            errorLayout.gone()
        }
    }
    
    private fun showContent(data: HomeData) {
        binding.apply {
            progressBar.gone()
            contentLayout.visible()
            errorLayout.gone()
            
            // Отображаем факт дня
            data.dailyFact?.let { fact ->
                displayDailyFact(fact)
            }
            
            // Отображаем недавние факты
            factAdapter.submitList(data.recentFacts)
        }
    }
    
    private fun displayDailyFact(fact: Fact) {
        binding.apply {
            tvFactTitle.text = fact.title
            tvFactContent.text = fact.content
            tvFactCategory.text = fact.category.name
            tvFactSource.text = "Source: ${fact.source}"
            
            // Настройка кнопок действий
            btnShare.setOnClickListener { shareFact(fact) }
            btnFavorite.setOnClickListener { 
                // TODO: Добавить в избранное
                Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            }
            btnCopy.setOnClickListener { copyFactToClipboard(fact) }
        }
    }
    
    private fun shareFact(fact: Fact) {
        val shareText = "${fact.title}\n\n${fact.content}\n\nSource: ${fact.source}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
    
    private fun copyFactToClipboard(fact: Fact) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Fact", "${fact.title}\n\n${fact.content}")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Fact copied to clipboard", Toast.LENGTH_SHORT).show()
    }
    
    private fun showError(message: String) {
        binding.apply {
            progressBar.gone()
            contentLayout.gone()
            errorLayout.visible()
            
            tvErrorMessage.text = message
            btnRetry.setOnClickListener {
                viewModel.loadHomeData()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 