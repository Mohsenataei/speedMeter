package com.mohsen.speedmeter.fragment
import android.os.Bundle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton


import kotlinx.android.synthetic.main.fragment_speed_meter_intro.*
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.mohsen.speedmeter.R
import com.mohsen.speedmeter.activity.InnerPageActivity
import com.mohsen.speedmeter.activity.fragExtraName
import com.mohsen.speedmeter.activity.fragExtraValueTag
import com.mohsen.speedmeter.activity.fragName
import com.mohsen.speedmeter.util.HEAD_UP_SHOW
import com.mohsen.speedmeter.util.NORMAL_SHOW


const val MY_PREFS_NAME = "earthquake"
const val SPEED_LIMIT = "speed_limit"

class SpeedMeterIntroFragment : Fragment(), CompoundButton.OnCheckedChangeListener {
    lateinit var navController: NavController

    companion object {
        fun newInstance(): SpeedMeterIntroFragment {
            return SpeedMeterIntroFragment()
        }
    }

    private var limited: Boolean = false
    private var limit = 0
    private lateinit var editor:SharedPreferences

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
        editor = activity!!.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)

        edtLimit.setText(editor.getInt(SPEED_LIMIT, 60).toString())

        limit = Integer.parseInt(edtLimit.text.toString())



        imgUp.setOnClickListener {
            if (edtLimit.text.isNullOrEmpty()){
                edtLimit.setText("5")
            }else{
                limit = Integer.parseInt(edtLimit.text.toString())
                if (limit < 200){
                    limit += 5
                    edtLimit.setText((limit).toString())
                }
            }
        }

        imgDown.setOnClickListener {
            if (edtLimit.text.isNullOrEmpty()){
                edtLimit.setText("0")
            }else
            {
                limit = Integer.parseInt(edtLimit.text.toString())
                if (limit > 5){
                    limit -= 5
                    edtLimit.setText((limit).toString())
                }
            }

        }


        limitSwitch.setOnCheckedChangeListener(this)

        btnHeadUp.setOnClickListener {
            setSpeedToSharedPref()
            navigate(limit, HEAD_UP_SHOW)
        }

        btnSpeedMeter.setOnClickListener {
            setSpeedToSharedPref()
            navigate(limit, NORMAL_SHOW)
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

}
