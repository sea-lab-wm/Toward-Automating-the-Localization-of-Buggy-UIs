package com.german_software_engineers.trainerapp.ExerciseView.ViewModel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.german_software_engineers.trainerappmodel.Enumerations.ExerciseType;
import com.german_software_engineers.trainerappmodel.Exercise.DeviceExercise;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;

public class DeviceExerciseViewModel extends ViewModel {
    private Exercise ActiveExercise = null;

    private boolean IsSeatActivated = false;
    private int SeatPosition = 0;
    private boolean IsDeviceActivated = false;
    private int DevicePosition = 0;
    private boolean IsLegActivated = false;
    private int LegPosition = 0;
    private boolean IsFootActivated = false;
    private int FootPosition = 0;
    private boolean IsAngleActivated = false;
    private int AnglePosition = 0;
    private boolean IsBackActivated = false;
    private int BackPosition = 0;
    private boolean IsWeightActivated = false;
    private double Weight=0;
    private boolean IsAdditionalWeightActivated = false;
    private double AdditionalWeight=0;


    public DeviceExerciseViewModel(Exercise exercise) {
        ActiveExercise = exercise;
        applyData();
    }

    private void applyData(){
        if((ActiveExercise!=null)&&(ActiveExercise.type()== ExerciseType.Device)){
            IsSeatActivated = ((DeviceExercise)ActiveExercise).isSeatActivated();
            SeatPosition=((DeviceExercise) ActiveExercise).getSeatPosition();
            IsDeviceActivated = ((DeviceExercise) ActiveExercise).isDeviceNumberActivated();
            DevicePosition = ((DeviceExercise) ActiveExercise).getDeviceNumber();
            IsLegActivated = ((DeviceExercise) ActiveExercise).isLegActivated();
            LegPosition = ((DeviceExercise) ActiveExercise).getLegPosition();
            IsFootActivated = ((DeviceExercise) ActiveExercise).isFootActivated();
            FootPosition = ((DeviceExercise) ActiveExercise).getFootPosition();
            IsAngleActivated = ((DeviceExercise) ActiveExercise).isAngleActivated();
            AnglePosition = ((DeviceExercise) ActiveExercise).getAnglePosition();
            IsBackActivated = ((DeviceExercise) ActiveExercise).isBackActivated();
            BackPosition = ((DeviceExercise) ActiveExercise).getBackPosition();
            IsWeightActivated = ((DeviceExercise) ActiveExercise).isWeightActivated();
            Weight=((DeviceExercise) ActiveExercise).getWeight();
            IsAdditionalWeightActivated = ((DeviceExercise) ActiveExercise).isAdditionalWeightActivated();
            AdditionalWeight = ((DeviceExercise) ActiveExercise).getAdditionalWeight();
        }
    }

    public void setAdditionalWeightActivated(boolean activated){
        IsAdditionalWeightActivated = activated;
    }

    public boolean isAdditionalWeightActivated() {
        return IsAdditionalWeightActivated;
    }

    public void setWeightActivated(boolean activated){
        IsWeightActivated =activated;
    }

    public boolean isWeightActivated() {
        return IsWeightActivated;
    }

    public void setAngleActivated(boolean activated){
        IsAngleActivated = activated;
    }

    public boolean isAngleActivated() {
        return IsAngleActivated;
    }

    public void setFootActivated(boolean activated){
        IsFootActivated = activated;
    }

    public boolean isFootActivated() {
        return IsFootActivated;
    }

    public void setLegActivated(boolean activated){
        IsLegActivated = activated;
    }

    public boolean isLegActivated() {
        return IsLegActivated;
    }

    public void setDeviceActivated(boolean activated){
        IsDeviceActivated = activated;
    }

    public boolean isDeviceActivated() {
        return IsDeviceActivated;
    }

    public boolean isSeatActivated() {
        return IsSeatActivated;
    }

    public void setSeatActivated(boolean activated){
        IsSeatActivated = activated;
    }

    public boolean isBackActivated() {
        return IsBackActivated;
    }

    public void setBackActivated(boolean activated){
        IsBackActivated = activated;
    }

    public void setSeatPosition(int seatPosition) {
        SeatPosition = seatPosition;
    }

    public int getSeatPosition() {
        return SeatPosition;
    }

    public int getDevicePosition() {
        return DevicePosition;
    }

    public void setDevicePosition(int devicePosition) {
        DevicePosition = devicePosition;
    }

    public int getFootPosition() {
        return FootPosition;
    }

    public void setFootPosition(int footPosition) {
        FootPosition = footPosition;
    }

    public int getLegPosition() {
        return LegPosition;
    }

    public void setLegPosition(int legPosition) {
        LegPosition = legPosition;
    }

    public int getAnglePosition() {
        return AnglePosition;
    }

    public void setAnglePosition(int anglePosition) {
        AnglePosition = anglePosition;
    }

    public int getBackPosition() {
        return BackPosition;
    }

    public void setBackPosition(int backPosition) {
        BackPosition = backPosition;
    }

    public double getWeight() {
        return Weight;
    }

    public void setWeight(double weight) {
        Weight = weight;
    }

    public double getAdditionalWeight() {
        return AdditionalWeight;
    }

    public void setAdditionalWeight(double additionalWeight) {
        AdditionalWeight = additionalWeight;
    }
}
