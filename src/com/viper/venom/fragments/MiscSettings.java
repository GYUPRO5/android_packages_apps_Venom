package com.viper.venom.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.text.format.DateFormat;
import android.provider.Settings;
import android.os.UserHandle;
import android.view.View;
import android.widget.EditText;

import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.dashboard.DashboardSummary;

import com.viper.venom.preference.CustomSeekBarPreference;
import com.viper.venom.utils.TelephonyUtils;

import java.util.Date;

public class MiscSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    private static final String WIRED_RINGTONE_FOCUS_MODE = "wired_ringtone_focus_mode";
    private static final String DASHBOARD_PORTRAIT_COLUMNS = "dashboard_portrait_columns";
    private static final String DASHBOARD_LANDSCAPE_COLUMNS = "dashboard_landscape_columns";
    private static final String INCALL_VIB_OPTIONS = "incall_vib_options";

    private ListPreference mWiredHeadsetRingtoneFocus;
    private CustomSeekBarPreference mDashboardPortraitColumns;
    private CustomSeekBarPreference mDashboardLandscapeColumns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.misc_settings);
        ContentResolver resolver = getActivity().getContentResolver();

        mWiredHeadsetRingtoneFocus = (ListPreference) findPreference(WIRED_RINGTONE_FOCUS_MODE);
        int mWiredHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.WIRED_RINGTONE_FOCUS_MODE, 1);
        mWiredHeadsetRingtoneFocus.setValue(Integer.toString(mWiredHeadsetRingtoneFocusValue));
        mWiredHeadsetRingtoneFocus.setSummary(mWiredHeadsetRingtoneFocus.getEntry());
        mWiredHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        mDashboardPortraitColumns = (CustomSeekBarPreference) findPreference(DASHBOARD_PORTRAIT_COLUMNS);
        int columnsPortrait = Settings.System.getInt(resolver,
                Settings.System.DASHBOARD_PORTRAIT_COLUMNS, DashboardSummary.mNumColumns);
        mDashboardPortraitColumns.setValue(columnsPortrait / 1);
        mDashboardPortraitColumns.setOnPreferenceChangeListener(this);

        mDashboardLandscapeColumns = (CustomSeekBarPreference) findPreference(DASHBOARD_LANDSCAPE_COLUMNS);
        int columnsLandscape = Settings.System.getInt(resolver,
                Settings.System.DASHBOARD_LANDSCAPE_COLUMNS, 2);
        mDashboardLandscapeColumns.setValue(columnsLandscape / 1);
        mDashboardLandscapeColumns.setOnPreferenceChangeListener(this);

        PreferenceCategory incallVibCategory = (PreferenceCategory) findPreference(INCALL_VIB_OPTIONS);
        if (!TelephonyUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(incallVibCategory);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mWiredHeadsetRingtoneFocus) {
            int mWiredHeadsetRingtoneFocusValue = Integer.valueOf((String) newValue);
            int index = mWiredHeadsetRingtoneFocus.findIndexOfValue((String) newValue);
            mWiredHeadsetRingtoneFocus.setSummary(
                    mWiredHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(resolver, Settings.Global.WIRED_RINGTONE_FOCUS_MODE,
                    mWiredHeadsetRingtoneFocusValue);
            return true;
        }else if (preference == mDashboardPortraitColumns) {
            int columnsPortrait = (Integer) newValue;
            Settings.System.putInt(resolver, Settings.System.DASHBOARD_PORTRAIT_COLUMNS, columnsPortrait * 1);
            return true;
        }else if (preference == mDashboardLandscapeColumns) {
            int columnsLandscape = (Integer) newValue;
            Settings.System.putInt(resolver, Settings.System.DASHBOARD_LANDSCAPE_COLUMNS, columnsLandscape * 1);
            return true;
        }
        return false;
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.VENOM;
    }
	
}