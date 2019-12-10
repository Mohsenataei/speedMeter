package co.avalinejad.iq.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder

import java.util.concurrent.TimeUnit

import co.avalinejad.iq.util.WEB_SERVICE_ROOT
import okhttp3.OkHttpClient
import retrofit2.Retrofit

/**
 * Created by alirezaahmadi on 9/11/16.
 * This class provides a singleton of retrofit client for all the places in the application.
 */
object RetrofitSingleton {
    private lateinit var retrofit: Retrofit
    private lateinit var gson: Gson
    private var webService: co.avalinejad.iq.network.WebService? = null

    /**
     * returns a retrofit instance. if none exists it first create one.
     * @return retrofit instance
     */
    val instance: Retrofit
        get() {
            val builder = OkHttpClient().newBuilder()
            builder.readTimeout(200, TimeUnit.SECONDS)
            val client = builder.build()
            gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()

            retrofit = Retrofit.Builder()
                    .baseUrl(WEB_SERVICE_ROOT)
                    .client(client)
                    .addConverterFactory(co.avalinejad.iq.network.Convertor.JsonConvertFactory.create(gson))
                    .build()

            return retrofit
        }

    fun getWebService(): co.avalinejad.iq.network.WebService {
        if (webService == null)
            webService = instance.create(co.avalinejad.iq.network.WebService::class.java)

        return webService!!
    }

    fun getGson(): Gson {
        return gson
    }
}

