package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.NotificationAdapter;
import com.metreat.adapter.ReceivedGiftedAdapter;
import com.metreat.model.NotificationModel;
import com.metreat.model.ReceivedGiftedModel;
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
 * Created by vinove on 5/8/16.
 */
public class NotificationActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult {

    Context mContext;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    NotificationAdapter adapter;
    CallWebService webService;
    String tokenId ,userId;
    JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_activity);
        mContext = NotificationActivity.this;

        initView();
        myToolbar();
        addListener();
        tokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId= SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        notificationApi(tokenId, userId);
    }
    @Override
    protected void initView() {
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerViewNotifiaction);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new NotificationAdapter(NotificationActivity.this);

    }

    @Override
    protected void addListener() {
        ivIconLeft.setOnClickListener(this);
    }

    @Override
    protected void myToolbar() {
        ivIconRight.setVisibility(View.INVISIBLE);
        titleName.setVisibility(View.VISIBLE);
        titleName.setText(R.string.notifyTitle);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break;
        }
    }


    private void notificationApi(String tokenId, String userId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "getNotification");
                jObject.put("tokenID", tokenId);
                jObject.put("userID", userId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.NOTIFICATION, this);
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
            case NOTIFICATION:
                if(result!=null) {
                    try {
                        System.out.println("Result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                        if (code == 200) {
                            jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "notificationList");
                            NotificationModel model;
                            JSONObject resultObject;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                resultObject = jsonArray.getJSONObject(i);
                                model = new NotificationModel();
                                model.setUserEventID(JSONUtils.getStringFromJSON(resultObject, "userEventID"));
                                model.setReceiverID(JSONUtils.getStringFromJSON(resultObject, "userID"));
                                model.setEventType(JSONUtils.getStringFromJSON(resultObject, "eventType"));
                                model.setEventDate(JSONUtils.getStringFromJSON(resultObject, "eventDate"));
                                model.setName(JSONUtils.getStringFromJSON(resultObject, "name"));
                                model.setAge(JSONUtils.getStringFromJSON(resultObject, "age"));
                                adapter.addtoArray(model);
                            }
                            mRecyclerView.setAdapter(adapter);

                        } else {
                            CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    CommonUtils.showToast(mContext,getString(R.string.dataNotFound));
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
}
