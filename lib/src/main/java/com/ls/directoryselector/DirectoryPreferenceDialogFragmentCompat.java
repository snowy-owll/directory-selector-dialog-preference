package com.ls.directoryselector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceDialogFragmentCompat;

public class DirectoryPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private static final String KEY_DIALOG_STATE = "dialogState";
    private static final String KEY_PATH = "path";

    private final DirectorySelector.Callback dirSelectorCallback = new DirectorySelector.Callback() {
        @Override
        public void onNewDirButtonClicked() {
            createNewFolderDialog(null);
        }
    };

    private final DirectorySelector dirChooser = new DirectorySelector(dirSelectorCallback) {
        @Override
        protected Context getContext() {
            return DirectoryPreferenceDialogFragmentCompat.this.getContext();
        }

        @Override
        protected File getInitialDirectory() {
            File ret = null;
            String value = getPersistedString();
            if (value != null) {
                File file = new File(value);
                if (file.exists() && file.isDirectory()) ret = file;
            }
            if (ret == null) ret = Environment.getExternalStorageDirectory();
            return ret;
        }
    };

    private AlertDialog dialog;

    public static DirectoryPreferenceDialogFragmentCompat newInstance(String key) {
        final DirectoryPreferenceDialogFragmentCompat
                fragment = new DirectoryPreferenceDialogFragmentCompat();
        final Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        dirChooser.initViews(view);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            DirectoryPreference preference = (DirectoryPreference) getPreference();
            if (preference != null) {
                String value = dirChooser.getSelectedDir().getPath();
                if (preference.callChangeListener(value)) {
                    preference.setPath(value);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            dirChooser.setSelectedDir(savedInstanceState.getString(KEY_PATH));
            Bundle bundle = savedInstanceState.getBundle(KEY_DIALOG_STATE);
            if (bundle != null) {
                createNewFolderDialog(bundle);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        File selectedDir = dirChooser.getSelectedDir();
        if (selectedDir == null) return;
        Bundle dialogState = dialog == null ? null : dialog.onSaveInstanceState();
        outState.putString(KEY_PATH, selectedDir.getPath());
        outState.putBundle(KEY_DIALOG_STATE, dialogState);
    }

    @Override
    public void onDestroyView() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        dirChooser.onPause();
        super.onDestroyView();
    }

    private void createNewFolderDialog(Bundle savedState) {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.edit_text_layout, null);
        final EditText input = view.findViewById(R.id.edit_value);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (dialog != null) {
                    Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setEnabled(!s.toString().trim().isEmpty());
                }
            }
        });

        AlertDialog.Builder ab = new AlertDialog.Builder(getContext())
                .setTitle(R.string.create_folder)
                .setMessage(R.string.create_folder_msg)
                .setView(view)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                String dirName = input.getText().toString().trim();
                                if (!dirName.isEmpty()) dirChooser.createFolder(dirName);
                            }
                        });

        dialog = ab.create();
        if (savedState != null) dialog.onRestoreInstanceState(savedState);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(!input.getText().toString().trim().isEmpty());
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        input.requestFocus();
    }

    private String getPersistedString() {
        String path = null;
        DirectoryPreference preference = (DirectoryPreference) getPreference();
        if (preference != null) {
            path = preference.getPath();
        }
        return path;
    }
}
