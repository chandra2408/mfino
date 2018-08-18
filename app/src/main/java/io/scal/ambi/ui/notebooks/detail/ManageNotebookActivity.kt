package io.scal.ambi.ui.notebooks.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.RelativeLayout
import com.ambi.work.R
import com.ambi.work.databinding.ActivityNotebookManageBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.SystemUtils
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.BottomSheetList
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.global.picker.FileResource
import io.scal.ambi.ui.global.picker.PickerViewController
import io.scal.ambi.ui.global.picker.PickerViewModel
import io.scal.ambi.ui.home.notifications.NotebookManageDetailAdapter
import io.scal.ambi.ui.notebooks.create.CreateNotebookActivity
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookDataState
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookErrorState
import io.scal.ambi.ui.notebooks.create.CreateOrDeleteNotebookState
import io.scal.ambi.ui.notebooks.list.NotebookData
import io.scal.ambi.ui.webview.viewer.FileViewerActivity
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass

@RuntimePermissions
class ManageNotebookActivity : BaseToolbarActivity<ManageNotebookViewModel, ActivityNotebookManageBinding>(), PickerViewController{

    override val layoutId: Int = R.layout.activity_notebook_manage
    override val viewModelClass: KClass<ManageNotebookViewModel> = ManageNotebookViewModel::class

    private val adapter by lazy { NotebookManageDetailAdapter(viewModel) }
    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>

    private val pickerViewModel: PickerViewModel by lazy {
        ViewModelProviders.of(this, viewModelFactory).get(PickerViewModel::class.java)
    }

    private var docsList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView()
        initBottomSheet()
        initObservers()
    }

    private fun initRecyclerView() {
        binding.rvNotebooks.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvNotebooks.setNestedScrollingEnabled(false);
        binding.rvNotebooks.adapter = adapter
        binding.rvNotebooks.setItemViewCacheSize(30)
        binding.rvNotebooks.isDrawingCacheEnabled = true
        binding.rvNotebooks.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
    }

    private fun initBottomSheet(){
        sheetBehavior = BottomSheetBehavior.from<RelativeLayout>(binding.bmsView)
        viewModel.bottomSheetState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if(it!= 0){
                        toggleBottomView(it)
                    }
                }

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.bottomSheetState.set(0)
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        })


        docsList.addAll(viewModel.notebookData.notes!!)
        docsList.addAll(viewModel.notebookData.files!!)
        refreshAdapter()
    }

    private fun refreshAdapter(){
        if(docsList!=null && docsList.size>0){
            adapter.updateData(docsList)
        }
    }

    private fun toggleBottomView(action: Int){
        binding.bmsView.setTitle(if(action==1) "add new" else "notebook options")
        binding.bmsView.setActionsList(
                if(action==1){
                    //listOf("edit and manage notebook","delete notebook","download notebook")
                    listOf("edit and manage notebook","delete notebook")
                }else{
                    listOf("add from your files","upload photos")
                },
                object : BottomSheetList.BottomSheetItemListener {
                    override fun selectedItem(value: String) {
                        sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
                        viewModel.bottomSheetState.set(0)

                        if(value.equals("delete notebook")){
                            SystemUtils.okCancelDialog(this@ManageNotebookActivity,
                                    "Delete this notebook?",
                                    "This action cannot be undone and your notebook will be removed",
                                    Runnable {viewModel.deleteNotebook()},
                                    Runnable {})
                        }else if(value.equals("edit and manage notebook")){
                            router.navigateTo(NavigateTo.CREATE_NOTEBOOK, viewModel.notebookData)
                        }else if(value.equals("upload photos")){
                            pickerViewModel.pickAnImage(this@ManageNotebookActivity, this@ManageNotebookActivity)
                        }else if(value.equals("add from your files")){
                            pickerViewModel.pickFile(this@ManageNotebookActivity)
                        }
                    }
                })
        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            viewModel.bottomSheetState.set(0)
        }
    }

    private fun initObservers(){
        viewModel.progressState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.progress.visibility = View.GONE

                    when (it) {
                        is CreateOrDeleteNotebookState.RefreshProgress   -> binding.progress.visibility = View.VISIBLE
                        is CreateOrDeleteNotebookState.NoProgress        -> binding.progress.visibility = View.GONE
                    }
                }
                .addTo(destroyDisposables)

        viewModel.errorState.asErrorState(binding.rootContainer,
                {
                    viewModel.progressState.set(CreateOrDeleteNotebookState.NoProgress)
                },
                {
                    when (it) {
                        is CreateOrDeleteNotebookErrorState.NoErrorState       -> ErrorState.NoError
                        is CreateOrDeleteNotebookErrorState.NonFatalErrorState -> ErrorState.NonFatalError(it.error)
                        is CreateOrDeleteNotebookErrorState.FatalErrorState    -> ErrorState.FatalError(it.error)
                    }
                },
                destroyDisposables)

        viewModel.dataState
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    viewModel.progressState.set(CreateOrDeleteNotebookState.NoProgress)
                    when (it) {
                        is CreateOrDeleteNotebookDataState.CreateOrDeleteNotebookSuccess -> {
                            SystemUtils.okDialog(this,it.message, Runnable { router.exitWithResult(ResultCodes.CREATE_NOTEBOOK, Activity.RESULT_OK) })
                        }
                        is CreateOrDeleteNotebookDataState.AttachedFileToNotebookSuccess -> {
                            SystemUtils.okDialog(this,it.fileData.message!!,
                                    Runnable {
                                        docsList.add(it.fileData)
                                        refreshAdapter() }
                            )
                        }
                    }
                }
                .addTo(destroyDisposables)
    }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {

            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.CREATE_NOTEBOOK     -> CreateNotebookActivity.createScreen(this@ManageNotebookActivity,data as NotebookData)
                        NavigateTo.OPEN_FILE     -> FileViewerActivity.createScreen(this@ManageNotebookActivity,data as String)
                        else                        -> super.createActivityIntent(screenKey, data)
                    }
        }

    companion object {

        internal val EXTRA_NOTEBOOK_DATA = "NOTEBOOK_DATA"

        fun createScreen(context: Context, chatDescription: NotebookData): Intent =
                Intent(context, ManageNotebookActivity::class.java).putExtra(EXTRA_NOTEBOOK_DATA, chatDescription)

    }

    override fun setPickedFile(fileResource: FileResource, image: Boolean) {
        viewModel.uploadFile(fileResource)
    }

    override fun showPickerDialogFragment(dialogFragment: DialogFragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.add(dialogFragment, null)
        ft.commitAllowingStateLoss()
    }

    override fun askForReadExternalStoragePermission() = notifyPickerViewModelAboutPermissionWithPermissionCheck()

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    internal fun notifyPickerViewModelAboutPermission() = pickerViewModel.onReadExternalStoragePermissionGranted(this)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pickerViewModel.onActivityResult(this, requestCode, resultCode, data)
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        onRequestPermissionsResult(requestCode, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}


