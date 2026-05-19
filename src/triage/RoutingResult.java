package triage;

public class RoutingResult {
    public Hospital hospital;
    public String[] path;
    public int distance;
    public String errorMessage;

    public RoutingResult(Hospital h, String[] p, int d, String e) {
        this.hospital = h; this.path = p; this.distance = d; this.errorMessage = e;
    }
}