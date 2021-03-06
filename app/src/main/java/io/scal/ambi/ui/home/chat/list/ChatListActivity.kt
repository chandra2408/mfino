package io.scal.ambi.ui.home.chat.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.TabLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.ambi.work.R
import com.ambi.work.databinding.FragmentChatListBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.entity.chat.PreviewChatItem
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.SimpleDividerItemDecoration
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.extensions.view.listenForEndScroll
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.home.chat.details.ChatDetailsActivity
import io.scal.ambi.ui.home.chat.list.adapter.ChatListAdapter
import io.scal.ambi.ui.home.chat.newmessage.ChatNewMessageActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class ChatListActivity : BaseToolbarActivity<ChatListViewModel, FragmentChatListBinding>() {

    override val layoutId: Int = R.layout.fragment_chat_list
    override val viewModelClass: KClass<ChatListViewModel> = ChatListViewModel::class

    private val adapter by lazy { ChatListAdapter(viewModel) }

    private var defaultToolbarType: ToolbarType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initToolbar();
        initRecyclerView()
        initTabBar()
        observeStates()

        binding.textChangedListener = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                adapter.filter.filter(""+p0)
            }
        }
    }

    private fun initTabBar() {
        viewModel.initChatTypeFilters(0)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.initChatTypeFilters(tab.position)
                viewModel.refresh()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
            }
        })
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.title_chat)),
                IconImage(R.drawable.ic_chat_add_user),
                Runnable { viewModel.createNewMessage() },
                IconImage(R.drawable.ic_new_video_call),
                Runnable { /*viewModel.openChat()*/ })
        defaultToolbarType!!.makeScrolling()
        setToolbarType(defaultToolbarType)
    }

    private fun initRecyclerView() {
        binding.rvChats.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvChats.adapter = adapter
        binding.rvChats.addItemDecoration(SimpleDividerItemDecoration(this));
        binding.rvChats.listenForEndScroll(1)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { viewModel.loadNextPage() }
                .addTo(destroyDisposables)
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                { adapter.showPageProgress(it) },
                {
                    when (it) {
                        is ChatListProgressState.EmptyProgress   -> ProgressState.EmptyProgress
                        is ChatListProgressState.PageProgress    -> ProgressState.PageProgress
                        is ChatListProgressState.RefreshProgress -> ProgressState.RefreshProgress
                        is ChatListProgressState.NoProgress      -> ProgressState.NoProgress
                    }
                },
                destroyDisposables)

        viewModel.errorState.asErrorState(binding.coordinator.parent as View,
                { viewModel.refresh() },
                {
                    when (it) {
                        is ChatListErrorState.NoErrorState       -> ErrorState.NoError
                        is ChatListErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is ChatListErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                    }
                },
                destroyDisposables)

        viewModel.filteredDataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is ChatListDataState.Empty -> adapter.releaseData()
                        is ChatListDataState.Data  -> adapter.updateData(it.chats)
                    }
                }
                .addTo(destroyDisposables)
    }

    private fun findAppBarLayout(root: View?): AppBarLayout? =
            when (root) {
                null            -> throw IllegalStateException("can not find app bar layout")
                is AppBarLayout -> root
                is ViewGroup    -> (0 until root.childCount).mapNotNull { findAppBarLayout(root.getChildAt(it)) }.firstOrNull()
                else            -> null
            }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.CHAT_DETAILS     -> ChatDetailsActivity.createScreen(this@ChatListActivity, data as PreviewChatItem)
                        NavigateTo.CHAT_NEW_MESSAGE -> ChatNewMessageActivity.createScreen(this@ChatListActivity)
                        else                        -> super.createActivityIntent(screenKey, data)
                    }
        }

    companion object {
        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
                Intent(context, ChatListActivity::class.java)

    }

}