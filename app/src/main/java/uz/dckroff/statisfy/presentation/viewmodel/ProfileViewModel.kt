package uz.dckroff.statisfy.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import uz.dckroff.statisfy.domain.model.ProfileData
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel<ProfileData>() 