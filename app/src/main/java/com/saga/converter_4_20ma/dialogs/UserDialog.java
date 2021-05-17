package com.saga.converter_4_20ma.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.saga.converter_4_20ma.R;
import com.saga.converter_4_20ma.interfaces.Interfaces;

public class UserDialog {

    public final static int DIALOG_TYPE_ALERT = 0;
    public final static int DIALOG_TYPE_POSITIVE = 1;
    public final static int DIALOG_TYPE_NEGATIVE = 2;

    private Context context;

    private int dialogType = 0;
    private View imageView;
    private TextView positiveBtn;
    private TextView negativeBtn;

    private TextView titleDialog;
    private TextView textDialog;

    private AlertDialog.Builder helpBuilder;
    private AlertDialog dialog;

    private Interfaces.OnPositiveButtonDialog onPositiveListener = null;
    private Interfaces.OnNegativeButtonDialog onNegativListener = null;

    /**
     * @param
     * @return
     */
    public UserDialog(Context context) {

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.dialog_custom2, null);

        helpBuilder = new AlertDialog.Builder(this.context);
        helpBuilder.setCancelable(false);
        helpBuilder.setView(inflatedView);
        // Image
        imageView = (View) inflatedView.findViewById(R.id.view1);
        //
        titleDialog = (TextView) inflatedView.findViewById(R.id.dialog_title);
        titleDialog.setText("Alerta");
        //
        textDialog = (TextView) inflatedView.findViewById(R.id.dialog_message);
        positiveBtn = (TextView) inflatedView.findViewById(R.id.btn_ok);
        positiveBtn.setVisibility(View.INVISIBLE);
        negativeBtn = (TextView) inflatedView.findViewById(R.id.btn_cancel);
        negativeBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * @param
     * @return
     */
    public void show() {

        if (dialogType == 0) {
            imageView.setBackground(context.getResources().getDrawable(R.mipmap.icon_alert));
        } else if (dialogType == 1) {
            imageView.setBackground(context.getResources().getDrawable(R.mipmap.icon_check));
        } else if (dialogType == 2) {
            imageView.setBackground(context.getResources().getDrawable(R.mipmap.icon_uncheck));
        }
        //
        dialog = helpBuilder.create();
        //
        positiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPositiveListener != null) {
                    onPositiveListener.onPositiveListener(null);
                }
                dialog.dismiss();
            }
        });
        //
        negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onNegativListener != null) {
                    onNegativListener.onNegativeListener();
                }
                dialog.dismiss();
            }
        });
        //
        dialog.show();
    }

    /**
     * @param
     * @return
     */
    public void setOnPositiveButtonClickListener(Interfaces.OnPositiveButtonDialog listener) {
        this.onPositiveListener = listener;
    }

    /**
     * @param
     * @return
     */
    public void setOnNegativeButtonClickListener(Interfaces.OnNegativeButtonDialog listener) {
        this.onNegativListener = listener;
    }

    /**
     * @param
     * @return
     */
    public void setDialogType(int type) {
        dialogType = type;
    }

    /**
     * @param
     * @return
     */
    public void setTitleDialog(String msg) {
        titleDialog.setText(msg);
    }

    /**
     * @param
     * @return
     */
    public void setTextDialog(String msg) {
        textDialog.setText(msg);
    }

    /**
     * @param
     * @return
     */
    public void setLabelPositiveButton(String label) {
        positiveBtn.setText(label);
        positiveBtn.setVisibility(View.VISIBLE);
    }

    /**
     * @param
     * @return
     */
    public void setLabelNegativeButton(String label) {
        negativeBtn.setText(label);
        negativeBtn.setVisibility(View.VISIBLE);
    }

    /**
     * @param
     * @return
     */
    public void finish() {
        dialog.dismiss();
    }
}
