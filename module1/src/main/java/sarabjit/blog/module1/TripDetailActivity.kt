package sarabjit.blog.module1


import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.layout_trip_detail.*
import sarabjit.blog.appbundlesample.activity.BaseSplitActivity
import sarabjit.blog.appbundlesample.activity.TOUR_DATA
import sarabjit.blog.appbundlesample.model.TourData

class TripDetailActivity : BaseSplitActivity() {
    private lateinit var tour: TourData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_trip_detail)
        tour = intent.getParcelableExtra(TOUR_DATA)
        Log.d(TOUR_DATA, tour.toString())
        init()
    }

    fun init() {
        tour.name.let { tripNameValue.setText(tour.name) }
        tour.description.let { tripDescriptionValue.setText(tour.description) }
        tour.inviteLetter.let { tripInviteLetter.setText(tour.inviteLetter) }
        tour.cost.let { tripCostValue.setText(tour.cost.toString()) }
        tour.image.let { tripImage.setImageResource(tour.image) }
    }
}
