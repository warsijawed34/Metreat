package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.network.ConnectionDetector;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Hashtable;

/**
 * forget password activity
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener, OnWebServiceResult {

    private EditText email;
    private Button submit;
    private TextView titleName;
    private Context mContext;
    CallWebService webService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_password);
        mContext = ForgetPasswordActivity.this;

        initView();
        myToolbar();
        addListener();

    }

    @Override
    protected void initView() {
        email = (EditText) findViewById(R.id.edEmail);
        submit = (Button) findViewById(R.id.bSubmit);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
    }

    @Override
    protected void addListener() {
        submit.setOnClickListener(this);
        ivIconLeft.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.INVISIBLE);
        ivIconLeft.setImageResource(R.drawable.back);
        titleName.setText(R.string.forgetPassTitle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bSubmit:
                if (validateLoginForm()) {
                    forgetPasswordApi();
                }
                    break;
            case R.id.ivIconLeft:
               onBackPressed();
                break;
            default:
                break;
        }


    }

    private void forgetPasswordApi() {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {

                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "forgetPassword");
                jObject.put("email", email.getText().toString());
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.FORGETPASSWORD, this);
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
        switch (type) {
            case FORGETPASSWORD:
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        Intent intent=new Intent(mContext,OtpActivity.class);
                        intent.putExtra("otpType", "forgot");
                        intent.putExtra("emailID",email.getText().toString());
                        startActivity(intent);
                        overridePendingTransition(0, R.anim.exit_slide_right);


                    }else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    }

                 }catch (Exception e) {
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
//validation method
    public boolean validateLoginForm() {

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            if (email.getText().toString().isEmpty()) {
                CommonUtils.showToast(mContext, getString(R.string.forgotValidation));
                return false;
            }
            CommonUtils.showToast(mContext, getString(R.string.validate_email));
            return false;
        }

        return true;
    }


}
