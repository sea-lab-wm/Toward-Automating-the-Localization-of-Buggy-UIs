package com.german_software_engineers.trainerapp.ExerciseView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.german_software_engineers.trainerapp.Controller.ApplicationManager;
import com.german_software_engineers.trainerapp.ExerciseView.Fragments.BodyWeightExerciseFragment;
import com.german_software_engineers.trainerapp.ExerciseView.Fragments.DeviceExerciseFragment;
import com.german_software_engineers.trainerapp.ExerciseView.Fragments.ExerciseFragment;
import com.german_software_engineers.trainerapp.ExerciseView.Fragments.WarmUpExerciseFragment;
import com.german_software_engineers.trainerapp.ExerciseView.ViewModel.ExerciseViewModel;
import com.german_software_engineers.trainerapp.ExerciseViewActivity;
import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Exceptions.ScheduleAvailableException;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;
import com.german_software_engineers.trainerappmodel.Legacy.Schedule;

public class EditExerciseActivity extends AppCompatActivity implements ExerciseFragment.OnFragmentInteractionListener {
    ExerciseViewModel ViewModel = null;
    String ScheduleName;

    BodyWeightExerciseFragment bodyWeightExerciseFragment = BodyWeightExerciseFragment.newInstance();
    DeviceExerciseFragment deviceExerciseFragment = DeviceExerciseFragment.newInstance();
    WarmUpExerciseFragment warmUpExerciseFragment = WarmUpExerciseFragment.newInstance();

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

        makeConnections();
    }

    private void getNessearyData(String scheduleName, String excName) {
        Schedule sched=null;
        ScheduleName = scheduleName;
        try {
            sched= ((ApplicationManager)getApplication()).getApplicationModel().getSchedule(scheduleName);
        } catch (ScheduleAvailableException e) {
            e.printStackTrace();
        }
        Exercise exercise=null;
        if(!excName.isEmpty()) {
            if (sched != null) {
                for (Exercise exc : sched.exercises()) {
                    if (exc.getName().equals(excName)) {
                        exercise = exc;
                    }
                }
            }
        }
        ViewModel=new ExerciseViewModel(sched,exercise);
        if(exercise!=null){
            ((EditText)findViewById(R.id.excName)).setText(exercise.getName());
//            updateGui();
        }else{
            ((EditText)findViewById(R.id.excName)).setText("");
        }
        ViewModel.getExerciseTypeLiveData().observe(this, observer -> {
            updateGui();
        });

        deviceExerciseFragment.setExerciseViewModel(ViewModel);
        bodyWeightExerciseFragment.setExerciseViewModel(ViewModel);
        warmUpExerciseFragment.setExerciseViewModel(ViewModel);
    }

    private void makeConnections(){
        ((Spinner)findViewById(R.id.ExerciseTypeSpinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ViewModel.typeChanged((int)id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((EditText)findViewById(R.id.excName)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                ViewModel.ExerciseName.postValue(((EditText)findViewById(R.id.excName)).getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ViewModel.ExerciseName.postValue(((EditText)findViewById(R.id.excName)).getText().toString());
            }
        });
    }

    private void updateGui(){
        switch (ViewModel.getExerciseType()){
            case WarmUp:
                getSupportFragmentManager().beginTransaction().replace(R.id.ExerciseFragment, warmUpExerciseFragment ).commit();
                break;
                default:
            case Device:
                getSupportFragmentManager().beginTransaction().replace(R.id.ExerciseFragment, deviceExerciseFragment ).commit();
                break;
            case BodyWeight:
                getSupportFragmentManager().beginTransaction().replace(R.id.ExerciseFragment, bodyWeightExerciseFragment ).commit();
                break;
        }
    }

    private void updateExc(){
        if(((EditText)findViewById(R.id.excName)).getText().toString().isEmpty()){
            ((EditText)findViewById(R.id.excName)).setError(getString(R.string.NoNameError));
            return;
        }
        ViewModel.addExercise();
        ((ApplicationManager)getApplication()).saveFile();
        finish();
    }

    @Override
    public void finish() {
        Intent intent = new Intent(this, ExerciseViewActivity.class);
        intent.putExtra("scheduleName",ScheduleName);
        setResult(RESULT_OK,intent);
        super.finish();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
