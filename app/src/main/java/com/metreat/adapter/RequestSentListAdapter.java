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

import com.metreat.Interface.OnWebServiceResult;
import com.metreat.R;
import com.metreat.activity.HomeActivity;
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

public class RequestSentListAdapter extends RecyclerView.Adapter<RequestSentListAdapter.MyViewHolder> implements Filterable, OnWebServiceResult {
    ArrayList<syncContactsModel> requestSentList;
    ArrayList<syncContactsModel> originalRequestSentList;
    Context mContext;
    CallWebService webService;
    String tokenId, userId;
    int currentPosition;
    Boolean msgReq = true;


    public RequestSentListAdapter(Context context, ArrayList<syncContactsModel> requestSentList) {
        this.mContext = context;
        this.requestSentList = requestSentList;
        this.originalRequestSentList = requestSentList;

        tokenId = SharedPreferencesManger.getPrefValue(mContext, Constants.TOKENID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
        userId = SharedPreferencesManger.getPrefValue(mContext, Constants.USERID, SharedPreferencesManger.PREF_DATA_TYPE.STRING).toString();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.home_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    public void clear(){
        requestSentList.clear();
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final syncContactsModel model = requestSentList.get(position);
        holder.name.setText(model.getUserName());
        holder.number.setText(model.getNumber());


        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.profile)
                .showImageOnFail(R.drawable.profile).build();
        imageLoader.displayImage(model.getThumbUrl(), holder.image, options);


        if (model.getStatus().equalsIgnoreCase("2")) {
            holder.btnSendRequest.setVisibility(View.INVISIBLE);
            holder.btnInvite.setVisibility(View.VISIBLE);
            holder.frameLayout.setVisibility(View.GONE);
            holder.btnInvite.setText("Cancel Request");
        }
        else if(model.getStatus().equalsIgnoreCase("3")){
            holder.btnSendRequest.setVisibility(View.INVISIBLE);
            holder.btnInvite.setVisibility(View.INVISIBLE);
            holder.frameLayout.setVisibility(View.VISIBLE);

        }
        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition=holder.getAdapterPosition();
                cancelFriend(userId,tokenId,model.getNumber());

            }
        });
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition=holder.getAdapterPosition();
                 acceptApi(userId, tokenId,model.getUserId());

            }
        });

        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition=holder.getAdapterPosition();
                rejectApi(userId, tokenId,model.getUserId());
            }
        });


    }
    public syncContactsModel getItem(int position) {
        return requestSentList.get(position);
    }

    @Override
    public int getItemCount() {
        return requestSentList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, number;
        public ImageViewRounded image;
        public Button btnSendRequest,btnInvite;
        public Button btnAccept,btnReject;
        public FrameLayout frameLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tvUser);
            image = (ImageViewRounded) itemView.findViewById(R.id.ivProfileHeader);
            btnSendRequest = (Button) itemView.findViewById(R.id.btnSendRequest);
            btnInvite = (Button) itemView.findViewById(R.id.btnInvite);
            number = (TextView) itemView.findViewById(R.id.tv_number);
            btnAccept= (Button) itemView.findViewById(R.id.btnAccept);
            btnReject= (Button) itemView.findViewById(R.id.btnReject);
            frameLayout= (FrameLayout) itemView.findViewById(R.id.frame_layout);
        }
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
                jObject.put("type", "Accept");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.ACCEPTREQUEST, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, "Please connect with internet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void rejectApi(String userId, String tokenId, String senderId) {
        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "acceptRequest");
                jObject.put("userID", userId);
                jObject.put("senderID", senderId);
                jObject.put("tokenID", tokenId);
                jObject.put("type", "Reject");
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.REJECTREQUEST, this);
                webService.execute();
            } else {
                CommonUtils.showToast(mContext, "Please connect with internet");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void cancelFriend(String userId, String tokenId, String number) {

        try {
            if (new ConnectionDetector(mContext).isConnectingToInternet()) {
                Hashtable<String, String> params = new Hashtable<>();
                JSONObject jObject = new JSONObject();
                jObject.put("method", "sendRequest");
                jObject.put("userID", userId);
                jObject.put("mobileNumber", number);
                jObject.put("type", "revoke");
                jObject.put("tokenID", tokenId);
                params.put("json_data", jObject.toString());
                System.out.println("Request: " + params);
                webService = new CallWebService(mContext, new WebServiceApis(mContext).callApi(), params, Constants.SERVICE_TYPE.CANCELFFRIEND, this);
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
            case CANCELFFRIEND:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                    /*    requestSentList.remove(currentPosition);
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
            case ACCEPTREQUEST:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                       /* requestSentList.remove(currentPosition);
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

            case REJECTREQUEST:
                try {
                    System.out.println("Result: " + result);
                    JSONObject jsonObject = new JSONObject(result);
                    int code = JSONUtils.getIntFromJSON(jsonObject, "code");
                    if (code == 200) {
                        CommonUtils.showToast(mContext, JSONUtils.getStringFromJSON(jsonObject, "message"));
                        requestSentList.remove(currentPosition);
                        notifyDataSetChanged();

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
                if (charSequence == null || charSequence.length() ==0) {
                    results.values = originalRequestSentList;
                    results.count = originalRequestSentList.size();
                } else {
                    ArrayList<syncContactsModel> filterResultsData = new ArrayList<syncContactsModel>();
                    for (syncContactsModel data : originalRequestSentList) {
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
                    results.count = requestSentList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
                requestSentList = (ArrayList<syncContactsModel>) filterResults.values;
                notifyDataSetChanged();


            }
        };
    }
}