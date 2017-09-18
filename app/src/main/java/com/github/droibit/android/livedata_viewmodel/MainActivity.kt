package com.github.droibit.android.livedata_viewmodel

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.View

class MainActivity : AppCompatActivity() {

    companion object {

        private const val FRAGMENT_PROGRESS_DIALOG = "FRAGMENT_PROGRESS_DIALOG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun authenticationButtonClick(v: View) {
        showProgress()
    }

    private fun showProgress() {
        val fm = supportFragmentManager
        if (fm.findFragmentByTag(FRAGMENT_PROGRESS_DIALOG) == null) {
            val df = ProgressDialogFragment().apply {
                isCancelable = false
            }
            df.show(fm, FRAGMENT_PROGRESS_DIALOG)
        }
    }

    private fun hideProgress() {
        supportFragmentManager.findFragmentByTag(FRAGMENT_PROGRESS_DIALOG)
                ?.let {
                    (it as DialogFragment).dismiss()
                }
    }
}
