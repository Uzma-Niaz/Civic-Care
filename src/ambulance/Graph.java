package ambulance;

import utils.FileUtil;

class EdgeNode {
    String destination;
    int weight;
    EdgeNode next;
    EdgeNode(String destination, int weight) {
        this.destination = destination;
        this.weight = weight;
        this.next = null;
    }
}

class GraphEntry {
    String key;
    EdgeNode head;
    GraphEntry next;
    GraphEntry(String key) {
        this.key = key;
        this.head = null;
        this.next = null;
    }
}

class GraphHashMap {
    private GraphEntry[] buckets = new GraphEntry[100];
    private String[] keyList = new String[200];
    private int keyCount = 0;

    private int hash(String key) {
        int h = 0;
        for (int i = 0; i < key.length(); i++) h = (h * 31 + key.charAt(i)) % 50;
        return Math.abs(h);
    }

    void addVertex(String key) {
        int idx = hash(key);
        GraphEntry e = buckets[idx];
        while (e != null) { if (e.key.equals(key)) return; e = e.next; }
        GraphEntry newE = new GraphEntry(key);
        newE.next = buckets[idx];
        buckets[idx] = newE;
        keyList[keyCount++] = key;
    }

    EdgeNode getEdges(String key) {
        int idx = hash(key);
        GraphEntry e = buckets[idx];
        while (e != null) { if (e.key.equals(key)) return e.head; e = e.next; }
        return null;
    }

    void addEdge(String from, String to, int w) {
        int idx = hash(from);
        GraphEntry e = buckets[idx];
        while (e != null) {
            if (e.key.equals(from)) {
                EdgeNode n = new EdgeNode(to, w);
                n.next = e.head; e.head = n; return;
            }
            e = e.next;
        }
    }

    String[] getKeys() {
        String[] res = new String[keyCount];
        System.arraycopy(keyList, 0, res, 0, keyCount);
        return res;
    }
}

public class Graph {
    private GraphHashMap adjacencyList = new GraphHashMap();

    public void addLocation(String name) { adjacencyList.addVertex(name); }
    public void addRoad(String from, String to, int d) {
        adjacencyList.addEdge(from, to, d);
        adjacencyList.addEdge(to, from, d);
    }
    public EdgeNode getNeighbors(String loc) { return adjacencyList.getEdges(loc); }
    public String[] getAllLocations() { return adjacencyList.getKeys(); }

    // --- YE WALA METHOD UPDATE KIYA HAI ---
    public void loadFromFiles(String nodesFile, String disastersFile) {
        String[] lines = FileUtil.readLines(nodesFile);
        if (lines != null) {
            for (String line : lines) {
                String[] p = line.trim().split(",");
                if (p.length >= 3) {
                    String src = p[0].trim();
                    String dest = p[1].trim();
                    int weight = Integer.parseInt(p[2].trim());
                    
                    // Phele nodes add karein
                    addLocation(src);
                    addLocation(dest);
                    // Phir road add karein
                    addRoad(src, dest, weight);
                }
            }
        }
    }
        
    
    // Purana method bhi rakh letay hain back-up ke liye
    public void loadFromFile(String filePath) {
        loadFromFiles(filePath, "");
    }
}