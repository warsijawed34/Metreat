package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.CouponCatAdapter;
import com.metreat.model.BuyCouponModel;
import com.metreat.model.CouponCatModel;
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
 * Created by vinove on 23/8/16.
 */
public class CouponCatActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult {
    Context mContext;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    CouponCatAdapter adapter;
    CallWebService webService;
    String tokenId, receiverId;
    JSONArray jsonArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coupon_cat_activity);
        mContext = CouponCatActivity.this;
        initView();
        addListener();
        myToolbar();
        tokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        catListApi(tokenId);
        Intent in = getIntent();
        receiverId = in.getStringExtra("recieverId");
    }

    @Override
    protected void initView() {
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerViewcouponCat);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new CouponCatAdapter(mContext,CouponCatActivity.this);
        mRecyclerView.setAdapter(adapter);

        ((CouponCatAdapter) adapter).setOnItemClickListener(new CouponCatAdapter.couponCatClickListner() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent=new Intent(mContext,BuyCouponActivity.class);
                intent.putExtra("catId", adapter.getItem(position).getId());
                intent.putExtra("recieverId",receiverId );
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
            }
        });
    }

    @Override
    protected void addListener() {
      ivIconRight.setOnClickListener(this);
      ivIconLeft.setOnClickListener(this);

    }

    @Override
    protected void myToolbar() {
        ivIconLeft.setVisibility(View.VISIBLE);
        ivIconRight.setVisibility(View.VISIBLE);
        titleName.setText(R.string.couponCat);
        setSupportActionBar(toolbar);

    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.ivIconLeft:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.ivIconRight:
                intent=new Intent(mContext, NotificationActivity.class);
                startActivity(intent);
                overridePendingTransition(0, R.anim.exit_slide_right);
                break;
            default:
                break;
        }
    }

    private void catListApi(String tokenId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "getCouponsCat");
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.COUPONCAT, this);
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
            case COUPONCAT:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                        jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "catList");
                        CouponCatModel model;
                        JSONObject resultObject;
                        for (int i = 0; i < jsonArray.length(); i++){
                            resultObject = jsonArray.getJSONObject(i);
                            model = new CouponCatModel();
                            model.setId(JSONUtils.getStringFromJSON(resultObject,"id"));
                            model.setName(JSONUtils.getStringFromJSON(resultObject,"name"));
                            model.setDetails(JSONUtils.getStringFromJSON(resultObject,"details"));
                            model.setImage(JSONUtils.getStringFromJSON(resultObject,"image"));
                            model.setReceiverID(receiverId);
                            adapter.addToArray(model);
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
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.exit_slide_left);
    }
}
