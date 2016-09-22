package com.metreat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.activity.HomeActivity;
import com.metreat.activity.NotificationActivity;
import com.metreat.customViews.ImageViewRounded;
import com.metreat.model.syncContactsModel;
import com.metreat.network.ConnectionDetector;
import com.metreat.pereferences.SharedPreferencesManger;
import com.metreat.utils.CallWebService;
import com.metreat.utils.CommonUtils;
import com.metreat.utils.Constants;
import com.metreat.utils.JSONUtils;
import com.metreat.utils.WebServiceApis;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> implements OnWebServiceResult, Filterable {
    ArrayList<syncContactsModel> contactList;
    ArrayList<syncContactsModel> originalContactList;
    Context mContext;
    CallWebService webService;
    Boolean msgReq = true;
    // public static ClickListner listClickListener;
    String tokenId, userId;

    public ContactListAdapter(Context context, ArrayList<syncContactsModel> contactList) {
        this.mContext = context;
        this.contactList = contactList;
        this.originalContactList = contactList;
        tokenId = SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId = SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.home_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

//    public void addToArray(syncContactsModel model) {
//        contactList.add(model);
//    }

    public void clear() {
        contactList.clear();
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final syncContactsModel model = contactList.get(position);
        holder.name.setText(model.getUserName());
        holder.number.setText(model.getNumber());
        holder.frameLayout.setVisibility(View.GONE);


        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.profile)
                .showImageOnFail(R.drawable.profile).build();
        imageLoader.displayImage(model.getThumbUrl(), holder.image, options);


        if(model.getStatus().equalsIgnoreCase("4") ){
            holder.btnSendRequest.setText("Invite");

        }else if(model.getStatus().equalsIgnoreCase("0")){
            holder.btnSendRequest.setText("Send Request");
        }
        else if(model.getStatus().equalsIgnoreCase("2")){
            holder.btnSendRequest.setText("Sent Request");
        }
        else if(model.getStatus().equalsIgnoreCase("1")){
            holder.btnSendRequest.setVisibility(View.INVISIBLE);
        }
        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestApi(model.getNumber(), userId, tokenId);

            }
        });
    }


    public syncContactsModel getItem(int position) {
        return contactList.get(position);
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number;
        public ImageViewRounded image;
        public Button btnSendRequest, btnInvite;
        public FrameLayout frameLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvUser);
            image = (ImageViewRounded) itemView.findViewById(R.id.ivProfileHeader);
            btnSendRequest = (Button) itemView.findViewById(R.id.btnSendRequest);
            btnInvite = (Button) itemView.findViewById(R.id.btnInvite);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            frameLayout= (FrameLayout) itemView.findViewById(R.id.frame_layout);
            // itemView.setOnClickListener(this);

        }
    }

    private void sendRequestApi(String number, String userId, String tokenId) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "sendRequest");
                jObject.put("userID", userId);
                jObject.put("mobileNumber", number);
                jObject.put("type", "send");
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.SENDREQUEST, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, "Please connect with internet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWebServiceResult(String result, Constants.SERVICE_TYPE type) {
        switch (type) {
            case SENDREQUEST:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        Intent refresh = new Intent(mContext, HomeActivity.class);
                        ((Activity)mContext).finish();
                        mContext.startActivity(refresh);
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

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                //If there's nothing to filter on, return the original data for your list
                if (charSequence == null || charSequence.length() == 0) {
                    results.values = originalContactList;
                    results.count = originalContactList.size();
                } else {
                    ArrayList<syncContactsModel> filterResultsData = new ArrayList<syncContactsModel>();
                    for (syncContactsModel data : originalContactList) {
                        //In this loop, you'll filter through originalData and compare each item to charSequence.
                        //If you find a match, add it to your new ArrayList
                        //I'm not sure how you're going to do comparison, so you'll need to fill out this conditional
                        if (data.getUserName().toLowerCase().contains(charSequence)) {
                            filterResultsData.add(data);

                            notifyDataSetChanged();
                            msgReq = true;
                        }
                    }
                    if (filterResultsData.size() == 0) {
                        if (msgReq) {
                            CommonUtils.showToast(mContext, "NO record found");
                            msgReq = false;
                        }
                    }
                    results.values = filterResultsData;
                    results.count = contactList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                contactList = (ArrayList<syncContactsModel>) filterResults.values;
                notifyDataSetChanged();


            }
        };
    }

}