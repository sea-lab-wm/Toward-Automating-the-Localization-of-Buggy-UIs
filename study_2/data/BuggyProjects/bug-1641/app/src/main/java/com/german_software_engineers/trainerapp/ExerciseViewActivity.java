package com.german_software_engineers.trainerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.german_software_engineers.trainerapp.Controller.ApplicationManager;
import com.german_software_engineers.trainerapp.ExerciseView.EditExerciseActivity;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;
import com.german_software_engineers.trainerappmodel.Legacy.Schedule;
import com.german_software_engineers.trainerappmodel.Exceptions.ScheduleAvailableException;

public class ExerciseViewActivity extends ExerciseListActivity {
    ExcersizeListFragment fragment;
    Schedule ActiveSchedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addExcersize();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActiveSchedule = ((ApplicationManager)getApplication()).getApplicationModel().activeSchedule();

        setTitle(ActiveSchedule.getName());

        fragment = ExcersizeListFragment.newInstance(1, ActiveSchedule.getName());
        getSupportFragmentManager().beginTransaction().replace(R.id.execView, fragment).commit();
    }


    @Override
    protected void onStart() {
        super.onStart();
        TextView ScheduleInfo = (TextView) findViewById(R.id.ScheduleInfo);

        String scheduleInfo = String.format(getResources().getString(R.string.ScheduleInfo), ActiveSchedule.getRepetitions(), ActiveSchedule.getPauseTime(), ActiveSchedule.getSets(), ActiveSchedule.getSpeed());
        ScheduleInfo.setText(scheduleInfo);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("scheduleName", ActiveSchedule.getName());
    }

    public void addExcersize() {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra("scheduleName", ActiveSchedule.getName());
        intent.putExtra("excName", "");
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Exercise item) {
        Intent intent = new Intent(this, EditExerciseActivity.class);
        intent.putExtra("scheduleName", ActiveSchedule.getName());
        intent.putExtra("excName", item.getName());
        startActivity(intent);
    }
}
