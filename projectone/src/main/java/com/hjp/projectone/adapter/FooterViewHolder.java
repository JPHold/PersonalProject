package com.hjp.projectone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjp.projectone.R;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class FooterViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout mBankItemRootLayout;
    public ProgressBar mFooterProgressBar;
    public TextView mFooterTitle;

    /**
     * @param itemView 也是不能为null,╮(╯-╰)╭
     */
    public FooterViewHolder(View itemView) {
        super(itemView);
        if (itemView != null) {
            mBankItemRootLayout = (LinearLayout) itemView.findViewById(R.id.bankItem_rootLayout);

        }
    }

    public void addLayout(Context context, String title) {
        if (mFooterProgressBar == null) {
            mFooterProgressBar = new ProgressBar(context);
            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.gravity = Gravity.CENTER;
            mFooterProgressBar.setLayoutParams(lp1);
            mFooterProgressBar.setId(R.id.footerProBar);
            mBankItemRootLayout.addView(mFooterProgressBar);

        }
        if (mFooterTitle == null) {
            LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                    , ViewGroup.LayoutParams.WRAP_CONTENT);
            lp2.gravity = Gravity.CENTER;
            mFooterTitle = new TextView(context);
            mFooterTitle.setLayoutParams(lp2);
            mFooterTitle.setId(R.id.footerTitle);
            mBankItemRootLayout.addView(mFooterTitle);
        } else {
            mFooterTitle.setText(title);
        }

    }

}
