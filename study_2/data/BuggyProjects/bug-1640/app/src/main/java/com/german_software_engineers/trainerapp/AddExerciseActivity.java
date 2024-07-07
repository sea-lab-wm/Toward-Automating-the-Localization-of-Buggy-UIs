package com.german_software_engineers.trainerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.german_software_engineers.trainerapp.Model.Exercise;
import com.german_software_engineers.trainerapp.Model.Schedule;
import com.german_software_engineers.trainerapp.Model.ScheduleAvailableException;

import java.io.FileOutputStream;

public class AddExerciseActivity extends AppCompatActivity implements ExcersizeListFragment.OnListFragmentInteractionListener {

    String ScheduleName;
    ExcersizeListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finished();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ScheduleName = intent.getStringExtra("scheduleName");
    }

    @Override
    protected void onStart()
    {
        fragment = ExcersizeListFragment.newInstance(1,ScheduleName);
        getSupportFragmentManager().beginTransaction().replace(R.id.excEdit, fragment ).commit();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_exercise_menu, menu);
        return true;
    }

    public void openExerciseDialog(){
        ExerciseDialog dialog = new ExerciseDialog();
        try {
            dialog.setSchedule(ApplicationHandler.getModel().getSchedule(ScheduleName));
        } catch (ScheduleAvailableException e) {
            e.printStackTrace();
        }
        dialog.show(getSupportFragmentManager(),"ExerciseDialog");
//
//        if(exc!=null) {
//            try {
//                ApplicationHandler.getModel().getSchedule(ScheduleName).addExercise(exc);
//            } catch (ScheduleAvailableException e) {
//                e.printStackTrace();
//            }
//        }else
//        {
//            Log.i("openExerciseDialog","Somethings Wrong");
//        }
//        try {
//            fragment.getAdatpoer().notifyItemRangeChanged(0,ApplicationHandler.getModel().getSchedule(ScheduleName).exercises().size());
//        } catch (ScheduleAvailableException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onListFragmentInteraction(Exercise item) {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra("scheduleName", ScheduleName);
        intent.putExtra("excName", item.getName());
        startActivity(intent);
    }

    public void finished(){
        try {
            FileOutputStream outputStream = openFileOutput(ApplicationHandler.FileName, MODE_PRIVATE);
            String  value = ApplicationHandler.getModel().getGson();
            outputStream.write(value.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(this, TrainingScheduleEditor.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_addExercise:
                openExerciseDialog();
                return true;
            case R.id.action_settings:
                //showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
