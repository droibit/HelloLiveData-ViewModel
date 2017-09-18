package com.github.droibit.android.livedata_viewmodel

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.github.droibit.android.livedata_viewmodel.PocketOAuthViewModel.AccessTokenEvent
import com.github.droibit.android.livedata_viewmodel.PocketOAuthViewModel.RequestTokenEvent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {

        private const val FRAGMENT_PROGRESS_DIALOG = "FRAGMENT_PROGRESS_DIALOG"
    }

    private val oauthViewModel: PocketOAuthViewModel by lazy {
        ViewModelProviders.of(this).get<PocketOAuthViewModel>()
    }

    private val redirectUri: Uri by lazy { Uri.parse(getString(R.string.pocket_redirect_uri)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        oauthViewModel.requestToken.observe(this) {
            when (it) {
                is RequestTokenEvent.Success -> {
                    val uri = it.value.run { asAuthenticationUri(redirectUri.toString()) }
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                }
                is RequestTokenEvent.Error -> {
                    showToast(msg = it.error)
                    hideProgress()
                }
            }
        }

        oauthViewModel.accesssToken.observe(this) {
            when (it) {
                is AccessTokenEvent.Success -> {
                    message.text = "Authenticated with ${it.value.userName}"
                }
                is AccessTokenEvent.Error -> {
                    showToast(msg = "Failed...")
                }
            }
            hideProgress()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val data = intent.data
        val redirectUriString = getString(R.string.pocket_redirect_uri)
        if ("$data".startsWith(redirectUriString)) {
            val requestTokenEvent: RequestTokenEvent = oauthViewModel.requestToken.value!!
            oauthViewModel.getAccessToken((requestTokenEvent as RequestTokenEvent.Success).value.code)
        } else {
            hideProgress()
        }
    }

    fun authenticationButtonClick(v: View) {
        showProgress()
        oauthViewModel.getRequestToken(redirectUri)
    }

    // Private


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

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
