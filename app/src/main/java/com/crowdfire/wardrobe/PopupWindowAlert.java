package com.crowdfire.wardrobe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class PopupWindowAlert implements OnClickListener {
    private OnAlertDialogButtonClickListener onAlertDialogButtonClick;

    private View view;

    private AlertDialog alertDialogCreater;

    private int popupId;


    public void setTitle(String title) {
        alertDialogCreater.setTitle(title);
    }

    public void showDialog() {
        if (!alertDialogCreater.isShowing())
            alertDialogCreater.show();
    }

    public void allowDismiss(boolean toBeAllowed) {
        alertDialogCreater.setCancelable(toBeAllowed);
    }

    public PopupWindowAlert(Context context, String title, View view, String positiveTitle, String neutralTitle,
                            String negativeTitle, OnAlertDialogButtonClickListener onAlertDialogButton, int popUpId) {
        onAlertDialogButtonClick = onAlertDialogButton;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = view;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(view);

        if (!title.equals("")) {
            builder.setTitle(title);
        }

        if (!positiveTitle.equals("")) {
            builder.setPositiveButton(positiveTitle, null);
        }
        if (!negativeTitle.equals("")) {
            builder.setNegativeButton(negativeTitle, null);
        }
        if (!neutralTitle.equals("")) {
            builder.setNeutralButton(neutralTitle, null);
        }

        alertDialogCreater = builder.create();
        alertDialogCreater.setCancelable(false);

        alertDialogCreater.show();

        this.popupId = popUpId;

        Button b1 = alertDialogCreater.getButton(DialogInterface.BUTTON_POSITIVE);
        b1.setId(DialogInterface.BUTTON_POSITIVE);
        b1.setOnClickListener(this);

        b1 = alertDialogCreater.getButton(DialogInterface.BUTTON_NEGATIVE);
        b1.setId(DialogInterface.BUTTON_NEGATIVE);
        b1.setOnClickListener(this);

        b1 = alertDialogCreater.getButton(DialogInterface.BUTTON_NEUTRAL);
        b1.setId(DialogInterface.BUTTON_NEUTRAL);
        b1.setOnClickListener(this);
    }


    public void setCancelable(boolean flag) {
        alertDialogCreater.setCancelable(flag);
    }

    public void dismiss() {
        alertDialogCreater.dismiss();
    }

    @Override
    public void onClick(View v) {
        onAlertDialogButtonClick.onButtonClick(alertDialogCreater, view, v.getId(), popupId);
    }

    public Button getButton(int buttonType) {
        return alertDialogCreater.getButton(buttonType);
    }

    public View getView() {
        return view;
    }

    public int getPopupId() {
        return popupId;
    }
}

interface OnAlertDialogButtonClickListener {

    public void onButtonClick(AlertDialog dialog, View view, int buttonId, int popupId);
}

