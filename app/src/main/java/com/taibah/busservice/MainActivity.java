package com.taibah.busservice;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.taibah.busservice.fragments.ChangePasswordFragment;
import com.taibah.busservice.fragments.ContactFragment;
import com.taibah.busservice.fragments.HomeFragment;
import com.taibah.busservice.fragments.ManageDrivers;
import com.taibah.busservice.fragments.ManageRoutes;
import com.taibah.busservice.fragments.ManageStudent;
import com.taibah.busservice.fragments.MapsFragment;
import com.taibah.busservice.fragments.ScheduleFragment;
import com.taibah.busservice.fragments.TwitterFragment;
import com.taibah.busservice.gcm.QuickstartPreferences;
import com.taibah.busservice.gcm.RegistrationIntentService;
import com.taibah.busservice.utils.AppGlobals;
import com.taibah.busservice.utils.Helpers;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    public static boolean isHomeFragmentOpen;
    public static NavigationView navigationView;

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    System.out.println(R.string.gcm_send_message);
                } else {
                    System.out.println(R.string.token_error_message);
                }
            }

        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (AppGlobals.isFirstRun()) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return;
        }

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.container_main, new HomeFragment());
        tx.commit();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        if (AppGlobals.getUserType() == 0) {

            // User Type: Admin

            navigationView.getMenu().getItem(1).setVisible(false);
            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(true);
            navigationView.getMenu().getItem(4).setVisible(true);
            navigationView.getMenu().getItem(5).setVisible(true);
            navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setVisible(false);

        } else if (AppGlobals.getUserType() == 1) {

            // User Type: Student

            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setVisible(true);
        } else if (AppGlobals.getUserType() == 2) {

            // User Type: Driver

            navigationView.getMenu().getItem(2).setVisible(false);
            navigationView.getMenu().getItem(3).setVisible(false);
            navigationView.getMenu().getItem(4).setVisible(false);
            navigationView.getMenu().getItem(5).setVisible(false);
            navigationView.getMenu().getItem(6).getSubMenu().getItem(0).setVisible(true);
        }

        navigationView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                navigationView.removeOnLayoutChangeListener(this);
                TextView textView = (TextView) navigationView.findViewById(R.id.tv_header_username);
                textView.setText(AppGlobals.getUsername());
                TextView textView1 = (TextView) navigationView.findViewById(R.id.tv_header_name);
                textView1.setText(AppGlobals.getName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!isHomeFragmentOpen) {
            AppGlobals.replaceFragment(getSupportFragmentManager(), new HomeFragment());
            navigationView.getMenu().getItem(0).setChecked(true);
            setTitle(navigationView.getMenu().getItem(0).getTitle());
        } else {
            super.onBackPressed();
        }
    }

    public void selectDrawerItem(MenuItem menuItem) {
        boolean logoutCheck = false;
        fragment = null;
        Class fragmentClass = null;
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_map:
                fragmentClass = MapsFragment.class;
                break;
            case R.id.nav_schedule:
                fragmentClass = ScheduleFragment.class;
                break;
            case R.id.nav_manage_routes:
                fragmentClass = ManageRoutes.class;
                break;
            case R.id.nav_manage_drivers:
                fragmentClass = ManageDrivers.class;
                break;
            case R.id.nav_manage_students:
                fragmentClass = ManageStudent.class;
                break;
            case R.id.nav_change_password:
                fragmentClass = ChangePasswordFragment.class;
                break;
            case R.id.nav_logout:
                logoutCheck = true;
                showLogoutDialog();
                break;
            case R.id.nav_twitter:
                fragmentClass = TwitterFragment.class;
                break;
            case R.id.nav_contact:
                fragmentClass = ContactFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
                break;
        }

        if (!logoutCheck) {
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
            menuItem.setCheckable(true);
            setTitle(menuItem.getTitle());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.container_main, fragment).commit();
                }
            }, 300);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        selectDrawerItem(item);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);
        alertDialogBuilder.setTitle("Logout");
        alertDialogBuilder
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        logout();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void logout() {

        Helpers.showProgressDialog(MainActivity.this, "Logging out");

        // TODO: Implement logout logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        AppGlobals.setFirstRun(true);
                        Helpers.dismissProgressDialog();
                        AppGlobals.putToken(null);
                        launchLoginActivity();
                    }
                }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    public void launchLoginActivity() {
        Intent startIntent = new Intent(MainActivity.this, LoginActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.this.startActivity(startIntent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
