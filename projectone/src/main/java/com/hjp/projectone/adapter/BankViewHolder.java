package com.hjp.projectone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjp.projectone.R;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class BankViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout bankRootLayout;
    public ImageView bankSign;
    public TextView bankName;

    public BankViewHolder(View itemView) {
        super(itemView);
        bankRootLayout= (RelativeLayout) itemView.findViewById(R.id.bank_rootLayout);
        bankSign = (ImageView) itemView.findViewById(R.id.bankSign);
        bankName = (TextView) itemView.findViewById(R.id.bankName);
    }
}
