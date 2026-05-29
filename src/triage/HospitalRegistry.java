package triage;

import java.io.*;
import java.util.*;
import ambulance.Graph;
import ambulance.Dijkstra;

public class HospitalRegistry {
    private static final String FILE_PATH = "Data/hospitals.txt";
    private List<Hospital> hospitals = new ArrayList<>();
    private Graph graph;

    public HospitalRegistry(Graph graph) {
        this.graph = graph;
        loadFromFile();
    }

    private void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.err.println("Error: Configuration file not found at " + FILE_PATH);
            return;
        }
        
        hospitals.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length < 7) continue; 
                hospitals.add(new Hospital(data[0], data[1], 
                    Integer.parseInt(data[2].trim()), 
                    Integer.parseInt(data[3].trim()), 
                    Integer.parseInt(data[4].trim()), 
                    Double.parseDouble(data[5].trim()), 
                    Double.parseDouble(data[6].trim())));
            }
        } catch (Exception e) { 
            System.err.println("Error parsing hospital data: " + e.getMessage());
        }
    }

    public Hospital[] getAllHospitals() {
        return hospitals.toArray(new Hospital[0]);
    }

    public void admitPatient(Hospital h, Patient p) {
        if (h != null && p != null) {
            h.admitPatient(p.getSeverityLevel());
        } else {
            System.err.println("Error: Cannot admit patient - Hospital or Patient object is null.");
        }
    }

    public RoutingResult findBestHospital(String patientLocation, int severityLevel) {
        if (hospitals == null || hospitals.isEmpty()) {
            return new RoutingResult(null, null, -1, "No hospitals registered.");
        }
        
        Hospital bestHospital = null;
        String[] bestPath = null;
        int minDistance = Integer.MAX_VALUE;

        for (Hospital h : hospitals) {
            if (h != null && h.canAdmit(severityLevel)) {
                String[] result = Dijkstra.findShortestPath(graph, patientLocation, h.getLocation());
                if (result != null && result.length > 0) {
                    try {
                        int dist = Integer.parseInt(result[result.length - 1]);
                        if (dist < minDistance) {
                            minDistance = dist;
                            bestHospital = h;
                            bestPath = result;
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing distance for hospital: " + h.getName());
                    }
                }
            }
        }
        return (bestHospital != null) ? new RoutingResult(bestHospital, bestPath, minDistance, null) 
                                      : new RoutingResult(null, null, -1, "No route found for this severity.");
    }
}