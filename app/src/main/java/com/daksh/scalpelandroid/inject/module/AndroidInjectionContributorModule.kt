package com.daksh.scalpelandroid.inject.module

import com.daksh.scalpelandroid.screens.carve.CarveActivity
import com.daksh.scalpelandroid.inject.scope.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class AndroidInjectionContributorModule {

    @ContributesAndroidInjector
    @ActivityScope
    protected abstract fun CarveActivity(): CarveActivity
}
