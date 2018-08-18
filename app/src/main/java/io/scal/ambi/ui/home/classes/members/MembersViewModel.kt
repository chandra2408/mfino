package io.scal.ambi.ui.home.classes.members

import android.content.Context
import android.databinding.ObservableField
import com.ambi.work.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.scal.ambi.extensions.binding.toObservable
import io.scal.ambi.extensions.rx.general.RxSchedulersAbs
import io.scal.ambi.extensions.view.IconImage
import io.scal.ambi.ui.global.base.BetterRouter
import io.scal.ambi.ui.global.search.SearchViewModel
import io.scal.ambi.ui.home.classes.ClassesData
import io.scal.ambi.ui.home.classes.about.*
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by chandra on 03-08-2018.
 */
class MembersViewModel @Inject internal constructor(private val context: Context, router: BetterRouter, private val interactor: IAboutInteractor,
                                                    private val rxSchedulersAbs: RxSchedulersAbs,@Named("aboutData") val memberdata: ClassesData,
                                                    val searchViewModel: MembersSearchViewModel
) : AboutViewModel(context, router, interactor, rxSchedulersAbs, memberdata), IMembersViewModel{


    val openAction = ObservableField<MembersData>()
    val membersCount = ObservableField<Int>()

    override fun openActionSheet(element: MembersData) {
        openAction.set(element)
    }

    init {



        val fields = HashMap<String,String>()

        if(memberdata.adminIds!=null && memberdata.adminIds.size>0){
            fields.putAll(memberdata.adminIds)
        }

        if(memberdata.membersIds!=null && memberdata.membersIds.size>0){
            fields.putAll(memberdata.membersIds)
        }

        if(memberdata.ownersIds!=null && memberdata.ownersIds.size>0){
            fields.putAll(memberdata.ownersIds)
        }

        var adminsList = ArrayList<MembersData>()
        var membersList = ArrayList<MembersData>()
        if(fields.size>0){
            interactor.getUserDetailsById(fields)
                    .subscribeOn(rxSchedulersAbs.ioScheduler)
                    .observeOn(rxSchedulersAbs.computationScheduler)
                    .observeOn(rxSchedulersAbs.mainThreadScheduler)
                    .subscribe(
                            {
                                for(s:MembersData in it){
                                    if((s.id in memberdata.admins!! || s.id in memberdata.owners!!) && s.id in memberdata.members!!){
                                        adminsList.add(s)
                                        membersList.add(s)
                                    }else if(s.id in memberdata.admins!! || s.id in memberdata.owners!!){
                                        adminsList.add(s)
                                    }else{
                                        membersList.add(s)
                                    }
                                }

                                var overallList = ArrayList<Any>()
                                //overallList.add(MemberCount(membersList.size))
                                overallList.add(Header("admins","admins", IconImage(R.drawable.ic_profile),"admins"))

                                for(s:MembersData in adminsList){
                                    if(s.id in memberdata.owners!!) {
                                        s.userType = s.userType+","+"owner"
                                    }
                                    if(s.id in memberdata.admins!!) {
                                        s.userType = s.userType+","+"admin"
                                    }
                                }

                                adminsList.distinctBy {
                                    it.userType
                                }

                                membersCount.set(adminsList.size);

                                overallList.addAll(adminsList)
                                overallList.add(HeaderSecondary("members","members",IconImage(R.drawable.ic_profile),"members",membersList.size))
                                overallList.addAll(membersList)
                                stateModel.set(AboutDataState.AboutFeed(stateModel.get()?.profileInfo, overallList))
                            },
                            {
                                //todo
                            })
                    .addTo(disposables)
        }


    }

}