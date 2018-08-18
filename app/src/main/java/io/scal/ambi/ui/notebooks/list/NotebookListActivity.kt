package io.scal.ambi.ui.notebooks.list

/**
 * Created by chandra on 10-08-2018.
 */

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivityNotebooksListActivityBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.home.notifications.NotebookListAdapter
import io.scal.ambi.ui.notebooks.create.CreateNotebookActivity
import io.scal.ambi.ui.notebooks.detail.ManageNotebookActivity
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

class NotebookListActivity : BaseToolbarActivity<NotebookListViewModel, ActivityNotebooksListActivityBinding>() {

    override val layoutId: Int = R.layout.activity_notebooks_list_activity
    override val viewModelClass: KClass<NotebookListViewModel> = NotebookListViewModel::class
    private var defaultToolbarType: ToolbarType? = null
    private val adapter by lazy { NotebookListAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initToolbar()
        initRecyclerView()
        viewModel.init()
        observeStates()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_back),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.title_notebooks)),
                IconImage(R.drawable.ic_add_item_blue),
                Runnable { viewModel.createNewNotebook()},
                null,
                null)

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    private fun initRecyclerView() {
        binding.rvNotebooks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotebooks.setEmptyView(binding.llNotebookEmptyView);
        binding.rvNotebooks.adapter = adapter
        binding.rvNotebooks.setItemViewCacheSize(30)
        binding.rvNotebooks.isDrawingCacheEnabled = true
        binding.rvNotebooks.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    companion object {

        internal val EXTRA_USER_ID = "EXTRA_USER_ID"

        fun createScreen(context: Context?,uid: String?) =
                Intent(context, NotebookListActivity::class.java).putExtra(EXTRA_USER_ID,uid)
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                { adapter.showPageProgress(it) },
                {
                    when (it) {
                        is NotebookListState.TotalProgress   -> ProgressState.NoProgress
                        is NotebookListState.EmptyProgress   -> ProgressState.EmptyProgress
                        is NotebookListState.PageProgress    -> ProgressState.PageProgress
                        is NotebookListState.RefreshProgress -> ProgressState.RefreshProgress
                        is NotebookListState.NoProgress      -> ProgressState.NoProgress
                    }
                },
                destroyDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                { viewModel.retry() },
                {
                    when (it) {
                        is NotebookListErrorState.NoErrorState       -> ErrorState.NoError
                        is NotebookListErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is NotebookListErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                    }
                },
                destroyDisposables)

        var expandFirstTime = true
        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is NotebookListDataState.NotebookListData -> {
                            for(s:NotebookData in it.newsFeed){
                                    s.isOnlyYou = s.isOnlyYou(viewModel.userId)
                                    s.memberCount = s.memberCount(viewModel.userId)
                            }
                            it.newsFeed.reverse()
                            adapter.updateData(it.newsFeed)
                            if (expandFirstTime) {
                                binding.appBarLayout.setExpanded(true, false)
                                //binding.vFocus.requestFocus()
                                expandFirstTime = false
                            }
                        }
                        else -> adapter.releaseData()

                    }
                }
                .addTo(destroyDisposables)

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

    override val navigator: Navigator by lazy {
        object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.OPEN_NOTEBOOK_DETAILS -> {
                            ManageNotebookActivity.createScreen(this@NotebookListActivity,data as NotebookData)
                        }
                        NavigateTo.CREATE_NOTEBOOK -> {
                            CreateNotebookActivity.createScreen(this@NotebookListActivity)
                        }
                        else                              -> super.createActivityIntent(screenKey, data)
                    }
        }
    }
}