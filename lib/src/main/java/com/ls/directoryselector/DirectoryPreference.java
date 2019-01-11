package com.ls.directoryselector;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.preference.DialogPreference;

public class DirectoryPreference extends DialogPreference {

    private String path;
    private int dialogLayoutResId = R.layout.directory_chooser;

    public DirectoryPreference(Context context) {
        super(context);
        init(context);
    }

    public DirectoryPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DirectoryPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public DirectoryPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
        persistString(path);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(@Nullable Object defaultValue) {
        setPath(getPersistedString((String) defaultValue));
    }

    @Override
    public int getDialogLayoutResource() {
        return dialogLayoutResId;
    }

    private void init(Context context) {
        setPersistent(true);
        setDialogTitle(null);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
    }
}
