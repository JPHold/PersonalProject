package com.hjp.projectone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjp.projectone.BaseContent.CallListener;
import com.hjp.projectone.R;
import com.hjp.projectone.util.PhotoAsyncTask;

import java.util.List;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class BankAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "BankAdapter";
    private static final String PHOTOURL = "http://img.dd369.com/space/100481/1151012261649.png";
    private static final int TYPE_ITEM = 0;  //普通Item
    private static final int TYPE_FOOTER = 1;  //底部Footer
    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //加载完成
    public static final int NO_MORE_DATA = 2;

    private final Context mContext;
    public List<String> mBankNames;
    private final LayoutInflater mInflater;
    private CallListener mCallListener;
    private int load_more_status = -1;
    private int dataCount;
    private boolean isRefresh = false;
    private boolean isRemove=false;

    public BankAdapter(Context context, List<String> bankNames) {
        mContext = context;
        mBankNames = bankNames;
        mInflater = LayoutInflater.from(mContext);
    }

    //更新第三个
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: " + viewType);
        //进行判断显示类型，来创建返回不同的View
        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.bankitem, parent, false);
            BankViewHolder itemViewHolder = new BankViewHolder(itemView);
            return itemViewHolder;
        }
        //不能返回null,超奇葩╮(╯-╰)╭
        View footerView = mInflater.inflate(R.layout.footer, parent, false);
        FooterViewHolder footViewHolder = new FooterViewHolder(footerView);
        if (viewType == TYPE_FOOTER) {
            if (load_more_status != -1) {
                Log.i(TAG, "onCreateViewHolder: " + load_more_status);
                return footViewHolder;
            }
        }
        return footViewHolder;
    }

    //更新第四个
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof BankViewHolder) {
            Log.i(TAG, "onBankViewHolder: " + position);

            final String bankName = mBankNames.get(position);
            BankViewHolder bankHolder = (BankViewHolder) holder;
            bankHolder.bankSign.setId(position);
            CallListener callListener = null;
            if (isRefresh) {
                callListener = removeListener;
                load_more_status = -1;
            }
            //显示银行标志
            PhotoAsyncTask asyncTask = new PhotoAsyncTask(bankHolder.bankSign, position, callListener);
            asyncTask.execute(BankAdapter.PHOTOURL);

            //显示银行名
            bankHolder.bankName.setText(bankName);

            bankHolder.bankRootLayout.setId(position);
            //item的点击事件,跳转到银行业务
            bankHolder.bankRootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = v.getId();
                    if (id == position) {
                        mCallListener.call(bankName);

                    }
                }
            });
        } else if (holder instanceof FooterViewHolder) {
            Log.i(TAG, "onFooterViewHolder: " + position + "/////" + load_more_status);
            if (load_more_status != -1) {
                Log.i(TAG, "onBindViewHolder: " + position);
                FooterViewHolder footerHolder = (FooterViewHolder) holder;

                switch (load_more_status) {
                    case LOADING_MORE:
                        footerHolder.addLayout(mContext, "正在加载更多数据...");
                        break;
                    case NO_MORE_DATA:
                        footerHolder.addLayout(mContext, "加载完成");
                        break;
                }
            }
        }
    }

    //更新第一个
    @Override
    public int getItemCount() {
        dataCount = mBankNames.size() + 1;
        Log.i(TAG, "getItemCount: " + dataCount);
        return dataCount;
    }

    //更新第二个
    @Override
    public int getItemViewType(int position) {
        if (position == dataCount - 1) {
            Log.i(TAG, "getItemViewType: " + position + "////" + TYPE_FOOTER);

            return TYPE_FOOTER;
        }
        Log.i(TAG, "getItemViewType: " + position + "////" + TYPE_ITEM);
        return TYPE_ITEM;
    }

    /**
     * 添加银行名
     *
     * @param bankName
     */
    public void addData(String bankName) {
        Log.i("test", "addData: " + mBankNames.size());
        isRefresh = true;
        mBankNames.add(bankName);
        changeMoreStatus(LOADING_MORE);
    }


    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        Log.i("test", "changeMoreStatus: " + status);
        load_more_status = status;

        notifyItemChanged(dataCount - 2);
    }

    public void setJumpListener(CallListener callListener) {
        mCallListener = callListener;
    }

    private CallListener removeListener = new CallListener() {
        @Override
        public void call(Object data) {
            if (((String) data).equals("ok")) {
                Log.i("test", "call: ");
                isRemove=true;
                //删除底部footerView
                notifyItemRemoved(dataCount - 1);
//                mCallListener.call(dataCount -3);
            }
        }
    };
}
