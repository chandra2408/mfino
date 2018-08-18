package io.scal.ambi.entity.user

import com.ambi.work.R

enum class UserType(val colorId: Int) {

    STUDENT(
            R.drawable.news_feed_actor_category_bg
    ),
    FACULTY(
            R.drawable.news_feed_actor_category_faculty_bg
    ),
    UNKNOWN(
            R.drawable.news_feed_actor_category_bg
    ),
    OWNER(
            R.drawable.news_feed_actor_category_owner
    ),
    ADMIN(
            R.drawable.news_feed_actor_category_admin
    ),
    STAFF(
            R.drawable.news_feed_actor_category_faculty_bg
    )

}