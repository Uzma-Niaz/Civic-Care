package triage;

public class Hospital {
    private String name, location;
    private int[] totalBeds;
    private int[] occupiedBeds;
    private double lat, lon; // Lat/Lon add kiye

    public Hospital(String name, String location, int c1, int c2, int c3, double lat, double lon) {
        this.name = name;
        this.location = location;
        this.totalBeds = new int[]{c1, c2, c3};
        this.occupiedBeds = new int[]{0, 0, 0};
        this.lat = lat;
        this.lon = lon;
    }

    public int getAvailableBeds(int sev) { 
        if (sev < 1 || sev > 3) return 0;
        return totalBeds[sev-1] - occupiedBeds[sev-1]; 
    }
 // Naya method: Check karne ke liye ki kya patient admit ho sakta hai
    public boolean canAdmit(int severity) {
        if (severity < 1 || severity > 3) return false;
        return (totalBeds[severity - 1] - occupiedBeds[severity - 1]) > 0;
    }

    // Naya method: Patient admit karne ke liye
    public void admitPatient(int severity) {
        if (canAdmit(severity)) {
            occupiedBeds[severity - 1]++;
        }
    }
    public String getName() { return name; }
    public String getLocation() { return location; }
}