package com.daksh.scalpelandroid.base

import android.view.View
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity : DaggerAppCompatActivity() {

    protected fun shortSnackbar(view: View, resId: Int) =
            Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).show()

    protected fun shortSnackbar(view: View, text: String) =
            Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()

    protected fun longSnackbar(view: View, resId: Int) =
            Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show()

    protected fun longSnackbar(view: View, text: String) =
            Snackbar.make(view, text, Snackbar.LENGTH_LONG).show()
}
