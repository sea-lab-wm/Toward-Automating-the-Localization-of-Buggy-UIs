package com.german_software_engineers.trainerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.TextView;

import com.german_software_engineers.trainerappmodel.Legacy.Schedule;

public class ScheduleEditDialog extends DialogFragment  {
    private Dialog dialog;
    private Schedule Schedule = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.schedule_edit_dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        updateSchedule();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        dialog = builder.create();
        //decorateDialogWithSchedule();
        return dialog;
    }

    public void setSchedule(Schedule schedule) {
        Schedule = schedule;
        //decorateDialogWithSchedule();
    }

    private void decorateDialogWithSchedule(){
        ((TextView)dialog.findViewById(R.id.repeEdit)).setText("Test");
        ((TextView)dialog.findViewById(R.id.pauseEdit2)).setText(Schedule.getPauseTime());
        ((TextView)dialog.findViewById(R.id.setEdit2)).setText(Schedule.getSets());
        ((TextView)dialog.findViewById(R.id.speedEdit2)).setText(Schedule.getSpeed());
    }

    private void updateSchedule(){

    }
}
