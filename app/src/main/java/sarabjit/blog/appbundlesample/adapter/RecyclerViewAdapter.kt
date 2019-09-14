package sarabjit.blog.appbundlesample.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import kotlinx.android.synthetic.main.layout_tour.view.*
import sarabjit.blog.appbundlesample.R
import sarabjit.blog.appbundlesample.model.TourData
import sarabjit.blog.appbundlesample.listener.RecyclerviewClickHandler

class RecyclerViewAdapter(
    private val tourList: List<TourData>,
    private val clickListener: RecyclerviewClickHandler?
) :
    RecyclerView.Adapter<RecyclerViewAdapter.TourViewHolder>() {
    override fun getItemCount() = tourList.size


    override fun onBindViewHolder(tourViewHolder: TourViewHolder, count: Int) {
        tourViewHolder.title.setText(tourList.get(count).name)
        tourViewHolder.description.setText(tourList.get(count).description)
        tourViewHolder.imageView.setImageResource(tourList.get(count).image)
        tourViewHolder.itemView.setOnClickListener {
            Toast.makeText(
                tourViewHolder.description.context,
                tourList.get(count).toString(),
                Toast.LENGTH_LONG
            ).show()
            clickListener?.onClick(tourList[count], count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TourViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.layout_tour, parent, false)
        return TourViewHolder(
            view
        )
    }

    class TourViewHolder(localView: View) : RecyclerView.ViewHolder(localView) {
        val imageView = localView.imageView
        val title = localView.tvtitle
        val description = localView.tvdescription
    }
}

