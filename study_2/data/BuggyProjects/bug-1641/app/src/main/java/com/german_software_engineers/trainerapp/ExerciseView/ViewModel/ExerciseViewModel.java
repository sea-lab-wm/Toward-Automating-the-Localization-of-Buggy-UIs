package com.german_software_engineers.trainerapp.ExerciseView.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;
import com.german_software_engineers.trainerappmodel.Exercise.BodyWeightExercise;
import com.german_software_engineers.trainerappmodel.Exercise.DeviceExercise;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;
import com.german_software_engineers.trainerappmodel.Exercise.WarmUpExercise;
import com.german_software_engineers.trainerappmodel.Legacy.Schedule;

public class ExerciseViewModel extends ViewModel {
    private Exercise ActiveExcercise;
    private Schedule ActiveSchedule;
    private MutableLiveData<ExerciseType> ActiveExerciseType = new MutableLiveData<>();
    public MutableLiveData<String> ExerciseName = new MutableLiveData<>();

    private DeviceExerciseViewModel DeviceExerciseViewModel = null;
    private BodyWeightExerciseViewModel BodyWeightExerciseViewModel = null;
    private WarmUpExerciseViewModel WarmUpExerciseViewModel = null;

    public ExerciseViewModel(Schedule schedule, Exercise exercise){
        ActiveSchedule = schedule;
        ActiveExcercise = exercise;
        DeviceExerciseViewModel = new DeviceExerciseViewModel(exercise);
        BodyWeightExerciseViewModel = new BodyWeightExerciseViewModel(exercise);
        WarmUpExerciseViewModel = new WarmUpExerciseViewModel(exercise);
        setRequiredValues();
    }

    private void setRequiredValues(){
        if(ActiveExcercise==null){
            ExerciseName.postValue("");
        }else{
            ExerciseName.postValue(ActiveExcercise.getName());
        }
    }

    public void typeChanged(int chosenType){
        ActiveExerciseType.postValue(ExerciseType.values()[chosenType]);
    }

    public LiveData<ExerciseType> getExerciseTypeLiveData(){
        return ActiveExerciseType;
    }

    public ExerciseType getExerciseType(){
        return ActiveExerciseType.getValue();
    }

    public void addExercise(){
        Exercise exercise;
        switch (ActiveExerciseType.getValue()){
            case Device:
                default:
                    exercise = new DeviceExercise(ExerciseName.getValue());
                    ((DeviceExercise) exercise).setSeatActivated(DeviceExerciseViewModel.isSeatActivated());
                    ((DeviceExercise) exercise).setSeatPosition(DeviceExerciseViewModel.getSeatPosition());
                    ((DeviceExercise) exercise).setLegActivated(DeviceExerciseViewModel.isLegActivated());
                    ((DeviceExercise) exercise).setLegPosition(DeviceExerciseViewModel.getLegPosition());
                    ((DeviceExercise) exercise).setFootActivated(DeviceExerciseViewModel.isFootActivated());
                    ((DeviceExercise) exercise).setFootPosition(DeviceExerciseViewModel.getFootPosition());
                    ((DeviceExercise) exercise).setAngleActivated(DeviceExerciseViewModel.isAngleActivated());
                    ((DeviceExercise) exercise).setAnglePosition(DeviceExerciseViewModel.getAnglePosition());
                    ((DeviceExercise) exercise).setWeightActivated(DeviceExerciseViewModel.isWeightActivated());
                    ((DeviceExercise) exercise).setWeight(DeviceExerciseViewModel.getWeight());
                    ((DeviceExercise) exercise).setAdditionalWeightActivated(DeviceExerciseViewModel.isAdditionalWeightActivated());
                    ((DeviceExercise) exercise).setAdditionalWeight(DeviceExerciseViewModel.getAdditionalWeight());
                    ((DeviceExercise) exercise).setDeviceNumberActivated(DeviceExerciseViewModel.isDeviceActivated());
                    ((DeviceExercise) exercise).setDeviceNumber(DeviceExerciseViewModel.getDevicePosition());
                break;
            case BodyWeight:
                exercise = new BodyWeightExercise(ExerciseName.getValue());
                ((BodyWeightExercise) exercise).setAdditionalInformationActivated(BodyWeightExerciseViewModel.isAdditionalInformationActivated());
                ((BodyWeightExercise) exercise).setAdditionalInformation(BodyWeightExerciseViewModel.getAdditionalInformation());
                break;
            case WarmUp:
                exercise = new WarmUpExercise(ExerciseName.getValue());
                ((WarmUpExercise) exercise).setExecutionTimeActivated(WarmUpExerciseViewModel.isWarmUpTimeActivated());
                ((WarmUpExercise) exercise).setExecutionTime(WarmUpExerciseViewModel.getWamUpTime());
                ((WarmUpExercise) exercise).setIntensityActivated(WarmUpExerciseViewModel.isIntensityActivated());
                ((WarmUpExercise) exercise).setIntenity(WarmUpExerciseViewModel.getIntensity());
                ((WarmUpExercise) exercise).setSubintensityActivated(WarmUpExerciseViewModel.isSubIntensityActivated());
                ((WarmUpExercise) exercise).setSubIntensity(WarmUpExerciseViewModel.getSubIntensity());
                ((WarmUpExercise) exercise).setBPMActivated(WarmUpExerciseViewModel.isHeartfrequencyActivated());
                ((WarmUpExercise) exercise).setBPM(WarmUpExerciseViewModel.getHeartfrequency());
                break;
        }

        if(ActiveExcercise!=null)
            ActiveSchedule.exercises().remove(ActiveExcercise);

        ActiveSchedule.addExercise(exercise);
    }


    public DeviceExerciseViewModel getDeviceExerciseViewModel() {
        return DeviceExerciseViewModel;
    }

    public BodyWeightExerciseViewModel getBodyWeightExerciseViewModel(){
        return BodyWeightExerciseViewModel;
    }

    public WarmUpExerciseViewModel getWarmUpExerciseViewModel(){
        return WarmUpExerciseViewModel;
    }
}
