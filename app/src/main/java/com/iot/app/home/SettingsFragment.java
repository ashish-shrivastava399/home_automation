package com.iot.app.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v11.Const;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.iot.app.PasscodeActivity;
import com.iot.app.R;

import java.util.List;

public class SettingsFragment extends HBFragment {
    String TAG = this.getClass().getSimpleName();
    Toolbar aSTToolbar;
    PrimePreference rhPreference;
    View languageLayout, passcodeLayout, shufflePasscodeLayout, changePasscode;
    SwitchCompat switchPasscode, switchShufflePasscode;
    TextView languageShow;
    ImageView languageTitleIcon;
    ImageView aSTBackIcon;
    boolean isPasscodeSwitchTouched = false, isShufflePasscodeSwitchTouched = false, isThemeSwitchTouched = false;
    List<LangData> data;
    SharedPreferences preferences;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_settings, container, false);

        aSTToolbar = view.findViewById(R.id.account_setting_toolbar);
        getAct().setSupportActionBar(aSTToolbar);
        aSTToolbar.setTitle(R.string.settings);
        aSTBackIcon = view.findViewById(R.id.account_setting_backIcon);
        aSTBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction;
                transaction = getAct().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.account_frame, new MainAccountFragment());
                transaction.commit();
                getCont().bottomNavigationView.setVisibility(View.VISIBLE);

            }
        });
        return view;
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rhPreference = new PrimePreference(getCont().getApplicationContext());
        switchPasscode = view.findViewById(R.id.passcode_switch);
        switchShufflePasscode = view.findViewById(R.id.shuffle_passcode_switch);
        languageLayout = view.findViewById(R.id.language_layout);
        changePasscode = view.findViewById(R.id.change_passcode_layout);
        passcodeLayout = view.findViewById(R.id.passcode_layout);
        shufflePasscodeLayout = view.findViewById(R.id.shuffle_passcode_layout);
        languageShow = view.findViewById(R.id.language_show);
        languageTitleIcon = view.findViewById(R.id.language_title_icon);

        String langName = rhPreference.getLanguage();
        int langImgId = rhPreference.getLanguageImgId();
        languageShow.setText(langName);
        languageTitleIcon.setImageResource(langImgId);
        languageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLangAlertSpin();
            }
        });

        preferences = PreferenceManager.getDefaultSharedPreferences(getCont().getApplicationContext());

        if (rhPreference != null && rhPreference.hasPasscode()) {
            switchPasscode.setChecked(true);
            changePasscode.setVisibility(View.VISIBLE);
            shufflePasscodeLayout.setVisibility(View.VISIBLE);
            if (rhPreference.isShufflePasscode()) {
                switchShufflePasscode.setChecked(true);
            } else {
                switchShufflePasscode.setChecked(false);
            }
        } else {
            switchPasscode.setChecked(false);
            changePasscode.setVisibility(View.GONE);
            shufflePasscodeLayout.setVisibility(View.GONE);
        }

        switchPasscode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isPasscodeSwitchTouched = true;
                return false;
            }
        });
        switchPasscode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isPasscodeSwitchTouched) {
                    isPasscodeSwitchTouched = false;
                    if (isChecked) {
                        Intent intent;
                        intent = new Intent(getAct().getApplication(), PasscodeActivity.class);
                        intent.putExtra(getString(R.string.entry_pass), R.string.setup_passcode);
                        getAct().startActivityForResult(intent, Const.RQ_CHANGE_PASS);
                    } else {
                        showAlert(R.string.passcode_turn_off_alert_message);
                    }
                }
            }
        });

        switchShufflePasscode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isShufflePasscodeSwitchTouched = true;
                return false;
            }
        });
        switchShufflePasscode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isShufflePasscodeSwitchTouched) {
                    isShufflePasscodeSwitchTouched = false;
                    if (isChecked) {
                        rhPreference.setShufflePasscodeStatus(true);
                    } else rhPreference.setShufflePasscodeStatus(false);
                }
            }
        });

        changePasscode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getAct().getApplication(), PasscodeActivity.class);
                intent.putExtra(getString(R.string.entry_pass), R.string.reset_pass);
                getAct().startActivityForResult(intent, Const.RQ_CHANGE_PASS);
            }
        });
    }

    private void showLangAlertSpin() {
        data = LangData.getDefaultList();
        AlertDialog.Builder builder = new AlertDialog.Builder(getAct());
        builder.setTitle("Choose Language");
        final CustomLangAdapter adapter = new CustomLangAdapter(getAct(), R.layout.view_spinner_item, data);
        builder.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LangData selected = data.get(which);
                String name = selected.getName();
                String code = selected.getCode();
                int id = selected.getImageId();
                if (!Const.updateResourcesLegacy(getCont(), code)) {
                    Const.showCustomMess(getCont(), R.string.unable_to_change_language, false);
                } else rhPreference.setLanguage(name, code, id);
                try {
                    getCont().recreate();
                } catch (Exception e) {
                    Log.e(TAG, "Exception --> " + e.getMessage());
                }
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void showAlert(int message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getAct());
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rhPreference.setPasscodeStatus(false);
                        changePasscode.setVisibility(View.GONE);
                        shufflePasscodeLayout.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        switchPasscode.setChecked(true);
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onResume() {
        if (aSTToolbar != null)
            aSTToolbar.setTitle(R.string.settings);
        super.onResume();
    }

    class CustomLangAdapter extends ArrayAdapter<LangData> {
        private List<LangData> langData;

        public CustomLangAdapter(@NonNull Context context, int resource, List<LangData> langData) {
            super(context, 0, langData);
            this.langData = langData;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if (listItem == null)
                listItem = LayoutInflater.from(getContext()).inflate(R.layout.view_spinner_item, parent, false);

            LangData current = langData.get(position);

            ImageView image = listItem.findViewById(R.id.flag_icon);
            image.setImageResource(current.getImageId());

            TextView name = listItem.findViewById(R.id.name_lang);
            name.setText(current.getName());
            return listItem;
        }
    }
}
