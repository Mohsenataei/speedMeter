package co.avalinejad.iq.fragment

import android.content.Context
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton


import kotlinx.android.synthetic.main.fragment_speed_meter_intro.*
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import co.avalinejad.iq.R
import co.avalinejad.iq.activity.InnerPageActivity
import co.avalinejad.iq.activity.fragExtraName
import co.avalinejad.iq.activity.fragExtraValueTag
import co.avalinejad.iq.activity.fragName
import co.avalinejad.iq.util.HEAD_UP_SHOW
import co.avalinejad.iq.util.NORMAL_SHOW
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds


const val MY_PREFS_NAME = "earthquake"
const val SPEED_LIMIT = "speed_limit"
const val FULL1_UNIT_ID = "ca-app-pub-8616739363688136/9546584662"
const val FULL2_UNIT_ID = "ca-app-pub-8616739363688136/5125151598"
//const val FULL2_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"


class SpeedMeterIntroFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    lateinit var navController: NavController
    private lateinit var mInterstitialAd1: InterstitialAd
    private lateinit var mInterstitialAd2: InterstitialAd


    companion object {
        fun newInstance(): SpeedMeterIntroFragment {
            return SpeedMeterIntroFragment()
        }
    }

    private var limited: Boolean = false
    private var limit = 0
    private lateinit var editor: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_speed_meter_intro, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        getView()!!.findNavController().navigate(R.id.action_LimitFragment_to_SpeedFragment)
        navController = Navigation.findNavController(view)
        MobileAds.initialize(activity) {}

        initFullAd1(context, FULL1_UNIT_ID)
        initFullAd2(context, FULL2_UNIT_ID)

        prepareAd(mInterstitialAd1)
        prepareAd(mInterstitialAd2)




        editor = activity!!.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)

        edtLimit.setText(editor.getInt(SPEED_LIMIT, 60).toString())

        limit = Integer.parseInt(edtLimit.text.toString())




        imgUp.setOnClickListener {
            if (edtLimit.text.isNullOrEmpty()) {
                edtLimit.setText("5")
            } else {
                limit = Integer.parseInt(edtLimit.text.toString())
                if (limit < 200) {
                    limit += 5
                    edtLimit.setText((limit).toString())
                }
            }
        }

        imgDown.setOnClickListener {
            if (edtLimit.text.isNullOrEmpty()) {
                edtLimit.setText("0")
            } else {
                limit = Integer.parseInt(edtLimit.text.toString())
                if (limit > 5) {
                    limit -= 5
                    edtLimit.setText((limit).toString())
                }
            }

        }


        limitSwitch.setOnCheckedChangeListener(this)

        btnHeadUp.setOnClickListener {
            showInterstitial1(mInterstitialAd1, HEAD_UP_SHOW)
            setSpeedToSharedPref()
            //navigate(limit, HEAD_UP_SHOW)
        }

        btnSpeedMeter.setOnClickListener {
            showInterstitial1(mInterstitialAd2, NORMAL_SHOW)
            setSpeedToSharedPref()
        }

    }

    private fun setSpeedToSharedPref() {
        editor.edit().putInt(SPEED_LIMIT, limit).apply()
    }

    private fun navigate(limitSpeed: Int, type: String) {
        val limit = if (limited) limitSpeed else 0
        val intent = Intent(context, InnerPageActivity::class.java)
        intent.putExtra(fragName, "SpeedMeterFragment")
        intent.putExtra(fragExtraName, type)
        intent.putExtra(fragExtraValueTag, limit)
        startActivity(intent)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        limited = isChecked
    }

    private fun initFullAd1(context: Context?, id: String) {
        btnHeadUp.visibility = View.GONE
        mInterstitialAd1 = InterstitialAd(context).apply {
            adUnitId = id
            adListener = (object : AdListener() {
                override fun onAdLoaded() {
                    Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                    Log.d("admob", "onAdLoaded()")
                    btnHeadUp.visibility = View.VISIBLE

                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    btnHeadUp.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        "onAdFailedToLoad1() with error code: $errorCode",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("admob", "onAdFailedToLoad() with error code: $errorCode")
                }

                override fun onAdClosed() {
                    prepareAd(mInterstitialAd1)
                    navigate(limit, HEAD_UP_SHOW)
                }
            })
        }
    }

    private fun initFullAd2(context: Context?, id: String) {
        btnSpeedMeter.visibility = View.GONE

        mInterstitialAd2 = InterstitialAd(context).apply {
            adUnitId = id
            adListener = (object : AdListener() {
                override fun onAdLoaded() {
                    Toast.makeText(context, "onAdLoaded()", Toast.LENGTH_SHORT).show()
//                    Log.d("admob", "onAdLoaded()")
                    btnSpeedMeter.visibility = View.VISIBLE
                }

                override fun onAdFailedToLoad(errorCode: Int) {
                    btnSpeedMeter.visibility = View.VISIBLE
                    Toast.makeText(
                        context,
                        "onAdFailedToLoad2() with error code: $errorCode",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("admob", "onAdFailedToLoad() with error code: $errorCode")
                }

                override fun onAdClosed() {
                    prepareAd(mInterstitialAd2)
                    navigate(limit, NORMAL_SHOW)


                }
            })
        }
    }

    private fun showInterstitial1(interstitialAd: InterstitialAd, showType: String) {
        if (interstitialAd.isLoaded) {
            btnSpeedMeter.visibility = View.VISIBLE

            interstitialAd.show()
            setSpeedToSharedPref()
        } else {
            Toast.makeText(activity, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            btnSpeedMeter.visibility = View.VISIBLE
            navigate(limit, showType)

        }
    }

    private fun startActivity() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun showInterstitial2() {
        if (mInterstitialAd2.isLoaded) {
            mInterstitialAd2.show()
        } else {
//            Toast.makeText(activity, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            setSpeedToSharedPref()
            //navigate(limit, HEAD_UP_SHOW)
        }
    }


    private fun prepareAd(interstitialAd: InterstitialAd) {
        if (!interstitialAd.isLoading && !interstitialAd.isLoaded) {
            //btnSpeedMeter.visibility = View.GONE
//            val adRequest = AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                .build()
            val adRequest = AdRequest.Builder().addTestDevice("A9C7C081C1B6772A522BA7536AAEA707").build()
            interstitialAd.loadAd(adRequest)
        }

    }


}
