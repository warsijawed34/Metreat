package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
 * Created by vinove on 2/8/16.
 */
public class ChangePasswordActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    private TextView titleName;
    private EditText oldPass, newPass, confirmPass;
    private ImageView oldPassImage, newPassImage, confirmPassImage;
    private Button submit;
    private Context mContext;
    boolean showPwd;
    private Toolbar toolbar;
    CallWebService webService;
    String strUserId, strTokenId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        mContext = ChangePasswordActivity.this;
        initView();
        myToolbar();
        addListener();
        strUserId= SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        strTokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
    }

    @Override
    protected void initView() {
        oldPass = (EditText) findViewById(R.id.edOldPassword);
        newPass = (EditText) findViewById(R.id.edNewPassword);
        confirmPass = (EditText) findViewById(R.id.edConfirmPassword);
        oldPassImage = (ImageView) findViewById(R.id.ivOldShow);
        newPassImage = (ImageView) findViewById(R.id.ivNewShow);
        confirmPassImage = (ImageView) findViewById(R.id.ivConfirmShow);
        submit = (Button) findViewById(R.id.bSubmitChangePass);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
    }

    @Override
    protected void addListener() {
        oldPass.setOnClickListener(this);
        newPass.setOnClickListener(this);
        confirmPass.setOnClickListener(this);
        oldPassImage.setOnClickListener(this);
        newPassImage.setOnClickListener(this);
        confirmPassImage.setOnClickListener(this);
        submit.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        titleName.setText(R.string.changePassTitle);
        ivIconRight.setVisibility(View.INVISIBLE);
        ivIconLeft.setImageResource(R.drawable.back);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.ivIconLeft:
                onBackPressed();
                break;
            case R.id.ivOldShow:
            if(oldPass.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        oldPass.setInputType(InputType.TYPE_CLASS_TEXT);
                        oldPass.setSelection(oldPass.getText().length());
                       // oldPassImage.setImageResource(R.drawable.user);//change Image here
                    } else {
                        showPwd = false;
                        oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        oldPass.setSelection(oldPass.getText().length());
                        oldPassImage.setImageResource(R.drawable.show_password);
                    }
                }
                break;
            case R.id.ivNewShow:
                if(newPass.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        newPass.setInputType(InputType.TYPE_CLASS_TEXT);
                        newPass.setSelection(newPass.getText().length());
                      //  newPassImage.setImageResource(R.drawable.username);//change Image here
                    } else {
                        showPwd = false;
                        newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        newPass.setSelection(newPass.getText().length());
                      //  newPassImage.setImageResource(R.drawable.show_password);
                    }
                }
                break;
            case R.id.ivConfirmShow:
                if(confirmPass.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        confirmPass.setInputType(InputType.TYPE_CLASS_TEXT);
                        confirmPass.setSelection(confirmPass.getText().length());
                       // confirmPassImage.setImageResource(R.drawable.username);//change Image here
                    } else {
                        showPwd = false;
                        confirmPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        confirmPass.setSelection(confirmPass.getText().length());
                        confirmPassImage.setImageResource(R.drawable.show_password);
                    }
                }
                break;
            case R.id.bSubmitChangePass:
              if(validateForm()){
                  changePasswordApi(strUserId, strTokenId);
              }
                break;
            default:
                break;
        }

    }

    private void changePasswordApi(String userId, String tokenId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "resetPassword");
                jObject.put("oldPassword", oldPass.getText().toString());
                jObject.put("newPassword", newPass.getText().toString());
                jObject.put("confirmPassword", confirmPass.getText().toString());
                jObject.put("userID", userId);
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.CHANGEPASSWORD, this);
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
            case CHANGEPASSWORD:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){

                        CommonUtils.showToast(mContext,JSONUtils.getStringFromJSON(jsonObject,"message"));

                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);

                    }else {
                        CommonUtils.showToast(mContext,JSONUtils.getStringFromJSON(jsonObject,"message"));
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


        public boolean validateForm() {

        if (oldPass.getText().toString().isEmpty() || oldPass.length() < 6 || oldPass.length() > 15) {
            if (oldPass.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.oldPasswordValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.passwordValodation));
            return false;
        }

            if (newPass.getText().toString().isEmpty() || newPass.length() < 6 || newPass.length() > 15) {
                if (newPass.getText().toString().isEmpty()) {
                    CommonUtils.showToast(mContext, getString(R.string.newPasswordValidation));
                    return false;
                }
                CommonUtils.showToast(mContext, getString(R.string.passwordValodation));
                return false;
            }

            if (confirmPass.getText().toString().isEmpty() || confirmPass.length() < 6 || confirmPass.length() > 15) {
                if (confirmPass.getText().toString().isEmpty()) {
                    CommonUtils.showToast(mContext, getString(R.string.confirmPasswordValidation));
                    return false;
                }
                CommonUtils.showToast(mContext, getString(R.string.passwordValodation));
                return false;
            }

            return true;
    }

}
