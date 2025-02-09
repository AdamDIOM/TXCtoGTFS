
import java.util.ArrayList;
import java.util.List;

public class TXCtoGTFS {
    // lists of TXC
    public static List<AnnotatedStopPointRef> stopPoints;
    public static List<RouteSection> routeSections;
    public static List<Route> routestxc;
    public static List<JourneyPatternSection> journeyPatternSections;
    public static List<Service> services;
    public static List<VehicleJourney> vehicleJourneys;

    // list of stops from xls
    public static List<BusStop> stops;

    // lists of GTFS
    public static List<agency> agencyList;
    public static List<stops> stopsList;
    public static List<routes> routesList;
    public static List<trips> tripsList;
    public static List<stop_times> stop_timesList;
    public static List<calendar> calendarList;

    // list of errors
    public static List<String> errors;

    public static void main(String[] args) {
        initLists();

        // something something import .xml

        // convert to csv in GTFS


        // something something export .txt
    }

    public static void initLists(){
        stopPoints = new ArrayList<AnnotatedStopPointRef>();
        routeSections = new ArrayList<RouteSection>();
        routestxc = new ArrayList<Route>();
        journeyPatternSections = new ArrayList<JourneyPatternSection>();
        services = new ArrayList<Service>();
        vehicleJourneys = new ArrayList<VehicleJourney>();

        stops = new ArrayList<BusStop>();

        agencyList = new ArrayList<agency>();
        stopsList = new ArrayList<stops>();
        routesList = new ArrayList<routes>();
        tripsList = new ArrayList<trips>();
        stop_timesList = new ArrayList<stop_times>();
        calendarList = new ArrayList<calendar>();

        errors = new ArrayList<String>();
    }

    public static void agencyCreation() {
        agencyList.add(new agency("BV", "Bus Vannin", "https://bus.im", "Europe/Isle_of_Man", "en-GB", "+441624662525"));
    }

    public static void stopsCreation(){
        for(AnnotatedStopPointRef stop : stopPoints){
            String lat = "";
            String lon = "";
            String code = "";
            for(BusStop locationStop : stops){
                if (stop.stopPointRef.equals(locationStop.Naptan)) {
                    lat = locationStop.Latitude;
                    lon = locationStop.Longitude;
                    code = locationStop.StopNo;
                    break;
                }
            }
            if(lat.equals("") || lon.equals("") || code.equals("")) {
                errors.add("Stop number " + stop.stopPointRef + " is missing in location data");
            } else {
                stopsList.add(new stops(stop.stopPointRef, stop.commonName, lat, lon, code, "0"));
            }

        }
    }

    public static void routesCreation(){
        String shortname = services.get(0).serviceCode;
        String longname = services.get(0).standardService.destination;
        for(Route route: routestxc) {
            routesList.add(new routes(route.id, "BV", shortname, longname, "3"));
        }
    }

    public static void tripsCreation(){
        for(VehicleJourney vj: vehicleJourneys){
            String tid = vj.vehicleJourneyCode;
            // gets JPxx number
            String jpref = vj.journeyPatternRef;
            String rid;
            for(JourneyPattern jp: services.get(0).standardService.journeyPatterns){
                if(jp.id.equals(jpref)){
                    rid = jp.routeRef;
                }
                break;
            }
            tripsList.add(new trips(tid, rid, vj.serviceRef));
        }
    }

    public static void stop_timesCreation(){
        for(VehicleJourney vj : vehicleJourneys){
            String tid = vj.vehicleJourneyCode;
            String depTime = vj.departureTime;
            if(depTime == null){
                errors.add("Missing departure time on vehicle journey " + vj.vehicleJourneyCode);
                continue;
            }

            int depHour = Integer.valueOf(depTime.substring(0, 2));
            int depMins = Integer.valueOf(depTime.substring(3, 5));
            int depSecs = Integer.valueOf(depTime.substring(6,8));

            String jpref = vj.journeyPatternRef;
            String jpsref = "";
            for(JourneyPattern jp: services.get(0).standardService.journeyPatterns){
                if(jp.id.equals(jpref)){
                    jpsref = jp.journeyPatternSectionRefs;
                }
                break;
            }
            if(jpsref.equals("")){
                errors.add("Missing journey pattern section ref in vehicle journey " + vj.vehicleJourneyCode);
                continue;
            }
            JourneyPatternSection jPS = null;
            for(JourneyPatternSection jps: journeyPatternSections) {
                if(jps.id.equals(jpsref)){
                    // this is the timing link, each one of these is one stop
                    jPS = jps;
                    break;
                }
            }
            if(jPS == null){
                errors.add("missing journey pattern section in vehicle journey " + vj.vehicleJourneyCode);
                continue;
            }

            for(JourneyPatternTimingLink jptl: jPS.journeyPatternTimingLinks){
                String stopSeq = jptl.to.sequenceNumber;
                String stopId = jptl.to.stopPointRef;
                int arrHr = depHour, arrMn = depMins, arrSc = depSecs;
                if(jptl.runTime.substring(3).equals("M")){
                    arrMn = arrMn + Integer.parseInt(jptl.runTime.substring(2,3));
                    if(arrMn > 59) {
                        arrMn-= 60;
                        arrHr++;
                    }
                }
                String arrTime = arrHr + ":" + arrMn + ":" + arrSc;

                stop_timesList.add(new stop_times(tid, stopSeq, stopId, arrTime, arrTime));

            }
        }
    }

    public static void calendarCreation(){
        for(VehicleJourney vj: vehicleJourneys){
            String sid = vj.serviceRef;
            String[] days = new String[7];
            for (int i = 0; i < 7; i++) {
                days[i] = "1";
            }
            if(vj.operatingProfile.regularDayOps.equals("MondayToSunday")){
                for (int i = 0; i < 7; i++) {
                    days[i] = "0";
                }
            }
            else if(vj.operatingProfile.regularDayOps.equals("MondayToFriday")){
                for (int i = 0; i < 5; i++) {
                    days[i] = "0";
                }
            }
            else if(vj.operatingProfile.regularDayOps.equals("Saturday")){
                days[5] = "0";
            }
            else if(vj.operatingProfile.regularDayOps.equals("Sunday")){
                days[6] = "0";
            }
            calendarList.add(new calendar(sid, days));
        }
    }
}