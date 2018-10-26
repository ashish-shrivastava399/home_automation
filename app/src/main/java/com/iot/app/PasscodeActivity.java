package com.iot.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v11.BottomSheetFragment;
import android.support.v11.Const;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iot.app.home.HomeActivity;
import com.iot.app.home.PrimePreference;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class PasscodeActivity extends AppCompatActivity {

    protected int entry_type;
    String TAG = this.getClass().getSimpleName();
    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME = "AndroidKey";
    private PassAdapter passAdapter;
    private String pin = "";
    private String pre_pin = "";
    private int cnt;
    private TextView mTitle, mMessage;
    private PrimePreference spc;
    private int stage = 0;
    private boolean hasDestination = false;

    private LinearLayout mDotsLayout;
    private TextView mDots[];
    private int previous_pos = -1;

    private Animation upTODown;
    private TranslateAnimation translateAnimation;
    private int passcodeSize;
    protected String tempSize = passcodeSize + "";
    private View setting, skip;
    private TextView fingerprintLabel;
    private boolean isFinishAffinity;
    private boolean isSizeChangeFromThere = false;
    private RecyclerView passView;

    private CoordinatorLayout mCoordinatorLayout;
    private BottomSheetFragment bottomSheetFragment;
    private View slideBSF;
    private FingerprintHandler fingerprintHandler;
    private boolean fromThere = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        spc = new PrimePreference(getApplicationContext());
        if (!intent.hasExtra(getString(R.string.from_there_only)) || !intent.getBooleanExtra(getString(R.string.from_there_only), false))
            spc.setPasscodeSize(spc.getOrgPasscodeSize());
        passcodeSize = spc.getPasscodeSize();
        initializeAnimation();

        setContentView(R.layout.activity_passcode);
        mCoordinatorLayout = findViewById(R.id.pass_main_layout);
        passView = findViewById(R.id.passView);
        mDotsLayout = findViewById(R.id.dotsLayout);
        mTitle = findViewById(R.id.passcode_title);
        mMessage = findViewById(R.id.message);
        setting = findViewById(R.id.setting);
        skip = findViewById(R.id.skip);
        fingerprintLabel = findViewById(R.id.fingerprintLabel);
        slideBSF = findViewById(R.id.slide_bsf);

        // Added dots in the View
        addDots();

        bottomSheetFragment = new BottomSheetFragment();
        bottomSheetFragment.setContext(fingerprintHandler);


        // Setting RecyclerView
        passView.setHasFixedSize(true);
        passAdapter = new PassAdapter(this, ((GridLayoutManager) passView.getLayoutManager()).getSpanCount(), R.drawable.ic_exit);
        passView.setAdapter(passAdapter);
        passView.setAnimation(upTODown);

        // Checking Type of Current Passcode
        hasDestination = intent.hasExtra(getString(R.string.destination));
        if (intent.hasExtra(getString(R.string.entry_pass))) {
            entry_type = intent.getIntExtra(getString(R.string.entry_pass), -1);
            switch (entry_type) {
                case R.string.setup_passcode:
                    setFingerprint(false);
                    if (!intent.getBooleanExtra(getString(R.string.from_there_only), false)) {
                        showChangePasscodeAlertBox(R.string.choose_passcode_size);
                    }
                    mTitle.setText(R.string.create_passcode);
                    mMessage.setText(R.string.create_passcode_message);
                    setSetting(true);
                    isFinishAffinity = intent.getBooleanExtra("isFinishAffinity", true);
                    break;
                case R.string.pass_check_point:
                    setSetting(false);
                    setFingerprint(true);
                    mTitle.setText(R.string.enter_passcode);
                    mMessage.setText(R.string.pass_check_point_message);
                    break;
                case R.string.reset_pass:
                    findViewById(R.id.skip).setVisibility(View.GONE);
                    if (intent.getBooleanExtra(getString(R.string.from_there_only), false)) {
                        changeActivityToCreatePasscode();
                    } else {
                        setSetting(false);
                        setFingerprint(true);
                        mTitle.setText(R.string.confirm_passcode);
                        mMessage.setText(R.string.pass_check_point_message);
                    }
                    break;
            }
        } else if (intent.getDataString() != null && intent.getDataString().equalsIgnoreCase(getString(R.string.reset_pass_from_pref))) {
            findViewById(R.id.skip).setVisibility(View.GONE);
            mTitle.setText(R.string.confirm_passcode);
            mMessage.setText(R.string.pass_check_point_message);
            entry_type = R.string.reset_pass;
            intent.putExtra(getString(R.string.entry_pass), R.string.reset_pass);
            setSetting(false);
            setFingerprint(true);
        }

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasscodeAlertBox(R.string.change_passcode_size);
            }
        });
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                skipView();
            }
        });

    }

    private void setFingerprint(boolean status) {
        fingerprintLabel.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (fingerprintManager == null || keyguardManager == null) {
                Log.e(TAG, "Fingerprint Scanner not detected in Device");
                fingerprintLabel.setText(R.string.fingerprint_service_not_available);
                fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else if (!fingerprintManager.isHardwareDetected()) {
                Log.e(TAG, "Fingerprint Scanner not detected in Device");
                fingerprintLabel.setText(R.string.fingerprint_scanner_not_detected);
                fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Permission not granted to use Fingerprint Scanner");
                fingerprintLabel.setText(R.string.fingerprint_scanner_permission_not_granted);
                fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else if (!keyguardManager.isKeyguardSecure()) {
                Log.e(TAG, "Add Lock to your Phone in Settings");
                fingerprintLabel.setText(R.string.add_lock_to_phone);
                fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                Log.e(TAG, "You should add atleast 1 Fingerprint to use this Feature");
                fingerprintLabel.setText(R.string.add_minimum_one_fingerprint);
                fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            } else {
                Log.e(TAG, "Place your Finger on Scanner to Access the App.");
                if (status) {
                    generateKey();
                    if (cipherInit()) {
                        fingerprintLabel.setText(R.string.swipe_up_to_scanner);
                        fingerprintLabel.setTextColor(getColor(R.color.textColorSecondary));
                        fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.ic_up), null, null, null);
                        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                        slideBSF.setOnTouchListener(new OnSwipeTouchListener(this) {
                            public void onSwipeTop() {
                                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
                                restartAuth();
                            }
                        });
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        fingerprintHandler = new FingerprintHandler(this);
                        fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                    }
                } else {
                    if (bottomSheetFragment != null)
                        try {
                            bottomSheetFragment.dismiss();
                        } catch (Exception e) {
                        }
                    slideBSF.setOnTouchListener(null);
                    fingerprintLabel.setText(R.string.finger_scanner_detected);
                    fingerprintLabel.setTextColor(getColor(R.color.textColorSecondary));
                    fingerprintLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }
        }
    }

    public void fingerprintResponseUpdate(String message, boolean b) {
        fingerprintLabel.setText(message);
        final ImageView fingerImg = bottomSheetFragment.contentView.findViewById(R.id.fingerprintImage);
        if (b) {
            Log.e("AAAAAAAAAAAAAAAAAA", getIntent().getDataString() + " getIntent().getDataString(");
            Log.e("AAAAAAAAAAAAAAAAAA", " entry_type  " + entry_type);
            fingerprintLabel.setTextColor(getResources().getColor(R.color.textColorGreen));
            fingerImg.setImageResource(R.mipmap.action_done);
            if (entry_type == R.string.reset_pass) {
                Log.e("AAAAAAAAAAAAAAAAAA", " changeActivityToCreatePasscode 01 " + entry_type);
                changeActivityToCreatePasscode();
            } else {
                Log.e("AAAAAAAAAAAAAAAAAA", " goTOHome 01 " + entry_type);
                goTOHome();
            }
        } else {
            fingerprintLabel.setTextColor(getResources().getColor(R.color.textColorRed));
            bottomSheetFragment.dismiss();
            fingerImg.setImageResource(R.mipmap.action_error);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fingerImg.setImageResource(R.mipmap.ic_fingerprint);
                }
            }, 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void restartAuth() {
        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
        fingerprintHandler = new FingerprintHandler(this);
        fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
        if (bottomSheetFragment != null) {
            bottomSheetFragment.dismiss();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyStore.load(null);
            keyGenerator.init(new
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            keyGenerator.generateKey();

        } catch (KeyStoreException | IOException | CertificateException
                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
                | NoSuchProviderException e) {

            e.printStackTrace();

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }
        try {

            keyStore.load(null);

            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;

        } catch (KeyPermanentlyInvalidatedException e) {
            return false;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }

    }

    private void changeLevel() {
        switch (stage) {
            case 0:
                if (entry_type == R.string.setup_passcode)
                    changeActivityToReenterPasscode();
                else {
                    if (spc.matchPasscode(pin)) {
                        if (entry_type == R.string.reset_pass)
                            changeActivityToCreatePasscode();
                        else
                            goTOHome();
                    } else {
                        int wal = spc.getWrongAttemptsLeft();
                        if (wal <= 0) {
                            Const.logOut(getApplicationContext());
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            passAdapter.regenerateKey();
                            passAdapter.notifyDataSetChanged();
                            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getString(R.string.wrong_passcode, wal), Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(Color.TRANSPARENT);
                            TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.RED);
                            snackbar.show();
                            showShakeEffect();
                            cnt = 0;
                            addDotsClearAll();
                            pin = "";
                        }
                    }
                }
                break;
            case 1:
                if (pre_pin.equals(pin)) {
                    spc.setPasscode(pin);
                    spc.setOrgPasscodeSize(pin.length());
                    showSnackMessage(true, R.string.passcode_successfully_set);
                    goTOHome();
                } else {
                    wrongPasscode(R.string.passcode_not_match);
                }
                break;
            default:
                wrongPasscode(R.string.exception_passcode);
                finish();
                break;
        }
    }

    private void wrongPasscode(int message) {
        passAdapter.regenerateKey();
        passAdapter.notifyDataSetChanged();
        showSnackMessage(false, message);
        showShakeEffect();
        cnt = 0;
        addDotsClearAll();
        pin = "";
    }

    private void showSnackMessage(boolean status, int messId) {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, messId, Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(Color.TRANSPARENT);
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        if (status)
            textView.setTextColor(Color.GREEN);
        else textView.setTextColor(Color.RED);
        snackbar.show();
        snackbar.setDuration(1000);
    }

    private void initializeAnimation() {
        int animationDuration = 1000;
        upTODown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        upTODown.setDuration(animationDuration);
        translateAnimation = new TranslateAnimation(0.0f, 0.0f, 0.0f, -100.0f);
        translateAnimation.setDuration(100);
    }

    private void addDots() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int textSizeUnit = TypedValue.COMPLEX_UNIT_SP;
        float textSize = getResources().getInteger(R.integer.passcode_dots_size);
        int textColor = getResources().getColor(R.color.colorLightGray);
        mDots = new TextView[passcodeSize];
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText("\u25E6");
            mDots[i].setLayoutParams(params);
            mDots[i].setTextSize(textSizeUnit, textSize);
            mDots[i].setTextColor(textColor);
            mDotsLayout.addView(mDots[i]);
        }
    }

    private void addDotsIndicator(int pos) {
        upTODown.setDuration(100);
        if (mDots.length > 0) {
            if (pos > previous_pos) {
                mDots[pos].setText("\u2022");
                mDots[pos].setTextColor(getResources().getColor(R.color.colorAccent));
                mDots[pos].startAnimation(upTODown);
                previous_pos = pos;
            } else {
                mDots[pos].setText("\u25E6");
                mDots[pos].setTextColor(getResources().getColor(R.color.colorLightGray));
                previous_pos = pos - 1;
            }
        }
    }

    private void addDotsClearAll() {
        for (TextView mDot : mDots) {
            mDot.setText("\u25E6");
            mDot.setTextColor(getResources().getColor(R.color.colorLightGray));
        }
        previous_pos = -1;
    }

    public void goTOHome() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Log.e("AAAAAAAAAA", "AAA1 ");
                if (getIntent().getDataString() != null && getIntent().getDataString().equalsIgnoreCase(getString(R.string.reset_pass_from_pref))) {
                    Log.e("AAAAAAAAAA", "AAA2 ");
                    setResult(Const.ON_RESTART_RESULT_CODE, new Intent().putExtra(getString(R.string.security_check), true));
                    Const.showCustomMess(PasscodeActivity.this, R.string.change_passcode_successfully, true);
                    fromThere = true;
                } else if (!hasDestination) {
                    startActivity(new Intent(PasscodeActivity.this, HomeActivity.class));
                } else {
                    Log.e("AAAAAAAAAA", "AAA4 ");
                    setResult(Const.ON_RESTART_RESULT_CODE, new Intent().putExtra(getString(R.string.security_check), true));
                }
                Log.e("AAAAAAAAAA", "AAA5 ");
                overridePendingTransition(R.anim.gofrom_leftright, R.anim.goto_leftright);
                try {
                    Log.e("AAAAAAAAAA", "AAA6 ");
                    if (isFinishAffinity) {
                        Log.e("AAAAAAAAAA", "AAA7 ");
                        finish();
                    } else {
                        Log.e("AAAAAAAAAA", "AAA8 ");
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("AAAAAAAAAA", "AAA9 ");
                    finish();
                }
                Log.e("AAAAAAAAAA", "AAA10 ");
            }
        }, 500);
    }


    private void changeActivityToReenterPasscode() {
        setSetting(false);
        stage++;
        passAdapter.regenerateKey();
        passAdapter.buttonPosition[2] = R.drawable.ic_navigate_previous;
        passAdapter.notifyDataSetChanged();
        mTitle.setText(R.string.reenter_passcode);
        mMessage.setText(R.string.reenter_passcode_message);
        cnt = 0;
        addDotsClearAll();
        pre_pin = pin;
        pin = "";
    }

    protected void changeActivityToCreatePasscode() {
        if (!getIntent().getBooleanExtra(getString(R.string.from_there_only), false))
            showChangePasscodeAlertBox(R.string.choose_passcode_size);
        setFingerprint(false);
        setSetting(true);
        stage = 0;
        passAdapter.regenerateKey();
        passAdapter.buttonPosition[2] = R.drawable.ic_exit;
        passAdapter.notifyDataSetChanged();
        entry_type = R.string.setup_passcode;
        mTitle.setText(R.string.create_passcode);
        mMessage.setText(R.string.create_passcode_message);
        cnt = 0;
        addDotsClearAll();
        pre_pin = "";
        pin = "";
    }

    private void goToBack() {
        if (stage == 0) {
            if (hasDestination) {
                finish();
            } else {
                onBackPressed();
            }
            spc.setPasscodeSize(spc.getOrgPasscodeSize());
        } else {
            setSetting(true);
            stage--;
            passAdapter.regenerateKey();
            if (stage == 0) passAdapter.buttonPosition[2] = R.drawable.ic_exit;
            passAdapter.notifyDataSetChanged();
            mTitle.setText(R.string.create_passcode);
            mMessage.setText(R.string.create_passcode_message);
            cnt = 0;
            addDotsClearAll();
            pre_pin = "";
            pin = "";
        }
    }

    private void setSetting(boolean status) {
        if (status) {
            setting.setVisibility(View.VISIBLE);
            setting.setClickable(true);
            setting.setFocusable(true);
            skip.setVisibility(View.VISIBLE);
            skip.setClickable(true);
            skip.setFocusable(true);

        } else {
            setting.setVisibility(View.INVISIBLE);
            setting.setClickable(false);
            setting.setFocusable(false);
            skip.setVisibility(View.INVISIBLE);
            skip.setClickable(false);
            skip.setFocusable(false);
        }
    }

    public void skipView() {
        spc.setPasscodeStatus(false);
        spc.setPasscodeSize(spc.getOrgPasscodeSize());
        startActivity(new Intent(PasscodeActivity.this, HomeActivity.class));
        overridePendingTransition(R.anim.gofrom_leftright, R.anim.goto_leftright);
        finish();
    }

    private void showShakeEffect() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        mDotsLayout.startAnimation(shake);
        passView.startAnimation(shake);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {200, 1500, 1500};
        Objects.requireNonNull(vibrator).vibrate(pattern, -1);
    }

    private void showChangePasscodeAlertBox(int messageID) {
        Log.e(TAG, "spc.getPasscodeSize() " + spc.getPasscodeSize());
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(messageID);
        alertDialog.setIcon(R.drawable.ic_settings);
        final String[] sizePass = new String[]{"3", "4", "5", "6"};
        tempSize = spc.getPasscodeSize() + "";
        Log.e(TAG, "spc.tempSize  " + tempSize);
        alertDialog.setSingleChoiceItems(sizePass, spc.getPasscodeSize() - 3, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tempSize = sizePass[i];
                Log.e(TAG, "setSingleChoiceItems.tempSize  " + tempSize);
            }
        });

        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "setNegativeButton.tempSize  " + tempSize);
            }
        });

        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.e(TAG, "setPositiveButton.tempSize  " + tempSize);
                spc.setPasscodeSize(Integer.parseInt(tempSize));
                isSizeChangeFromThere = true;
                Log.e(TAG, "spc.getPasscodeSize() after " + spc.getPasscodeSize());
                Intent intent = getIntent();
                intent.putExtra(getString(R.string.from_there_only), true);
                startActivity(intent);
                fromThere = true;
                finish();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!hasDestination || getIntent().getDataString() != null && getIntent().getDataString().equalsIgnoreCase(getString(R.string.reset_pass_from_pref))) {
            setResult(Const.ON_RESTART_RESULT_CODE, new Intent().putExtra(getString(R.string.security_check), true));
            spc.setPasscodeSize(spc.getOrgPasscodeSize());
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        if (!fromThere && getIntent().getDataString() != null && getIntent().getDataString().equalsIgnoreCase(getString(R.string.reset_pass_from_pref))) {
            spc.setPasscodeSize(spc.getOrgPasscodeSize());
            Const.showCustomMess(PasscodeActivity.this, R.string.change_passcode_canceled, false);
            finish();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isSizeChangeFromThere)
            spc.setPasscodeSize(spc.getOrgPasscodeSize());
    }


    class PassAdapter extends RecyclerView.Adapter<PassAdapter.ItemVH> {

        int[] buttonPosition = new int[3];
        private boolean shuffle;
        private Integer[] numberList = new Integer[10];

        {
            for (int i = 1; i < numberList.length; i++) {
                numberList[i - 1] = i;
            }
            numberList[9] = 0;
        }

        PassAdapter(PasscodeActivity context, int spanCount, int resValue) {
            shuffle = spc.isShufflePasscode();
            buttonPosition[0] = getItemCount() - spanCount;
            buttonPosition[1] = getItemCount() - 1;
            buttonPosition[2] = resValue;
            regenerateKey();
        }

        void regenerateKey() {
            if (shuffle)
                Collections.shuffle(Arrays.asList(numberList));
            else {
                numberList = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
            }
        }

        @NonNull
        @Override
        public ItemVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
            View v = inflater.inflate(R.layout.view_passcode_digit, viewGroup, false);
            return new ItemVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemVH holder, int position) {

            if (position == buttonPosition[0]) {
                holder.textView.setVisibility(View.GONE);
                holder.imageView.setImageResource(buttonPosition[2]);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        goToBack();
                    }
                });
            } else if (position == buttonPosition[1]) {
                holder.textView.setVisibility(View.GONE);
                holder.imageView.setImageResource(R.drawable.ic_backspace);
                holder.imageView.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (cnt > 0) {
                            cnt--;
                            pin = pin.substring(0, pin.length() - 1);
                            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    addDotsIndicator(cnt);
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            mDots[cnt].startAnimation(translateAnimation);
                        }
                    }
                });
            } else {
                final int currValue;
                if (position < buttonPosition[0]) {
                    currValue = numberList[position];
                } else {
                    currValue = numberList[position - 1];
                }

                holder.imageView.setVisibility(View.GONE);
                holder.textView.setVisibility(View.VISIBLE);
                holder.textView.setText(String.valueOf(currValue));

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (cnt < passcodeSize) {
                            try {
                                translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        addDotsIndicator(cnt);
                                        pin = pin + currValue;
                                        cnt++;
                                        if (cnt == passcodeSize) changeLevel();
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                mDots[cnt].startAnimation(translateAnimation);
                            } catch (NullPointerException e) {
                                wrongPasscode(R.string.exception_passcode);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return 12;
        }

        class ItemVH extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView imageView;

            ItemVH(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.number);
                imageView = itemView.findViewById(R.id.buttonImage);
            }

        }
    }
}