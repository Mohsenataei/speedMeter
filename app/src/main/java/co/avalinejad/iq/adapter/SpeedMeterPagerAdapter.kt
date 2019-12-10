package co.avalinejad.iq.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.PagerAdapter

import co.avalinejad.iq.R
import co.avalinejad.iq.viewmodel.SpeedViewModel


class SpeedMeterPagerAdapter(private val activity: FragmentActivity, private var mViewModel: SpeedViewModel, private var limit:Int) : PagerAdapter() {

    private var speedMeters = arrayOf(
        R.drawable.speedo1,
        R.drawable.speedo2,
        R.drawable.speedo3
    )

    override fun instantiateItem(collection: ViewGroup, position: Int): Any {
        val item = speedMeters[position]
        val inflater = LayoutInflater.from(activity)
        val layout = inflater.inflate(R.layout.item_speed_meter, collection, false) as ViewGroup
        var image: ImageView = layout.findViewById(R.id.imgSpeedMeter)
        var txtSpeed: TextView = layout.findViewById(R.id.txtLiveSpeed)
        mViewModel.getSpeed().observe(activity, Observer {
            txtSpeed.text=it.toString()
            if (limit !=0 && it!! >= limit && position != speedMeters.size-1)
                txtSpeed.setTextColor(activity.resources.getColor(R.color.notifSpeed))
            else
                txtSpeed.setTextColor(activity.resources.getColor(android.R.color.white))
        })
        image.setImageResource(item)
        collection.addView(layout)
        return layout
    }

    override fun destroyItem(collection: ViewGroup, position: Int, view: Any) {
        collection.removeView(view as View)
    }

    override fun getCount(): Int {
        return speedMeters.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }


}