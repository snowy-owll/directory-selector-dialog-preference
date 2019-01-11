package com.ls.directoryselectordemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.ls.directoryselector.DirectoryPreference;
import com.ls.directoryselector.DirectoryPreferenceDialogFragmentCompat;
import com.ls.directoryselectordemo.utils.PermissionUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends AppCompatActivity {

    private final static String DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG";

    public static void startThisActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void startThisActivityForResult(AppCompatActivity activity, int requestCode) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.preferences);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        }
        initActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        private static final int REQUEST_READWRITE_STORAGE = 0;

        private String preferenceDialogKey;
        private AppSettings settings;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference storePathPrefs = findPreference("store_path");
            storePathPrefs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary((String) newValue);
                    return true;
                }
            });
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            settings = AppSettings.getSettings((AppCompatActivity) getActivity());

            Preference storePathPrefs = findPreference("store_path");
            storePathPrefs.setSummary(settings.getStorePath());
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
        }

        @Override
        public void onDisplayPreferenceDialog(Preference preference) {
            DialogFragment dialogFragment = null;
            if (preference instanceof DirectoryPreference) {
                if (PermissionUtils.checkExternalStoragePermission(getContext())) {
                    dialogFragment = newDirectoryPreferenceDialog(preference.getKey());
                } else {
                    preferenceDialogKey = preference.getKey();
                    PermissionUtils.requestExternalStoragePermission(this);
                    return;
                }
            }
            if (dialogFragment != null) {
                showPreferenceDialog(dialogFragment);
            } else {
                super.onDisplayPreferenceDialog(preference);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_READWRITE_STORAGE) {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(getContext(), getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                    if (preferenceDialogKey == null) {
                        return;
                    }
                    showPreferenceDialog(newDirectoryPreferenceDialog(preferenceDialogKey));
                    preferenceDialogKey = null;
                } else {
                    Toast.makeText(getContext(), getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
                }
            }
        }

        private final SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                settings.load();
            }
        };

        private void showPreferenceDialog(DialogFragment dialogFragment) {
            dialogFragment.setTargetFragment(this, 0);
            dialogFragment.show(this.getFragmentManager(), DIALOG_FRAGMENT_TAG);
        }

        private DialogFragment newDirectoryPreferenceDialog(String key) {
            return DirectoryPreferenceDialogFragmentCompat
                    .newInstance(key);
        }
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
