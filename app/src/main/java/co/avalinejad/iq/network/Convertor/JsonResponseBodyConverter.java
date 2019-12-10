package co.avalinejad.iq.network.Convertor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by alirezaahmadi on 2016/11/29AD.
 * This class is not completed yet and can not be used
 */

public class  JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final TypeAdapter<T> adapter;

    JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String rawResponse = value.string();
            Log.e("networkResponse", rawResponse);

            return adapter.fromJson(rawResponse);
        } finally {
            value.close();
        }
    }
}
