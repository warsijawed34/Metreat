package com.metreat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.metreat.R;
import com.metreat.model.GiftedCouponsModel;
import com.metreat.model.ReceivedGiftedModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class GiftedCouponAdapter extends RecyclerView.Adapter<GiftedCouponAdapter.MyViewHolder> {
    List<GiftedCouponsModel> giftedCouponList;
    Context mContext;
    public static GiftedCouponClickListner listClickListener;

    public GiftedCouponAdapter(Context context) {
        this.mContext = context;
        giftedCouponList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.received_gifted_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public void addtoArray(GiftedCouponsModel giftedCouponsModel) {
        giftedCouponList.add(giftedCouponsModel);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
      GiftedCouponsModel model=giftedCouponList.get(position);
        holder.tvCName.setText(model.getcName());
        holder.tvCPrice.setText("$"+model.getcPrice());

       //change date formate of Bought date
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd MMMM, yyyy");
        try {
            Date oneWayTripDate = input.parse(model.getcBoughtDate());
            holder.tvCBoughtCoupon.setText(output.format(oneWayTripDate));
        }catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvGiftedTo.setText(model.getGiftedTo());

        //change date formate of event date
        SimpleDateFormat input1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat output1 = new SimpleDateFormat("dd MMMM, yyyy");
        try {
            Date oneWayTripDate = input.parse(model.getBirthDayAniv());
            holder.tvBirthAniv.setText(output.format(oneWayTripDate));
        }catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
     return giftedCouponList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tvCName;
        public TextView tvCPrice;
        public TextView tvCBoughtCoupon;
        public TextView tvGiftedTo;
        public TextView tvBirthAniv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCName= (TextView) itemView.findViewById(R.id.tv_CName);
            tvCPrice= (TextView) itemView.findViewById(R.id.tv_CPrice);
            tvCBoughtCoupon= (TextView) itemView.findViewById(R.id.tv_coupon_bought_date);
            tvGiftedTo= (TextView) itemView.findViewById(R.id.tv_gifted_to);
            tvBirthAniv= (TextView) itemView.findViewById(R.id.tv_birthdayAniv);
            itemView.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        listClickListener.onItemClick(getPosition(), v);
    }
}

    public void setOnItemClickListener(GiftedCouponClickListner myClickListener) {
        this.listClickListener = myClickListener;
    }

public interface GiftedCouponClickListner {
    public void onItemClick(int position, View v);
}
}

