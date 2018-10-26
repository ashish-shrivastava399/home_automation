package com.iot.app.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v11.Const;
import android.support.v11.GenericAdapter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.iot.app.LoginActivity;
import com.iot.app.R;

import java.util.ArrayList;
import java.util.List;

public class MainAccountFragment extends HBFragment {
    public SwipeRefreshLayout swipeLayout;
    public FragmentTransaction transaction;
    public FragmentManager fragmentManager;
    Toolbar accountMToolbar;
    PrimePreference rhPreference;
    View progressView;
    RecyclerView recyclerView;
    AccountAdapter mAdapter;
    List<AccountData> accountList;
    FrameLayout accountMFrame;
    private String TAG = this.getClass().getSimpleName();

    public static MainAccountFragment newInstance() {
        Log.e("Instance", "MainAccountFragment newInstance");
        return new MainAccountFragment();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_main, container, false);

        accountMToolbar = view.findViewById(R.id.account_m_toolbar);
        getAct().setSupportActionBar(accountMToolbar);
        accountMToolbar.setTitle(R.string.account);
        fragmentManager = getCont().getSupportFragmentManager();

        progressView = view.findViewById(R.id.progressView);
        rhPreference = new PrimePreference(getAct().getApplicationContext());
        recyclerView = view.findViewById(R.id.account_recyclerView);
        swipeLayout = view.findViewById(R.id.swipe_container);
        progressView = view.findViewById(R.id.progressView);
        accountList = new ArrayList<>();
        mAdapter = new AccountAdapter(getAct(), accountList);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getAct());
        recyclerView.setLayoutManager(layoutManager);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));
        recyclerView.setAdapter(mAdapter);
        progressView.setVisibility(View.GONE);
        swipeLayout.setRefreshing(false);
        addItem();
        recyclerView.setAdapter(mAdapter);
        progressView.setVisibility(View.GONE);
        return view;
    }

    private void addItem() {

        String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String role = "User";

        accountList.add(0, new AccountData(R.mipmap.ic_launcher, name, role));
        accountList.add(1, new AccountData(R.drawable.ic_account, getString(R.string.my_details), getString(R.string.account_my_details_desc)));
        accountList.add(2, new AccountData(R.drawable.ic_settings, getString(R.string.settings), getString(R.string.account_settings_desc)));
        accountList.add(3, new AccountData(R.drawable.ic_about_us, getString(R.string.about_us), getString(R.string.account_about_us_desc)));
        accountList.add(4, new AccountData(R.drawable.ic_logout, getString(R.string.logout)));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        Log.e(TAG, TAG + " setUserVisibleHint " + isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null && accountMToolbar != null) {
                accountMToolbar.setTitle(R.string.account);
            }
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onStop() {
        setUserVisibleHint(false);
        Log.e("PRAMFrag", TAG + " onStop ");
        super.onStop();
    }

    class AccountAdapter extends GenericAdapter<AccountData> {

        public AccountAdapter(@NonNull Context context, List<AccountData> list) {
            super(context, list);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            if (viewType == Const.HEADER) {
                return new AccountHeaderVH(inflater.inflate(R.layout.view_account_list_header, parent, false));
            } else if (viewType == Const.LOGOUT) {
                return new AccountLogoutVH(inflater.inflate(R.layout.view_account_list_logout, parent, false));
            } else {
                return new AccountItemVH(inflater.inflate(R.layout.view_account_list_item, parent, false));
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            Log.e("Adapter", " onBindViewHolder " + position);
            if (holder.getItemViewType() == Const.ITEM) {
                String name = dataList.get(position).getName();
                String desc = dataList.get(position).getDesc();
                int imgId = dataList.get(position).getImageId();
                Log.e("Adapter", " onBindViewHolder " + dataList.get(position).getName() + " " + name);

                AccountItemVH hold = (AccountItemVH) holder;

                hold.name.setText(name);
                hold.desc.setText(desc);
                hold.logo.setImageResource(imgId);
                hold.fixedIcon.setImageResource(R.drawable.ic_navigate_previous);
                Log.e("AdapLog", "in the On Item View");
                hold.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("AdapLog", "Clicken On e On Item View");
                        goFor(position);
                    }
                });

            } else if (holder.getItemViewType() == Const.LOGOUT) {
                AccountLogoutVH hold = (AccountLogoutVH) holder;
                hold.logOut.setBackgroundColor(context.getResources().getColor(R.color.textColorGreen));
                hold.logOut.setText(dataList.get(position).getName());
                Log.e("AdapLog", "in the On Logout");
                hold.logOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("AdapLog", "Clicken On Logout");
                        showLogoutAlert(R.string.logout_alert_message);
                    }
                });
            } else {
                String name = dataList.get(0).getName();
                int imgId = dataList.get(0).getImageId();
                Log.e("Adapter", " onBindViewHolder " + dataList.get(position).getName() + " " + name);

                AccountHeaderVH hold = (AccountHeaderVH) holder;
                hold.name.setText(name);
                hold.role_icon.setImageResource(R.mipmap.ic_launcher);
                Log.e("AdapLog", "in the On Item View");
                hold.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("AdapLog", "Clicken On e On Item View");
                    }
                });
            }
        }

        private void goFor(int position) {
            FragmentTransaction transaction;
            transaction = fragmentManager.beginTransaction();
            switch (position) {
                case 1:
                    transaction.replace(R.id.account_frame, UserDetailsFragment.newInstance());
                    transaction.commit();
                    getCont().bottomNavigationView.setVisibility(View.GONE);
                    break;
                case 2:
                    transaction.replace(R.id.account_frame, SettingsFragment.newInstance());
                    transaction.commit();
                    getCont().bottomNavigationView.setVisibility(View.GONE);
                    break;
                case 3:
                    transaction.replace(R.id.account_frame, AboutUsFragment.newInstance());
                    transaction.commit();
                    getCont().bottomNavigationView.setVisibility(View.GONE);
                    break;
            }
        }

        private void showLogoutAlert(int message) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.BaseAppTheme));
            builder.setTitle(R.string.app_name);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Const.logOut(context);
                            getCont().finishAffinity();
                            context.startActivity(new Intent(context, LoginActivity.class));

                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return Const.HEADER;
            } else if (position == getItemCount() - 1) {
                return Const.LOGOUT;
            } else return Const.ITEM;
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }


        class AccountLogoutVH extends ItemViewHolder {
            Button logOut;

            AccountLogoutVH(View itemView) {
                super(itemView);
                logOut = findById(R.id.log_out);
            }
        }

        class AccountItemVH extends ItemViewHolder {
            TextView name, desc;
            ImageView logo, fixedIcon;

            AccountItemVH(View itemView) {
                super(itemView);
                name = findById(R.id.list_item_name);
                desc = findById(R.id.list_item_desc);
                logo = findById(R.id.list_item_title_icon);
                fixedIcon = findById(R.id.list_item_icon);
            }
        }

        class AccountHeaderVH extends HeaderViewHolder {
            TextView name;
            ImageView role_icon;

            AccountHeaderVH(View itemView) {
                super(itemView);
                role_icon = findById(R.id.role_icon);
                name = findById(R.id.name);
            }
        }
    }
}
