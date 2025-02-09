public class stops implements writable{
    public String stop_id;
    public String stop_name;
    public String stop_lat;
    public String stop_lon;
    public String stop_code;
    public String location_type;

    public stops(String id, String name, String lat, String lon, String code, String type){
        stop_id = id;
        stop_name = name;
        stop_lat = lat;
        stop_lon = lon;
        stop_code = code;
        location_type = type;
    }

    public String toString(){
        return stop_id + "," + stop_name + "," + stop_lat + "," + stop_lon + "," + stop_code + "," +location_type;
    }

    public static String getFormat(){
        return "stop_id,stop_name,stop_lat,stop_lon,stop_code,location_type";
    }
}