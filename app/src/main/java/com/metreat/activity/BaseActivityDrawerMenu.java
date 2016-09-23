package com.metreat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.MenuBaseAdapter;
import com.metreat.imageLoading.ImageLoader;
import com.metreat.imageLoading.ImageViewRounded;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;

/**
 * Created by vinove on 2/8/16.
 */
public class BaseActivityDrawerMenu extends AppCompatActivity implements OnWebServiceResult {

    private Context mContext;
    public static ImageView ivIconLeft,ivIconRight,ivProfileHeader;
    public TextView titleName,tvUserName, ivImageClose;
    public DrawerLayout mDrawer;
    public Toolbar toolbar;
    public Calendar newCalendar;
    ListView list;
    private MenuBaseAdapter menuAdapter;
    View headerView;
    public SimpleDateFormat dateFormatResult,dateFormatResultOccasion,newDateFomate,dateFormatEvent;
    public String userName, StrProfilePic, userId, tokenId;
    ImageLoader imgL;
    CallWebService webService;

    private String[] menu = {"Home", "My Profile","Received Gift", "Gifted Coupons", "Settings", "Logout"};
    private int[] image = {R.drawable.home,R.drawable.my_profile, R.drawable.gift, R.drawable.send,
            R.drawable.setting, R.drawable.logout };

    public BaseActivityDrawerMenu(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = BaseActivityDrawerMenu.this;
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        titleName = (TextView) findViewById(R.id.tvTitleName);
        setSupportActionBar(toolbar);
        setTitle("Activity Title");
        dateFormatResult = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        newDateFomate = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatEvent = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        dateFormatResultOccasion = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        newCalendar = Calendar.getInstance();
        userName= SharedPreferencesManger.getPrefValue(mContext, Constants.USERNAME, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        StrProfilePic= SharedPreferencesManger.getPrefValue(mContext, Constants.IMAGES, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId = SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        tokenId = SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
    }

    public void setContentView(int layoutResID){
        mDrawer = (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer_list, null);
        FrameLayout activityContainer = (FrameLayout) mDrawer.findViewById(R.id.frame_layout);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(mDrawer);

        menuAdapter = new MenuBaseAdapter(mContext, menu, image);
        list = (ListView) findViewById(R.id.listdrawer);
        list.setAdapter(menuAdapter);

        headerView = (View) getLayoutInflater().inflate(R.layout.drawer_header, list, false);
        list.addHeaderView(headerView);

        ivImageClose = (TextView) findViewById(R.id.ivImageClose);
        ivProfileHeader = (ImageViewRounded) findViewById(R.id.ivProfileHeader);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(userName);


        if (!StrProfilePic.isEmpty()){
            imgL = new ImageLoader(mContext);
            imgL.DisplayImage(StrProfilePic, ivProfileHeader);
        }else {
            ivProfileHeader.setImageResource(R.drawable.profile);
        }


            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position){
                    case 1:
                       //  startActivity(new Intent(BaseActivityDrawerMenu.this,HomeActivity.class));
                        intent=new Intent(BaseActivityDrawerMenu.this, HomeActivity.class);
                        finish();
                        startActivity(intent);
                        mDrawer.closeDrawer(Gravity.LEFT);
                         overridePendingTransition(0, R.anim.exit_slide_right);
                        break;
                    case 2:
                        startActivity(new Intent(BaseActivityDrawerMenu.this,MyProfileActivity.class));
                        mDrawer.closeDrawer(Gravity.LEFT);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        break;

                    case 3:
                        startActivity(new Intent(BaseActivityDrawerMenu.this, ReceivedGiftedActivity.class));
                        mDrawer.closeDrawer(Gravity.LEFT);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        break;
                    case 4:
                        startActivity(new Intent(BaseActivityDrawerMenu.this, GiftedCouponActivity.class));
                        mDrawer.closeDrawer(Gravity.LEFT);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        break;
                    case 5:
                        startActivity(new Intent(BaseActivityDrawerMenu.this, SettingActivity.class));
                        mDrawer.closeDrawer(Gravity.LEFT);
                        overridePendingTransition(0, R.anim.exit_slide_right);
                        break;
                    case 6:
                        SharedPreferencesManger.removeAllPrefValue(mContext);
                        intent = new Intent(mContext, LoginActivity.class);
                        intent.setAction(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        logoutApi(userId, tokenId);
                        break;
                    default:
                        break;
                }
            }
        });

        ivImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer(Gravity.LEFT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        //overridePendingTransition(0, R.anim.exit_slide_left);
    }

    protected void initView(){};
    protected void addListener(){};
    protected void myToolbar(){};

    private void logoutApi(String userId, String tokenId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "logout");
                jObject.put("tokenID", tokenId);
                jObject.put("userID", userId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.LOGOUT, this);
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
            case LOGOUT:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if(code==200){
                       // CommonUtils.showToast(mContext,JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }else {
                      //  CommonUtils.showToast(mContext,JSONUtils.getStringFromJSON(jsonObject,"message"));
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }

    }

}
