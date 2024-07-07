package com.german_software_engineers.trainerapp.Model;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Schedule {
    private String Name;
    private TrainingsTypes TrainingsType;
    private Intensities WarmUpIntensity;
    private int Repeations;
    private int PauseTime;
    private int Sets;
    private int Speed;
    private String WarmUpExcersize;
    private int WarmUpTime;
    private int BPM;
    private List<Exercise> Exercises;

    private Schedule(){}

    public Schedule(String name, TrainingsTypes trainingsType, int repeations, int pauseTime, int sets, int speed, String warmUpEx, int warmUpTime, Intensities intensity, int bpm){
        Name = name;
        WarmUpIntensity = intensity;
        TrainingsType = trainingsType;
        Repeations = repeations;
        PauseTime = pauseTime;
        Sets = sets;
        Speed = speed;
        WarmUpExcersize = warmUpEx;
        WarmUpTime = warmUpTime;
        BPM = bpm;
        Exercises = new ArrayList<>();
    }

    public String getName() {
        return Name;
    }

    public TrainingsTypes getTrainingsType() {
        return TrainingsType;
    }

    public Intensities getWarmUpIntensity() {
        return WarmUpIntensity;
    }

    public int getRepeations() {
        return Repeations;
    }

    public int getPauseTime() {
        return PauseTime;
    }

    public int getSets() {
        return Sets;
    }

    public int getSpeed() {
        return Speed;
    }

    public String getWarmUpExcersize() {
        return WarmUpExcersize;
    }

    public int getWarmUpTime() {
        return WarmUpTime;
    }

    public int getBPM() {
        return BPM;
    }

    public void addExercise(Exercise exercise){
        Exercises.add(exercise);
    }

    public List<Exercise> exercises(){
        return Exercises;
    }

    public String toGson(){
        JsonObject object = new JsonObject();
        object.addProperty("Name",Name);
        object.addProperty("TrainingsType",TrainingsType.name());
        object.addProperty("Intensity", WarmUpIntensity.name());
        object.addProperty("Repeations",Repeations);
        object.addProperty("PauseTime",PauseTime);
        object.addProperty("Sets",Sets);
        object.addProperty("Speed",Speed);
        object.addProperty("WarmUpExc",WarmUpExcersize);
        object.addProperty("WarmUpTime",WarmUpTime);
        object.addProperty("bpm",BPM);

        Gson gson = new Gson();
        object.addProperty("Exercises",gson.toJson(Exercises.toArray()));

        return object.toString();
    }

    public static Schedule fromGson(String ScheduleGson){
        JsonParser parser = new JsonParser();
        JsonElement parsedElement = parser.parse(ScheduleGson);
        JsonObject parsedObject=parsedElement.getAsJsonObject();

        Schedule returnValue = new Schedule();

        returnValue.Name = parsedObject.get("Name").getAsString();
        returnValue.TrainingsType = TrainingsTypes.valueOf(parsedObject.get("TrainingsType").getAsString());
        returnValue.WarmUpIntensity = Intensities.valueOf(parsedObject.get("Intensity").getAsString());
        returnValue.Repeations = parsedObject.get("Repeations").getAsInt();
        returnValue.PauseTime = parsedObject.get("PauseTime").getAsInt();
        returnValue.Sets = parsedObject.get("Sets").getAsInt();
        returnValue.Speed = parsedObject.get("Speed").getAsInt();
        returnValue.WarmUpExcersize = parsedObject.get("WarmUpExc").getAsString();

        while (returnValue.WarmUpExcersize.contains("\\")){
            returnValue.WarmUpExcersize=returnValue.WarmUpExcersize.replace("\\","");
        }

        while (returnValue.WarmUpExcersize.contains("\"")){
            returnValue.WarmUpExcersize=returnValue.WarmUpExcersize.replace("\"","");
        }


        returnValue.WarmUpTime = parsedObject.get("WarmUpTime").getAsInt();
        returnValue.BPM = parsedObject.get("bpm").getAsInt();

        String excString = parsedObject.get("Exercises").getAsString();
        Gson gson = new Gson();
        Exercise[] exercises = gson.fromJson(excString,Exercise[].class);
        returnValue.Exercises = new ArrayList<>();
        returnValue.Exercises.addAll(Arrays.asList(exercises));

        return returnValue;
    }
}