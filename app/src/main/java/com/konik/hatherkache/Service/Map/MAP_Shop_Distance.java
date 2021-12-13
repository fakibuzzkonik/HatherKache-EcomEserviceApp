package com.konik.hatherkache.Service.Map;

import java.util.Comparator;

public class MAP_Shop_Distance {

}

class Map_Distance_Model
{
    long destination_duration;long destination_distance;
    String user_uid, user_name;

    // Constructor
    public Map_Distance_Model(long destination_duration,long destination_distance, String user_uid, String user_name)
    {
        this.destination_duration = destination_duration;
        this.destination_distance = destination_distance;
        this.user_uid = user_uid;
        this.user_name = user_name;
    }

    // Used to print student details in main()
    public String toString()
    {
        return this.destination_duration + " " + this.user_uid +
                " " + this.destination_distance;
    }

    public long getDestination_duration() {
        return destination_duration;
    }

    public long getDestination_distance() {
        return destination_distance;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public String getUser_name() {
        return user_name;
    }
}

class SortbyDuration implements Comparator<Map_Distance_Model>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Map_Distance_Model a, Map_Distance_Model b)
    {
        return (int)(a.destination_duration - b.destination_duration);
    }
}
class SortbyDistance implements Comparator<Map_Distance_Model>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(Map_Distance_Model a, Map_Distance_Model b)
    {
        return (int)(a.destination_distance - b.destination_distance);
    }
}
