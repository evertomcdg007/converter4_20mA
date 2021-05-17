package com.saga.converter_4_20ma.service;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.saga.converter_4_20ma.R;
import com.saga.converter_4_20ma.interfaces.Interfaces;

public class TaskProgressService {

    private Context context;

    private AlertDialog.Builder helpBuilder;

    private AlertDialog helpDialog;
    private TextView textDialog;

    private TextView okBtn;

    private Interfaces.OnPositiveButtonDialog onPositiveListener = null;

    /**
     * @param
     * @return
     */
    public TaskProgressService(Context context) {

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
        View inflatedView = inflater.inflate(R.layout.progress_dialog, null);

        helpBuilder = new AlertDialog.Builder(this.context);
        helpBuilder.setCancelable(false);
        helpBuilder.setView(inflatedView);
        //
        TextView title_dialog = (TextView) inflatedView.findViewById(R.id.dialog_title);
        title_dialog.setText("Aguarde");
        //
        textDialog = (TextView) inflatedView.findViewById(R.id.dialog_message);
        //
        okBtn = (TextView) inflatedView.findViewById(R.id.btn_ok);
        okBtn.setVisibility(View.INVISIBLE);
        //
        helpDialog = helpBuilder.create();
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
    public void show() {
        helpDialog.show();
    }

    /**
     * @param
     * @return
     */
    public void start() {

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPositiveListener != null) {
                    onPositiveListener.onPositiveListener(null);
                }
                helpDialog.dismiss();
            }
        });
    }

    /**
     * @param
     * @return
     */
    public void finish() {
        helpDialog.dismiss();
    }

    /**
     * @param
     * @return
     */
    public void setUserMessage(String str) {
        textDialog.setText(str);
    }

    /**
     * @param
     * @return
     */
    public void setLabelPositiveButton(String label) {
        okBtn.setText(label);
        okBtn.setVisibility(View.VISIBLE);
    }

    /**
     * @param
     * @return
     */
    public void cleanButtons() {
        // Button ok
        okBtn.setVisibility(View.INVISIBLE);
    }
}