package io.scal.ambi.ui.home.classes.members

import android.content.Context
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.search.SearchViewModel
import javax.inject.Inject

/**
 * Created by chandra on 09-08-2018.
 */
internal class MembersSearchViewModel @Inject constructor(context: Context,
                                                          router: BetterRouter) : SearchViewModel(context, router) {
}