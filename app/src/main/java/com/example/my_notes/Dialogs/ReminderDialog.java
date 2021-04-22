package com.example.my_notes.Dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.my_notes.R;
import com.google.android.material.textfield.TextInputEditText;

public class ReminderDialog extends DialogFragment {

    //Constantes
    private final String ACCEPT_BUTTON = "Accept";
    private final String CANCEL_BUTTON = "Cancel";

    //Variables
    private ImageButton add_alarm;
    private TextInputEditText title, description;
    private DatePickerDialog selected_date;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.reminder_form, null))
                .setPositiveButton(ACCEPT_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(CANCEL_BUTTON, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        Dialog root = builder.create();

        this.title = (TextInputEditText)root.findViewById(R.id.dialog_title);
        this.description = (TextInputEditText)root.findViewById(R.id.dialog_description);
        this.add_alarm = (ImageButton)root.findViewById(R.id.add_alarm_button);

        this.add_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }
}
