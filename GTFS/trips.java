public class trips implements writable{
    public String trip_id;
    public String route_id;
    public String service_id;

    public trips(String trip, String route, String service){
        trip_id = trip;
        route_id = route;
        service_id = service;
    }

    public String toString(){
        return trip_id + "," + route_id + "," + service_id;
    }

    public static String getFormat(){
        return "trip_id,route_id,service_id";
    }
}