package uz.dckroff.statisfy.presentation.ui.facts

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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.dckroff.statisfy.R
import uz.dckroff.statisfy.databinding.FragmentFactDetailBinding
import uz.dckroff.statisfy.domain.model.Fact
import uz.dckroff.statisfy.presentation.viewmodel.FactsViewModel
import uz.dckroff.statisfy.utils.UiState
import uz.dckroff.statisfy.utils.gone
import uz.dckroff.statisfy.utils.visible

/**
 * Фрагмент для детального просмотра факта
 */
@AndroidEntryPoint
class FactDetailFragment : Fragment() {

    private var _binding: FragmentFactDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FactsViewModel by viewModels()
    private val args: FactDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        loadFactDetails()
        observeViewModel()
    }

    private fun setupUI() {
        // Настройка тулбара
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Настройка кнопок действий
        binding.btnShare.setOnClickListener {
            viewModel.currentFact.value?.let { fact ->
                shareFact(fact)
            }
        }

        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        binding.btnCopy.setOnClickListener {
            viewModel.currentFact.value?.let { fact ->
                copyFactToClipboard(fact)
            }
        }
    }

    private fun loadFactDetails() {
        viewModel.getFactById(args.factId)
    }

    private fun observeViewModel() {
        // Наблюдение за состоянием факта
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.factState.collectLatest { state ->
                when (state) {
                    is UiState.Loading -> showLoading()
                    is UiState.Success -> showContent(state.data)
                    is UiState.Error -> showError(state.message)
                    else -> {
                        TODO()
                    }
                }
            }
        }

        // Наблюдение за состоянием избранного
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFavorite ->
            binding.btnFavorite.setIconResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )
        }
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visible()
            contentLayout.gone()
            errorLayout.gone()
        }
    }

    private fun showContent(fact: Fact) {
        binding.apply {
            progressBar.gone()
            contentLayout.visible()
            errorLayout.gone()

            // Отображаем детали факта
            tvFactTitle.text = fact.title
            tvFactContent.text = fact.content
            tvFactCategory.text = fact.category.name
            tvFactSource.text = "Источник: ${fact.source}"
            tvFactDate.text = "Опубликовано: ${fact.createdAt}"

            // Проверяем статус избранного для этого факта
            viewModel.checkFavoriteStatus(fact.id)
        }
    }

    private fun shareFact(fact: Fact) {
        val shareText = "${fact.title}\n\n${fact.content}\n\nИсточник: ${fact.source}"
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Поделиться фактом")
        startActivity(shareIntent)
    }

    private fun copyFactToClipboard(fact: Fact) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Факт", "${fact.title}\n\n${fact.content}")
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Факт скопирован в буфер обмена", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showError(message: String) {
        binding.apply {
            progressBar.gone()
            contentLayout.gone()
            errorLayout.visible()

            tvErrorMessage.text = message
            btnRetry.setOnClickListener {
                loadFactDetails()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 