package com.metreat.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.adapter.ContactListAdapter;
import com.metreat.adapter.FriendListAdapter;
import com.metreat.adapter.RequestSentListAdapter;
import com.metreat.model.ContactsModel;
import com.metreat.model.syncContactsModel;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.PermissionGranted;
import com.metreat.utils.WebServiceApis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vinove on 3/8/16.
 */
public class HomeActivity extends BaseActivityDrawerMenu implements View.OnClickListener, OnWebServiceResult, ActivityCompat.OnRequestPermissionsResultCallback {

    private Context mContext;
    private ImageView ivIconLeft;
    private RecyclerView recyclerView;
    private TextView friends, request, tvcontacts;
    private EditText etSearch;
    String searchKeyword = "";
    ArrayList<ContactsModel> sortedArray = new ArrayList<>();
    ArrayList<ContactsModel> arrListArray = new ArrayList<>();
    String userId, tokenId;
    CallWebService webService;
    JSONArray jsonArray;
    boolean doubleBackToExitPressedOnce = false;
    ContactListAdapter contactsAdapter;
    FriendListAdapter friendsAdapter;
    RequestSentListAdapter requestSentAdapter;

    ArrayList<syncContactsModel> friendList = new ArrayList<>();
    ArrayList<syncContactsModel> requestSentList = new ArrayList<>();
    ArrayList<syncContactsModel> contactList = new ArrayList<>();

    String contactName, phNumber, senderId, messageType, selectType = "Friends";
    ArrayList<ContactsModel> contacts = new ArrayList<>();
    private static String[] PERMISSIONS_CONTACTS = {Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private static final int REQUEST_CONATCT = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen);
        mContext = HomeActivity.this;
      //  requestContactsPermissions();
        initView();
        myToolbar();
        addListener();
        tokenId = SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId = SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

        Intent in = getIntent();
        senderId = in.getStringExtra("senderId");
        messageType = in.getStringExtra("messageType");
//
/*
        if (messageType != null) {
            alert();
        }*/

