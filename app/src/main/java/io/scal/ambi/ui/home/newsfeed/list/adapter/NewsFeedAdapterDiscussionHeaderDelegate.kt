package io.scal.ambi.ui.home.newsfeed.list.adapter

import com.ambi.work.R
import com.ambi.work.databinding.ItemNewsFeedHeaderDiscussionBinding
import io.scal.ambi.ui.home.classes.discussion.DiscussionViewModel

internal class NewsFeedAdapterDiscussionHeaderDelegate(private val headerElement: Any, viewModel: DiscussionViewModel) :
    NewsFeedAdapterDelegateBase<ItemNewsFeedHeaderDiscussionBinding>(viewModel) {

    override val layoutId: Int = R.layout.item_news_feed_header_discussion

    override fun isForViewType(items: List<Any>, position: Int): Boolean =
        headerElement == items[position]
}