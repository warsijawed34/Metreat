package com.metreat.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.activity.HomeActivity;
import com.metreat.customViews.ImageViewRounded;
import com.metreat.model.ContactsModel;
import com.metreat.model.CouponCatModel;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.MyViewHolder> implements Filterable, OnWebServiceResult {
    ArrayList<syncContactsModel> friendList;
    ArrayList<syncContactsModel> originalfriendList;
    Context mContext;
    CallWebService webService;
    String tokenId, userId;
    int currentPosition;
    Boolean msgReq = true;

    public FriendListAdapter(Context context, ArrayList<syncContactsModel> friendList) {
        this.mContext = context;
        this.friendList = friendList;
        this.originalfriendList = friendList;
        tokenId = SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId = SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.home_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    public void clear() {
        friendList.clear();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final syncContactsModel model = friendList.get(position);
        holder.name.setText(model.getUserName());


        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.profile)
                .showImageOnFail(R.drawable.profile).build();
        imageLoader.displayImage(model.getThumbUrl(), holder.image, options);

        holder.frameLayout.setVisibility(View.GONE);
        if (model.getStatus().equalsIgnoreCase("1")) {
            holder.btnSendRequest.setVisibility(View.INVISIBLE);
            holder.btnInvite.setVisibility(View.INVISIBLE);
            holder.ivRemove.setVisibility(View.VISIBLE);
        }
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition=holder.getAdapterPosition();
                alert(model.getUserId());
            }
        });

    }

    public syncContactsModel getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number;
        public ImageViewRounded image;
        public Button btnSendRequest, btnInvite;
        public FrameLayout frameLayout;
        public ImageView ivRemove;


        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvUser);
            image = (ImageViewRounded) itemView.findViewById(R.id.ivProfileHeader);
            btnSendRequest = (Button) itemView.findViewById(R.id.btnSendRequest);
            btnInvite = (Button) itemView.findViewById(R.id.btnInvite);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            frameLayout= (FrameLayout) itemView.findViewById(R.id.frame_layout);
            ivRemove= (ImageView) itemView.findViewById(R.id.ivRemove);
        }
    }


    public void alert(final String RemoveUserId) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setMessage("Are you sure you want to remove from friend list?");
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                removeFriend(userId,tokenId,RemoveUserId);

            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void removeFriend(String userId, String tokenId, String friendId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "revokeFriendship");
                jObject.put("userID", userId);
                jObject.put("friendID", friendId);
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.REMOVEFRIEND, this);
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
        switch (type){
            case REMOVEFRIEND:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                       // CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                      /*  friendList.remove(currentPosition);
                        notifyDataSetChanged();*/
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
        }

    }



    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();
                if (charSequence == null || charSequence.length() ==0) {
                    results.values = originalfriendList;
                    results.count = originalfriendList.size();
                } else {
                    ArrayList<syncContactsModel> filterResultsData = new ArrayList<syncContactsModel>();
                    for (syncContactsModel data : originalfriendList) {
                        if (data.getUserName().toLowerCase().contains(charSequence)) {
                            filterResultsData.add(data);
                            notifyDataSetChanged();
                        }
                    }
                    if (filterResultsData.size() == 0) {
                        if (msgReq) {
                            CommonUtils.showToast(mContext, "NO record found");
                            msgReq = false;
                        }
                    }

                    results.values = filterResultsData;
                    results.count = friendList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                friendList = (ArrayList<syncContactsModel>) filterResults.values;
                notifyDataSetChanged();


            }
        };
    }


}