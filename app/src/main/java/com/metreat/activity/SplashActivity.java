package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.metreat.R;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.Constants;

/**
 * Splash Activity for 3 seconds
 */
public class SplashActivity extends AppCompatActivity{

    private static int SPLASH_TIME_OUT = 3000;
    private Context mContext;
    SharedPreferencesManger sharedPreferencesManger;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        mContext = SplashActivity.this;
        userId = sharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userId.isEmpty()) {//check userID
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);
    }
}
