package com.daksh.scalpelandroid.screens.carve

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.base.BaseActivity
import com.daksh.scalpelandroid.extensions.observeK
import com.daksh.scalpelandroid.inject.viewmodel.ViewModelFactory
import com.jakewharton.rxbinding2.view.clicks
import com.nbsp.materialfilepicker.MaterialFilePicker
import com.nbsp.materialfilepicker.ui.FilePickerActivity
import kotlinx.android.synthetic.main.carve_activity.*
import java.io.File
import javax.inject.Inject

class CarveActivity : BaseActivity() {

    companion object {
        private const val REQUEST_CODE_FILE_PICKER = 123
    }

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
        viewModel.liveSelectedFilePath.observeK(this) {
            it?.let { textSelectedFile.text = it }
        }
    }

    private fun setUpWidgets() {
        buttonSelectFile.clicks().subscribe {
            MaterialFilePicker()
                    .withActivity(this)
                    .withRequestCode(REQUEST_CODE_FILE_PICKER)
                    .withHiddenFiles(true)
                    .start()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            viewModel.selectedFile = File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH))
        }
    }
}
