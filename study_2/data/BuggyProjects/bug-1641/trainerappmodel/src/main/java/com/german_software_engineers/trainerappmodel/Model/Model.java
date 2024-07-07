package com.german_software_engineers.trainerappmodel.Model;


import com.german_software_engineers.trainerappmodel.Legacy.Schedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.german_software_engineers.trainerappmodel.Exceptions.ScheduleAvailableException;


public class Model {
    private String ApplicationVersion;
    private Map<String, Schedule> Schedules;
    private Schedule ActiveSchedule;

    public Model(){
        Schedules=new HashMap<>();
    }

    public void addSchedule(Schedule schedule) throws ScheduleAvailableException{
        if(Schedules.containsKey(schedule.getName()))
            throw new ScheduleAvailableException();
        Schedules.put(schedule.getName(),schedule);
    }

    public Schedule getSchedule(String name) throws ScheduleAvailableException{
        if(Schedules.containsKey(name)){
            return Schedules.get(name);
        }
        throw new ScheduleAvailableException();
    }

    public List<Schedule> getSchedulesList(){
        List<Schedule> returnValue = new ArrayList<>();
        returnValue.addAll(Schedules.values());
        return returnValue;
    }

    public void deleteSchedule(String name){
        Schedules.remove(name);
    }

    public void setApplicationVersion(String applicationVersion) {
        ApplicationVersion = applicationVersion;
    }

    public void setActiveSchedule(Schedule schedule){
        ActiveSchedule = schedule;
    }

    public Schedule activeSchedule(){
        return ActiveSchedule;
    }
}
