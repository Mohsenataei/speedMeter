package co.avalinejad.iq.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.DetectedActivity
import co.avalinejad.iq.R
import co.avalinejad.iq.adapter.SpeedMeterPagerAdapter
import co.avalinejad.iq.fragment.SpeedMeterFragment
import co.avalinejad.iq.util.APP_ADDRESS
import co.avalinejad.iq.util.ActivityStore
import co.avalinejad.iq.util.HEAD_UP_SHOW
import co.avalinejad.iq.util.NORMAL_SHOW
import co.avalinejad.iq.viewmodel.SpeedViewModel
import kotlinx.android.synthetic.main.fragment_speed_meter.*
import kotlin.math.roundToInt
const val ARG_LIMIT = "limit"
const val ARG_SHOW_TYPE = "showType"
const val MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1

class SpeedMeterActivity : AppCompatActivity(), LocationListener, GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {

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


    var minDistatnceChangeForGPSUpdate: Long = 0L // 1 meters
    var minTimeChangeForGPSUpdate = 0L
    var updateCounter = 0

    lateinit var googleApiClient: GoogleApiClient

    override fun onLocationChanged(location: Location) {
        this.location = location
        updateGPSCoordinates()
        updateUI()
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(provider: String?) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConnected(p0: Bundle?) {
        if (co.avalinejad.iq.fragment.googleApiClient.isConnected)
            return

        val intent = Intent(this, co.avalinejad.iq.service.DetectedActivitiesIntentService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
            co.avalinejad.iq.fragment.googleApiClient,
            300 /* detection interval */,
            pendingIntent
        )
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_speed_meter)

        startActivityRecognition()

        speedViewModel = ViewModelProviders.of(this).get(SpeedViewModel::class.java)

        limit = 0
        val showType = NORMAL_SHOW

        configShowType(showType!!)
        adapter = SpeedMeterPagerAdapter(this, speedViewModel, limit)
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
    private fun launchTracker() {
        getLocation()
        if (this.isGPSTrackingEnabled)
            updateUI()
        else
            showSettingsAlert()
    }
    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(this)

        alertDialog.setTitle("عدم دسترسی به GPS")

        alertDialog.setMessage("لطفا دسترسی GPS را فعال نمایید")

        alertDialog.setPositiveButton("تنظیمات") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }

        alertDialog.setNegativeButton(
            "لغو"
        ) { dialog, which -> dialog.cancel() }

        alertDialog.show()
    }


    private fun getLocation() {

        try {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
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

    private fun updateGPSCoordinates() {
        try {
            latitude = location.latitude
            longitude = location.longitude
        } catch (e: Exception) {
            Log.e("SpeedMeter", "Impossible to connect to LocationManager", e)
        }
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
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Long {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lng2 - lng1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + (Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2))
        val c = 2 * Math.asin(Math.sqrt(a))
        return Math.round(6371000 * c)
    }

    private fun askAccessFineLocationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
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
    private fun stopUsingGPS() {
        locationManager.removeUpdates(this)
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

    override fun onResume() {
        super.onResume()

        if (firstUse)
            launchTracker()
        firstUse = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_FINE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchTracker()
            }
        }
    }

    private fun startActivityRecognition() {
        co.avalinejad.iq.fragment.googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(ActivityRecognition.API)
            .build()

        co.avalinejad.iq.fragment.googleApiClient.connect()
    }


}