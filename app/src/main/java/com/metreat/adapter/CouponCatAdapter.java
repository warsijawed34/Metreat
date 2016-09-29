package com.metreat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.metreat.R;
import com.metreat.activity.BuyCouponActivity;
import com.metreat.model.CouponCatModel;
import com.metreat.utils.CommonUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinove on 4/7/16.
 */

public class CouponCatAdapter extends RecyclerView.Adapter<CouponCatAdapter.MyViewHolder> {
    List<CouponCatModel> couponCatList;
    Context mContext;
    Activity mActivity;
    public static couponCatClickListner listClickListener;
    int lineCount;

    public CouponCatAdapter(Context context, Activity actvity) {
        this.mContext = context;
        this.mActivity=actvity;
        couponCatList = new ArrayList<>();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.coupon_cat_item, parent, false);
        return new MyViewHolder(itemView);
    }
    public void addToArray(CouponCatModel model) {
        couponCatList.add(model);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final CouponCatModel model=couponCatList.get(position);
        holder.tvCatName.setText(model.getName());
        holder.tvDescription.setText(model.getDetails());
        lineCount=holder.tvDescription.getLineCount();
      //  holder.tvDescription.setMovementMethod(new ScrollingMovementMethod());
        holder.tvDescription.post(new Runnable() {
            @Override
            public void run() {
                lineCount = holder.tvDescription.getLineCount();
                if(lineCount>4){
                    holder.tvViewMore.setText("View more");
                    holder.tvViewMore.setVisibility(View.VISIBLE);
                }else {
                    holder.tvViewLess.setText("View less");
                    holder.tvViewMore.setVisibility(View.INVISIBLE);
                }
            }
        });

        com.nostra13.universalimageloader.core.ImageLoader imageLoader = com.nostra13.universalimageloader.core.ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.not_image)
                .showImageOnFail(R.drawable.not_image).build();
        imageLoader.displayImage(model.getImage(), holder.ivCouponCatImage, options);


      holder.tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,BuyCouponActivity.class);
                intent.putExtra("catId", model.getId());
                intent.putExtra("recieverId",model.getReceiverID() );
                mContext.startActivity(intent);
                mActivity.overridePendingTransition(0, R.anim.exit_slide_right);
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
                holder.tvDescription.setMaxLines(5);
            }
        });


    }

    public CouponCatModel getItem(int position) {
        return couponCatList.get(position);
    }

    @Override
    public int getItemCount() {
       return couponCatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
           public TextView tvCatName;
           public TextView tvDescription;
           public ImageView ivCouponCatImage;
           public TextView tvViewMore,tvViewLess;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCatName= (TextView) itemView.findViewById(R.id.tv_couponCatName);
            tvDescription= (TextView) itemView.findViewById(R.id.tv_CouponCatDescription);
            ivCouponCatImage= (ImageView) itemView.findViewById(R.id.iv_couponCatImage);
            tvViewMore= (TextView) itemView.findViewById(R.id.tvViewMore);
            tvViewLess= (TextView) itemView.findViewById(R.id.tvViewLess);
            itemView.setOnClickListener(this);
        }

    @Override
    public void onClick(View v) {
        listClickListener.onItemClick(getPosition(), v);
    }
}

    public void setOnItemClickListener(couponCatClickListner myClickListener) {
        this.listClickListener = myClickListener;
    }

public interface couponCatClickListner {
    public void onItemClick(int position, View v);
}
}