        LoadContactsAyscn lca = new LoadContactsAyscn();
        lca.execute();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        friends.setTextColor(getResources().getColor(R.color.colorWhite));
        friends.setBackgroundResource(R.drawable.home_custom_border_color_friends);

    }

    @Override
    protected void initView() {
        titleName = (TextView) findViewById(R.id.tvTitleName);
        ivIconLeft = (ImageView) findViewById(R.id.ivIconLeft);
        ivIconRight = (ImageView) findViewById(R.id.ivIconRight);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        friends = (TextView) findViewById(R.id.tvHomeFriends);
        tvcontacts = (TextView) findViewById(R.id.tvHomeContacts);
        request = (TextView) findViewById(R.id.tvHomeReqSent);
        etSearch = (EditText) findViewById(R.id.ed_searchHome);

    }

    @Override
    protected void addListener() {
        ivIconLeft.setOnClickListener(this);
        friends.setOnClickListener(this);
        tvcontacts.setOnClickListener(this);
        request.setOnClickListener(this);
        ivIconRight.setOnClickListener(this);
        searchFilter();
    }

    private void searchFilter() {
        //filter from adapter class
        etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        searchKeyword = s.toString().toLowerCase();
                        if (selectType.equalsIgnoreCase("Friends")) {
                            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            friendsAdapter.getFilter().filter(s);
                            ////                        for (int i = 0; i < friendsAdapter.getItemCount(); i++) {
////                            if (friendsAdapter.getItem(i).getUserName().toLowerCase().contains(searchKeyword)) {
////                                friendListSearch.add(friendsAdapter.getItem(i));
////                                friendsAdapter.notifyDataSetChanged();
////                            }
//                    //    }
////                        if(friendListSearch.size()!=0){
////                            friendsAdapter = new FriendListAdapter(mContext, friendListSearch);
////                            recyclerView.setAdapter(friendsAdapter);
////                            friendsAdapter.notifyDataSetChanged();
////                        } else {
////                            CommonUtils.showToast(mContext,"NO record found");
////                        }
//                    } else if (searchKeyword.length() == 0) {
//                        final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//                        imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
//                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.search, 0);
//                        searchKeyword = "";
//                        friendsAdapter = new FriendListAdapter(mContext, friendList);
//                        recyclerView.setAdapter(friendsAdapter);
//                        friendsAdapter.notifyDataSetChanged();
//                    }

                        } else if (selectType.equalsIgnoreCase("Requests")) {
                            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            requestSentAdapter.getFilter().filter(s);//check adapter class
                        } else if (selectType.equalsIgnoreCase("Contacts")) {
                            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            contactsAdapter.getFilter().filter(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                }

        );


    }

    @Override
    protected void myToolbar() {
        titleName.setVisibility(View.VISIBLE);
        titleName.setText(R.string.homeTitle);
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

            case R.id.tvHomeContacts:
                // set adapter and changes textColor here..
                selectType = "Contacts";
                tvcontacts.setTextColor(getResources().getColor(R.color.colorWhite));
                tvcontacts.setBackgroundResource(R.drawable.home_custom_border_color_contacts);

                friends.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                friends.setBackgroundResource(R.drawable.home_custom_border_friends);

                request.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                request.setBackgroundResource(R.drawable.home_custom_border_request_sent);

                contactsAdapter = new ContactListAdapter(mContext, contactList);
                recyclerView.setAdapter(contactsAdapter);
                contactsAdapter.notifyDataSetChanged();

                break;
            case R.id.tvHomeFriends:
                // set adapter and changes textColor here..
                selectType = "Friends";
                friendsAdapter = new FriendListAdapter(mContext, friendList);
                recyclerView.setAdapter(friendsAdapter);
                friendsAdapter.notifyDataSetChanged();

                friends.setTextColor(getResources().getColor(R.color.colorWhite));
                friends.setBackgroundResource(R.drawable.home_custom_border_color_friends);

                tvcontacts.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                tvcontacts.setBackgroundResource(R.drawable.home_custom_border_contacts);
                request.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                request.setBackgroundResource(R.drawable.home_custom_border_request_sent);

                break;
            case R.id.tvHomeReqSent:
                // set adapter and changes textColor here..
                selectType = "Requests";
                requestSentAdapter = new RequestSentListAdapter(mContext, requestSentList);
                recyclerView.setAdapter(requestSentAdapter);
                requestSentAdapter.notifyDataSetChanged();

                request.setTextColor(getResources().getColor(R.color.colorWhite));
                request.setBackgroundResource(R.drawable.home_custom_border_color_request_sent);

                friends.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                friends.setBackgroundResource(R.drawable.home_custom_border_friends);

                tvcontacts.setTextColor(getResources().getColor(R.color.colorPurpleLight));
                tvcontacts.setBackgroundResource(R.drawable.home_custom_border_contacts);
                break;
            default:
                break;

        }
    }
      //send all contacts number and name from this method...
    private void syncContact(String tokenId, String userId, String searchKeyword) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "syncContact");
                jObject.put("keyword", CommonUtils.convertUTF(searchKeyword));
                jObject.put("userID", userId);

                JSONArray contactsArray = new JSONArray();

                for (int i = 0; i < contacts.size(); i++) {
                    JSONObject contactObject = new JSONObject();
                    contactObject.put("user_name", contacts.get(i).getName());
                    contactObject.put("number", contacts.get(i).getPhoneNumber());
                    contactsArray.put(contactObject);
                }
                jObject.put("contacts", contactsArray);

                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SYNCCONTACTS, this);
                webService.execute();
            } else {
                CommonUtils.showAlertDialog(this,getString(R.string.no_network_connection),
                        getString(R.string.check_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case SYNCCONTACTS:
                try {
                    System.out.println("Result: " + result);//get all result here...

                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        jsonArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "friendList");
                        syncContactsModel model;
                        JSONObject resultObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            resultObject = jsonArray.getJSONObject(i);
                            model = new syncContactsModel();
                            model.setUserId(JSONUtils.getStringFromJSON(resultObject, "user_id"));
                            model.setUserName(JSONUtils.getStringFromJSON(resultObject, "user_name"));
                            model.setEmail(JSONUtils.getStringFromJSON(resultObject, "email"));
                            model.setThumbUrl(JSONUtils.getStringFromJSON(resultObject, "image"));
                            model.setStatus(JSONUtils.getStringFromJSON(resultObject, "status"));
                            model.setNumber(JSONUtils.getStringFromJSON(resultObject, "number"));
                            int status = JSONUtils.getIntFromJSON(resultObject, "status");

                            if (status == 1) {
                                friendList.add(model);
                            } else if (status == 2 || status == 3) {
                                requestSentList.add(model);
                            }
                            else if(status != 3 ) {
                                contactList.add(model);
                            }
                        }

                        JSONArray notFoundArray = JSONUtils.getJSONArrayFromJSON(jsonObject, "notFound");
                        JSONObject notFoundObject;
                        for (int j = 0; j < notFoundArray.length(); j++) {
                            notFoundObject = notFoundArray.getJSONObject(j);
                            model = new syncContactsModel();
                            model.setUserId("");
                            model.setUserName(notFoundObject.getString("user_name"));
                            model.setEmail("");
                            model.setThumbUrl("");
                            model.setStatus("4");
                            model.setNumber(notFoundObject.getString("number"));
                            contactList.add(model);
                        }

                        friendsAdapter = new FriendListAdapter(mContext, friendList);
                        recyclerView.setAdapter(friendsAdapter);
                        friendsAdapter.notifyDataSetChanged();

                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ACCEPTREQUEST://accept friends request
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));

                    } else {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }

    }
