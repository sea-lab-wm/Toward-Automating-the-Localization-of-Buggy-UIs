package com.german_software_engineers.trainerapp.ExerciseView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.german_software_engineers.trainerapp.R;
import com.german_software_engineers.trainerappmodel.Exercise.BodyWeightExercise;
import com.german_software_engineers.trainerappmodel.Exercise.DeviceExercise;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;
import com.german_software_engineers.trainerappmodel.Exercise.WarmUpExercise;

import org.w3c.dom.Text;

/**
 * {@Link RecyclerView.ViewHolder} to Display the {@Link Exercise}
 * Decorates it self to Display the Exercises in the RecyclerView
 */
public class ExerciseViewHolder extends RecyclerView.ViewHolder {
    private View ExerciseView;
    private Exercise Exercise;

    /**
     * Constructor
     * @param view defined by the super-class
     */
    public ExerciseViewHolder(View view){
        super(view);
        ExerciseView = view;
    }

    /**
     * Sets the exercise and decorates the tile
     * @param exercise the given Exercise
     */
    public void setExercise(Exercise exercise){
        Exercise = exercise;
        decorateExerciseTile();
    }

    private void decorateExerciseTile(){
        switch(Exercise.type()){
            case Device:
                default:
                    decorateDeviceExercise();
                break;
            case WarmUp:
                decorateWarmUpExercise();
                break;
            case BodyWeight:
                decorateBodyWeightExercise();
                break;

        }
    }

    private void decorateDeviceExercise(){
        DeviceExercise exc = (DeviceExercise) Exercise;
//        ((TextView)ExerciseView.findViewById(R.id.ExerciseOrder)).setText(exc.getPosition());
        ((TextView)ExerciseView.findViewById(R.id.ExerciseTitle)).setText(exc.getName());


        StringBuilder builder = new StringBuilder();

        if(exc.isDeviceNumberActivated())
            builder.append(ExerciseView.getResources().getString(
                    R.string.DeviceExerciseInfoDeviceNumber, exc.getDeviceNumber()));
            //((TextView)ExerciseView.findViewById(R.id.ExerciseDeviceNumber)).setText(exc.getDeviceNumber());

        if(exc.isWeightActivated())
        {
            if(exc.isAdditionalWeightActivated()) {
                builder.append(ExerciseView.getResources().getString(
                        R.string.DeviceExerciseInfoWeight, exc.getWeight()));
                builder.append(ExerciseView.getResources().getString(
                        R.string.DeviceExerciseInfoAdditionalWeight, exc.getAdditionalWeight()));
            }else{
                builder.append(ExerciseView.getResources().getString(
                        R.string.DeviceExerciseInfoWeight, exc.getWeight()));
                builder.append(ExerciseView.getResources().getString(R.string.LineBreak));
            }
        }

        if(exc.isSeatActivated())
            builder.append(ExerciseView.getResources().getString(R.string.DeviceExerciseInfoSeatPosition,exc.getSeatPosition()));

        if(exc.isLegActivated())
            builder.append(ExerciseView.getResources().getString(R.string.DeviceExerciseInfoLegPosition,exc.getLegPosition()));

        if(exc.isFootActivated())
            builder.append(ExerciseView.getResources().getString(R.string.DeviceExerciseInfoFootPosition,exc.getFootPosition()));

        if(exc.isAngleActivated())
            builder.append(ExerciseView.getResources().getString(R.string.DeviceExerciseInfoAnglePosition, exc.getAnglePosition()));

        if(exc.isBackActivated())
            builder.append(ExerciseView.getResources().getString(R.string.DeviceExerciseInfoBackPosition, exc.getBackPosition()));

        ((TextView)ExerciseView.findViewById(R.id.ExerciseInformation)).setText(builder.toString());
    }

    private void decorateWarmUpExercise(){
        ExerciseView.findViewById(R.id.ExerciseCard).setBackgroundResource(R.color.colorPrimary);
        WarmUpExercise exc = (WarmUpExercise)Exercise;
//        ((TextView)ExerciseView.findViewById(R.id.ExerciseOrder)).setText(exc.getPosition());
        ((TextView)ExerciseView.findViewById(R.id.ExerciseTitle)).setText(exc.getName());
        ((TextView)ExerciseView.findViewById(R.id.ExerciseDeviceNumber)).setText("");

        StringBuilder builder = new StringBuilder();

        if(exc.isExecutionTimeActivated())
            builder.append(ExerciseView.getResources().getString(
                    R.string.WarmUpExerciseInfoExecutionTime,exc.getExecutionTime()));

        if(exc.isExecutionTimeActivated())
            builder.append(ExerciseView.getResources().getString(
                    R.string.WarmUpExerciseInfoIntensity,exc.getIntenity().name()));

        if(exc.isSubintensityActivated())
            builder.append(ExerciseView.getResources().getString(
                    R.string.WarmUpExerciseInfoSubIntensity,exc.getSubIntensity()));

        if(exc.isIntensityActivated())
            builder.append(ExerciseView.getResources().getString(
                    R.string.WarmUpExerciseInfoBPM, exc.getBPM()));

        ((TextView)ExerciseView.findViewById(R.id.ExerciseInformation)).setText(builder.toString());
    }

    private void decorateBodyWeightExercise(){
        BodyWeightExercise exc = (BodyWeightExercise)Exercise;
//        ((TextView)ExerciseView.findViewById(R.id.ExerciseOrder)).setText(exc.getPosition());
        ((TextView)ExerciseView.findViewById(R.id.ExerciseTitle)).setText(exc.getName());
        ((TextView)ExerciseView.findViewById(R.id.ExerciseDeviceNumber)).setText("");

        StringBuilder builder = new StringBuilder();

        if(exc.isAdditionalInformationActivated())
            builder.append(exc.getAdditionalInformation());

        ((TextView)ExerciseView.findViewById(R.id.ExerciseInformation)).setText(builder.toString());
    }
    /**
     * Gives the view of the Exercise
     * @return The view tile
     */
    public View getExerciseView(){
        return ExerciseView;
    }

    /**
     * Gives the Exercise to perform Operations on them.
     * @return The Exercise with Operation
     */
    public Exercise getExercise() {
        return Exercise;
    }
}
