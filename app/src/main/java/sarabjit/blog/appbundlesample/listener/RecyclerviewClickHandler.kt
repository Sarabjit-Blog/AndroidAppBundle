package sarabjit.blog.appbundlesample.listener

import android.support.annotation.NonNull
import sarabjit.blog.appbundlesample.model.TourData

interface RecyclerviewClickHandler {
    fun onClick(@NonNull tour: TourData, position: Int)
}