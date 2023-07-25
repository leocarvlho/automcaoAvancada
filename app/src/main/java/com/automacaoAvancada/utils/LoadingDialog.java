package com.automacaoAvancada.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.automacaoAvancada.R;

public class LoadingDialog {

    private Context mContext;
    private Dialog mDialog;

    public LoadingDialog(Context context) {
        mContext = context;
        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.loading);
        mDialog.setCancelable(true);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams layoutParams = mDialog.getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        mDialog.getWindow().setAttributes(layoutParams);
    }

    public void setMessage(String message) {
        TextView textViewMessage = mDialog.findViewById(R.id.textViewMessage);
        if (textViewMessage != null) {
            textViewMessage.setText(message);
        }
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
