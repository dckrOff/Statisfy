package uz.dckroff.statisfy.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import uz.dckroff.statisfy.domain.model.HomeData
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeData>() 