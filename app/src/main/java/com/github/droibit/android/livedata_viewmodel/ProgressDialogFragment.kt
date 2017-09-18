package com.github.droibit.android.livedata_viewmodel

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v4.app.DialogFragment


class ProgressDialogFragment : DialogFragment() {

    @Suppress("DEPRECATION")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return ProgressDialog(context).apply {
            setMessage("Authenticating...")
            setCanceledOnTouchOutside(false)
        }
    }
}