//get all contacts number and name in this class
    class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<ContactsModel>> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setMessage(mContext.getResources().getString(R.string.loading));
            pd.setIndeterminate(false);
            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected ArrayList<ContactsModel> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            contacts = new ArrayList<ContactsModel>();
            Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    null, null, null);
            while (c.moveToNext()) {
                contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));//get contacts name
                phNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));//get contacts number

                ContactsModel model = new ContactsModel();
                model.setName(contactName);//set contact name in model
                model.setPhoneNumber(phNumber);//set contact number in model
                contacts.add(model);

            }
            c.close();
            Collections.sort(contacts);//sorting contact name...
            return contacts;
        }

        @Override
        protected void onPostExecute(ArrayList<ContactsModel> contacts) {
            super.onPostExecute(contacts);
            pd.cancel();
            searchKeyword = "";
            syncContact(tokenId, userId, searchKeyword);

        }

    }

    public void alert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setMessage("Are you sure want to accept?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                acceptApi(userId, tokenId, senderId);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void acceptApi(String userId, String tokenId, String senderId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "acceptRequest");
                jObject.put("userID", userId);
                jObject.put("senderID", senderId);
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.ACCEPTREQUEST, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, getString(R.string.internetConnection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//BackPressed to exist
    public void onBackPressed() {

        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(Gravity.LEFT);

        } else {
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
    }

    private void requestContactsPermissions() {

        if (Build.VERSION.SDK_INT >= 23) {
            if (mContext.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED &&
                    mContext.checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                  requestPermissions(PERMISSIONS_CONTACTS, REQUEST_CONATCT);
            } else {
                CommonUtils.showToast(mContext, getString(R.string.already_granted));
                LoadContactsAyscn lca = new LoadContactsAyscn();
                lca.execute();

            }
        } else {
            CommonUtils.showToast(mContext, getString(R.string.already_granted));
            LoadContactsAyscn lca = new LoadContactsAyscn();
            lca.execute();
        }

    }
    //marshMallow permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == REQUEST_CONATCT && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            // do something here..
            Toast.makeText(mContext, "Contacts permissions granted.", Toast.LENGTH_LONG).show();
            LoadContactsAyscn lca = new LoadContactsAyscn();
            lca.execute();
        } else {
            Toast.makeText(mContext, "Contacts permissions were not granted.", Toast.LENGTH_LONG).show();
        }
    }
}
