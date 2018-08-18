package io.scal.ambi.entity.notebooks

import com.ambi.work.BuildConfig
import io.scal.ambi.extensions.SystemUtils
import io.scal.ambi.extensions.notNullOrThrow
import io.scal.ambi.extensions.view.IconImage
import java.io.Serializable


/**
 * Created by chandra on 14-08-2018.
 */

data class FileData(var updatedAt: String? = null,
                    var fileType: String? = null,
                    var _id: String? = null,
                    var createdAt: String? = null,
                    var name: String? = null,
                    var __v: String? = null,
                    var deleted: String? = null,
                    var url: String? = null,
                    var size: String? = null,
                    var documentColor: String? = null) : Serializable {

    var message: String? = null;
    var icon: IconImage = parseBanner()
    var isImage = SystemUtils.isImage(url!!)


    fun parseBanner(): IconImage {
        return url.notNullOrThrow("url")
                .let { if (it.startsWith("http")) it else BuildConfig.MAIN_IMAGES_URL + it }
                .let { IconImage(it) }
    }

}

