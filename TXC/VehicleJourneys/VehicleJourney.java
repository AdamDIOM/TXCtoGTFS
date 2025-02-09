
import java.util.ArrayList;
import java.util.List;

public class VehicleJourney {
    public String sequenceNumber;
    public String privateCode;
    public OperatingProfile operatingProfile;
    public String garageRef;
    public String vehicleJourneyCode;
    public String serviceRef;
    public String lineRef;
    public String journeyPatternRef;
    public String departureTime;
    public List<VehicleJourneyTimingLink> vehicleJourneyTimingLinks = new ArrayList<VehicleJourneyTimingLink>();
}