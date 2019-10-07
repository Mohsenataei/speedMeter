package co.avalinejad.iq.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.avalinejad.iq.R
import co.avalinejad.iq.adapter.SpeedMeterPagerAdapter
import co.avalinejad.iq.util.APP_ADDRESS
import co.avalinejad.iq.util.HEAD_UP_SHOW
import co.avalinejad.iq.util.NORMAL_SHOW
import co.avalinejad.iq.viewmodel.SpeedViewModel
import kotlinx.android.synthetic.main.fragment_speed_meter.*
import kotlin.math.roundToInt
import com.google.android.gms.location.ActivityRecognition
import android.app.PendingIntent
import com.google.android.gms.common.api.GoogleApiClient
import co.avalinejad.iq.service.DetectedActivitiesIntentService
import co.avalinejad.iq.util.ActivityStore
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.DetectedActivity
import ir.ihome.agencyapp.extentions.toPersianNumbers


const val ARG_LIMIT = "limit"
const val ARG_SHOW_TYPE = "showType"
var minDistatnceChangeForGPSUpdate: Long = 0L // 1 meters
var minTimeChangeForGPSUpdate = 0L
var updateCounter = 0
const val MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1

private lateinit var googleApiClient: GoogleApiClient

open class SpeedMeterFragment : Fragment(), LocationListener, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {
    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }


    companion object {
        fun newInstance(limit: Int, type: String): SpeedMeterFragment {
            val fragment = SpeedMeterFragment()
            val args = Bundle()
            args.putInt(ARG_LIMIT, limit)
            args.putString(ARG_SHOW_TYPE, type)
            fragment.arguments = args
            return fragment
        }
    }


    lateinit var adapter: SpeedMeterPagerAdapter
    lateinit var speedViewModel: SpeedViewModel
    private var startTime: Long = 0
    private var requestingLocationUpdates = false
    private var firstTime = false
    private var firstUse = false
    private var status = false
    private var limit: Int = 0
    private var maxSpeed: Int = 0
    private var averageSpeed = 0.0
    private var distanceFromStart = 0
    internal var firstLat = 0.0
    internal var firstLng = 0.0
    internal var lastLat = 0.0
    internal var lastLng = 0.0
    internal var lastTime = System.currentTimeMillis()


    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var isGPSTrackingEnabled = false
    private lateinit var locationManager: LocationManager
    private lateinit var providerInfo: String
    private lateinit var location: Location
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()

    private var updateCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_speed_meter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        speedViewModel = ViewModelProviders.of(activity!!).get(SpeedViewModel::class.java)
        limit = arguments!!.getInt(ARG_LIMIT)
        val showType = arguments!!.getString(ARG_SHOW_TYPE)

        configShowType(showType)
        adapter = SpeedMeterPagerAdapter(activity!!, speedViewModel, limit)
        speedMeterViewPager.adapter = adapter
        indicator.setViewPager(speedMeterViewPager)
        adapter.registerDataSetObserver(indicator.getDataSetObserver())
        lastTime = System.currentTimeMillis()
        btnStop.setOnClickListener {
            when (!requestingLocationUpdates) {
                true -> {
                    stopUsingGPS()
                    btnStop.text = resources.getText(R.string.start)
                    speedViewModel.speed.postValue(0)
                    status = true
                    imgShareSpeedMeter.visibility = View.VISIBLE
                    requestingLocationUpdates = true
                }
                false -> {
                    launchTracker()
                    btnStop.text = resources.getText(R.string.stop)
                    status = false
                    imgShareSpeedMeter.visibility = View.GONE
                    requestingLocationUpdates = false
                }
            }
        }
        imgShareSpeedMeter.setOnClickListener {
            var message ="سلام من با اپلیکیشن هشدار تونستم اطلاعات سرعت و مسافت خودم رو مشاهده کنم."
                    "$message \nبالاترین سرعت: $maxSpeed کیلومتر در ساعت \nسرعت میانگین: $averageSpeed کیلومتر در ساعت \nمسافت طی شده: $distanceFromStart متر"
            message += "\nشما هم میتونید برای دریافت این اپلیکیشن از لینک زیر استفاده کنید"
            message += "\n$APP_ADDRESS"
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, resources.getText(R.string.send_to)))
        }

        if (askAccessFineLocationPermission())
            launchTracker()

    }

    override fun onStart() {
        super.onStart()
        startActivityRecognition()
    }

    private fun launchTracker() {
        getLocation()
        if (this.isGPSTrackingEnabled)
            updateUI()
        else
            showSettingsAlert()
    }

    override fun onResume() {
        super.onResume()

        if (firstUse)
            launchTracker()
        firstUse = true
    }

    private fun getLocation() {

        try {
            locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            // Prepare Provider
            if (isGPSEnabled) {
                this.isGPSTrackingEnabled = true
                providerInfo = LocationManager.NETWORK_PROVIDER

            } else if (isNetworkEnabled) {
                this.isGPSTrackingEnabled = true
                providerInfo = LocationManager.NETWORK_PROVIDER
            }

            if (!providerInfo.isEmpty()) {
                // Check Permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && activity!!.checkSelfPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                )
                    return


                requestLocationUpdate()


                location = if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null)
                    locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                else
                    locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                updateGPSCoordinates()


            }
        } catch (e: Exception) {
            Log.e("SpeedMeter", "Impossible to connect to LocationManager", e)
        }

    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdate() {
        locationManager.removeUpdates(this)

        locationManager.requestLocationUpdates(
            "gps",
            minTimeChangeForGPSUpdate,
            minDistatnceChangeForGPSUpdate.toFloat(),
            this
        )

        locationManager.requestLocationUpdates(
            "network",
            minTimeChangeForGPSUpdate,
            minDistatnceChangeForGPSUpdate.toFloat(),
            this
        )
    }

    private fun stopUsingGPS() {
        locationManager.removeUpdates(this)
    }

    private fun updateGPSCoordinates() {
        try {
            latitude = location.latitude
            longitude = location.longitude
        } catch (e: Exception) {
            Log.e("SpeedMeter", "Impossible to connect to LocationManager", e)
        }
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        updateGPSCoordinates()
        updateUI()
    }

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(activity)

        alertDialog.setTitle("عدم دسترسی به GPS")

        alertDialog.setMessage("لطفا دسترسی GPS را فعال نمایید")

        alertDialog.setPositiveButton("تنظیمات") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity!!.startActivity(intent)
        }

        alertDialog.setNegativeButton(
            "لغو"
        ) { dialog, which -> dialog.cancel() }

        alertDialog.show()
    }

    private fun configShowType(type: String) {
        when (type) {
            NORMAL_SHOW -> {
                txtHeadUpSpeed.visibility = View.GONE
                limitParent.visibility = View.VISIBLE
            }
            HEAD_UP_SHOW -> {
                txtHeadUpSpeed.visibility = View.VISIBLE
                limitParent.visibility = View.GONE
            }
        }
    }


    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Long {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        return Math.round(6371000 * c)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchTracker()
            }
        }
    }

    private fun askAccessFineLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (activity!!.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || activity!!.checkSelfPermission(
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), MY_PERMISSIONS_ACCESS_FINE_LOCATION
                )
                false
            } else
                true
        }
        return true
    }


    private fun updateUI() {
        try {
            updateCounter++

            //for the first 20 updates we want gps coordinates as fast as possible so we get more accurate location.
            //after that we use normal values that ops for performance.
            if (updateCounter == 20) {
                minDistatnceChangeForGPSUpdate = 1L
                minTimeChangeForGPSUpdate = 1000L
                requestLocationUpdate()
            }

            if (!firstTime)
                startTime = System.currentTimeMillis()
            firstTime = true

            if (limit > 0)
                txtSpeedLimitValue.text = limit.toString()
            else
                txtSpeedLimitValue.text = getString(R.string.not_set)

            val lat = location.latitude
            val lng = location.longitude
            val time = System.currentTimeMillis()
            if (lat != 0.0 && lng != 0.0) { // We Are Connected

                // Set First Data
                if (lastLat == 0.0) {
                    lastLat = lat
                    lastLng = lng
                    return
                }

                // Calculate
                var distanceM = 0.0
                var speedMS = 0.0
                var speedKMH = 0.0
                val duration = Math.round(((time - lastTime) / 1000).toFloat()).toDouble() // Second
                distanceM = calculateDistance(lastLat, lastLng, lat, lng).toDouble()
                speedMS = distanceM / duration
                speedKMH = speedMS * 3.6


                // Set last data
                lastLat = lat
                lastLng = lng
                lastTime = time

                Log.e("alz", speedKMH.toString() + " - " + location.speed)

                speedKMH = location.speed.toDouble() * 3.6
                Log.e("alz", "speed: " + speedKMH)

                if (speedKMH > 150.0)
                    return

                val stillProbability = ActivityStore.currentActivity[DetectedActivity.STILL] ?: 0
                if (stillProbability > 75) {
                    speedKMH = 0.0
                }

                if (firstLat == 0.0) {//first time we have the correct value
                    firstLat = lat
                    firstLng = lng
                }

                var numList =  listOf(4,2,4,6,8,9,3,5,6,7,2,5)


                // prepare result
                speedKMH = Math.ceil(speedKMH) // round up

                //calculating average speed with use of previous value to avoid storing and summing up all values.
                averageSpeed = ((averageSpeed * updateCounter + speedKMH) / (updateCounter +1))
                updateCounter++

                var totalDistanceM = calculateDistance(firstLat, firstLng, lat, lng).toDouble()
                if (totalDistanceM < 10)
                    totalDistanceM = 0.0

                txtDistanceValue.text = totalDistanceM.toString() + " متر"

                if (limit != 0 && speedKMH.toInt() > limit)
                    txtHeadUpSpeed.setTextColor(resources.getColor(R.color.notifSpeed))
                else
                    txtHeadUpSpeed.setTextColor(resources.getColor(R.color.white))

                speedViewModel.speed.postValue(speedKMH.toInt())
                txtHeadUpSpeed.text = speedKMH.toInt().toString()

                if (speedKMH.toInt() > maxSpeed) {
                    maxSpeed = speedKMH.toInt()
                    txtMaxSpeed.text = maxSpeed.toString()
                }

                lastTime = System.currentTimeMillis()
                val timeDiff = lastTime - startTime
                if (timeDiff > 0) {
                    txtAverageSpeedValue.text = averageSpeed.roundToInt().toString()
                } else {
                    txtAverageSpeedValue.text = speedKMH.toInt().toString()
                }

            }

        } catch (e: Exception) {
            Log.e("SpeedMeter", "Impossible to connect to LocationManager", e)
        }

    }

    private fun startActivityRecognition() {
        googleApiClient = GoogleApiClient.Builder(context!!)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(ActivityRecognition.API)
            .build()

        googleApiClient.connect()
    }

    override fun onConnected(p0: Bundle?) {
        if (googleApiClient.isConnected)
            return

        val intent = Intent(context, DetectedActivitiesIntentService::class.java)
        val pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
            googleApiClient,
            300 /* detection interval */,
            pendingIntent
        )
    }

    override fun onStop() {
        super.onStop()
        if (googleApiClient.isConnected)
            googleApiClient.disconnect()
    }
}
