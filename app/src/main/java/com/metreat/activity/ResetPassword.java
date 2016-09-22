package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * Created by vinove on 31/8/16.
 */
public class ResetPassword extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    EditText edResetNewPassword,edResetConfirmPassword;
    ImageView ivResetNewShow, ivResetConfirmShow;
    Button resetPasswordSubmit;
    boolean showPwd;
    Context mContext;
    private TextView titleName;
    CallWebService webService;
    String emailId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reset_password);
        mContext = ResetPassword.this;

        initView();
        myToolbar();
        addListener();
        emailId= SharedPreferencesManger.getPrefValue(mContext, Constants.EMAILID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

    }
    protected void initView(){
        edResetNewPassword = (EditText) findViewById(R.id.edResetNewPassword);
        edResetConfirmPassword = (EditText) findViewById(R.id.edResetConfirmPassword);
        resetPasswordSubmit = (Button) findViewById(R.id.bResetSubmitChangePass);
        ivResetNewShow = (ImageView) findViewById(R.id.ivResetNewShow);
        ivResetConfirmShow = (ImageView) findViewById(R.id.ivResetConfirmShow);

        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);

    }

    protected void addListener(){
        resetPasswordSubmit.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
        ivResetNewShow.setOnClickListener(this);
        ivResetConfirmShow.setOnClickListener(this);

    }


    protected void myToolbar(){
        titleName.setVisibility(View.VISIBLE);
        titleName.setText(R.string.resetPassTitle);
        ivIconRight.setVisibility(View.INVISIBLE);
        ivIconLeft.setImageResource(R.drawable.back);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.bResetSubmitChangePass:
                if(validateForm()){
                    resetPasswordApi(emailId);
                }
                break;

            case R.id.ivResetNewShow:
                if(edResetNewPassword.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        edResetNewPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        edResetNewPassword.setSelection(edResetNewPassword.getText().length());
                    } else {
                        showPwd = false;
                        edResetNewPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edResetNewPassword.setSelection(edResetNewPassword.getText().length());
                    }
                }
                break;

            case R.id.ivResetConfirmShow:
                if(edResetConfirmPassword.getText().toString().length() > 0) {
                    if (!showPwd) {
                        showPwd = true;
                        edResetConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        edResetConfirmPassword.setSelection(edResetConfirmPassword.getText().length());
                    } else {
                        showPwd = false;
                        edResetConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edResetConfirmPassword.setSelection(edResetConfirmPassword.getText().length());
                    }
                }
                break;
            case R.id.ivIconLeft:
                onBackPressed();
                break;
        }
    }

    private void resetPasswordApi(String emailId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "resetForgottenPassword");
                jObject.put("email", emailId);
                jObject.put("newPassword", CommonUtils.convertUTF(edResetNewPassword.getText().toString().trim()));
                jObject.put("confirmPassword", CommonUtils.convertUTF(edResetConfirmPassword.getText().toString().trim()));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.RESETPASSWORD, this);
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
            case RESETPASSWORD:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                        CommonUtils.showToast(mContext,JSONUtils.getStringFromJSON(jsonObject,"message"));
                        Intent intent=new Intent(ResetPassword.this,HomeActivity.class);
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

    public boolean validateForm() {

        if (edResetNewPassword.getText().toString().isEmpty() || edResetNewPassword.length() < 6 || edResetNewPassword.length() > 15) {
            if (edResetNewPassword.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.newPasswordValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.passwordValodation));
            return false;
        }

        if (edResetConfirmPassword.getText().toString().isEmpty() || edResetConfirmPassword.length() < 6 || edResetConfirmPassword.length() > 15) {
            if (edResetConfirmPassword.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.confirmPasswordValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.passwordValodation));
            return false;
        }

        return true;
    }
}
