
import java.util.ArrayList;
import java.util.List;
import javax.imageio.plugins.jpeg.JPEGQTable;


public class TXCtoGTFS {
    // lists of TXC
    public static List<AnnotatedStopPointRef> stopPoints;
    public static List<RouteSection> routeSections;
    public static List<Route> routestxc;
    public static List<JourneyPatternSection> journeyPatternSections;
    public static List<Service> services;
    public static List<VehicleJourney> vehicleJourneys;

    // list of stops from xls
    public static List<BusStop> busStops;

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
        importXML();
        importCSV();

        // convert to csv in GTFS
        agencyCreation();
        stopsCreation();
        routesCreation();
        tripsCreation();
        stop_timesCreation();
        calendarCreation();

        // something something export .txt
        exportGTFS();
    }

    public static void initLists(){
        stopPoints = new ArrayList<AnnotatedStopPointRef>();
        routeSections = new ArrayList<RouteSection>();
        routestxc = new ArrayList<Route>();
        journeyPatternSections = new ArrayList<JourneyPatternSection>();
        services = new ArrayList<Service>();
        vehicleJourneys = new ArrayList<VehicleJourney>();

        busStops = new ArrayList<BusStop>();

        agencyList = new ArrayList<agency>();
        stopsList = new ArrayList<stops>();
        routesList = new ArrayList<routes>();
        tripsList = new ArrayList<trips>();
        stop_timesList = new ArrayList<stop_times>();
        calendarList = new ArrayList<calendar>();

        errors = new ArrayList<String>();
    }

    public static void importXML(){

        try { 
            EasyReader reader = new EasyReader();
            //System.out.println("Enter a file name (excluding extension) to convert");
            System.out.println("Enter a bus service number");
            String filename = reader.readLine();
            if(filename.isBlank() || filename.isEmpty() || filename.equals("")){
                filename = "Ser 16";
            }
            filename = "Services/Ser " + filename;

            reader = new EasyReader(filename + ".xml");
             while(!reader.eof()){
                String line = reader.readLine().trim();
                List<String> rows = new ArrayList<String>();
                if(line.equals("<StopPoints>")){
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</StopPoints>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    //System.out.println("processing stops");
                    processStopPoints(rows);
                    //System.out.println("processing complete");
                    //System.out.println(stopPoints.size());
                }

                if(line.equals("<RouteSections>")){
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</RouteSections>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    processRouteSections(rows);
                }
               //System.out.println(line);

                if(line.equals("<Routes>")){
                    //System.out.println("routes");
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</Routes>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    processRoutes(rows);
                    //System.out.println(routestxc);
                }

                if(line.equals("<JourneyPatternSections>")){
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</JourneyPatternSections>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    processJourneyPatternSection(rows);
                }

                if(line.equals("<Services>")){
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</Services>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    processServices(rows);
                //System.out.println("services done");
                }

                if(line.equals("<VehicleJourneys>")){
                    line = reader.readLine().trim();
                    while(!reader.eof() && !line.equals("</VehicleJourneys>")){
                        rows.add(line);
                        line = reader.readLine().trim();
                    }
                    //System.out.println("vjs read");
                    processVehicleJourneys(rows);
                    //System.out.println("finished vj import");
                }

                if(line.equals("</TransXChange>")){
                    break;
                }

            //System.out.println(line);
             }
        } catch (Exception e) {
            System.out.println("failed importing xml: " + e.getMessage());
        }


    }

    public static String stripTags(String taggedString){
        return taggedString.substring(taggedString.indexOf('>') + 1, taggedString.lastIndexOf('<'));
    }
    public static String getTagValue(String taggedString){
        return taggedString.substring(taggedString.indexOf('<') + 1, taggedString.indexOf('>'));
    }

    public static void processStopPoints(List<String> lines){
        for(int i = 2; i < lines.size(); i += 4){
            //System.out.println(i);
            //System.out.println(stripTags(lines.get(i)) + " | " + stripTags(lines.get(i-1)));
            stopPoints.add(new AnnotatedStopPointRef(stripTags(lines.get(i-1)), stripTags(lines.get(i))));
            while(!lines.get(i+1).contains("</AnnotatedStopPointRef>")){
                i++;
            }
        }

            //System.out.println("stops complete");
    }

    public static void processRouteSections(List<String> lines){
        int i = 0;
        while(i < lines.size()){
            System.out.println(lines.get(i));
            if(getTagValue(lines.get(i)).startsWith("RouteSection")){
                RouteSection rs = new RouteSection();
                String l = lines.get(i);
                rs.id = l.substring(l.indexOf('\"')+1, l.lastIndexOf('\"'));
                rs.routeLinks = new ArrayList<RouteLink>();
                while(!getTagValue(lines.get(i)).equals("/RouteSection")){
                    if(getTagValue(lines.get(i)).startsWith("RouteLink")){
                        RouteLink rl = new RouteLink();
                        l = lines.get(i);
                        rl.id = l.substring(l.indexOf('\"')+1, l.lastIndexOf('\"'));
                        while(!getTagValue(lines.get(i)).equals("/RouteLink")){
                            switch(getTagValue(lines.get(i))){
                                case "From":
                                    rl.from = stripTags(lines.get(i+1));
                                    i+=2;
                                    break;
                                case "To":
                                    rl.to = stripTags(lines.get(i+1));
                                    i+=2;
                                    break;
                                case "Distance":
                                    rl.distance = stripTags(lines.get(i));
                                    break;
                                case "Direction":
                                    rl.direction = stripTags(lines.get(i));
                                    break;
                            }
                            i++;
                        }
                    }
                    i++;
                }
            }
            i++;
        }
    }

    public static void processRoutes(List<String> lines){
        for(int i = 0; i < lines.size(); i += 4){
            String idLine = lines.get(i);
            String id = idLine.substring(idLine.indexOf('\"') + 1, idLine.lastIndexOf('\"'));
            routestxc.add(new Route(id, stripTags(lines.get(i+1)), stripTags(lines.get(i+2))));
        }
        //System.out.println(routestxc.size());
    }

    public static void processJourneyPatternSection(List<String> lines){
        int i = 0;
        //System.out.println(lines);
        while(i < lines.size()){
            // do stuff
            if(getTagValue(lines.get(i)).startsWith("JourneyPatternSection ")){
                //System.out.println("in jps");
                JourneyPatternSection jps = new JourneyPatternSection();
                String idLine = lines.get(i);
                jps.id = idLine.substring(idLine.indexOf('\"') + 1, idLine.lastIndexOf('\"'));
                // get jptls - loop until line is </jps>
                while(!lines.get(i).equals("</JourneyPatternSection>")){
                    //System.out.println("in loop");
                    if(getTagValue(lines.get(i)).startsWith("JourneyPatternTimingLink ")){
                        JourneyPatternTimingLink jptl = new JourneyPatternTimingLink();
                        String tlIdLine = lines.get(i);
                        jptl.id = tlIdLine.substring(tlIdLine.indexOf('\"') + 1, tlIdLine.lastIndexOf('\"'));
                        while(!lines.get(i).equals("</JourneyPatternTimingLink>")){
                            //System.out.println("jptl");
                            switch(getTagValue(lines.get(i))){
                                case "RouteLinkRef":
                                    jptl.routeLinkRef = stripTags(lines.get(i));
                                    break;
                                case "RunTime":
                                    jptl.runTime = stripTags(lines.get(i));
                                    break;
                                default:
                                    if(getTagValue(lines.get(i)).startsWith("From")){
                                        jptl.from = new JPSToFrom();
                                        String[] splitTag = lines.get(i).split(" ");
                                        jptl.from.id = splitTag[1].substring(splitTag[1].indexOf('\"') + 1, splitTag[1].lastIndexOf('\"'));
                                        jptl.from.sequenceNumber = splitTag[2].substring(splitTag[2].indexOf('\"') + 1, splitTag[2].lastIndexOf('\"'));
                                        while(!lines.get(i).equals("</From>")){
                                            switch(getTagValue(lines.get(i))){
                                                case "StopPointRef":
                                                    jptl.from.stopPointRef = stripTags(lines.get(i));
                                                    break;
                                                case "TimingStatus":
                                                    jptl.from.timingStatus = stripTags(lines.get(i));
                                                    break;
                                                case "WaitTime":
                                                    jptl.waitTime = stripTags(lines.get(i));
                                            }
                                            i++;
                                        }
                                    }
                                    else if(getTagValue(lines.get(i)).startsWith("To")){
                                        jptl.to = new JPSToFrom();
                                        String[] splitTag = lines.get(i).split(" ");
                                        jptl.to.id = splitTag[1].substring(splitTag[1].indexOf('\"') + 1, splitTag[1].lastIndexOf('\"'));
                                        jptl.to.sequenceNumber = splitTag[2].substring(splitTag[2].indexOf('\"') + 1, splitTag[2].lastIndexOf('\"'));
                                        while(!lines.get(i).equals("</To>")){
                                            switch(getTagValue(lines.get(i))){
                                                case "StopPointRef":
                                                    jptl.to.stopPointRef = stripTags(lines.get(i));
                                                    break;
                                                case "TimingStatus":
                                                    jptl.to.timingStatus = stripTags(lines.get(i));
                                                    break;
                                            }
                                            i++;
                                        }
                                    }
                                    break;
                            }
                            i++;
                        }
                        //System.out.println("add jptl");
                        jps.journeyPatternTimingLinks.add(jptl);
                    }
                    i++;
                }
                //System.out.println("jps added");
                journeyPatternSections.add(jps);
            }
            i++;
        }
    }

    public static void processServices(List<String> lines){
        int i = 0;
        while (i < lines.size()){
            Service s = new Service();
            while(!lines.get(i).equals("</Service>")){
                //System.out.println("line: " + lines.get(i));
                switch(getTagValue(lines.get(i))){
                    case "ServiceCode":
                        s.serviceCode = stripTags(lines.get(i));
                        break;
                    case "PrivateCode":
                        s.privateCode = stripTags(lines.get(i));
                        break;
                    case "Lines":
                        Line l = new Line();
                        String nextL = lines.get(i+1);
                        if(getTagValue(nextL).equals("Line")){
                            l.id = nextL.substring(nextL.indexOf('\"') + 1, nextL.lastIndexOf('\"'));
                            String doubleNextL = lines.get(i+2);
                            if(getTagValue(doubleNextL).equals("LineName")){
                                l.lineName = stripTags(doubleNextL);
                                s.line = l;
                            }
                        }
                        while(!getTagValue(lines.get(i+1)).equals("/Lines")){
                            i++;
                        }
                        break;
                    case "OperatingPeriod":
                        s.startDate = stripTags(lines.get(i+1));
                        while(!getTagValue(lines.get(i+1)).equals("/OperatingPeriod")){
                            i++;
                        }
                        break;
                    case "OperatingProfile":
                        if(getTagValue(lines.get(i+1)).equals("RegularDayType") && getTagValue(lines.get(i+2)).equals("DaysOfWeek")){
                            
                            s.operatingDays = getTagValue(lines.get(i+3));
                        }
                        while(!getTagValue(lines.get(i+1)).equals("/OperatingProfile")){
                            i++;
                        }
                        break;
                    case "StandardService":
                        StandardService ss = new StandardService();
                        ss.journeyPatterns = new ArrayList<JourneyPattern>();
                        while(!getTagValue(lines.get(i)).equals("/StandardService")) {
                            switch(getTagValue(lines.get(i))){
                                case "Origin":
                                    ss.origin = stripTags(lines.get(i));
                                    break;
                                case "Destination":
                                    ss.destination = stripTags(lines.get(i));
                                    break;
                                case "Vias":
                                    // handle vias
                                    ss.vias = new ArrayList<String>();
                                    i++;
                                    while(!getTagValue(lines.get(i)).equals("/Vias")){
                                        ss.vias.add(stripTags(lines.get(i)));
                                        i++;
                                    }
                                    break;
                                default:
                                    if(getTagValue(lines.get(i)).startsWith("JourneyPattern")){
                                        JourneyPattern jp = new JourneyPattern();
                                        String line2 = lines.get(i);
                                        //System.out.println("jp: " + line2.substring(line2.indexOf('\"') + 1, line2.lastIndexOf('\"')));
                                        jp.id = line2.substring(line2.indexOf('\"') + 1, line2.lastIndexOf('\"'));
                                        //System.out.println(jp.id);
                                        i++;
                                        // deal with journey patterns
                                        while(!getTagValue(lines.get(i)).equals("/JourneyPattern")){
                                            //System.out.println("here also");
                                            
                                            switch(getTagValue(lines.get(i))){
                                                case "Direction":
                                                    jp.direction = stripTags(lines.get(i));
                                                    break;
                                                case "Description":
                                                    jp.description = stripTags(lines.get(i));
                                                    break;
                                                case "RouteRef":
                                                    jp.routeRef = stripTags(lines.get(i));
                                                    //System.out.println("route ref: " + jp.routeRef + ", jp id: " + jp.id);
                                                    break;
                                                case "JourneyPatternSectionRefs":
                                                    jp.journeyPatternSectionRefs = stripTags(lines.get(i));
                                                    break;
                                            }

                                            //System.out.println(jp.routeRef);
                                            i++;
                                            //System.out.println("here" + lines.get(i));
                                        }
                                        ss.journeyPatterns.add(jp);
                                    }
                            }
                            i++;
                        }
                        s.standardService = ss;
                        break;
                    case "Direction":
                        s.direction = stripTags(lines.get(i));
                        break;
                }
                i++;
            }
            services.add(s);
            i++;
        }

    }

    public static void processVehicleJourneys(List<String> lines){
        //System.out.println(lines.size());
        int i = 0;
        while(i<lines.size()){
            if(getTagValue(lines.get(i)).contains("VehicleJourney ")) {
                VehicleJourney vj = new VehicleJourney();
                String l = lines.get(i);
                vj.sequenceNumber = l.substring(l.indexOf('\"')+1,l.lastIndexOf('\"'));
                //System.out.println("journey " + vj.sequenceNumber + ":");
                while(!lines.get(i).equals("</VehicleJourney>")){
                    switch(getTagValue(lines.get(i))) {
                        case "PrivateCode":
                            vj.privateCode = stripTags(lines.get(i));
                            break;
                        case "OperatingProfile":
                            vj.operatingProfile = new OperatingProfile();
                            i++;
                            while(!getTagValue(lines.get(i+1)).equals("/OperatingProfile")){
                                if(getTagValue(lines.get(i)).equals("RegularDayType")){
                                    if(getTagValue(lines.get(i+1)).equals("DaysOfWeek")){
                                        i+=2;
                                        vj.operatingProfile.regularDayOps = "";
                                        while(!getTagValue(lines.get(i)).equals("/DaysOfWeek")){
                                            vj.operatingProfile.regularDayOps += getTagValue(lines.get(i));
                                            i++;
                                        }
                                    }
                                    else if(getTagValue(lines.get(i+1)).equals("HolidaysOnly /")){
                                        vj.operatingProfile.regularDayOps = "";
                                    }
                                }
                                if(getTagValue(lines.get(i)).equals("SpecialDaysOperation")){
                                    vj.operatingProfile.specialDayOps = new ArrayList<DateRange>();
                                    vj.operatingProfile.specialDayNonOps = new ArrayList<DateRange>();
                                    while(!getTagValue(lines.get(i)).equals("/SpecialDaysOperation")){
                                        if(getTagValue(lines.get(i)).equals("DaysOfOperation")){
                                            while(!getTagValue(lines.get(i)).equals("/DaysOfOperation") && !getTagValue(lines.get(i)).equals("/SpecialDaysOperation")){
                                                if(getTagValue(lines.get(i)).equals("DateRange")){
                                                    DateRange dr = new DateRange();
                                                    dr.start = stripTags(lines.get(i+1));
                                                    dr.end = stripTags(lines.get(i+2));
                                                    vj.operatingProfile.specialDayOps.add(dr);
                                                    //System.out.println("special op date: " + dr.start + dr.end);
                                                }
                                                i++;
                                            }
                                        }
                                        if(getTagValue(lines.get(i)).equals("DaysOfNonOperation")){
                                            while(!getTagValue(lines.get(i)).equals("/DaysOfNonOperation") && !getTagValue(lines.get(i)).equals("/SpecialDaysOperation")){
                                                if(getTagValue(lines.get(i)).equals("DateRange")){
                                                    DateRange dr = new DateRange();
                                                    dr.start = stripTags(lines.get(i+1));
                                                    dr.end = stripTags(lines.get(i+2));
                                                    vj.operatingProfile.specialDayNonOps.add(dr);
                                                    //System.out.println("special op date: " + dr.start + dr.end);
                                                }
                                                i++;
                                            }
                                        }
                                        i++;
                                    }
                                    

                                    
                                } 
                                if(getTagValue(lines.get(i)).equals("BankHolidayOperation")){
                                    if(getTagValue(lines.get(i+1)).equals("DaysOfNonOperation")){
                                        vj.operatingProfile.nonOpBankHolidays = getTagValue(lines.get(i+2));
                                    }
                                }  
                                i++;
                            }
                            break;
                        case "GarageRef":
                            vj.garageRef = stripTags(lines.get(i));
                            break;
                        case "VehicleJourneyCode":
                            vj.vehicleJourneyCode = stripTags(lines.get(i));
                            break;
                        case "ServiceRef":
                            vj.serviceRef = stripTags(lines.get(i));
                            break;
                        case "LineRef":
                            vj.lineRef = stripTags(lines.get(i));
                            break;
                        case "JourneyPatternRef":
                            vj.journeyPatternRef = stripTags(lines.get(i));
                            break;
                        case "DepartureTime":
                            vj.departureTime = stripTags(lines.get(i));
                            break;
                        default:
                            if(getTagValue(lines.get(i)).startsWith("VehicleJourneyTimingLink")){
                                VehicleJourneyTimingLink vjtl = new VehicleJourneyTimingLink();
                                String vjtll = lines.get(i);
                                //System.out.println(vjtll);
                                vjtl.id = vjtll.substring(vjtll.indexOf('\"')+1,vjtll.lastIndexOf('\"'));
                                i++;
                                while(!getTagValue(lines.get(i)).equals("/VehicleJourneyTimingLink")){
                                    switch(getTagValue(lines.get(i))){
                                        case "JourneyPatternTimingLinkRef":
                                            vjtl.journeyPatternTimingLinkRef = stripTags(lines.get(i));
                                            break;
                                        case "RunTime":
                                            vjtl.runTime = stripTags(lines.get(i));
                                            break;
                                        case "From":
                                            vjtl.runTime = stripTags(lines.get(i+1));
                                            i+=2;
                                            break;
                                    }
                                    i++;
                                }
                                
                                vj.vehicleJourneyTimingLinks.add(vjtl);
                                //System.out.println("vjtl: " + vjtl.id + " " + vjtl.journeyPatternTimingLinkRef + " " + vjtl.runTime);
                            }
                            break;
                    }
                    i++;
                }
                // process a journey

                vehicleJourneys.add(vj);
            }
            i++;
        }
        //System.out.println("finished vj");
    }

    public static void importCSV() {
        EasyReader reader = new EasyReader("locations.csv");
        try {
            while(!reader.eof()){
                String line = reader.readLine();
                if(!line.contains("NaPTAN")){
                    String[] splitline = line.split(",");
                    busStops.add(new BusStop(splitline[0], splitline[1], splitline[2], splitline[3]));
                }
            }
        } catch (Exception e) {
            errors.add("Error reading csv file");
        }
        
    }

    public static void agencyCreation() {
        agencyList.add(new agency("BV", "Bus Vannin", "https://bus.im", "Europe/Isle_of_Man", "en-GB", "+441624662525"));
    }

    public static void stopsCreation(){
        for(AnnotatedStopPointRef stop : stopPoints){
            String lat = "";
            String lon = "";
            String code = "";
            //System.out.println(stop.stopPointRef);
            for(BusStop locationStop : busStops){
                //System.out.println(locationStop.Naptan);
                if (stop.stopPointRef.equals(locationStop.Naptan)) {
                    lat = locationStop.Latitude;
                    lon = locationStop.Longitude;
                    code = locationStop.StopNo;
                    break;
                }
            }
            if(lat.equals("") || lon.equals("") || code.equals("")) {
                errors.add("Stop number " + stop.stopPointRef + " is missing in location data");
                System.out.println("lat " + lat + " lon " + lon + "code" + code);
            } else {
                stopsList.add(new stops(stop.stopPointRef, "\"" + stop.commonName + "\"", lat, lon, code, "0"));
            }

        }
    }

    public static void routesCreation(){
        //System.out.println("route creation");
        String shortname = "";
        try {
            shortname = services.get(0).serviceCode;
        } catch (Exception e) {
        }
        String longname = "";
        try {
            longname = services.get(0).standardService.destination;
        } catch (Exception e) {
        }
        for(Route route: routestxc) {
            //System.out.println(route.id + " | " + route.description);
            routesList.add(new routes(route.id, "BV", shortname, longname, "3"));
        }
    }

    public static void tripsCreation(){
        for(VehicleJourney vj: vehicleJourneys){
            String tid = vj.vehicleJourneyCode;
            // gets JPxx number
            String jpref = vj.journeyPatternRef;
            //System.out.println(jpref);
            String rid = "";
            //System.out.println(services.get(0).standardService.journeyPatterns.size());
            for(JourneyPattern jp: services.get(0).standardService.journeyPatterns){
                //System.out.println("loop - " + jp.id + " vs " + jpref + " equals " + jp.id.equals(jpref));
                if(jp.id.equals(jpref)){
                    rid = jp.routeRef;
                    break;
                }
            }
            if(rid.equals("")){
                errors.add("Missing route id in vehicle journey " + vj.vehicleJourneyCode);
            }
            tripsList.add(new trips(tid, rid, vj.serviceRef));
        }
    }

    public static void stop_timesCreation(){
        for(VehicleJourney vj : vehicleJourneys){
            //System.out.println(vj.vehicleJourneyCode);
            String tid = vj.vehicleJourneyCode;
            String depTime = vj.departureTime;
            if(depTime == null){
                errors.add("Missing departure time on vehicle journey " + vj.vehicleJourneyCode);
                System.out.println("Missing departure time on vehicle journey " + vj.vehicleJourneyCode);
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
                    break;
                }
            }
            //System.out.println(jpsref);
            if(jpsref.equals("")){
                errors.add("Missing journey pattern section ref in vehicle journey " + vj.vehicleJourneyCode);
                continue;
            }
            JourneyPatternSection jPS = null;
            //System.out.println(journeyPatternSections.size());
            for(JourneyPatternSection jps: journeyPatternSections) {
                //System.out.println("jpsref: " + jpsref + "jps.id: " + jps.id);
                if(jps.id.equals(jpsref)){
                    // this is the timing link, each one of these is one stop
                    jPS = jps;
                    break;
                }
            }
            //System.out.println(jPS.id);
            if(jPS == null){
                errors.add("missing journey pattern section in vehicle journey " + vj.vehicleJourneyCode);
                continue;
            }

            int arrHr = depHour, arrMn = depMins, arrSc = depSecs;
            //System.out.println(jPS.journeyPatternTimingLinks.size());
            stop_timesList.add(new stop_times(tid, jPS.journeyPatternTimingLinks.get(0).from.sequenceNumber, jPS.journeyPatternTimingLinks.get(0).from.stopPointRef, depTime, depTime));
            for(JourneyPatternTimingLink jptl: jPS.journeyPatternTimingLinks){
                //System.out.println("doing stuff with jps");
                String stopSeq = jptl.to.sequenceNumber;
                String stopId = jptl.to.stopPointRef;
                //System.out.println(jptl.runTime);
                if(jptl.runTime.substring(jptl.runTime.length()-1).equals("M")){
                    arrMn = arrMn + Integer.parseInt(jptl.runTime.substring(2,jptl.runTime.length()-1));
                    if(arrMn > 59) {
                        arrMn-= 60;
                        arrHr++;
                    }
                }
                String arrTime = String.format("%02d:%02d:%02d", arrHr, arrMn, arrSc);

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
            //System.out.println(vj.operatingProfile.regularDayOps);
            String[] regularOps = vj.operatingProfile.regularDayOps.split(" /");
            //System.out.println(regularOps);

            for(String op: regularOps){
                switch(op){
                    case "MondayToSunday":
                        for (int i = 0; i < 7; i++) {
                            days[i] = "0";
                        }
                        break;
                    case "MondayToSaturday":
                        for (int i = 0; i < 6; i++) {
                        days[i] = "0";
                        }
                        break;  
                    case "NotSaturday":
                        days[6] = "0";
                        // fallthrough into mon to fri
                    case "MondayToFriday":
                        for (int i = 0; i < 5; i++) {
                            days[i] = "0";
                        }
                        break;
                    case "Weekend":
                        days[5] = "0";
                        days[6] = "0";
                        break;
                    case "Monday":
                        days[0] = "0";
                        break;
                    case "Tuesday":
                        days[1] = "0";
                        break;
                    case "Wednesday":
                        days[2] = "0";
                        break;
                    case "Thursday":
                        days[3] = "0";
                        break;
                    case "Friday":
                        days[4] = "0";
                        break;
                    case "Saturday":
                        days[5] = "0";
                        break;
                    case "Sunday":
                        days[6] = "0";
                        break;
                }
            }
            calendarList.add(new calendar(sid, days));
        }
    }

    public static void exportGTFS(){
        EasyWriter writer = new EasyWriter("output/agency.txt");
        writer.println(agency.getFormat());
        for(agency line: agencyList){
            writer.println(line.toString());
        }
        writer.close();


        writer = new EasyWriter("output/stops.txt");
        writer.println(stops.getFormat());
        //System.out.println(stopsList.size());
        for(stops line: stopsList){
            //System.out.println(line);
            writer.println(line.toString());
        }
        writer.close();

        writer = new EasyWriter("output/routes.txt");
        writer.println(routes.getFormat());
        for(routes line: routesList){
            writer.println(line.toString());
        }
        writer.close();

        writer = new EasyWriter("output/trips.txt");
        writer.println(trips.getFormat());
        for(trips line: tripsList){
            writer.println(line.toString());
        }
        writer.close();

        writer = new EasyWriter("output/stop_times.txt");
        writer.println(stop_times.getFormat());
        for(stop_times line: stop_timesList){
            writer.println(line.toString());
        }
        writer.close();

        writer = new EasyWriter("output/calendar.txt");
        writer.println(calendar.getFormat());
        for(calendar line: calendarList){
            writer.println(line.toString());
        }
        writer.close();
        

        //exportFile("agency", agencyList, agency.getFormat());
        //exportFile("stops", stopsList, stops.getFormat());
    }

    /*public static void exportFile(String filename, ArrayList<writable> listToExport, String header){
        EasyWriter writer = new EasyWriter(filename + ".txt");
        writer.println(header);
        for(writable line: listToExport){
            writer.println(line.toString());
        }
    }*/
}