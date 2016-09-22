package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.GiftedCouponAdapter;
import com.metreat.adapter.ReceivedGiftedAdapter;
import com.metreat.model.GiftedCouponsModel;
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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Jawed on 8/8/16.
 */
public class GiftedCouponActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult {

    Context mContext;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    GiftedCouponAdapter adapter;
    String tokenId ,userId;
    CallWebService webService;
    JSONArray jsonArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gifted_coupon_activity);
        mContext = GiftedCouponActivity.this;
        initView();
        addListener();
        myToolbar();
        tokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId= SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        giftedCouponApi(tokenId,userId);
    }


    @Override
    protected void initView() {

        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewReceived_gifted);


        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new GiftedCouponAdapter(GiftedCouponActivity.this);
        mRecyclerView.setAdapter(adapter);


        ((GiftedCouponAdapter) adapter).setOnItemClickListener(new GiftedCouponAdapter.GiftedCouponClickListner() {
            @Override
            public void onItemClick(int position, View v) {
               // Toast.makeText(mContext, "" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void addListener() {
        ivIconLeft.setOnClickListener(this);
        ivIconRight.setOnClickListener(this);

    }

    @Override
    protected void myToolbar() {
        ivIconLeft.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.VISIBLE);
        titleName.setText(R.string.giftedCoupons);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivIconRight:
                intent = new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
        }
    }

    private void giftedCouponApi(String tokenId,String userId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "giftCoupon");
                jObject.put("tokenID", tokenId);
                jObject.put("userId", userId);
                jObject.put("listingType", "gifted");
                jObject.put("startLimit", "0");
                jObject.put("endLimit", "100");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.GIFTEDCOUPON, this);
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
            case GIFTEDCOUPON:
             try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                        jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "couponList");
                        GiftedCouponsModel model;
                        JSONObject resultObject;
                        for (int i = 0; i < jsonArray.length(); i++){
                            resultObject = jsonArray.getJSONObject(i);
                            model =new GiftedCouponsModel();
                            model.setCouponId(JSONUtils.getStringFromJSON(resultObject,"couponId"));
                            model.setcName(JSONUtils.getStringFromJSON(resultObject,"name"));
                            model.setcPrice(JSONUtils.getStringFromJSON(resultObject,"price"));
                            model.setcBoughtDate(JSONUtils.getStringFromJSON(resultObject,"boughtDate"));
                            model.setGiftedTo(JSONUtils.getStringFromJSON(resultObject,"receiverName"));
                            model.setBirthDayAniv(JSONUtils.getStringFromJSON(resultObject,"eventDate"));
                            adapter.addtoArray(model);
                        }
                        mRecyclerView.setAdapter(adapter);

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
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(Gravity.LEFT);

        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(0, R.anim.exit_slide_left);
        }
    }

}
