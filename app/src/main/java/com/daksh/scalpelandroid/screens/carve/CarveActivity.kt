package com.daksh.scalpelandroid.screens.carve

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import com.daksh.scalpelandroid.R
import com.daksh.scalpelandroid.base.BaseActivity
import com.daksh.scalpelandroid.extensions.observeK
import com.daksh.scalpelandroid.inject.viewmodel.ViewModelFactory
import com.jakewharton.rxbinding2.view.clicks
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
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

    private lateinit var snackBarCarving: Snackbar

    private lateinit var listAdapter: CarvedFilesListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(CarveViewModel::class.java)
        setContentView(R.layout.carve_activity)
        setUpWidgets()
        setUpLiveDataObservers()
        requestStoragePermission()
    }

    private fun setUpWidgets() {
        buttonSelectFile.clicks().subscribe {
            MaterialFilePicker()
                    .withActivity(this)
                    .withRequestCode(REQUEST_CODE_FILE_PICKER)
                    .withHiddenFiles(true)
                    .start()
        }

        buttonCarve.clicks().subscribe {
            viewModel.carve()
        }

        snackBarCarving = Snackbar.make(listResults, getString(R.string.carving),
                Snackbar.LENGTH_INDEFINITE)
        snackBarCarving.setAction(getString(R.string.cancel)) {
            viewModel.cancelCarving()
        }
    }

    private fun setUpLiveDataObservers() {
        viewModel.liveSelectedSourceFilePath.observeK(this) {
            it?.let { textSelectedFile.text = it }
        }

        viewModel.liveCarving.observeK(this) {
            it?.let {
                buttonSelectFile.isEnabled = !it
                buttonCarve.isEnabled = !it

                if (it) {
                    snackBarCarving.show()
                } else {
                    snackBarCarving.dismiss()
                }
            }
        }

        viewModel.liveCarvedFiles.observeK(this) {
            it?.let {
                listAdapter.updateList(it)
            }
        }
    }

    private fun requestStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?,
                            token: PermissionToken?) {
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    }
                })
                .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_FILE_PICKER && resultCode == Activity.RESULT_OK) {
            viewModel.selectedFile = File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH))
        }
    }
}
