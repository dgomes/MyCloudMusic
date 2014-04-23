package com.diogogomes.mycloudmusic.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by dgomes on 23/04/14.
 */

public class VerifyMeoCloud extends DialogFragment {
    public static VerifyMeoCloud newInstance() {
        VerifyMeoCloud v = new VerifyMeoCloud();
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_verify_dialog, null);

        verifyText = (EditText) view.findViewById(R.id.verify);

        alertDialogBuilder.setView(view);
        alertDialogBuilder.setTitle("Meo Cloud");
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "OK meo!", Toast.LENGTH_SHORT).show();
                VerifyDialogListener activity = (VerifyDialogListener) getActivity();
                activity.onFinishVerifyDialog(verifyText.getText().toString());
                dialog.dismiss();
            }
        });
        alertDialogBuilder.setNegativeButton("Esquece", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Fail miserably", Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });

        return alertDialogBuilder.create();
    }

    private EditText verifyText;

    public interface VerifyDialogListener {
        void onFinishVerifyDialog(String inputText);
    }

}
