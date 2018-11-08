package com.daksh.scalpelandroid.inject.viewmodel

import androidx.lifecycle.ViewModel
import com.daksh.scalpelandroid.screens.carve.CarveViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(CarveViewModel::class)
    protected abstract fun bindCarveViewModel(viewModel: CarveViewModel): ViewModel
}
