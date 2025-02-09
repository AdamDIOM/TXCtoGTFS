public class routes {
    public String route_id;
    public String agency_id;
    public String route_short_name;
    public String route_long_name;
    public String route_type;

    public routes(String rid, String aid, String short_name, String long_name, String type){
        route_id = rid;
        agency_id = aid;
        route_short_name = short_name;
        route_long_name = long_name;
        route_type = type;
    }

    public String toString(){
        return route_id + "," + agency_id + "," + route_short_name + "," + route_long_name + "," + route_type;
    }
}