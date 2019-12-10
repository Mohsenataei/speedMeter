package co.avalinejad.iq.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jaredrummler.android.device.DeviceName
import co.avalinejad.iq.APP_NAME
import co.avalinejad.iq.R
import co.avalinejad.iq.network.RetrofitSingleton
import co.avalinejad.iq.util.USER


import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    val data = co.avalinejad.iq.model.AppInstall()

    internal var smsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // you should first check if there is such intent extra as activation_code then try to retrieve it.
            /*
             if (intent.hasExtra("ACTIVATION_CODE")) {
                 //do stuff
             }*/

            val activationCode = intent.getStringExtra("ACTIVATION_CODE").toString()
            Log.d("shera", "Activity $activationCode")
            login_code.setText(activationCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*Bundle bundle = new Bundle();s
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "1");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);*/


        if (co.avalinejad.iq.util.SecurePreferences.get(this@LoginActivity, USER) != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity()
            }
            finish()
        }

        checkForUpdate()
        passwordFrame.visibility = View.GONE
        passwordTitle.visibility = View.GONE
        login_message.text = getString(R.string.login_message_1)


        showlaws_link()
        login.setOnClickListener {
            //                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            val intent = Intent(this,SpeedMeterActivity::class.java)
            startActivity(intent)
            /*
            val number = login_phone.text.toString()

            if (number == "" || !number.startsWith("09")) {
                Toast.makeText(
                    this@LoginActivity,
                    "شماره خود را به صورت 09121234567 وارد نمایید",
                    Toast.LENGTH_LONG
                ).show()
                login_phone.setText("")
            } else if (number.length < 11 || number.length > 11) {
                Toast.makeText(this@LoginActivity, "شماره نامعتبر", Toast.LENGTH_LONG).show()
                login_phone.setText("")
            } else {
                if (Util.isNetworkAvailable(this@LoginActivity)) {
                    SubAsync(
                        number.replaceFirst("0".toRegex(), "98"),
                        login_code.text.toString()
                    ).execute()
                } else {
                    Toast.makeText(this@LoginActivity, "عدم اتصال به شبکه", Toast.LENGTH_LONG)
                        .show()
                }
            }*/
        }

        laws_link.setOnClickListener {
            val intent = Intent(this@LoginActivity, LawsActivity::class.java)
            startActivity(intent)
        }

        if (READ_SMS)
            askAccessReceiveSmsPermission()

        if (READ_SMS)
            registerReceiver(smsReceiver, IntentFilter("CODE_RECEIVED"))


    }

    override fun onResume() {
        super.onResume()
        if (co.avalinejad.iq.util.SecurePreferences.get(this@LoginActivity, USER) != null)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (READ_SMS)
            unregisterReceiver(smsReceiver)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_ACCESS_RECIVE_SMS -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    fun askAccessReceiveSmsPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.RECEIVE_SMS),
                    MY_PERMISSIONS_ACCESS_RECIVE_SMS
                )
                return false
            } else
                return true
        }
        return true
    }

    internal inner class SubAsync(var phone: String, var password: String) :
        AsyncTask<String, Void, Int>() {

        var dialog: ProgressDialog? = null

        override fun onPreExecute() {
            super.onPreExecute()
            password = login_code.text.toString()
            dialog = ProgressDialog(this@LoginActivity)
            dialog!!.setMessage("لطفا صبر کنید")
            dialog!!.setCancelable(false)
            dialog!!.show()
        }

        override fun doInBackground(vararg params: String): Int? {
            try {
                co.avalinejad.iq.util.SecurePreferences.put(this@LoginActivity, "phone", phone)
                if (co.avalinejad.iq.util.Util.isEmpty(password)) { // Check Sub
                    var response =
                        co.avalinejad.iq.util.JSONParser().parsGet(LOGIN_ADDRESS + "checkSubOtp.jsp?p=" + phone + "&t=" + SERVICE_CODE)
                    if (response == "1")
                        return 0 // it's OK
                    else { // Send OTP
                        response =
                            co.avalinejad.iq.util.JSONParser().parsGet(LOGIN_ADDRESS + "action.jsp?type=generateOtp&channel=" + CHANNEL + "&msisdn=" + phone + "&sc=" + SERVICE_CODE)
                        return if (response == "SUCCESS")
                            1
                        else if (response == "PARTNER API GATEWAY RECORD ALREADY EXIST.")
                            2
                        else
                            999
                    }
                } else { // check otp
                    val response =
                        co.avalinejad.iq.util.JSONParser().parsGet(LOGIN_ADDRESS + "action.jsp?type=confirmOtp&channel=" + CHANNEL + "&msisdn=" + phone + "&sc=" + SERVICE_CODE + "&otp=" + password)
                    return if (response == "FAIL")
                        3
                    else
                        0
                }

            } catch (e: Exception) {
                return 999
            }

        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            dialog!!.dismiss()
            if (result == 0) {
                co.avalinejad.iq.util.SecurePreferences.put(this@LoginActivity, USER, phone)
                co.avalinejad.iq.util.SecurePreferences.put(this@LoginActivity, "phone", phone)
                DeviceName.with(this@LoginActivity).request { info, error ->

                    data.mobile = co.avalinejad.iq.util.SecurePreferences.get(this@LoginActivity, "phone")
                    data.type = "login"
                    data.userInfo.manufacturer = info.manufacturer
                    data.userInfo.name = info.marketName
                    data.userInfo.model = info.model
                    data.userInfo.codename = info.codename
                    data.userInfo.deviceName = info.name
                    data.userInfo.phoneNumber = phone
                    sendServerUserData()
                }

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity()
                }
                finish()

            } else {
                text_price.visibility = View.GONE
                laws_link.visibility = View.GONE
                if (result == 1) {
                    passwordFrame.visibility = View.VISIBLE
                    passwordTitle.visibility = View.VISIBLE
                    login_code.setText("")
                    loginTitle.text = "تایید"
                    Toast.makeText(
                        this@LoginActivity,
                        "به زودی کد فعالسازی برای شما پیامک خواهد شد.",
                        Toast.LENGTH_LONG
                    ).show()
                    login_message.text = getString(R.string.login_message_2)
                } else if (result == 2) {
                    passwordFrame.visibility = View.VISIBLE
                    passwordTitle.visibility = View.VISIBLE
                    loginTitle.text = "تایید"
                    login_code.setText("")
                    Toast.makeText(
                        this@LoginActivity,
                        "کد فعالسازی قبلا برای شما ارسال شده است. در صورت عدم دریافت، پس از چند دقیقه، مجددا امتحان کنید",
                        Toast.LENGTH_LONG
                    ).show()
                    login_message.text = getString(R.string.login_message_2)
                } else if (result == 3) { // otp received. we check sub again
                    passwordFrame.visibility = View.VISIBLE
                    passwordTitle.visibility = View.VISIBLE
                    loginTitle.text = "تایید"
                    login_code.setText("")
                    Toast.makeText(
                        this@LoginActivity,
                        "کد وارد شده معتبر نمی باشد",
                        Toast.LENGTH_LONG
                    ).show()
                    login_message.text = getString(R.string.login_message_2)
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "خطا در ارتباط با سرور. لطفا چند لحظه دیگر دوباره امتحان کنید",
                        Toast.LENGTH_LONG
                    ).show()
                    text_price.visibility = View.VISIBLE
                    showlaws_link()
                }
            }
        }
        val SERVICE_CODE = "3075758"
    }

    private fun showlaws_link() {
        laws_link.visibility = View.VISIBLE
        laws_link.setOnClickListener {
            startActivity(
                Intent(
                    this@LoginActivity,
                    LawsActivity::class.java
                )
            )
        }
    }


    private fun checkForUpdate() {
        RetrofitSingleton.getWebService().getResults(
            "Android", "roja",
            co.avalinejad.iq.BuildConfig.VERSION_CODE
        )
            .enqueue(object : Callback<co.avalinejad.iq.component.update.Repo.model.UpdateResult> {
                override fun onResponse(
                    call: Call<co.avalinejad.iq.component.update.Repo.model.UpdateResult>,
                    response: Response<co.avalinejad.iq.component.update.Repo.model.UpdateResult>
                ) {
                    val code = response.code()
                    val body = response.body()
                    if (body != null && code > 199 && code < 300)
                        co.avalinejad.iq.component.update.UpdateFragment.newInstance(body).show(
                            supportFragmentManager,
                            "new app available"
                        )

                }

                override fun onFailure(call: Call<co.avalinejad.iq.component.update.Repo.model.UpdateResult>, t: Throwable) {}
            })

    }

    private fun sendServerUserData() {
        data.userInfo.ip = co.avalinejad.iq.util.Util.getIPAddress(true)
        if (data.userInfo.deviceName.isNullOrEmpty()) {
            val deviceCode = co.avalinejad.iq.util.Util.getDeviceName()
            data.userInfo.codename = deviceCode
            data.userInfo.deviceName = DeviceName.getDeviceName(
                deviceCode,
                "Unknown device"
            )
        }

        sendAppInstall()
    }

    private fun sendAppInstall() {
        data.appName = APP_NAME
        data.channel = CHANNEL
        data.androidId = co.avalinejad.iq.util.InstallationHelper.getAndroidId(this)
        data.installationId = co.avalinejad.iq.util.InstallationHelper.getInstallationId(this)
        data.initUserInfo()

        RetrofitSingleton.getWebService().addAppInstall(data).enqueue(object : Callback<co.avalinejad.iq.model.AppInstallResponse> {
            override fun onResponse(call: Call<co.avalinejad.iq.model.AppInstallResponse>, response: Response<co.avalinejad.iq.model.AppInstallResponse>) {

            }

            override fun onFailure(call: Call<co.avalinejad.iq.model.AppInstallResponse>, t: Throwable) {
                t.printStackTrace()
                //Do nothing
            }
        })
    }

    companion object {
        val MY_PERMISSIONS_ACCESS_RECIVE_SMS = 1
        val LOGIN_ADDRESS = "http://79.175.138.108:8080/"

        val CHANNEL = "zelzele_sefaresh_1_mehr98"
     //   val channelName =

        var READ_SMS = false
    }
}
