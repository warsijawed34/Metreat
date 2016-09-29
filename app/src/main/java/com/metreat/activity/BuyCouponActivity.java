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
import com.metreat.adapter.BuyCouponAdapter;
import com.metreat.model.BuyCouponModel;
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
 * Created by vinove on 9/8/16.
 */
public class BuyCouponActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult {
    Context mContext;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    BuyCouponAdapter adapter;
    CallWebService webService;
    String tokenId,catId ,receiverId;
    JSONArray jsonArray;

    int startLimit = 0;
    int endLimit = 20, currentPosition;
    boolean loading = true;
    int firstVisibleItem, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buy_coupon_activity);
        mContext = BuyCouponActivity.this;
        initView();
        addListener();
        myToolbar();

        tokenId= SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        Intent intent = getIntent();
        catId = intent.getStringExtra("catId");
        receiverId = intent.getStringExtra("recieverId");
         couponListApi(tokenId, catId, startLimit, endLimit);
        ///cnages some here
        
    }


    @Override
    protected void initView() {
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        mRecyclerView= (RecyclerView) findViewById(R.id.recyclerViewBuyCoupon);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new BuyCouponAdapter(BuyCouponActivity.this);
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
        titleName.setText(R.string.buyCoupon);
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
    private void couponListApi(String tokenId ,String catId, int startLimit, int endLimit) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "couponList");
                jObject.put("tokenID", tokenId);
                jObject.put("catId", catId);
                jObject.put("startLimit", String.valueOf(startLimit));
                jObject.put("endLimit", String.valueOf(endLimit));
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.COUPONLIST, this);
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
            case COUPONLIST:
                if(result!=null) {
                    try {
                        System.out.println("Result: " + result);
                        JSONObject jsonObject = new JSONObject(result);
                        int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                        if (code == 200) {
                            jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "couponList");
                            BuyCouponModel model;
                            JSONObject resultObject;
                            if (jsonArray.length() >= 20) {
                                loading = false;
                            }
                            for (int i = 0; i < jsonArray.length(); i++) {
                                resultObject = jsonArray.getJSONObject(i);
                                model = new BuyCouponModel();
                                model.setCouponId(JSONUtils.getStringFromJSON(resultObject, "couponId"));
                                model.setName(JSONUtils.getStringFromJSON(resultObject, "name"));
                                model.setImage(JSONUtils.getStringFromJSON(resultObject, "image"));
                                model.setDescription(JSONUtils.getStringFromJSON(resultObject, "description"));
                                model.setAmount(JSONUtils.getStringFromJSON(resultObject, "amount"));
                                model.setRecieverId(receiverId);
                                adapter.addToArray(model);
                            }
                            mRecyclerView.setAdapter(adapter);
                            mRecyclerView.getLayoutManager().scrollToPosition(currentPosition);
                            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);

                                    visibleItemCount = mRecyclerView.getChildCount();
                                    totalItemCount = mLayoutManager.getItemCount();
                                    firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                                    totalItemCount = firstVisibleItem + visibleItemCount;

                                    if (totalItemCount == adapter.getCount() && !loading) {
                                        loading = true;
                                        startLimit = endLimit;
                                        endLimit = endLimit + 20;
                                        currentPosition = firstVisibleItem;
                                        couponListApi(tokenId, catId, startLimit, endLimit);
                                    }
                                }
                            });


                        } else {
                            if (!adapter.hasArrayItems())
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
