package io.scal.ambi.ui.notebooks.create

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import android.widget.RelativeLayout
import com.ambi.work.R
import com.ambi.work.databinding.ActivityNewNotebookBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.SystemUtils
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.view.BottomSheetList
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.extensions.view.ToolbarType
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.navigation.NavigateTo
import io.scal.ambi.navigation.ResultCodes
import io.scal.ambi.ui.global.base.ErrorState
import io.scal.ambi.ui.global.base.activity.BaseNavigator
import io.scal.ambi.ui.global.base.activity.BaseToolbarActivity
import io.scal.ambi.ui.global.base.asErrorState
import io.scal.ambi.ui.home.chat.newmessage.UIDetailsChip
import io.scal.ambi.ui.notebooks.contact.ContactPickerActivity
import io.scal.ambi.ui.notebooks.list.NotebookData
import ru.terrakok.cicerone.Navigator
import kotlin.reflect.KClass


class CreateNotebookActivity : BaseToolbarActivity<CreateNotebookViewModel, ActivityNewNotebookBinding>() {

    override val layoutId: Int = R.layout.activity_new_notebook
    override val viewModelClass: KClass<CreateNotebookViewModel> = CreateNotebookViewModel::class
    private var defaultToolbarType: ToolbarType? = null
    private lateinit var sheetBehavior: BottomSheetBehavior<RelativeLayout>
    var REQUEST_CONTACT = 1
    var selectedContacts: List<UsersDetails>? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sheetBehavior = BottomSheetBehavior.from<RelativeLayout>(binding.bmsView)

        initNotebookColorPalette()
        initObservers()

        if(!viewModel.notebookData.title.isNullOrEmpty()){
            viewModel.isInEditableModel.set(true)
            viewModel.stateModel.set(CreateNotebookStateModel.DataInputStateModel(viewModel.notebookData.title,""))
            if(!viewModel.notebookData.color.isNullOrEmpty()){
                (binding.ivSelectedColor.getBackground() as GradientDrawable).setColor(SystemUtils.parseColor(viewModel.notebookData.color))
                viewModel.notebookColor = viewModel.notebookData.color
            }
            viewModel.privateSelected.set(true)
        }

        initToolbar()
    }

    private fun initToolbar() {
        defaultToolbarType = ToolbarType(IconImage(R.drawable.ic_close),
                Runnable { router.exit() },
                ToolbarType.TitleContent(if(viewModel.isInEditableModel.get()) getString(R.string.title_edit_notebook) else getString(R.string.title_new_notebook)),
                null,
                null,
                null,
                null)

        defaultToolbarType!!.makeScrolling()

        setToolbarType(defaultToolbarType)
    }

    private fun initNotebookColorPalette(){
        var colorsList = resources.getStringArray(R.array.notebook_colors).toList()

        for (i in 0 until colorsList.size) {
            val view = layoutInflater.inflate(R.layout.notebook_color_palette_item, binding.llContainer, false)
            view.setTag(colorsList.get(i))
            (view.getBackground() as GradientDrawable).setColor(Color.parseColor(colorsList.get(i)))
            binding.llContainer.addView(view)

            view.setOnClickListener(object : View.OnClickListener {

                override fun onClick(v: View) {
                    (binding.ivSelectedColor.getBackground() as GradientDrawable).setColor(Color.parseColor(colorsList.get(i)))
                    viewModel.notebookColor = colorsList.get(i)
                    binding.llContainer.visibility = View.GONE
                }
            })
        }

        (binding.ivSelectedColor.getBackground() as GradientDrawable).setColor(Color.parseColor(colorsList.get(0)))
        viewModel.notebookColor = colorsList.get(0)

        binding.llSelectNotebookColor.setOnClickListener(object : View.OnClickListener {

            override fun onClick(v: View) {
                binding.llContainer.visibility = if (binding.llContainer.visibility == View.VISIBLE) View.GONE else View.VISIBLE

            }
        });
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
                    when (it) {
                        is CreateOrDeleteNotebookDataState.GroupInfo -> {
                            viewModel.groupsList = it.newsFeed
                        }
                        is CreateOrDeleteNotebookDataState.ClassesDataInfo -> {
                            viewModel.classesList = it.newsFeed
                        }
                        is CreateOrDeleteNotebookDataState.CreateOrDeleteNotebookSuccess -> {
                            onSuccess(it.message)
                        }
                    }
                }
                .addTo(destroyDisposables)

        viewModel.actionSelectClass
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    toggleBottomSheet()
                }

        viewModel.actionSelectGroup
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    toggleBottomSheet()
                }

        sheetBehavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    viewModel.actionSelectGroup.set(false)
                    viewModel.actionSelectClass.set(false)
                }
            }

            override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
        })

        binding?.selectContactListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                var intent = ContactPickerActivity.createScreen(this@CreateNotebookActivity)
                if(selectedContacts!=null){
                    var bundle = Bundle()
                    bundle.putSerializable("DATA",ArrayList<UsersDetails>(selectedContacts))
                    intent.putExtras(bundle)
                }
                startActivityForResult(intent,REQUEST_CONTACT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            binding.chipsInput.selectedChipList.clear()
            selectedContacts = data?.getSerializableExtra("DATA") as List<UsersDetails>
            var uiChipList = selectedContacts!!.map { UIDetailsChip(it) }.toList()
            binding.chipsInput.selectedChipList = uiChipList
            viewModel.setSelectedContactListArray(selectedContacts!!)
        }
    }

    private fun toggleBottomSheet(){
        binding.bmsView.setActionsList(viewModel.getGroupNames(),object : BottomSheetList.BottomSheetItemListener {
            override fun selectedItem(value: String) {
                viewModel.selectedClassOrGroup.set(value)
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        })
        if(viewModel.actionSelectGroup.get() || viewModel.actionSelectClass.get()){
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }else{
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    override val navigator: Navigator
        get() = object : BaseNavigator(this) {
            override fun createActivityIntent(screenKey: String, data: Any?): Intent? =
                    when (screenKey) {
                        NavigateTo.SELECT_CONTACT     -> ContactPickerActivity.createScreen(this@CreateNotebookActivity)
                        else                        -> super.createActivityIntent(screenKey, data)
                    }
        }

    private fun onSuccess(message: String){
        SystemUtils.okDialog(this,message, Runnable {router.exitWithResult(ResultCodes.CREATE_NOTEBOOK, Activity.RESULT_OK)})
    }

    companion object {

        internal val EXTRA_NOTEBOOK_DATA = "NOTEBOOK_DATA"

        fun createScreen(context: Context?) =
                Intent(context, CreateNotebookActivity::class.java)

        fun createScreen(context: Context?, data: NotebookData) =
                Intent(context, CreateNotebookActivity::class.java).putExtra(EXTRA_NOTEBOOK_DATA, data)

    }
}