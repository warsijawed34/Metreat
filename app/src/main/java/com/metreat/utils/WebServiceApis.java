package com.metreat.utils;

import android.content.Context;

import com.metreat.R;

/**
 * Created by vinove on 16/8/16.
 */
public class WebServiceApis {

    Context mContext;

    public WebServiceApis(Context context) {
        mContext = context;
    }
    public String callApi() {

        return mContext.getString(R.string.api_url1);

    }

}
