package uz.dckroff.statisfy.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import uz.dckroff.statisfy.domain.model.FavoritesData
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor() : BaseViewModel<FavoritesData>() 