package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONObject;

import java.util.Hashtable;

/**
 * Setting activity
 */
public class SettingActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult {
    Context mContext;
    FrameLayout flMyProfile,flNotification,flChangePass;
    ToggleButton toggleBtnNotification;
    String strYesNo, tokenId, userId, yesNOStr;
    CallWebService webService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        mContext = SettingActivity.this;
        initView();
        addListener();
        myToolbar();
        yesNOStr = SharedPreferencesManger.getPrefValue(mContext, Constants.NOTIFICATION_YESNO, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        if(yesNOStr.equals("yes")){//check notification are ON and OFF
            toggleBtnNotification.setBackgroundResource(R.drawable.on_button);
        }else {
            toggleBtnNotification.setBackgroundResource(R.drawable.off_button);
        }
        tokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId= SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

    }


    @Override
    protected void initView() {
        flMyProfile= (FrameLayout) findViewById(R.id.fl_myProfile);
        flNotification= (FrameLayout) findViewById(R.id.fl_notification);
        flChangePass= (FrameLayout) findViewById(R.id.fl_changePassword);
        toggleBtnNotification= (ToggleButton) findViewById(R.id.tb_notification);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);


    }

    @Override
    protected void addListener() {
        flMyProfile.setOnClickListener(this);
        flChangePass.setOnClickListener(this);
        toggleBtnNotification.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
        ivIconRight.setOnClickListener(this);

    }

    @Override
    protected void myToolbar() {
        ivIconLeft.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.VISIBLE);
        titleName.setText(R.string.setting);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.fl_myProfile:
                intent=new Intent(mContext,MyProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.fl_changePassword:
                intent=new Intent(mContext,ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);

                break;
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivIconRight:
                intent=new Intent(mContext,NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;

            case R.id.tb_notification:
                if(toggleBtnNotification.isChecked()){
                    toggleBtnNotification.setBackgroundResource(R.drawable.on_button);
                    strYesNo="yes";
                    SharedPreferencesManger.setPrefValue(mContext, Constants.NOTIFICATION_YESNO, strYesNo, SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                    settingApi(tokenId,userId,strYesNo);

                }else {
                    toggleBtnNotification.setBackgroundResource(R.drawable.off_button);
                    strYesNo="no";
                    SharedPreferencesManger.setPrefValue(mContext, Constants.NOTIFICATION_YESNO, strYesNo, SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                    settingApi(tokenId,userId,strYesNo);
                }
                break;
            default:
                break;
        }

    }


    private void settingApi(String tokenId, String userId, String strYesNo) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "setting");
                jObject.put("tokenID", tokenId);
                jObject.put("userId", userId);
                jObject.put("allowNotification", strYesNo);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SETTING, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type){
            case SETTING:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code==200){
                    }else {
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
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
