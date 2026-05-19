package triage;

public class Hospital {
    private String name, location;
    private int[] totalBeds;    // [0]=Critical, [1]=Moderate, [2]=Stable
    private int[] occupiedBeds;

    public Hospital(String name, String location, int crit, int mod, int stab) {
        this.name = name;
        this.location = location;
        this.totalBeds = new int[]{crit, mod, stab};
        this.occupiedBeds = new int[]{0, 0, 0};
    }

    // --- YE METHOD TRIAGE PANEL KE LIYE ZAROORI HAI ---
    public int getAvailableBeds() {
        int totalAvailable = 0;
        for (int i = 0; i < 3; i++) {
            totalAvailable += (totalBeds[i] - occupiedBeds[i]);
        }
        return totalAvailable;
    }

    public boolean canAdmit(int severity) {
        int idx = severity - 1;
        if (idx < 0 || idx >= 3) return false;
        return occupiedBeds[idx] < totalBeds[idx];
    }

    public boolean admitPatient(int severity) {
        if (canAdmit(severity)) {
            occupiedBeds[severity - 1]++;
            return true;
        }
        return false;
    }

    public void dischargePatient(int severity) {
        if (severity >= 1 && severity <= 3 && occupiedBeds[severity - 1] > 0) {
            occupiedBeds[severity - 1]--;
        }
    }

    public String getName() { return name; }
    
    public String getLocation() { return location; }
    
    public int getAvailableBeds(int sev) { 
        if (sev < 1 || sev > 3) return 0;
        return totalBeds[sev-1] - occupiedBeds[sev-1]; 
    }

    public String toFileString() {
        return String.format("%s,%s,%d,%d,%d,%d,%d,%d", name, location, 
            totalBeds[0], totalBeds[1], totalBeds[2], 
            occupiedBeds[0], occupiedBeds[1], occupiedBeds[2]);
    }

    public static Hospital fromFileString(String s) {
        try {
            String[] p = s.split(",");
            Hospital h = new Hospital(p[0], p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]), Integer.parseInt(p[4]));
            h.occupiedBeds = new int[]{Integer.parseInt(p[5]), Integer.parseInt(p[6]), Integer.parseInt(p[7])};
            return h;
        } catch (Exception e) { return null; }
    }
}