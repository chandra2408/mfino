package io.scal.ambi.ui.home.classes

import io.reactivex.Single
import io.scal.ambi.entity.feed.NewsFeedItem
import io.scal.ambi.model.data.server.responses.newsfeed.PostsResponse
import io.scal.ambi.ui.home.classes.about.MembersResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ClassesApi {

    @GET("v1/classes")
    fun getClasses(): Single<ClassesResponse>

    @GET("v1/classes/filter/all")
    fun getAllClasses(): Single<List<ItemClasses>>

    @GET("v1/users?populate[]=profilePicture")
    fun getUserDetailsById(@QueryMap params: Map<String,String>): Single<MembersResponse>


}

