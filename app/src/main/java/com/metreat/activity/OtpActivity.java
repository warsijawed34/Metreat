package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;

/**
 * OTP Activity
 */
public class OtpActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    private Context mContext;
    private EditText otpCode;
    private Button verify;
    private TextView resendCode;
    CallWebService webService;
    String mobileNumber, receiveEmailId, otpType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen);
        mContext = OtpActivity.this;
        initView();
        myToolbar();
        addListener();
        mobileNumber= SharedPreferencesManger.getPrefValue(mContext, Constants.MOBILENUMBER, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        Intent in = getIntent();
        receiveEmailId = in.getStringExtra("emailID");
        otpType = in.getStringExtra("otpType");
    }

    @Override
    protected void initView() {
        titleName = (TextView) findViewById(R.id.tvTitleName);
        otpCode = (EditText) findViewById(R.id.edOtpCode);
        verify = (Button) findViewById(R.id.bVerify);
        resendCode = (TextView) findViewById(R.id.tvResendOtp);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
    }

    @Override
    protected void addListener() {
        otpCode.setOnClickListener(this);
        verify.setOnClickListener(this);
        resendCode.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        ivIconLeft.setImageResource(R.drawable.back);
        ivIconRight.setVisibility(View.INVISIBLE);
        titleName.setText(R.string.otpTitle);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bVerify:
              if(validateUpdate()){
               OtpApi(receiveEmailId, otpType);
               }
                break;
            case R.id.ivIconLeft:
                onBackPressed();
                break;
            case R.id.tvResendOtp:
                resendApi(receiveEmailId, otpType);
                break;
            default:
                break;
        }
    }

    private void OtpApi(String receiveEmailId, String otpType) {

            try {
                if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                    Hashtable<String, String> params = new Hashtable<>();
                    JSONObject jObject = new JSONObject();
                    jObject.put("method", "validateOtp");
                    jObject.put("email",receiveEmailId);
                    jObject.put("otpCode",otpCode.getText().toString());
                    jObject.put("type",otpType);
                    params.put("json_data", jObject.toString());
                    System.out.println("Request: " + params);
                    webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.OTP, this);
                    webService.execute();
                } else {
                    CommonUtils.showToast(mContext, getString(R.string.internetConnection));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void resendApi(String receiveEmailId, String otpType) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "resendRegistrationOtp");
                jObject.put("email",receiveEmailId);
                jObject.put("otpCode",otpCode.getText().toString());
                jObject.put("type",otpType);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.RESENDOTP, this);
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
            case OTP:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                        JSONObject userData = JSONUtils.getJSONObjectFromJSON(jsonObject, "data");

                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(userData, "userID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(userData, "name"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(userData, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(userData, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESS, JSONUtils.getStringFromJSON(userData, "address"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(userData, "mobileNumber"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.DOB, JSONUtils.getStringFromJSON(userData, "dob"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.TOKENID, JSONUtils.getStringFromJSON(userData, "tokenID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);


                        JSONObject addressObject = JSONUtils.getJSONObjectFromJSON(userData, "addressParts");
                        SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESSLINE1, JSONUtils.getStringFromJSON(addressObject, "addressLine1"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESSLINE2, JSONUtils.getStringFromJSON(addressObject, "addressLine2"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.CITY, JSONUtils.getStringFromJSON(addressObject, "city"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.POSTALCODE, JSONUtils.getStringFromJSON(addressObject, "postalcode"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.STATE, JSONUtils.getStringFromJSON(addressObject, "state"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.COUNTRY, JSONUtils.getStringFromJSON(addressObject, "country"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);

                        JSONArray eventArray = JSONUtils.getJSONArrayFromJSON(userData, "event");
                        for (int i = 0; i < eventArray.length(); i++){
                            JSONObject eventObject = eventArray.getJSONObject(i);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTID, JSONUtils.getStringFromJSON(eventObject, "id"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTTYPE, JSONUtils.getStringFromJSON(eventObject, "eventType"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                            SharedPreferencesManger.setPrefValue(mContext, Constants.EVENTDATE, JSONUtils.getStringFromJSON(eventObject, "eventDate"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        }
                        String typeResult =JSONUtils.getStringFromJSON(userData,"type");
                     //   CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                         if(typeResult.equalsIgnoreCase("forgot")){
                             Intent intent = new Intent(OtpActivity.this, ResetPassword.class);
                             startActivity(intent);
                             overridePendingTransition(0, R.anim.exit_slide_right);
                         }else if(typeResult.equalsIgnoreCase("register")) {
                             Intent intent = new Intent(OtpActivity.this, HomeActivity.class);
                             startActivity(intent);
                             overridePendingTransition(0, R.anim.exit_slide_right);
                         }


                    }else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

            case RESENDOTP:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                      //  CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    }else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    private boolean validateUpdate() {

        if (otpCode.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, mContext.getResources().getString(R.string.validate_emptyField));
            return false;
        }
        return true;

    }

}