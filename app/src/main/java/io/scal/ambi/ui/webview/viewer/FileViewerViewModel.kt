package io.scal.ambi.ui.webview.viewer

import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseViewModel
import javax.inject.Inject
import javax.inject.Named

class FileViewerViewModel @Inject constructor(router: BetterRouter,
                                              @Named("url") val url: String) : BaseViewModel(router)