package com.ls.directoryselectordemo;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ls.directoryselector.DirectoryDialog;
import com.ls.directoryselectordemo.utils.PermissionUtils;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;


public class MainActivity extends AppCompatActivity implements DirectoryDialog.Listener {

    private AppSettings settings;
    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btn_change_dir) {
                if (PermissionUtils.checkExternalStoragePermission(MainActivity.this)) {
                    showDirectoryDialog();
                } else {
                    PermissionUtils.requestExternalStoragePermission(MainActivity.this);
                }
            }
        }
    };

    private TextView txtDirLocation;
    private final SharedPreferences.OnSharedPreferenceChangeListener sharedPrefsChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            settings.load();
            fillViews();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = AppSettings.getSettings(this);

        initViews();
        fillViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingsActivity.startThisActivity(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        settings.load();
        fillViews();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(sharedPrefsChangeListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_READWRITE_STORAGE) {
            if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                showDirectoryDialog();
            } else {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showDirectoryDialog() {
        DialogFragment dialog = DirectoryDialog.newInstance(settings.getStorePath());
        dialog.show(getSupportFragmentManager(), "directoryDialog");
    }
    //endregion

    //region DirectoryDialog.Listener interface
    @Override
    public void onDirectorySelected(File dir) {
        settings.setStorePath(dir.getPath());
        settings.saveDeferred();
        fillViews();
    }

    @Override
    public void onCancelled() {
    }

    private void initViews() {
        txtDirLocation = findViewById(R.id.txt_dir_location);
        Button btnChangeDir = findViewById(R.id.btn_change_dir);
        btnChangeDir.setOnClickListener(clickListener);
    }

    private void fillViews() {
        txtDirLocation.setText(settings.getStorePath());
    }
}
