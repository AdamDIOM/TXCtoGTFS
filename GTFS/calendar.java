public class calendar {
    public String service_id;
    public String monday;
    public String tuesday;
    public String wednesday;
    public String thursday;
    public String friday;
    public String saturday;
    public String sunday;

    public calendar(String sid, String[] days){
        service_id = sid;
        monday = days[0];
        tuesday = days[1];
        wednesday = days[2];
        thursday = days[3];
        friday = days[4];
        saturday = days[5];
        sunday = days[6];
    }

    public String toString(){
        return service_id + "," + monday + "," + tuesday + "," + wednesday + "," + thursday + "," + friday + "," + saturday + "," + sunday;
    }
}