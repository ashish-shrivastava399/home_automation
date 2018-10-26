package com.iot.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.app.home.PrimePreference;

import java.util.Arrays;
import java.util.Collections;

class PassGridViewAdapter extends ArrayAdapter<String> {

    private static int[] numberList = new int[10];
    private static boolean shuffle;

    static {
        for (int i = 1; i < numberList.length; i++) {
            numberList[i - 1] = i;
        }
        numberList[9] = 0;
    }

    private Activity mContext;
    private int[] mPatterns;

    PassGridViewAdapter(Activity context, int... patterns) {
        super(context, R.layout.view_passcode_digit);
        shuffle = new PrimePreference(context.getApplicationContext()).isShufflePasscode();
        mContext = context;
        mPatterns = patterns;
        regenerateKey();
    }

    static void regenerateKey() {
        if (shuffle)
            Collections.shuffle(Arrays.asList(numberList));
        else {
            numberList = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        }
    }

    @Override
    public int getCount() {
        return 12;
    }

    @NonNull
    @SuppressLint({"ViewHolder", "InflateParams"})
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.view_passcode_digit, null, true);
        TextView tvContent = rowView.findViewById(R.id.number);
        if (position == mPatterns[0]) {
            tvContent.setVisibility(View.GONE);
            ImageView img = rowView.findViewById(R.id.buttonImage);
            img.setImageResource(R.drawable.ic_backspace);
            img.setVisibility(View.VISIBLE);
        } else if (position == mPatterns[1]) {
            tvContent.setVisibility(View.GONE);
            ImageView img = rowView.findViewById(R.id.buttonImage);
            img.setImageResource(mPatterns[2]);
            img.setVisibility(View.VISIBLE);
        } else if (position < mPatterns[1]) tvContent.setText(String.valueOf(numberList[position]));
        else if (position == 10) tvContent.setText(String.valueOf(numberList[position - 1]));
        else tvContent.setText(String.valueOf(mPatterns[1] + 1));
        return rowView;
    }
}

