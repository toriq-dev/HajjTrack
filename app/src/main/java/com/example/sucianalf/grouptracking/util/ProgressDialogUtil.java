package com.example.sucianalf.grouptracking.util;

import android.app.Activity;
import android.app.ProgressDialog;


public class ProgressDialogUtil extends ProgressDialog {

    private Activity act;
    private int dialogType;
    private boolean cancelable;
    public ProgressDialogUtil(Activity act, int dialogType, boolean cancelable) {
        super(act);
        this.act=act;
        this.dialogType = dialogType;
        this.cancelable = cancelable;
    }

    @Override
    public void show() {
        setProgressStyle(dialogType);
        setMessage("Loading...");
        setCanceledOnTouchOutside(cancelable);
        super.show();
    }


    @Override
    public int getMax() {
        return super.getMax();
    }


    @Override
    public int getProgress() {
        return super.getProgress();
    }


    @Override
    public void dismiss() {
        super.dismiss();
    }


}
