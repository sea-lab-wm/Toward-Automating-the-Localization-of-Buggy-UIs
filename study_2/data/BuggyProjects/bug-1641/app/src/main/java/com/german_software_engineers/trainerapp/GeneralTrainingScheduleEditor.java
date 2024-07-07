package com.german_software_engineers.trainerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.german_software_engineers.trainerapp.Controller.ApplicationManager;
import com.german_software_engineers.trainerappmodel.Legacy.Schedule;
import com.german_software_engineers.trainerappmodel.Exceptions.ScheduleAvailableException;

public class GeneralTrainingScheduleEditor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_training_schedule_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNextActivity();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void openNextActivity(){
        if(addScheduleToModel()) {
            ((ApplicationManager)getApplication()).saveFile();
            Intent intent = new Intent(this, ExerciseViewActivity.class);
            intent.putExtra("scheduleName", ((EditText) findViewById(R.id.nameTextEdit)).getText().toString());
            startActivity(intent);
        }
    }

    private boolean addScheduleToModel(){
        if(((EditText)findViewById(R.id.nameTextEdit)).getText().toString().isEmpty())
        {
            ((EditText)findViewById(R.id.nameTextEdit)).setError("Please type a name in");
            return false;
        }

        Schedule schedule = new Schedule(((EditText)findViewById(R.id.nameTextEdit)).getText().toString());

        Integer reps = 0;
        if(!((EditText)findViewById(R.id.repEdit)).getText().toString().isEmpty()) {
            reps = Integer.valueOf(((EditText) findViewById(R.id.repEdit)).getText().toString());
            schedule.setRepetitions(reps);
        }

        Integer pause = 0;
        if(!((EditText)findViewById(R.id.pauseEdit)).getText().toString().isEmpty()) {
            pause = Integer.valueOf((((EditText) findViewById(R.id.pauseEdit)).getText().toString()));
            schedule.setPauseTime(pause);
        }

        Integer sets = 0;
        if(!((EditText)findViewById(R.id.setEdit)).getText().toString().isEmpty()) {
            sets = Integer.valueOf(((EditText) findViewById(R.id.setEdit)).getText().toString());
            schedule.setSets(sets);
        }

        Integer speed = 0;
        if(!((EditText)findViewById(R.id.speedEdit)).getText().toString().isEmpty()) {
            speed = Integer.valueOf(((EditText) findViewById(R.id.speedEdit)).getText().toString());
            schedule.setSpeed(speed);
        }


       try {
           ((ApplicationManager)getApplication()).getApplicationModel().addSchedule(schedule);
       } catch (ScheduleAvailableException e) {
           e.printStackTrace();
       }
        return true;
    }
}
