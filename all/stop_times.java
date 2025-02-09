public class stop_times implements writable{
    public String trip_id;
    public String stop_sequence;
    public String stop_id;
    public String arrival_time;
    public String departure_time;

    public stop_times(String tid, String seq, String sid, String arrTime, String depTime){
        trip_id = tid;
        stop_sequence = seq;
        stop_id = sid;
        arrival_time = arrTime;
        departure_time = depTime;
    }

    public String toString(){
        return trip_id + "," + stop_sequence + "," + stop_id + "," + arrival_time + "," + departure_time;
    }

    public static String getFormat(){
        return "trip_id,stop_sequence,stop_id,arrival_time,departure_time";
    }
}