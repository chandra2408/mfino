package io.scal.ambi.ui.notebooks.create

import android.content.Context
import android.databinding.ObservableField
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.model.data.server.responses.user.UsersDetails
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.base.viewmodel.BaseUserViewModel
import io.scal.ambi.ui.global.base.viewmodel.toGoodUserMessage
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.notebooks.list.NotebookData
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 10-08-2018.
 */
class CreateNotebookViewModel @Inject internal constructor(private val context: Context,
                                                           router: BetterRouter,
                                                           private val interactor: ICreateOrDeleteNotebookInteractor,
                                                           rxSchedulersAbs: RxSchedulersAbs,
                                                           @Named("notebookData") val notebookData: NotebookData) :
        BaseUserViewModel(router,{interactor.loadCurrentUser()},rxSchedulersAbs){

    val stateModel = ObservableField<CreateNotebookStateModel>(CreateNotebookStateModel.DataInputStateModel(null, null))
    internal val progressState = ObservableField<CreateOrDeleteNotebookState>(CreateOrDeleteNotebookState.TotalProgress)
    internal val errorState = ObservableField<CreateOrDeleteNotebookErrorState>()
    val dataState = ObservableField<CreateOrDeleteNotebookDataState>()

    val privateSelected = ObservableField<Boolean>()
    val classesSelected = ObservableField<Boolean>()
    val groupsSelected = ObservableField<Boolean>()

    var actionSelectClass = ObservableField<Boolean>()
    var actionSelectGroup = ObservableField<Boolean>()
    val allMembersSelected = ObservableField<Boolean>()
    val specificPeopleSelected = ObservableField<Boolean>()

    var classesList = emptyList<ClassesData>()
    var groupsList = emptyList<GroupData>()

    var selectedClassOrGroup = ObservableField<String>()

    //Selected notebook color
    var notebookColor: String? = null

    //Selected contacts when spcific tab selected
    var selectedContactList = mutableListOf<String>()

    var isInEditableModel = ObservableField<Boolean>(false)

    fun selectedPrivate(){
        privateSelected.set(true)
        classesSelected.set(false)
        groupsSelected.set(false)
    }

    fun selectedClass(){
        privateSelected.set(false)
        classesSelected.set(true)
        groupsSelected.set(false)
        selectedClassOrGroup.set("")

        if(classesList.isEmpty()){
            progressState.set(CreateOrDeleteNotebookState.RefreshProgress)
            interactor.loadClasses(1).subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .subscribe(
                            {
                                progressState.set(CreateOrDeleteNotebookState.NoProgress)
                                dataState.set(CreateOrDeleteNotebookDataState.ClassesDataInfo(dataState.get()?.profileInfo, it))
                            },
                            { t ->
                                handleError(t)
                                progressState.set(CreateOrDeleteNotebookState.NoProgress)
                                errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState(t.toGoodUserMessage(context)))
                            })
                    .addTo(disposables)
        }
    }

    fun selectedGroup(){
        selectedClassOrGroup.set("")
        privateSelected.set(false)
        classesSelected.set(false)
        groupsSelected.set(true)

        if(groupsList.isEmpty()){
            progressState.set(CreateOrDeleteNotebookState.RefreshProgress)
            interactor.loadGroups().subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .subscribe(
                            {
                                progressState.set(CreateOrDeleteNotebookState.NoProgress)
                                dataState.set(CreateOrDeleteNotebookDataState.GroupInfo(dataState.get()?.profileInfo, it))
                            },
                            { t ->
                                handleError(t)
                                progressState.set(CreateOrDeleteNotebookState.NoProgress)
                                errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState(t.toGoodUserMessage(context)))
                            })
                    .addTo(disposables)
        }
    }

    fun allMembers(){
        allMembersSelected.set(true)
        specificPeopleSelected.set(false)
    }

    fun specificPeople(){
        specificPeopleSelected.set(true)
        allMembersSelected.set(false)
    }

    fun saveNotebook(){
        val currentStateModel = stateModel.get()
        if (currentStateModel is CreateNotebookStateModel.DataInputStateModel) {
            val notebookTitle = currentStateModel.notebookTitle.get()
            val notebookColor = notebookColor

            if(notebookTitle.isNullOrEmpty()){
                errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState("Please enter title"))
            }else if(notebookColor.isNullOrEmpty()){
                errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState("Please select color"))
            }else if(isInEditableModel.get()){
                progressState.set(CreateOrDeleteNotebookState.RefreshProgress)

                var array = JsonArray()
                array.add(notebookData.creator?._id!!)

                interactor.updateNotebook(notebookTitle,
                        notebookColor!!,
                        notebookData.creator?._id!!,
                        array,
                        notebookData.creator?._id!!,
                        notebookData._id!!)
                        .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                        .subscribe(
                                {
                                    dataState.set(CreateOrDeleteNotebookDataState.CreateOrDeleteNotebookSuccess(dataState.get()?.profileInfo, it.message!!))
                                },
                                {
                                    handleError(it, false)
                                    errorState.set(CreateOrDeleteNotebookErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                                    errorState.set(CreateOrDeleteNotebookErrorState.NoErrorState)
                                })
                        .addTo(disposables)
            }else{
                if(classesSelected.get() && selectedClassOrGroup.get().isNullOrEmpty()){
                    errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState("Please select class"))
                }else if(groupsSelected.get() && selectedClassOrGroup.get().isNullOrEmpty()){
                    errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState("Please select group"))
                }else if(specificPeopleSelected.get() && selectedContactList.size==0){
                    errorState.set(CreateOrDeleteNotebookErrorState.FatalErrorState("Please select contacts"))
                }else{
                    progressState.set(CreateOrDeleteNotebookState.RefreshProgress)
                    interactor.createNotebook(notebookTitle,
                            notebookColor!!,
                            currentUser.get().uid,
                            currentUser.get().uid,
                            getNotebookHost(),
                            getSelectedGroupOrClassId(),
                            getInviteType(),
                            getSelectedContactsInArray())
                            .compose(rxSchedulersAbs.getIOToMainTransformerSingle())
                            .subscribe(
                                    {
                                        dataState.set(CreateOrDeleteNotebookDataState.CreateOrDeleteNotebookSuccess(dataState.get()?.profileInfo, it.message!!))
                                    },
                                    {
                                        handleError(it, false)
                                        errorState.set(CreateOrDeleteNotebookErrorState.NonFatalErrorState(it.toGoodUserMessage(context)))
                                        errorState.set(CreateOrDeleteNotebookErrorState.NoErrorState)
                                    })
                            .addTo(disposables)
                }
            }
        }
    }

    private fun getSelectedGroupOrClassId(): String{
        if(classesSelected.get()){
            return (classesList.filter { it ->
                it.title.equals(selectedClassOrGroup.get())
            }.toList().get(0)._id)!!
        }else if(groupsSelected.get()){
            return (groupsList.filter { it ->
                it.groupDataItem.name.equals(selectedClassOrGroup.get())
            }.toList().get(0).groupDataItem.id)!!
        }else{
            return ""
        }
    }

    private fun getInviteType():String{
        if(privateSelected.get()){
            return "me"
        }else if((classesSelected.get() || groupsSelected.get()) && allMembersSelected.get()){
            return "all"
        }else {
            return "specific"
        }
    }

    private fun getNotebookHost(): String{
        if(groupsSelected.get()){
            return "group"
        }else if(classesSelected.get()){
            return "class"
        }else{
            return "me"
        }
    }

    fun actionSelectGroup() {
        if(groupsSelected.get()){
            actionSelectGroup.set(true)
            actionSelectClass.set(false)
            allMembersSelected.set(true)
        }else if(classesSelected.get()){
            actionSelectClass.set(true)
            actionSelectGroup.set(false)
            allMembersSelected.set(true)
        }else{
            actionSelectClass.set(false)
            actionSelectGroup.set(false)
        }
    }


    fun getGroupNames(): List<String>{
        var list = mutableListOf<String>()
        if(groupsSelected.get()){
            for(s:GroupData in groupsList){
                list.add(s.groupDataItem.name!!)
            }
        }else if(classesSelected.get()){
            for(s:ClassesData in classesList){
                list.add(s.title!!)
            }
        }else{
            emptyList<String>()
        }
        return list
    }

    fun setSelectedContactListArray(contacts: List<UsersDetails>){
        selectedContactList.clear()
        if(contacts!=null){
            for(s:UsersDetails in contacts){
                selectedContactList.add(s._id)
            }
        }
    }

    fun getSelectedContactsInArray(): JsonArray?{
        if(specificPeopleSelected.get()){
            var array = JsonArray()
            for(s: String in selectedContactList){
                var jsonObject = JsonObject()
                jsonObject.addProperty("user",s)
                array.add(jsonObject)
            }
            return array
        }else{
            return null
        }
    }


    init{
        selectedPrivate()
        allMembersSelected.set(true)
        specificPeopleSelected.set(false)
        classesSelected.set(false)
        groupsSelected.set(false)
    }
}