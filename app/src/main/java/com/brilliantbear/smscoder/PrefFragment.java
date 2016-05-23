package com.brilliantbear.smscoder;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by Bear on 2016-5-21.
 */
public class PrefFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final int REQUEST_RECEIVE_SMS_CODE = 0;
    private Context context;
    private SwitchPreference preSwitch;
    private Preference preWay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        addPreferencesFromResource(R.xml.preference);

        preSwitch = (SwitchPreference) findPreference(getResources().getString(R.string.key_switch));
        preSwitch.setOnPreferenceChangeListener(this);
        preWay = findPreference(getResources().getString(R.string.key_way));
        preWay.setOnPreferenceChangeListener(this);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String way = sp.getString(getResources().getString(R.string.key_way), getResources().getString(R.string.copy_value));
        if (TextUtils.equals(way, getResources().getString(R.string.copy_value))) {
            preWay.setSummary(getResources().getString(R.string.copy));
        } else {
            preWay.setSummary(getResources().getString(R.string.notice));
        }

        checkPermission();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference.getKey().equals(getResources().getString(R.string.key_about))) {
            showAboutDialog();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView textView = new TextView(context);
        textView.setText(Html.fromHtml(getString(R.string.content_about)));
        builder.setView(textView, 50, 50, 50, 0);
        builder.setTitle(getString(R.string.about));
        builder.setPositiveButton("Ok", null);
        builder.show();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.d("TAG", "change");
        Resources resources = getResources();
        if (preference.getKey().equals(resources.getString(R.string.key_way))) {
            if (!(newValue instanceof String))
                return false;
            String str = (String) newValue;
            if (TextUtils.equals(str, resources.getString(R.string.copy_value))) {
                preference.setSummary(resources.getString(R.string.copy));
            } else if (TextUtils.equals(str, resources.getString(R.string.notice_value))) {
                preference.setSummary(resources.getString(R.string.notice));
            }
        } else if (preference.getKey().equals(resources.getString(R.string.key_switch))) {
            if (!(newValue instanceof Boolean)) {
                return false;
            }
            Boolean sw = (Boolean) newValue;
            if (sw) {
                checkPermission();
            }
        }
        return true;
    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.reason);
            builder.setPositiveButton(R.string.get_permission, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getPermission(Manifest.permission.RECEIVE_SMS);
                }
            });
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    private void getPermission(String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission}, REQUEST_RECEIVE_SMS_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_RECEIVE_SMS_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.permission_denied);
                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
                preSwitch.setChecked(false);
            } else {
                preSwitch.setChecked(true);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
