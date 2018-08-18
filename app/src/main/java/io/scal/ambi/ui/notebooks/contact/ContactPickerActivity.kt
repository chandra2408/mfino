package io.scal.ambi.ui.notebooks.contact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.ambi.work.R
import com.ambi.work.databinding.ActivitySelectContactBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.SimpleDividerItemDecoration
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.ProgressState
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.base.asProgressStateSrl
import io.scal.ambi.ui.home.notifications.ContactAdapter
import kotlin.reflect.KClass

/**
 * Created by chandra on 26-07-2018.
 */

class ContactPickerActivity : BaseToolbarActivity<SelectContactViewModel, ActivitySelectContactBinding>() {

    override val layoutId: Int = R.layout.activity_select_contact
    override val viewModelClass: KClass<SelectContactViewModel> = SelectContactViewModel::class
    private val adapter by lazy { ContactAdapter(viewModel) }
    private var defaultToolbarType: ToolbarType? = null
    private var alreadySelectedContacts: List<UsersDetails>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var arguments = intent.getSerializableExtra("DATA")
        if(arguments!=null){
            alreadySelectedContacts = arguments as List<UsersDetails>
        }

        initToolbar()
        initRecyclerView()
        observeStates()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(getString(R.string.title_new_notebook)),
                null,
                null,
                null,
                null,
                "Done",Runnable {setResult()})

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    companion object {

        internal val EXTRA_PROFILE_UID = "EXTRA_PROFILE_UID"

        fun createScreen(context: Context) =
                Intent(context, ContactPickerActivity::class.java)

    }

    private fun setResult(){
        var selectedList = adapter.getSelectedContacts()
        var data = Bundle()
        data.putSerializable("DATA",ArrayList(selectedList))
        var intent = Intent()
        intent.putExtras(data)
        setResult(RESULT_OK,intent);
        finish()
    }

    private fun initRecyclerView() {
        binding.rvContacts.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvContacts.adapter = adapter
        binding.rvContacts.addItemDecoration(SimpleDividerItemDecoration(this));
        binding.rvContacts.setItemViewCacheSize(30)
        binding.rvContacts.isDrawingCacheEnabled = true
        binding.rvContacts.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    private fun observeStates() {
        viewModel.progressState.asProgressStateSrl(binding.srl,
                { adapter.showPageProgress(it) },
                {
                    when (it) {
                        is SelectContactState.TotalProgress -> ProgressState.NoProgress
                        is SelectContactState.EmptyProgress -> ProgressState.EmptyProgress
                        is SelectContactState.PageProgress -> ProgressState.PageProgress
                        is SelectContactState.RefreshProgress -> ProgressState.RefreshProgress
                        is SelectContactState.NoProgress -> ProgressState.NoProgress
                    }
                },
                destroyDisposables)

        viewModel.errorState.asErrorState(binding.srl,
                { viewModel.retry() },
                {
                    when (it) {
                        is SelectContactErrorState.NoErrorState -> ErrorState.NoError
                        is SelectContactErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is SelectContactErrorState.FatalErrorState -> ErrorState.FatalError(it.error)
                    }
                },
                destroyDisposables)

        var expandFirstTime = true
        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    when (it) {
                        is SelectContactDataState.SelectContactData -> {

                            if(alreadySelectedContacts!=null){
                                //map selected contacts to overall list
                                for(s:UsersDetails in alreadySelectedContacts!!){
                                    for(d:UsersDetails in it.newsFeed){
                                        if(d._id.equals(s._id)){
                                            d.isContactSelected = true
                                        }
                                    }
                                }
                            }

                            adapter.updateData(it.newsFeed.filter {
                                it._id!=null && !it._id.equals(viewModel.currentUser.get().uid) && !it._id.isNullOrEmpty()
                            }.toList())

                            if (expandFirstTime) {
                                binding.appBarLayout.setExpanded(true, false)
                                //binding.vFocus.requestFocus()
                                expandFirstTime = false
                            }
                        }
                        else  -> adapter.releaseData()
                    }
                }
                .addTo(destroyDisposables)

        viewModel.contactSelectorState.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.updateItem(it)
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



}