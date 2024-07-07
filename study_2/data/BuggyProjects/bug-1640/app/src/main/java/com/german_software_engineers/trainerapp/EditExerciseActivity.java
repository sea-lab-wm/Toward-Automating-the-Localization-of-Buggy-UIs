package com.german_software_engineers.trainerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.german_software_engineers.trainerapp.Model.Exercise;
import com.german_software_engineers.trainerapp.Model.Schedule;
import com.german_software_engineers.trainerapp.Model.ScheduleAvailableException;

import java.io.FileOutputStream;

public class EditExerciseActivity extends AppCompatActivity {
    Schedule ActiveSchedule=null;
    Exercise ActiveExcercse = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateExc();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        getNessearyData(intent.getStringExtra("scheduleName"),intent.getStringExtra("excName"));
    }

    private void getNessearyData(String scheduleName, String excName) {
        try {
            ActiveSchedule=ApplicationHandler.getModel().getSchedule(scheduleName);
        } catch (ScheduleAvailableException e) {
            e.printStackTrace();
        }
        if(ActiveSchedule!=null){
            for (Exercise exc:ActiveSchedule.exercises()) {
                if(exc.getName().equals(excName)){
                    ActiveExcercse=exc;
                    updateGui();
                    return;
                }
            }
        }
    }

    private void updateGui(){
        ((EditText)findViewById(R.id.excName)).setText(ActiveExcercse.getName());

        ((CheckBox)findViewById(R.id.seatCheckBox2)).setChecked(ActiveExcercse.isSeatActivated());
        if(ActiveExcercse.isSeatActivated())
            ((EditText)findViewById(R.id.SeatEdit2)).setText(String.valueOf(ActiveExcercse.getSeatPosition()));

        ((CheckBox)findViewById(R.id.LegCheckBox2)).setChecked(ActiveExcercse.isLegActivated());
        if(ActiveExcercse.isLegActivated())
            ((EditText)findViewById(R.id.LegEdit2)).setText(String.valueOf(ActiveExcercse.getLegPosition()));

        ((CheckBox)findViewById(R.id.FootCheckBox2)).setChecked(ActiveExcercse.isFootActivated());
        if(ActiveExcercse.isFootActivated())
            ((EditText)findViewById(R.id.FootEdit2)).setText(String.valueOf(ActiveExcercse.getFootPosition()));

        ((CheckBox)findViewById(R.id.AngleCheckBox2)).setChecked(ActiveExcercse.isAngleActivated());
        if(ActiveExcercse.isAngleActivated())
            ((EditText)findViewById(R.id.AngleEdit2)).setText(String.valueOf(ActiveExcercse.getAnglePosition()));

        ((CheckBox)findViewById(R.id.BackCheckBox2)).setChecked(ActiveExcercse.isBackActivated());
        if(ActiveExcercse.isBackActivated())
            ((EditText)findViewById(R.id.BackEdit2)).setText(String.valueOf(ActiveExcercse.getBackPosition()));

        ((CheckBox)findViewById(R.id.WeightCheckBox2)).setChecked(ActiveExcercse.isWeightActivated());
        if(ActiveExcercse.isWeightActivated())
            ((EditText)findViewById(R.id.WeightEdit2)).setText(String.valueOf(ActiveExcercse.getWeight()));
    }

    private void updateExc(){
        String name = ((EditText)findViewById(R.id.excName)).getText().toString();

        boolean isSeatActivated = ((CheckBox)findViewById(R.id.seatCheckBox2)).isChecked();
        Integer seatPos = 0;
        if(isSeatActivated)
            seatPos = Integer.valueOf(((EditText)findViewById(R.id.SeatEdit2)).getText().toString());

        boolean isLegActivated = ((CheckBox)findViewById(R.id.LegCheckBox2)).isChecked();
        Integer LegPos = 0;
        if(isLegActivated)
            LegPos = Integer.valueOf(((EditText)findViewById(R.id.LegEdit2)).getText().toString());

        boolean isFootActivated = ((CheckBox)findViewById(R.id.FootCheckBox2)).isChecked();
        Integer footPos = 0;
        if(isFootActivated)
            footPos = Integer.valueOf(((EditText)findViewById(R.id.FootEdit2)).getText().toString());

        boolean isAngnleActivated = ((CheckBox)findViewById(R.id.AngleCheckBox2)).isChecked();
        Integer anglePos = 0;
        if(isAngnleActivated)
            anglePos = Integer.valueOf(((EditText)findViewById(R.id.AngleEdit2)).getText().toString());

        boolean isBackActivated = ((CheckBox)findViewById(R.id.BackCheckBox2)).isChecked();
        Integer backPos = 0;
        if(isBackActivated)
            backPos = Integer.valueOf(((EditText)findViewById(R.id.BackEdit2)).getText().toString());

        boolean isWeightActivated = ((CheckBox)findViewById(R.id.WeightCheckBox2)).isChecked();
        Double weight = 0.0;
        if(isWeightActivated)
            weight = Double.valueOf(((EditText)findViewById(R.id.WeightEdit2)).getText().toString());

        Exercise exc = new Exercise(name,isSeatActivated, isLegActivated,isFootActivated,isAngnleActivated,isWeightActivated, isBackActivated);
        exc.setAnglePosition(anglePos.intValue());
        exc.setFootPosition(footPos.intValue());
        exc.setLegPosition(LegPos.intValue());
        exc.setSeatPosition(seatPos.intValue());
        exc.setWeight(weight.doubleValue());
        exc.setBackPosition(backPos.intValue());

        ActiveExcercse.copy(exc);

        try {
            FileOutputStream outputStream = openFileOutput(ApplicationHandler.FileName, MODE_PRIVATE);
            String  value = ApplicationHandler.getModel().getGson();
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        finish();
    }


}
