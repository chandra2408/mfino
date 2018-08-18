package io.scal.ambi.ui.webview.viewer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ambi.work.R
import com.ambi.work.databinding.ActivityWebviewBinding
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import java.io.Serializable
import kotlin.reflect.KClass

/**
 * Created by chandra on 17-08-2018.
 */
class FileViewerActivity : BaseToolbarActivity<FileViewerViewModel, ActivityWebviewBinding>() {

    override val layoutId: Int = R.layout.activity_webview
    override val viewModelClass: KClass<FileViewerViewModel> = FileViewerViewModel::class

    private var defaultToolbarType: ToolbarType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar()

        binding.webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            setSupportZoom(true)
        }
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(viewModel.url)
                return true
            }


            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                binding.progress.visibility = View.VISIBLE
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView, url: String) {
                binding.progress.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
        binding.webView.loadUrl(viewModel.url)

    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.label_file_viewer)),
                null,
                null,
                null,
                null)

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    companion object {

        internal val FILE_DETAILS="FILE_DETAILS"

        fun createScreen(context: Context?, data: FileDetails) =
                Intent(context, FileViewerActivity::class.java).putExtra(FILE_DETAILS,data)
    }

    private fun getURLToLoad(){

    }

    class FileDetails(url: String, fileType: String): Serializable

}