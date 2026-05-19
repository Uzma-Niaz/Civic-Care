package triage;

public class TriageQueue {
    private Patient[] queue = new Patient[100];
    private int size = 0;

    public void addPatient(Patient p) { if (size < 100) queue[size++] = p; }
    public Patient[] getAll() {
        Patient[] res = new Patient[size];
        System.arraycopy(queue, 0, res, 0, size);
        return res;
    }
}