package com.metreat.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.gcmPushNotification.RegistrationIntentService;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Hashtable;


public class LoginActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    private EditText email,password;
    private ImageView showPassword;
    private Button login, register, login_buttonfacebook;
    private TextView forgetPassword;
    boolean showPwd;
    private Context mContext;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    boolean doubleBackToExitPressedOnce = false;
    CallWebService webService;
    private static final int HOME_ACTIVITIES_REQUEST_CODE = 123;

    //push notification
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;
    //push notification

    String appSocial, deviceToken;
    String socialEmailId,socialFirstName,socialLastName,socialId,socilaType;
    String socialName,SocialBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getfaceBookShaKey();
        FacebookSdk.sdkInitialize(this);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.login_screen);
        mContext = LoginActivity.this;

        callbackManager = CallbackManager.Factory.create();

        initView();
        myToolbar();
        addListener();
        tokenRecieve();
        deviceToken= SharedPreferencesManger.getTokenPrefValue(mContext, Constants.DEVICETOKEN, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

    }


    @Override
    protected void initView() {
        email = (EditText) findViewById(R.id.edEmailid);
        password = (EditText) findViewById(R.id.edPassword);
        showPassword = (ImageView) findViewById(R.id.ivShowPassword);
        forgetPassword = (TextView) findViewById(R.id.tvForgetPassword);
        login = (Button) findViewById(R.id.bLogin);
        login_buttonfacebook = (Button) findViewById(R.id.login_button1);
        register = (Button) findViewById(R.id.bRegister);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
    }

    @Override
    protected void addListener() {
        email.setOnClickListener(this);
        password.setOnClickListener(this);
        showPassword.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        login_buttonfacebook.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        ivIconLeft.setVisibility(View.INVISIBLE);
        ivIconRight.setVisibility(View.INVISIBLE);
        titleName.setText(R.string.loginTitle);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){

            case R.id.ivShowPassword:
                if(password.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        password.setInputType(InputType.TYPE_CLASS_TEXT);
                        password.setSelection(password.getText().length());
                        //confirmPassImage.setImageResource(R.drawable.username);//change Image here
                    } else {
                        showPwd = false;
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        password.setSelection(password.getText().length());
                        //confirmPassImage.setImageResource(R.drawable.username);//change Image here
                    }
                }
                break;
            case R.id.bLogin:
            if (validateLoginForm()){
                socilaType="app";
              loginApi(socilaType, deviceToken, socialId);
              }
                break;
            case R.id.login_button1:
                loginWithFacebook();
                break;
            case R.id.bRegister:
                intent = new Intent(mContext, RegistrationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            case R.id.tvForgetPassword:
                intent = new Intent(mContext, ForgetPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
        }
    }

    private void loginApi(String appSocial, String deviceToken, String socialId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "login");
                jObject.put("email", CommonUtils.convertUTF(email.getText().toString()));
                jObject.put("password", CommonUtils.convertUTF(password.getText().toString()));
                jObject.put("loginType", appSocial);
                jObject.put("socialID", socialId);
                jObject.put("deviceToken", deviceToken);
                jObject.put("deviceType", "android");
                jObject.put("deviceEnvironment", "");//for ios
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.LOGIN, this);
                webService.execute();
            } else {
                CommonUtils.showAlertDialog(this,getString(R.string.no_network_connection),
                        getString(R.string.check_connection));            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {

        switch (type) {
            case LOGIN:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        JSONObject userData = JSONUtils.getJSONObjectFromJSON(jsonObject, "data");

                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERID, JSONUtils.getStringFromJSON(userData, "userID"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.USERNAME, JSONUtils.getStringFromJSON(userData, "name"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.EMAILID, JSONUtils.getStringFromJSON(userData, "email"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.MOBILENUMBER, JSONUtils.getStringFromJSON(userData, "mobileNumber"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.IMAGES, JSONUtils.getStringFromJSON(userData, "image"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.DOB, JSONUtils.getStringFromJSON(userData, "dob"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
                        SharedPreferencesManger.setPrefValue(mContext, Constants.ADDRESS, JSONUtils.getStringFromJSON(userData, "address"), SharedPreferencesManger.PREF_DATA_TYPE.STRING);
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

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);

                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    } else if(code==500){
                         CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                    else if(code==501){
                       CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                        intent.putExtra("socialId",socialId);
                        intent.putExtra("socialName",socialName);
                        intent.putExtra("socialEmailId",socialEmailId);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    private void loginWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "user_friends","email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            public void onSuccess(LoginResult result) {
                Log.d("Error",result.toString());

           // Toast.makeText(mContext, "login successfully", Toast.LENGTH_LONG).show();
                GraphRequest request = GraphRequest.newMeRequest(
                        result.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                System.out.println("Result: " + response);
                                JSONObject res = response.getJSONObject();
                                try {
                                     socialEmailId = res.getString("email");
                                     socialFirstName = res.getString("first_name");
                                     socialLastName = res.getString("last_name");
                                     socialId = res.getString("id");
                                     socialName=res.getString("name");
                                //   SocialBirthday=res.getString("birthday");
                                     socilaType = "facebook";
                              //   CommonUtils.showToast(mContext,SocialBirthday);
                                    loginApi(socilaType, deviceToken, socialId);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                // Application code
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,first_name,last_name,locale");
                request.setParameters(parameters);
                request.executeAsync();

            }

            public void onError(FacebookException error) {

                CommonUtils.showToast(mContext,error.toString());
            }

            public void onCancel() {

                CommonUtils.showToast(mContext, "You have cancel facebook login");

            }
        });
    }


    public boolean validateLoginForm() {

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            if (email.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.email_idValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.validate_email));
            return false;
        }
        if (password.getText().toString().trim().isEmpty()) {
            CommonUtils.showToast(mContext, getResources().getString(R.string.password_validation));
            return false;
        }

       /* if (password.getText().toString().isEmpty() || password.length() < 6 || password.length() > 15) {
            if (password.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.password_validation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.validate_password));
            return false;
        }*/
        return true;
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        CommonUtils.showToast(mContext, (getString(R.string.backAgainToExit)));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void getfaceBookShaKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.metreat",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        //for fb
        try {
            callbackManager.onActivityResult(requestCode, responseCode, intent);
            if (requestCode == HOME_ACTIVITIES_REQUEST_CODE) {
       /*         Intent intent1=new Intent(mContext,HomeActivity.class);
                startActivity(intent1);
                finish();*/
            }
        } catch (Exception e) {

        }
    }

    //push notification
    public void tokenRecieve(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(Constants.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                } else {
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
    }

    public String testToast(){

        return null;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }



    //push notification
}
