package triage;

import utils.FileUtil;
import ambulance.Graph;
import ambulance.Dijkstra;

public class HospitalRegistry {

    private static final String FILE_PATH = "Data/hospitals.txt"; 
    private static final int    MAX       = 20;

    private Hospital[] hospitals;
    private int        hospitalCount;
    private Graph      graph;

    public HospitalRegistry(Graph graph) {
        this.graph         = graph;
        this.hospitals     = new Hospital[MAX];
        this.hospitalCount = 0;
        loadFromFile();
    }

    public RoutingResult findBestHospital(String patientLocation, int severityLevel) {
        // Basic Check: Agar hospitals load hi nahi huye
        if (hospitalCount == 0) {
            return new RoutingResult(null, null, -1, "No hospitals found in the system.");
        }

        if (patientLocation == null || patientLocation.trim().isEmpty()) {
            return new RoutingResult(null, null, -1, "Please enter a valid location.");
        }

        String cleanPatientLoc = patientLocation.trim();
        Hospital bestHospital = null;
        String[] bestPath = null;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < hospitalCount; i++) {
            Hospital h = hospitals[i];
            if (h == null) continue;

            // Check if hospital can admit for this severity FIRST
            if (h.canAdmit(severityLevel)) {
                String hospitalLoc = h.getLocation().trim();
                String[] result = Dijkstra.findShortestPath(graph, cleanPatientLoc, hospitalLoc);

                if (result != null && result.length > 0) {
                    try {
                        int dist = Integer.parseInt(result[result.length - 1]);
                        if (dist < minDistance) {
                            minDistance = dist;
                            bestHospital = h;
                            bestPath = result;
                        }
                    } catch (Exception e) {
                        // Skip if distance parsing fails
                    }
                }
            }
        }

        if (bestHospital != null) {
            return new RoutingResult(bestHospital, bestPath, minDistance, null);
        }

        return new RoutingResult(null, null, -1, "RED ALERT: No hospitals available for this severity!");
    }

    public boolean admitPatient(Hospital hospital, Patient patient) {
        if (hospital == null || patient == null) return false;
        boolean success = hospital.admitPatient(patient.getSeverityLevel());
        if (success) {
            patient.setAssignedHospital(hospital.getName());
            patient.setAdmitted(true);
            saveToFile();
        }
        return success;
    }

    public void dischargePatient(Hospital hospital, int severityLevel) {
        if (hospital != null) {
            hospital.dischargePatient(severityLevel);
            saveToFile();
        }
    }

    private void loadFromFile() {
        hospitalCount = 0;
        if (!FileUtil.fileExists(FILE_PATH)) {
            loadDefaultHospitals();
            return;
        }
        String[] lines = FileUtil.readLines(FILE_PATH);
        if (lines == null || lines.length == 0) {
            loadDefaultHospitals();
            return;
        }
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            Hospital h = Hospital.fromFileString(line);
            if (h != null && hospitalCount < MAX) {
                hospitals[hospitalCount++] = h;
            }
        }
    }

    private void saveToFile() {
        if (hospitalCount == 0) return;
        String[] lines = new String[hospitalCount];
        for (int i = 0; i < hospitalCount; i++) {
            lines[i] = hospitals[i].toFileString();
        }
        FileUtil.writeAllLines(FILE_PATH, lines);
    }

    private void loadDefaultHospitals() {
        hospitalCount = 0;
        // Make sure names match your Graph nodes exactly!
        Hospital[] defaults = {
            new Hospital("Aga Khan Hospital", "Saddar", 20, 30, 50),
            new Hospital("Civil Hospital", "Cantt", 15, 25, 40),
            new Hospital("Liaquat Hospital", "Gulshan", 10, 20, 30)
        };
        for (Hospital h : defaults) {
            if (hospitalCount < MAX) hospitals[hospitalCount++] = h;
        }
        saveToFile();
    }

    public Hospital getHospitalByName(String name) {
        if (name == null) return null;
        for (int i = 0; i < hospitalCount; i++) {
            if (hospitals[i].getName().equalsIgnoreCase(name.trim())) return hospitals[i];
        }
        return null;
    }

    public Hospital getHospitalByLocation(String loc) {
        if (loc == null) return null;
        for (int i = 0; i < hospitalCount; i++) {
            if (hospitals[i].getLocation().equalsIgnoreCase(loc.trim())) return hospitals[i];
        }
        return null;
    }

    public Hospital[] getAllHospitals() {
        Hospital[] res = new Hospital[hospitalCount];
        System.arraycopy(hospitals, 0, res, 0, hospitalCount);
        return res;
    }
}