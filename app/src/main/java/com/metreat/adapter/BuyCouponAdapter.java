package com.metreat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.metreat.R;
import com.metreat.activity.PaypalActivity;
import com.metreat.model.BuyCouponModel;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class  BuyCouponAdapter extends RecyclerView.Adapter<BuyCouponAdapter.MyViewHolder> {
    List<BuyCouponModel> buyCouponList;
    Context mContext;
    public static BuyCouponClickListner listClickListener;
    int lineCount;

    public BuyCouponAdapter(Context context) {
        this.mContext = context;
        buyCouponList = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.buy_coupon_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public void addToArray(BuyCouponModel model) {
        buyCouponList.add(model);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
      final BuyCouponModel model=buyCouponList.get(position);
        holder.tvName.setText(model.getName());
        holder.tvDescription.setText(model.getDescription());
        holder.tvAmount.setText("$"+model.getAmount());
        //holder.tvDescription.setMovementMethod(new ScrollingMovementMethod());

        holder.tvDescription.post(new Runnable() {
            @Override
            public void run() {
                lineCount = holder.tvDescription.getLineCount();
                if(lineCount>4){
                    holder.tvViewMore.setVisibility(View.VISIBLE);
                }else {
                    holder.tvViewMore.setVisibility(View.INVISIBLE);
                }
            }
        });

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.not_image)
                .showImageOnFail(R.drawable.not_image).build();

        imageLoader.displayImage(model.getImage(), holder.ivBuyCouponImage, options);

        holder.btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PaypalActivity.class);
                intent.putExtra("amount",model.getAmount());
                intent.putExtra("couponId", model.getCouponId());
                intent.putExtra("receiverID", model.getRecieverId());
                mContext.startActivity(intent);
            }
        });

        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvViewMore.setVisibility(View.INVISIBLE);
                holder.tvViewLess.setVisibility(View.VISIBLE);
                holder.tvDescription.setMaxLines(Integer.MAX_VALUE);
            }
        });
        holder.tvViewLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.tvViewMore.setVisibility(View.VISIBLE);
                holder.tvViewLess.setVisibility(View.INVISIBLE);
                holder.tvDescription.setMaxLines(3);
            }
        });
    }

    @Override
    public int getItemCount() {
     return buyCouponList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
           public TextView tvName;
           public TextView tvDescription;
           public TextView tvAmount;
           public ImageView ivBuyCouponImage;
           public Button btnBuyNow;
           public TextView tvViewMore,tvViewLess;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvName= (TextView) itemView.findViewById(R.id.tv_buyCouponOffer);
            tvDescription= (TextView) itemView.findViewById(R.id.tv_buyCouponDescription);
            tvAmount= (TextView) itemView.findViewById(R.id.tv_buyCouponPrice);
            ivBuyCouponImage= (ImageView) itemView.findViewById(R.id.iv_buyCouponImage);
            btnBuyNow= (Button) itemView.findViewById(R.id.btn_buyNow);
            tvViewMore= (TextView) itemView.findViewById(R.id.tvViewMore);
            tvViewLess= (TextView) itemView.findViewById(R.id.tvViewLess);
            itemView.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        listClickListener.onItemClick(getPosition(), v);
    }
}

    public void setOnItemClickListener(BuyCouponClickListner myClickListener) {
        this.listClickListener = myClickListener;
    }

public interface BuyCouponClickListner {
    public void onItemClick(int position, View v);
}
}

