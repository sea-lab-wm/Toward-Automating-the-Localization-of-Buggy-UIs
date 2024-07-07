package com.german_software_engineers.trainerappmodel.Legacy;


import com.german_software_engineers.trainerappmodel.Enumerations.*;
import com.german_software_engineers.trainerappmodel.Exercise.Exercise;

import java.util.ArrayList;
import java.util.List;

public class Schedule {
    private String Name;
    private TrainingsTypes TrainingsType;
    private int Repetitions = Integer.MAX_VALUE;
    private int PauseTime = Integer.MAX_VALUE;
    private int Sets = Integer.MAX_VALUE;
    private int Speed = Integer.MAX_VALUE;
    private List<Exercise> Exercises;

    public Schedule(String name){
        Name = name;
        Exercises=new ArrayList<>();
    }

    public String getName() {
        return Name;
    }

    public void setTrainingsType(TrainingsTypes trainingsType){
        TrainingsType=trainingsType;
    }

    public TrainingsTypes getTrainingsType() {
        return TrainingsType;
    }

    public void setRepetitions(int repetitions){
        Repetitions = repetitions;
    }

    public int getRepetitions() {
        return Repetitions;
    }

    public void setPauseTime(int pauseTime){
        PauseTime=pauseTime;
    }

    public int getPauseTime() {
        return PauseTime;
    }

    public void setSets(int sets){
        Sets = sets;
    }

    public int getSets() {
        return Sets;
    }

    public void setSpeed(int speed){
        Speed=speed;
    }

    public int getSpeed() {
        return Speed;
    }

    public List<Exercise> exercises(){
        return Exercises;
    }

    public void addExercise(Exercise exercise){
        exercise.setPosition(Exercises.size());
        Exercises.add(exercise);
    }
}