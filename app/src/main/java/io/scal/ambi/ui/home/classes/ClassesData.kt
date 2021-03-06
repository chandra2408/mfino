package io.scal.ambi.ui.home.classes

import io.scal.ambi.extensions.SystemUtils
import org.joda.time.DateTime
import java.io.Serializable


/**
 * Created by chandra on 02-08-2018.
 */
data class ClassesData(val _id: String, val title: String?,
                       val courseCode: String?,
                       val professorNames: String?,
                       val endDate: DateTime,
                       val term: String?,
                       val numberOfCredits: String?,
                       val description: String?,
                       val startDate: DateTime,
                       val admins: List<String>?,
                       val meetingDayAndTimes: String?,
                       val members: List<String>?,
                       val owners: List<String>?): Serializable{

    var courseCodeProferssorNames = courseCode + " - " + professorNames
    var startend = SystemUtils.toMMMddyyyy(startDate) + " - " + SystemUtils.toMMMddyyyy(endDate)
    var adminIds = getUserIds(admins!!,0)
    var membersIds = getUserIds(members!!,adminIds!!.size+1)
    var ownersIds = getUserIds(owners!!,adminIds!!.size+1)

    private fun getUserIds(admin: List<String>, index: Int): Map<String,String>{
        var myIndex = index
        val fields = HashMap<String,String>()
        for (i in 0 until admin!!.size) {
            val user = admin!!.get(i)
            myIndex = myIndex+1
            fields.put("userIds[$myIndex]", user)
        }
        return fields
    }

    enum class Category{
        CURRENT,
        PAST,
        FIND_CLASSES
    }

}