package uz.dckroff.statisfy.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import uz.dckroff.statisfy.domain.model.NewsData
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor() : BaseViewModel<NewsData>() 