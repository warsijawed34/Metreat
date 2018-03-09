package com.metreat.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.metreat.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * BaseActivity without drawerMenu
 */
public class BaseActivity extends AppCompatActivity {

    public Context mContext;
    public ImageView ivIconLeft,ivIconRight;
    public TextView titleName;
    public Toolbar toolbar;
    public Calendar newCalendar;
    public SimpleDateFormat dateFormatResult,dateFormatResultOccasion, newDateFomate;

    public BaseActivity(){
        super();
    }

    //hello

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseActivity.this;
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        setSupportActionBar(toolbar);
        setTitle("Activity Title");
        dateFormatResult = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        newDateFomate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatResultOccasion = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        newCalendar = Calendar.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void initView(){};
    protected void addListener(){};
    protected void myToolbar(){};
}
