package com.german_software_engineers.trainerapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.german_software_engineers.trainerapp.Model.Exercise;
import com.german_software_engineers.trainerapp.Model.Schedule;
import com.german_software_engineers.trainerapp.Model.ScheduleAvailableException;

import java.io.FileOutputStream;

public class ExerciseViewActivity extends ExerciseListActivity  {

    String ScheduleName;
    ExcersizeListFragment fragment;
    Schedule ActiveSchedule;
    Dialog scheduleDialog;
    Dialog exceriseDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEditOfSchedule();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScheduleName = intent.getStringExtra("scheduleName");

        try {
            ActiveSchedule = ApplicationHandler.getModel().getSchedule(ScheduleName);
        } catch (ScheduleAvailableException e) {
            e.printStackTrace();
        }

        setTitle(ActiveSchedule.getName());

    }


    @Override
    protected void onStart()
    {
        fragment = ExcersizeListFragment.newInstance(1,ScheduleName);
        getSupportFragmentManager().beginTransaction().replace(R.id.execView, fragment ).commit();

        TextView ScheduleInfo = (TextView)findViewById(R.id.ScheduleInfo);
        ScheduleInfo.setText("Repeations: "+ActiveSchedule.getRepeations()+"\nPause: "+ActiveSchedule.getPauseTime()+"\nSets: "+ ActiveSchedule.getSets()+"\nSpeed: "+ActiveSchedule.getSpeed());

        TextView WarmUpInfo = (TextView)findViewById(R.id.WarmUpInfo);
        WarmUpInfo.setText("Exercise: "+ActiveSchedule.getWarmUpExcersize()+"\nTime: "+ActiveSchedule.getWarmUpTime()+"\nIntensity: "+ ActiveSchedule.getWarmUpIntensity().toString()+"\nBPM: "+ ActiveSchedule.getBPM());
        super.onStart();
    }


    public void startEditOfSchedule(){
        ApplicationHandler.getModel().deleteSchedule(ActiveSchedule.getName());
        try {
            FileOutputStream outputStream = openFileOutput(ApplicationHandler.FileName, MODE_PRIVATE);
            String  value = ApplicationHandler.getModel().getGson();
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
//        scheduleDialog = new Dialog(this);
//        scheduleDialog.setContentView(R.layout.schedule_edit_dialog);
//        scheduleDialog.setTitle("Edit Schedule");
//        ((TextView)scheduleDialog.findViewById(R.id.repeEdit)).setText(String.valueOf(ActiveSchedule.getRepeations()));
//        ((TextView)scheduleDialog.findViewById(R.id.pauseEdit2)).setText(String.valueOf(ActiveSchedule.getPauseTime()));
//        ((TextView)scheduleDialog.findViewById(R.id.setEdit2)).setText(String.valueOf(ActiveSchedule.getSets()));
//        ((TextView)scheduleDialog.findViewById(R.id.speedEdit2)).setText(String.valueOf(ActiveSchedule.getSpeed()));
//        ((TextView)scheduleDialog.findViewById(R.id.execEdit2)).setText(String.valueOf(ActiveSchedule.getWarmUpExcersize()));
//        ((TextView)scheduleDialog.findViewById(R.id.timeEdit2)).setText(String.valueOf(ActiveSchedule.getWarmUpTime()));
//        ((Spinner)scheduleDialog.findViewById(R.id.intenEdit2)).setSelection(ActiveSchedule.getWarmUpIntensity().ordinal());
//        ((TextView)scheduleDialog.findViewById(R.id.bpmEdit)).setText(String.valueOf(ActiveSchedule.getBPM()));
//        Button b = (Button) scheduleDialog.findViewById(R.id.OkButton);
//        b.setOnClickListener(new View.OnClickListener() // button 1 click
//        {
//            @Override
//            public void onClick(View v) {
//                updateSchedule();
//                scheduleDialog.dismiss();
//            }
//
//        });
//        Button b1 = (Button) scheduleDialog.findViewById(R.id.CancelButton); // button2 click
//        b1.setOnClickListener(new View.OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                scheduleDialog.cancel(); // dismiss scheduleDialog
//            }
//
//        });
//        scheduleDialog.show();
    }

    @Override
    public void onListFragmentInteraction(Exercise item) {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra("scheduleName", ActiveSchedule.getName());
        intent.putExtra("excName", item.getName());
        startActivity(intent);
    }

    public void updateSchedule(){

    }
}
