package com.german_software_engineers.trainerapp.ExerciseView;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Legacy.Schedule;

public class ExerciseDialog extends DialogFragment {
    Dialog dialog;
    Schedule Schedule = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.exersice_dialog)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        generateExcercise();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialog = builder.create();
        return dialog;
    }

    public void generateExcercise(){
//        String name = ((TextView)dialog.findViewById(R.id.exerciseName)).getText().toString();
//
//        boolean isSeatActivated = ((CheckBox)dialog.findViewById(R.id.seatCheckBox)).isChecked();
//        Integer seatPos = 0;
//        if(isSeatActivated)
//            seatPos = Integer.valueOf(((TextView)dialog.findViewById(R.id.SeatEdit)).getText().toString());
//
//        boolean isLegActivated = ((CheckBox)dialog.findViewById(R.id.LegCheckBox)).isChecked();
//        Integer LegPos = 0;
//        if(isLegActivated)
//            LegPos = Integer.valueOf(((TextView)dialog.findViewById(R.id.LegEdit)).getText().toString());
//
//        boolean isFootActivated = ((CheckBox)dialog.findViewById(R.id.FootCheckBox)).isChecked();
//        Integer footPos = 0;
//        if(isFootActivated)
//            footPos = Integer.valueOf(((TextView)dialog.findViewById(R.id.FootEdit)).getText().toString());
//
//        boolean isAngnleActivated = ((CheckBox)dialog.findViewById(R.id.AngleCheckBox)).isChecked();
//        Integer anglePos = 0;
//        if(isAngnleActivated)
//            anglePos = Integer.valueOf(((TextView)dialog.findViewById(R.id.AngleEdit)).getText().toString());
//
//        boolean isBackActivated = ((CheckBox)dialog.findViewById(R.id.BackCheckBox)).isChecked();
//        Integer backPos = 0;
//        if(isBackActivated)
//            backPos = Integer.valueOf(((TextView)dialog.findViewById(R.id.BackEdit)).getText().toString());
//
//        boolean isWeightActivated = ((CheckBox)dialog.findViewById(R.id.WeightCheckBox)).isChecked();
//        Double weight = 0.0;
//        if(isWeightActivated)
//            weight = Double.valueOf(((TextView)dialog.findViewById(R.id.WeightEdit)).getText().toString());
//
//        Exercise exc = new Exercise(name,isSeatActivated, isLegActivated,isFootActivated,isAngnleActivated,isWeightActivated, isBackActivated);
//        exc.setAnglePosition(anglePos.intValue());
//        exc.setFootPosition(footPos.intValue());
//        exc.setLegPosition(LegPos.intValue());
//        exc.setSeatPosition(seatPos.intValue());
//        exc.setWeight(weight.doubleValue());
//        exc.setBackPosition(backPos.intValue());
//
//        Schedule.addExercise(exc);
    }

    public void setSchedule(Schedule schedule){
        Schedule = schedule;
    }
}
