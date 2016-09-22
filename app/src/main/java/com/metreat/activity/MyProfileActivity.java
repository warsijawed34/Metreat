package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.R;
import com.metreat.imageLoading.ImageLoader;
import com.metreat.imageLoading.ImageViewRounded;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jawed on 5/8/16.
 */
public class MyProfileActivity extends BaseActivityDrawerMenu implements View.OnClickListener {
    Context  mContext;
    TextView tvName,tvEmail,tvDOB,tvAddress,tvMobileNumber,tvEvent, tvEventDate;
    ImageViewRounded ivProfile;
    Button btnUpdate;
    String userName, emailId, address, StrProfilePic, dateOfBirth,mobileNumber,eventType, eventDate;
    ImageLoader imgL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_profile);
        mContext = MyProfileActivity.this;
        initView();
        myToolbar();
        addListener();
        setTextProfile();

    }

    @Override
    protected void initView() {
        ivProfile= (ImageViewRounded) findViewById(R.id.iv_update_photo);
        tvName= (TextView) findViewById(R.id.tv_name);
        tvEmail= (TextView) findViewById(R.id.tv_emai_address);
        tvMobileNumber= (TextView) findViewById(R.id.tvMobileNumber);
        tvDOB= (TextView) findViewById(R.id.tv_dob);
        tvAddress= (TextView) findViewById(R.id.tv_address);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        btnUpdate = (Button) findViewById(R.id.btn_update);
        tvEvent= (TextView) findViewById(R.id.tvEvent);
        tvEventDate= (TextView) findViewById(R.id.tvEventDate);


    }

    @Override
    protected void addListener() {
        ivIconLeft.setOnClickListener(this);
        ivIconRight.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        ivIconLeft.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.VISIBLE);
        titleName.setText(R.string.myProfile);
        setSupportActionBar(toolbar);

    }

    private void setTextProfile() {

        userName= SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        emailId= SharedPreferencesManger.getPrefValue(mContext, Constants.EMAILID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        dateOfBirth= SharedPreferencesManger.getPrefValue(mContext, Constants.DOB, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        mobileNumber= SharedPreferencesManger.getPrefValue(mContext, Constants.MOBILENUMBER, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        address= SharedPreferencesManger.getPrefValue(mContext, Constants.ADDRESS, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        eventType= SharedPreferencesManger.getPrefValue(mContext, Constants.EVENTTYPE, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        eventDate= SharedPreferencesManger.getPrefValue(mContext, Constants.EVENTDATE, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        StrProfilePic= SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        tvName.setText(userName);
        tvEmail.setText(emailId);
        tvMobileNumber.setText(mobileNumber);
        tvEvent.setText(eventType);

        //change date format of Event date.....
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM, yyyy");
        try {
            Date oneWayTripDate = input.parse(eventDate);
            tvEventDate.setText(output.format(oneWayTripDate));
        }catch (ParseException e) {
            e.printStackTrace();
        }

        //change date format of DOB.
        SimpleDateFormat input1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output1 = new SimpleDateFormat("dd MMMM, yyyy");
        try {
            Date oneWayTripDate1 = input1.parse(dateOfBirth);
            tvDOB.setText(output1.format(oneWayTripDate1));
        }catch (ParseException e) {
            e.printStackTrace();
        }

        tvAddress.setText(address);

        if (!StrProfilePic.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(StrProfilePic, ivProfile);
        }else {
            ivProfile.setImageResource(R.drawable.profile);
        }

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break ;
            case R.id.ivIconRight:
                intent = new Intent(mContext,NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.btn_update:
                intent = new Intent(mContext, UpdateProfile.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(Gravity.LEFT);

        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.exit_slide_left);
        }
    }
}
