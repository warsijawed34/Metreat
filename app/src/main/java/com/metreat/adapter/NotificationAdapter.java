package com.metreat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.metreat.R;
import com.metreat.activity.CouponCatActivity;
import com.metreat.model.NotificationModel;
import com.metreat.model.ReceivedGiftedModel;
import com.metreat.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {
    List<NotificationModel> notificationList;
    Context mContext;
    String eventDate;

    public NotificationAdapter(Context context) {
        this.mContext = context;
        notificationList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.notification_list_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public void addtoArray(NotificationModel notificationModel) {
        notificationList.add(notificationModel);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final NotificationModel model = notificationList.get(position);

        String comingDate=model.getEventDate();

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMM");
        try {
            Date oneWayTripDate = input.parse(comingDate);
            eventDate = output.format(oneWayTripDate).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String eventMsg = model.getName() + "'s " + model.getAge() + " " + model.getEventType();

        if (model.getEventDate().equalsIgnoreCase(CommonUtils.getCalculatedDate("yyyy-MM-dd", 0))) {
            holder.tvName.setText("Today is " + eventMsg);
        } else if (model.getEventDate().equalsIgnoreCase(CommonUtils.getCalculatedDate("yyyy-MM-dd", 1))) {
            holder.tvName.setText("Tomorrow is " + eventMsg);
        } else {
            holder.tvName.setText(eventDate + " " + eventMsg);
        }


        holder.btnBuyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CouponCatActivity.class);
                intent.putExtra("recieverId", model.getReceiverID());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        ;
        return notificationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public Button btnBuyCoupon;
        public ImageView ivImageGift;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tvNameId);
            btnBuyCoupon = (Button) itemView.findViewById(R.id.btnBuyCouponId);
            ivImageGift = (ImageView) itemView.findViewById(R.id.ivNotification);
        }

    }

}