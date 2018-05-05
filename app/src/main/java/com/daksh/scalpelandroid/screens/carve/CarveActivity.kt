package com.daksh.scalpelandroid.screens.carve

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.base.BaseActivity
import com.daksh.scalpelandroid.inject.viewmodel.ViewModelFactory
import javax.inject.Inject

class CarveActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private lateinit var viewModel: CarveViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarveViewModel::class.java)
        setContentView(R.layout.carve_activity)
        setUpLiveDataObservers()
        setUpWidgets()
    }

    private fun setUpLiveDataObservers() {
    }

    private fun setUpWidgets() {
    }
}
