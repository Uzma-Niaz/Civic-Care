package triage;

public class Patient {
    private String name;
    private int severityLevel; // 1: Critical, 2: Moderate, 3: Stable
    private String location;
    private String assignedHospital = "Not Assigned"; // Default value set kar di hai
    private boolean admitted = false;

    public Patient(String name, int severity, String location) {
        this.name = name; 
        this.severityLevel = severity; 
        this.location = location;
    }

    // --- Getters ---
    public String getName() { return name; }
    
    public int getSeverityLevel() { return severityLevel; }
    
    public String getLocation() { return location; }
    
    // Ye method table mein data dikhane ke liye zaroori hai
    public String getAssignedHospital() { 
        return assignedHospital != null ? assignedHospital : "Not Assigned"; 
    }
    
    public boolean isAdmitted() { return admitted; }

    // --- Setters ---
    public void setAssignedHospital(String h) { this.assignedHospital = h; }
    
    public void setAdmitted(boolean b) { this.admitted = b; }
}