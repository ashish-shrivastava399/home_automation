package com.iot.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v11.Const;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.NestedScrollView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.app.home.CustomLangAdapter;
import com.iot.app.home.LangData;
import com.iot.app.home.PrimePreference;

import java.util.List;
import java.util.Objects;


class InfoPagerAdapter extends PagerAdapter {

    private final Context context;
    private PrimePreference prime;
    private int childCount = 1;
    private int[] sliderTitleImage = {
            R.drawable.ic_language,
            R.drawable.ic_user_agreement,
            R.drawable.ic_permission,
            R.drawable.ic_create_acc
    };
    private int[] sliderTitle = {
            R.string.info_slider_one_title,
            R.string.info_slider_two_title,
            R.string.info_slider_three_title,
            R.string.info_slider_four_title
    };
    private int[] sliderDescription = {
            R.string.info_slider_one_desc,
            R.string.info_slider_two_desc,
            R.string.info_slider_three_desc,
            R.string.info_slider_four_desc
    };
    private int[] sliderButtonText = {
            R.string.info_slider_one_button_text,
            R.string.info_slider_two_button_text,
            R.string.info_slider_three_button_text,
            R.string.info_slider_four_button_text
    };
    private int[] sliderMessage = {
            R.string.info_slider_two_message,
            R.string.info_slider_three_message,
            R.string.info_slider_four_message
    };
    private List<LangData> data;

    InfoPagerAdapter(Context context) {
        this.context = context;
    }

    public void setChildCount(int childCount) {
        this.childCount = childCount;
    }

    @Override
    public int getCount() {
        return childCount;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = Objects.requireNonNull(layoutInflater).inflate(R.layout.view_info_slider, container, false);

        ImageView sliderTitleImage = v.findViewById(R.id.sliderTitleImage);
        TextView sliderTitle = v.findViewById(R.id.sliderTitle);
        final TextView sliderDescription = v.findViewById(R.id.sliderDescription);
        final Button sliderButton = v.findViewById(R.id.sliderButton);
        final TextView sliderMessage = v.findViewById(R.id.sliderMessage);

        sliderTitleImage.setImageResource(this.sliderTitleImage[position]);
        sliderTitle.setText(this.sliderTitle[position]);
        sliderDescription.setText(this.sliderDescription[position]);
        if (position == 0) {
            prime = new PrimePreference(context.getApplicationContext());
            sliderButton.setText(String.format(" %s", prime.getLanguage()));
            sliderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            sliderButton.setCompoundDrawablesWithIntrinsicBounds(prime.getLanguageImgId(), 0, 0, 0);
            sliderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showLangAlertSpin(sliderButton);
                }
            });
        }
        if (position < 1) {
            sliderMessage.setVisibility(View.GONE);
        } else {
            sliderButton.setText(getString(this.sliderButtonText[position]));
            sliderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().sliderOnClickListener(position, position == 2 ? sliderButton : null, position == 2 ? sliderMessage : null);
                }
            });
            if (position == 1) {
                sliderMessage.setText(getString(this.sliderMessage[position - 1]));
                sliderMessage.setTextColor(getActivity().getResources().getColor(R.color.info_slider_mess_permission_denied_color));
            } else if (position == 2 && getActivity().isAllGranted()) {
                sliderButton.setClickable(false);
                sliderButton.setAlpha(0.5f);
                sliderButton.setTextColor(getActivity().getResources().getColor(R.color.info_button_disable_txt_color));
                sliderMessage.setTextColor(getActivity().getResources().getColor(R.color.info_slider_mess_permission_granted_color));
                sliderMessage.setText(R.string.all_permission_granted);
            } else {
                sliderMessage.setText(getString(this.sliderMessage[position - 1]));
            }
        }
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((NestedScrollView) object);
    }

    private InformationActivity getActivity() {
        return ((InformationActivity) context);
    }

    private String getString(int id) {
        return context.getResources().getString(id);
    }

    private void showLangAlertSpin(final Button sliderButton) {
        data = LangData.getDefaultList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.choose_language);
        final CustomLangAdapter adapter = new CustomLangAdapter(getActivity(), R.layout.view_spinner_item, data);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LangData selected = data.get(which);
                String name = selected.getName();
                String code = selected.getCode();
                int id = selected.getImageId();
                sliderButton.setText(String.format(" %s", name));
                sliderButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                sliderButton.setCompoundDrawablesWithIntrinsicBounds(id, 0, 0, 0);

                if (Const.updateResourcesLegacy(context, code))
                    prime.setLanguage(name, code, id);
                else Const.showCustomMess(context, R.string.unable_to_change_language, false);

                ((InformationActivity) context).recreate();
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
