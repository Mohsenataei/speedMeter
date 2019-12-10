package co.avalinejad.iq.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import co.avalinejad.iq.R;
import co.avalinejad.iq.util.ConstantsKt;
import co.avalinejad.iq.util.SecurePreferences;
import co.avalinejad.iq.util.SecurePreferences;


public class SplashActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        routeToLogin("ir-mci");
    }







    private void routeToLogin(String simOperator) {
        if (!isMtn(simOperator)) {
            if (SecurePreferences.get(SplashActivity1.this,  ConstantsKt.USER) == null)
                startActivity(new Intent(SplashActivity1.this, LoginActivity.class));
            else
                startActivity(new Intent(SplashActivity1.this, SpeedMeterActivity.class));
        } else
            startActivity(new Intent(SplashActivity1.this, IntroActivity.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
        finish();
    }



    public boolean isMtn(String text) {
        return text.equalsIgnoreCase("irancell");
    }
}
