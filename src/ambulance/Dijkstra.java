package ambulance;



//=====================================================
//Stores shortest distance + previous node for path
//reconstruction
//=====================================================
class DijkstraResult {
 int distance;
 String previous;

 DijkstraResult(int distance, String previous) {
     this.distance = distance;
     this.previous = previous;
 }
}

//=====================================================
//Custom Distance Map: String -> DijkstraResult
//Built from scratch — no Java HashMap used
//=====================================================
class DistanceMap {
 private static final int SIZE = 100;
 private String[] keys;
 private DijkstraResult[] values;
 private int count;

 DistanceMap() {
     keys   = new String[SIZE];
     values = new DijkstraResult[SIZE];
     count  = 0;
 }

 private int indexOf(String key) {
     for (int i = 0; i < count; i++) {
         if (keys[i].equals(key)) return i;
     }
     return -1;
 }

 void put(String key, DijkstraResult result) {
     int idx = indexOf(key);
     if (idx >= 0) {
         values[idx] = result;
     } else {
         keys[count]   = key;
         values[count] = result;
         count++;
     }
 }

 DijkstraResult get(String key) {
     int idx = indexOf(key);
     return idx >= 0 ? values[idx] : null;
 }

 int getCount()          { return count; }
 String getKey(int i)    { return keys[i]; }
}

//=====================================================
//Min-Heap (Priority Queue) — built from scratch
//Extracts the node with the smallest distance first
//=====================================================
class MinHeap {

 static class HeapNode {
     String location;
     int distance;

     HeapNode(String location, int distance) {
         this.location = location;
         this.distance = distance;
     }
 }

 private HeapNode[] heap;
 private int size;
 private int capacity;

 MinHeap(int capacity) {
     this.capacity = capacity;
     heap = new HeapNode[capacity];
     size = 0;
 }

 private int parent(int i)     { return (i - 1) / 2; }
 private int leftChild(int i)  { return 2 * i + 1; }
 private int rightChild(int i) { return 2 * i + 2; }

 private void swap(int i, int j) {
     HeapNode temp = heap[i];
     heap[i] = heap[j];
     heap[j] = temp;
 }

 void insert(String location, int distance) {
     if (size == capacity) return;
     heap[size] = new HeapNode(location, distance);
     int i = size;
     size++;
     // Bubble up
     while (i > 0 && heap[parent(i)].distance > heap[i].distance) {
         swap(i, parent(i));
         i = parent(i);
     }
 }

 HeapNode extractMin() {
     if (size == 0) return null;
     HeapNode min = heap[0];
     heap[0] = heap[size - 1];
     size--;
     heapifyDown(0);
     return min;
 }

 private void heapifyDown(int i) {
     int smallest = i;
     int left     = leftChild(i);
     int right    = rightChild(i);
     if (left < size && heap[left].distance < heap[smallest].distance)
         smallest = left;
     if (right < size && heap[right].distance < heap[smallest].distance)
         smallest = right;
     if (smallest != i) {
         swap(i, smallest);
         heapifyDown(smallest);
     }
 }

 boolean isEmpty() { return size == 0; }
}

//=====================================================
//Dijkstra's Algorithm — fully from scratch
//=====================================================
public class Dijkstra {

 private static final int INFINITY = Integer.MAX_VALUE / 2;

 public static String[] findShortestPath(Graph graph, String source, String destination) {
     String[] locations = graph.getAllLocations();
     int n = locations.length;
     if (n == 0) return null;

     DistanceMap distMap = new DistanceMap();
     for (int i = 0; i < n; i++) {
         distMap.put(locations[i], new DijkstraResult(INFINITY, null));
     }
     distMap.put(source, new DijkstraResult(0, null));

     boolean[] visited = new boolean[n];
     MinHeap minHeap = new MinHeap(n * 2);
     minHeap.insert(source, 0);

     while (!minHeap.isEmpty()) {
         MinHeap.HeapNode current = minHeap.extractMin();
         String currentLoc = current.location;

         int currentIdx = getIndex(locations, currentLoc);
         if (currentIdx >= 0 && visited[currentIdx]) continue;
         if (currentIdx >= 0) visited[currentIdx] = true;

         if (currentLoc.equals(destination)) break;

         DijkstraResult currentResult = distMap.get(currentLoc);
         if (currentResult == null) continue;

         EdgeNode neighbor = graph.getNeighbors(currentLoc);
         while (neighbor != null) {
             String neighborLoc = neighbor.destination;
             int newDist = currentResult.distance + neighbor.weight;

             DijkstraResult neighborResult = distMap.get(neighborLoc);
             if (neighborResult != null && newDist < neighborResult.distance) {
                 distMap.put(neighborLoc, new DijkstraResult(newDist, currentLoc));
                 minHeap.insert(neighborLoc, newDist);
             }
             neighbor = neighbor.next;
         }
     }

     DijkstraResult destResult = distMap.get(destination);
     if (destResult == null || destResult.distance == INFINITY) return null;

     String[] stack = new String[n + 1];
     int stackTop = 0;
     String curr = destination;
     while (curr != null) {
         stack[stackTop++] = curr;
         DijkstraResult r = distMap.get(curr);
         curr = (r != null) ? r.previous : null;
     }

     String[] path = new String[stackTop + 1];
     for (int i = 0; i < stackTop; i++) {
         path[i] = stack[stackTop - 1 - i];
     }
     path[stackTop] = String.valueOf(destResult.distance);

     return path;
 }

 private static int getIndex(String[] locations, String name) {
     for (int i = 0; i < locations.length; i++) {
         if (locations[i] != null && locations[i].equals(name)) return i;
     }
     return -1;
 }
}