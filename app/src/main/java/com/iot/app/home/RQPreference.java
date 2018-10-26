package com.iot.app.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v11.Const;

import com.iot.app.R;

public class RQPreference {
    private Context mContext;
    private SharedPreferences sharedPrefs;

    public RQPreference(Context context) {
        mContext = context;
        sharedPrefs = context.getSharedPreferences(context.getString(R.string.rq_p_file_name), Context.MODE_PRIVATE);
    }

    public int getRequestedMarketView() {
        return sharedPrefs.getInt(mContext.getString(R.string.requested_market_view_pos), Const.RQ_MKT_VIEW_FAV);
    }

    public void setRequestedMarketView(int pos) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.requested_market_view_pos), pos);
        editor.apply();
    }

    public void removeRequestedMarketView() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.requested_market_view_pos));
        editor.apply();
    }

    public int getRequestedOrderView() {
        return sharedPrefs.getInt(mContext.getString(R.string.requested_order_view_pos), Const.RQ_ORD_VIEW_OPEN);
    }

    public void setRequestedOrderView(int pos) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.requested_order_view_pos), pos);
        editor.apply();
    }

    public void removeRequestedOrderView() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.requested_order_view_pos));
        editor.apply();
    }

    public int getRequestedTradeView() {
        return sharedPrefs.getInt(mContext.getString(R.string.requested_trdae_view_pos), Const.RQ_TRADE_VIEW_OPEN);
    }

    public void setRequestedTradeView(int type) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.requested_trdae_view_pos), type);
        editor.apply();
    }

    public void removeRequestedTradeView() {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(mContext.getString(R.string.requested_trdae_view_pos));
        editor.apply();
    }

    public void setChoosePairNameID(int pair_index) {
    }

    public int getBuySellChoice() {
        return sharedPrefs.getInt(mContext.getString(R.string.buy_sell_choice), 0);
    }

    public void setBuySellChoice(int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.buy_sell_choice), value);
        editor.apply();
    }

    public int getUserOrderType() {
        return sharedPrefs.getInt(mContext.getString(R.string.user_order_type), 0);
    }

    public void setUserOrderType(int value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putInt(mContext.getString(R.string.user_order_type), value);
        editor.apply();
    }

    public void setChoosePairName(String value, int id) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getString(R.string.choosed_pair_name), value);
        editor.putInt(mContext.getString(R.string.choosed_pair_id), id);
        editor.apply();
    }

    public String getChoosePairName() {
        return sharedPrefs.getString(mContext.getString(R.string.choosed_pair_name), "KRB/UAH");
    }

    public void setChoosePairName(String value) {
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(mContext.getString(R.string.choosed_pair_name), value);
        editor.apply();
    }

    public int getChoosePairID() {
        String pair = getChoosePairName();
        if (pair.equalsIgnoreCase("ETH/UAH")) return 15;
        else if (pair.equalsIgnoreCase("DOGE/UAH")) return 13;
        else if (pair.equalsIgnoreCase("BTC/UAH")) return 10;
        else if (pair.equalsIgnoreCase("LTC/UAH")) return 17;
        else if (pair.equalsIgnoreCase("KRB/UAH")) return 4;
        else if (pair.equalsIgnoreCase("KRB/LTC")) return 9;
        else if (pair.equalsIgnoreCase("ETH/BTC")) return 14;
        else if (pair.equalsIgnoreCase("DOGE/BTC")) return 12;
        else if (pair.equalsIgnoreCase("KRB/BTC")) return 7;
        else if (pair.equalsIgnoreCase("KRB/DOGE")) return 11;
        else if (pair.equalsIgnoreCase("KRB/ETH")) return 16;
        else return -1;
//        return sharedPrefs.getInt(mContext.getString(R.string.choose_pair_pos), 4);
    }

}