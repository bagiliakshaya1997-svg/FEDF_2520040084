import java.util.*;

// ============================================================
//  WATERGRID – Smart Water Distribution & Monitoring System
//  DSA-2 Project | All 6 Modules
// ============================================================

public class WaterGrid {

    static void printHeader(String title) {
        System.out.println("\n" + "=".repeat(65));
        System.out.println("  " + title);
        System.out.println("=".repeat(65));
    }

    static void printSub(String s) {
        System.out.println("\n  -- " + s + " --");
    }

    // ===========================================================
    //  MODULE 1: Trees & Balanced Search Structures (CO1)
    // ===========================================================

    // ---- BST: Index pipelines by PipelineID ----
    static class BSTNode {
        int pipelineID;
        String location;
        double flowRate;
        BSTNode left, right;

        BSTNode(int id, String loc, double fr) {
            pipelineID = id; location = loc; flowRate = fr;
        }
    }

    static class PipelineBST {
        BSTNode root;

        BSTNode insert(BSTNode node, int id, String loc, double fr) {
            if (node == null) return new BSTNode(id, loc, fr);
            if (id < node.pipelineID)      node.left  = insert(node.left,  id, loc, fr);
            else if (id > node.pipelineID) node.right = insert(node.right, id, loc, fr);
            return node;
        }

        void insert(int id, String loc, double fr) { root = insert(root, id, loc, fr); }

        BSTNode search(BSTNode node, int id) {
            if (node == null || node.pipelineID == id) return node;
            return id < node.pipelineID ? search(node.left, id) : search(node.right, id);
        }

        void inorder(BSTNode node) {
            if (node == null) return;
            inorder(node.left);
            System.out.printf("    PipelineID: %-5d | Location: %-25s | Flow: %.1f L/s%n",
                              node.pipelineID, node.location, node.flowRate);
            inorder(node.right);
        }

        BSTNode minNode(BSTNode node) {
            while (node.left != null) node = node.left;
            return node;
        }

        BSTNode delete(BSTNode node, int id) {
            if (node == null) return null;
            if (id < node.pipelineID)      node.left  = delete(node.left,  id);
            else if (id > node.pipelineID) node.right = delete(node.right, id);
            else {
                if (node.left == null)  return node.right;
                if (node.right == null) return node.left;
                BSTNode succ = minNode(node.right);
                node.pipelineID = succ.pipelineID;
                node.location   = succ.location;
                node.flowRate   = succ.flowRate;
                node.right = delete(node.right, succ.pipelineID);
            }
            return node;
        }

        void delete(int id) { root = delete(root, id); }

        void searchAndPrint(int id) {
            BSTNode r = search(root, id);
            if (r != null) System.out.printf("    Found -> ID: %d | %s | %.1f L/s%n",
                                              r.pipelineID, r.location, r.flowRate);
            else System.out.println("    PipelineID " + id + " not found.");
        }
    }

    // ---- AVL Tree: Balanced water usage records ----
    static class AVLNode {
        int zoneID, height;
        double usage;
        AVLNode left, right;

        AVLNode(int z, double u) { zoneID = z; usage = u; height = 1; }
    }

    static class AVLTree {
        AVLNode root;

        int height(AVLNode n) { return n == null ? 0 : n.height; }
        int balance(AVLNode n) { return n == null ? 0 : height(n.left) - height(n.right); }

        AVLNode rotateRight(AVLNode y) {
            AVLNode x = y.left, T = x.right;
            x.right = y; y.left = T;
            y.height = 1 + Math.max(height(y.left), height(y.right));
            x.height = 1 + Math.max(height(x.left), height(x.right));
            return x;
        }

        AVLNode rotateLeft(AVLNode x) {
            AVLNode y = x.right, T = y.left;
            y.left = x; x.right = T;
            x.height = 1 + Math.max(height(x.left), height(x.right));
            y.height = 1 + Math.max(height(y.left), height(y.right));
            return y;
        }

        AVLNode insert(AVLNode node, int z, double u) {
            if (node == null) return new AVLNode(z, u);
            if (u < node.usage) node.left  = insert(node.left,  z, u);
            else                node.right = insert(node.right, z, u);
            node.height = 1 + Math.max(height(node.left), height(node.right));
            int b = balance(node);
            if (b > 1  && u < node.left.usage)  return rotateRight(node);
            if (b < -1 && u > node.right.usage) return rotateLeft(node);
            if (b > 1  && u > node.left.usage)  { node.left  = rotateLeft(node.left);  return rotateRight(node); }
            if (b < -1 && u < node.right.usage) { node.right = rotateRight(node.right); return rotateLeft(node); }
            return node;
        }

        void insert(int z, double u) { root = insert(root, z, u); }

        void inorder(AVLNode node) {
            if (node == null) return;
            inorder(node.left);
            System.out.printf("    ZoneID: %-4d | Usage: %.1f L%n", node.zoneID, node.usage);
            inorder(node.right);
        }
    }

    // ===========================================================
    //  MODULE 2: Multiway Trees & Range Query Structures (CO2)
    // ===========================================================

    // ---- Segment Tree: Water consumption analytics ----
    static class SegmentTree {
        double[] tree;
        int n;

        SegmentTree(double[] arr) {
            n = arr.length;
            tree = new double[4 * n];
            build(arr, 0, 0, n - 1);
        }

        final void build(double[] arr, int node, int l, int r) {
            if (l == r) { tree[node] = arr[l]; return; }
            int mid = (l + r) / 2;
            build(arr, 2*node+1, l, mid);
            build(arr, 2*node+2, mid+1, r);
            tree[node] = tree[2*node+1] + tree[2*node+2];
        }

        double query(int node, int l, int r, int ql, int qr) {
            if (qr < l || r < ql) return 0;
            if (ql <= l && r <= qr) return tree[node];
            int mid = (l + r) / 2;
            return query(2*node+1, l, mid, ql, qr) + query(2*node+2, mid+1, r, ql, qr);
        }

        void update(int node, int l, int r, int idx, double val) {
            if (l == r) { tree[node] = val; return; }
            int mid = (l + r) / 2;
            if (idx <= mid) update(2*node+1, l, mid, idx, val);
            else            update(2*node+2, mid+1, r, idx, val);
            tree[node] = tree[2*node+1] + tree[2*node+2];
        }

        double queryRange(int l, int r) { return query(0, 0, n-1, l, r); }
        void update(int idx, double val) { update(0, 0, n-1, idx, val); }
    }

    // ---- Fenwick Tree: Cumulative water distribution ----
    static class FenwickTree {
        double[] bit;
        int n;

        FenwickTree(int n) { this.n = n; bit = new double[n + 1]; }

        void update(int i, double delta) {
            for (++i; i <= n; i += i & (-i)) bit[i] += delta;
        }

        double query(int i) {
            double s = 0;
            for (++i; i > 0; i -= i & (-i)) s += bit[i];
            return s;
        }

        double queryRange(int l, int r) { return query(r) - (l > 0 ? query(l-1) : 0); }
    }

    // ===========================================================
    //  MODULE 3: Graph Algorithms for Water Networks (CO3)
    // ===========================================================

    static class WaterGraph {
        int V;
        List<int[]>[] adj; // {neighbor, weight}
        String[] names;

        @SuppressWarnings("unchecked")
        WaterGraph(int v, String[] n) {
            V = v; names = n;
            adj = new ArrayList[v];
            for (int i = 0; i < v; i++) adj[i] = new ArrayList<>();
        }

        void addEdge(int u, int v, int w) {
            adj[u].add(new int[]{v, w});
            adj[v].add(new int[]{u, w});
        }

        // BFS – reachable distribution zones
        void bfs(int src) {
            boolean[] visited = new boolean[V];
            Queue<Integer> q = new LinkedList<>();
            visited[src] = true; q.add(src);
            System.out.print("    BFS from " + names[src] + ": ");
            while (!q.isEmpty()) {
                int u = q.poll();
                System.out.print(names[u] + " ");
                for (int[] e : adj[u])
                    if (!visited[e[0]]) { visited[e[0]] = true; q.add(e[0]); }
            }
            System.out.println();
        }

        // DFS – detect pipeline connectivity
        void dfsHelper(int u, boolean[] visited) {
            visited[u] = true;
            System.out.print(names[u] + " ");
            for (int[] e : adj[u])
                if (!visited[e[0]]) dfsHelper(e[0], visited);
        }

        void dfs(int src) {
            boolean[] visited = new boolean[V];
            System.out.print("    DFS from " + names[src] + ": ");
            dfsHelper(src, visited);
            System.out.println();
        }

        // Kruskal's MST – optimal pipeline network design
        int find(int[] parent, int x) {
            return parent[x] == x ? x : (parent[x] = find(parent, parent[x]));
        }

        boolean union(int[] parent, int[] rank, int x, int y) {
            int px = find(parent, x), py = find(parent, y);
            if (px == py) return false;
            if (rank[px] < rank[py]) { int t = px; px = py; py = t; }
            parent[py] = px;
            if (rank[px] == rank[py]) rank[px]++;
            return true;
        }

        void kruskalMST() {
            List<int[]> edges = new ArrayList<>();
            for (int u = 0; u < V; u++)
                for (int[] e : adj[u])
                    if (u < e[0]) edges.add(new int[]{u, e[0], e[1]});
            edges.sort(Comparator.comparingInt(a -> a[2]));
            int[] parent = new int[V], rank = new int[V];
            for (int i = 0; i < V; i++) parent[i] = i;
            int totalCost = 0;
            System.out.println("    MST Edges (Kruskal's):");
            for (int[] e : edges) {
                if (union(parent, rank, e[0], e[1])) {
                    System.out.printf("      %s -- %s [%d km]%n", names[e[0]], names[e[1]], e[2]);
                    totalCost += e[2];
                }
            }
            System.out.println("    Total MST Cost: " + totalCost + " km");
        }
    }

    // ===========================================================
    //  MODULE 4: Shortest Path Optimization (CO4)
    // ===========================================================

    static class ShortestPath {
        int V;
        String[] names;
        List<int[]>[] adj;

        @SuppressWarnings("unchecked")
        ShortestPath(int v, String[] n) {
            V = v; names = n;
            adj = new ArrayList[v];
            for (int i = 0; i < v; i++) adj[i] = new ArrayList<>();
        }

        void addEdge(int u, int v, int w) {
            adj[u].add(new int[]{v, w});
            adj[v].add(new int[]{u, w});
        }

        // Dijkstra's Algorithm
        void dijkstra(int src) {
            int[] dist = new int[V];
            Arrays.fill(dist, Integer.MAX_VALUE);
            PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
            dist[src] = 0; pq.add(new int[]{0, src});
            while (!pq.isEmpty()) {
                int[] curr = pq.poll();
                int d = curr[0], u = curr[1];
                if (d > dist[u]) continue;
                for (int[] e : adj[u]) {
                    if (dist[u] + e[1] < dist[e[0]]) {
                        dist[e[0]] = dist[u] + e[1];
                        pq.add(new int[]{dist[e[0]], e[0]});
                    }
                }
            }
            System.out.println("    Dijkstra from " + names[src] + ":");
            for (int i = 0; i < V; i++)
                System.out.printf("      -> %-20s : %s%n", names[i],
                    dist[i] == Integer.MAX_VALUE ? "UNREACHABLE" : dist[i] + " km");
        }

        // Bellman-Ford Algorithm
        void bellmanFord(int src) {
            int[] dist = new int[V];
            Arrays.fill(dist, Integer.MAX_VALUE);
            dist[src] = 0;
            List<int[]> edges = new ArrayList<>();
            for (int u = 0; u < V; u++)
                for (int[] e : adj[u]) edges.add(new int[]{u, e[0], e[1]});
            for (int i = 0; i < V - 1; i++)
                for (int[] e : edges)
                    if (dist[e[0]] != Integer.MAX_VALUE && dist[e[0]] + e[2] < dist[e[1]])
                        dist[e[1]] = dist[e[0]] + e[2];
            System.out.println("    Bellman-Ford from " + names[src] + ":");
            for (int i = 0; i < V; i++)
                System.out.printf("      -> %-20s : %s%n", names[i],
                    dist[i] == Integer.MAX_VALUE ? "UNREACHABLE" : dist[i] + " km");
        }

        // Floyd-Warshall Algorithm
        void floydWarshall() {
            int INF = Integer.MAX_VALUE / 2;
            int[][] dist = new int[V][V];
            for (int[] row : dist) Arrays.fill(row, INF);
            for (int i = 0; i < V; i++) dist[i][i] = 0;
            for (int u = 0; u < V; u++)
                for (int[] e : adj[u])
                    dist[u][e[0]] = Math.min(dist[u][e[0]], e[1]);
            for (int k = 0; k < V; k++)
                for (int i = 0; i < V; i++)
                    for (int j = 0; j < V; j++)
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
            System.out.println("    Floyd-Warshall All-Pairs Shortest Paths:");
            System.out.printf("    %15s", "");
            for (String name : names) System.out.printf("%12s", name);
            System.out.println();
            for (int i = 0; i < V; i++) {
                System.out.printf("    %15s", names[i]);
                for (int j = 0; j < V; j++)
                    System.out.printf("%12s", dist[i][j] >= INF ? "INF" : dist[i][j] + "km");
                System.out.println();
            }
        }

        // Topological Sort – maintenance scheduling (Kahn's algorithm)
        void topoSort(int vcount, int[][] edges, String[] taskNames) {
            int[] indegree = new int[vcount];
            List<List<Integer>> dag = new ArrayList<>();
            for (int i = 0; i < vcount; i++) dag.add(new ArrayList<>());
            for (int[] e : edges) { dag.get(e[0]).add(e[1]); indegree[e[1]]++; }
            Queue<Integer> q = new LinkedList<>();
            for (int i = 0; i < vcount; i++) if (indegree[i] == 0) q.add(i);
            System.out.print("    Maintenance Schedule: ");
            while (!q.isEmpty()) {
                int u = q.poll();
                System.out.print(taskNames[u] + " → ");
                for (int v : dag.get(u)) if (--indegree[v] == 0) q.add(v);
            }
            System.out.println("DONE");
        }
    }

    // ===========================================================
    //  MODULE 5: Advanced Sorting & Data Ranking (CO5)
    // ===========================================================

    static class WaterRecord {
        int zoneID;
        double usage;
        String zoneName;

        WaterRecord(int id, String name, double u) { zoneID = id; zoneName = name; usage = u; }

        @Override
        public String toString() {
            return String.format("Zone %-2d (%-15s): %7.1f L", zoneID, zoneName, usage);
        }
    }

    static class SortingModule {

        // Merge Sort – sort water usage records
        void mergeSort(WaterRecord[] arr, int l, int r) {
            if (l >= r) return;
            int mid = (l + r) / 2;
            mergeSort(arr, l, mid);
            mergeSort(arr, mid+1, r);
            WaterRecord[] tmp = new WaterRecord[r - l + 1];
            int i = l, j = mid+1, k = 0;
            while (i <= mid && j <= r)
                tmp[k++] = arr[i].usage <= arr[j].usage ? arr[i++] : arr[j++];
            while (i <= mid) tmp[k++] = arr[i++];
            while (j <= r)   tmp[k++] = arr[j++];
            System.arraycopy(tmp, 0, arr, l, tmp.length);
        }

        // Quick Sort – rank high-consumption regions (descending)
        int partition(WaterRecord[] arr, int l, int r) {
            double pivot = arr[r].usage;
            int i = l - 1;
            for (int j = l; j < r; j++)
                if (arr[j].usage > pivot) { i++; WaterRecord t = arr[i]; arr[i] = arr[j]; arr[j] = t; }
            WaterRecord t = arr[i+1]; arr[i+1] = arr[r]; arr[r] = t;
            return i + 1;
        }

        void quickSort(WaterRecord[] arr, int l, int r) {
            if (l < r) {
                int p = partition(arr, l, r);
                quickSort(arr, l, p-1);
                quickSort(arr, p+1, r);
            }
        }

        // Heap Sort – identify critical supply zones
        void heapify(WaterRecord[] arr, int n, int i) {
            int largest = i, l = 2*i+1, r = 2*i+2;
            if (l < n && arr[l].usage > arr[largest].usage) largest = l;
            if (r < n && arr[r].usage > arr[largest].usage) largest = r;
            if (largest != i) {
                WaterRecord t = arr[i]; arr[i] = arr[largest]; arr[largest] = t;
                heapify(arr, n, largest);
            }
        }

        void heapSort(WaterRecord[] arr) {
            int n = arr.length;
            for (int i = n/2-1; i >= 0; i--) heapify(arr, n, i);
            for (int i = n-1; i > 0; i--) {
                WaterRecord t = arr[0]; arr[0] = arr[i]; arr[i] = t;
                heapify(arr, i, 0);
            }
        }

        // Counting Sort – sort pipeline IDs
        int[] countingSort(int[] ids) {
            int max = 0;
            for (int id : ids) max = Math.max(max, id);
            int[] count = new int[max + 1];
            for (int id : ids) count[id]++;
            int[] sorted = new int[ids.length];
            int idx = 0;
            for (int i = 0; i <= max; i++)
                while (count[i]-- > 0) sorted[idx++] = i;
            return sorted;
        }

        void print(WaterRecord[] arr, String label) {
            System.out.println("    " + label + ":");
            for (WaterRecord r : arr) System.out.println("      " + r);
        }
    }

    // ===========================================================
    //  MODULE 6: Greedy Algorithms & Dynamic Programming (CO6)
    // ===========================================================

    static class GreedyDP {

        // Activity Selection – schedule maintenance tasks
        static class Task {
            int start, end;
            String name;
            Task(String n, int s, int e) { name = n; start = s; end = e; }
        }

        void activitySelection(List<Task> tasks) {
            tasks.sort(Comparator.comparingInt(t -> t.end));
            System.out.println("    Selected Maintenance Tasks:");
            int lastEnd = -1;
            for (Task t : tasks) {
                if (t.start >= lastEnd) {
                    System.out.printf("      [%02d:00 - %02d:00] %s%n", t.start, t.end, t.name);
                    lastEnd = t.end;
                }
            }
        }

        // Fractional Knapsack – optimize water distribution resources
        static class Resource {
            String name;
            double value, weight;
            Resource(String n, double v, double w) { name = n; value = v; weight = w; }
        }

        void fractionalKnapsack(List<Resource> resources, double capacity) {
            resources.sort((a, b) -> Double.compare(b.value/b.weight, a.value/a.weight));
            double totalValue = 0;
            System.out.println("    Fractional Knapsack (Water Resource Allocation):");
            for (Resource r : resources) {
                if (capacity <= 0) break;
                double take = Math.min(r.weight, capacity);
                totalValue += take * (r.value / r.weight);
                capacity -= take;
                System.out.printf("      %-22s : take %.1f units (ratio=%.2f)%n",
                                  r.name, take, r.value/r.weight);
            }
            System.out.printf("    Total Distribution Value: %.2f%n", totalValue);
        }

        // 0/1 Knapsack – select infrastructure upgrades within budget
        void knapsack01(String[] items, int[] costs, int[] benefits, int budget) {
            int n = items.length;
            int[][] dp = new int[n+1][budget+1];
            for (int i = 1; i <= n; i++)
                for (int w = 0; w <= budget; w++) {
                    dp[i][w] = dp[i-1][w];
                    if (costs[i-1] <= w)
                        dp[i][w] = Math.max(dp[i][w], dp[i-1][w-costs[i-1]] + benefits[i-1]);
                }
            System.out.println("    0/1 Knapsack – Infrastructure Upgrades (Budget: ₹" + budget + "L):");
            System.out.println("    Max Benefit Score: " + dp[n][budget]);
            int w = budget;
            System.out.println("    Selected Upgrades:");
            for (int i = n; i >= 1; i--)
                if (dp[i][w] != dp[i-1][w]) {
                    System.out.printf("      -> %-25s (Cost: ₹%dL, Benefit: %d)%n",
                                      items[i-1], costs[i-1], benefits[i-1]);
                    w -= costs[i-1];
                }
        }

        // LIS – analyze water demand trends
        int lis(int[] arr) {
            int n = arr.length;
            int[] dp = new int[n];
            Arrays.fill(dp, 1);
            for (int i = 1; i < n; i++)
                for (int j = 0; j < i; j++)
                    if (arr[j] < arr[i]) dp[i] = Math.max(dp[i], dp[j]+1);
            int max = 0;
            for (int v : dp) max = Math.max(max, v);
            return max;
        }
    }

    // ===========================================================
    //  MAIN – DEMONSTRATION
    // ===========================================================
    public static void main(String[] args) {
        System.out.println("\n" + "#".repeat(60));
        System.out.println("##   WaterGrid – Smart Water Distribution System         ##");
        System.out.println("##          DSA-2 Complete Project Demo                  ##");
        System.out.println("#".repeat(60));

        // ---- MODULE 1 ----
        printHeader("MODULE 1: Trees & Balanced Search Structures (CO1)");

        printSub("BST – Pipeline Index (sorted by PipelineID)");
        PipelineBST bst = new PipelineBST();
        bst.insert(105, "Sector-A Main Line",   45.2);
        bst.insert(203, "Sector-B Sub Line",    30.1);
        bst.insert(101, "Reservoir Entry",      60.0);
        bst.insert(312, "Sector-C Feeder",      25.5);
        bst.insert(150, "Junction-X",           50.0);
        bst.insert(250, "Central Distribution", 70.0);
        System.out.println("  Inorder Traversal (sorted pipelines):");
        bst.root = bst.insert(bst.root, 105, "Sector-A Main Line", 45.2); // already done via insert(int,...)
        // reset and redo
        PipelineBST bst2 = new PipelineBST();
        int[] ids = {105, 203, 101, 312, 150, 250};
        String[] locs = {"Sector-A Main", "Sector-B Sub", "Reservoir Entry", "Sector-C Feeder", "Junction-X", "Central Dist."};
        double[] flows = {45.2, 30.1, 60.0, 25.5, 50.0, 70.0};
        for (int i = 0; i < ids.length; i++) bst2.insert(ids[i], locs[i], flows[i]);
        bst2.inorder(bst2.root);

        printSub("BST – Search & Delete");
        bst2.searchAndPrint(150);
        bst2.searchAndPrint(999);
        bst2.delete(150);
        System.out.println("  After deleting PipelineID 150:");
        bst2.inorder(bst2.root);

        printSub("AVL Tree – Balanced Water Usage Records (sorted by usage)");
        AVLTree avl = new AVLTree();
        int[][] zoneData = {{1,1200},{2,3400},{3,800},{4,5600},{5,2100},{6,4300}};
        for (int[] z : zoneData) avl.insert(z[0], z[1]);
        avl.inorder(avl.root);

        // ---- MODULE 2 ----
        printHeader("MODULE 2: Multiway Trees & Range Query Structures (CO2)");

        printSub("Segment Tree – Water Consumption Analytics");
        double[] regionConsumption = {1200, 3400, 800, 5600, 2100, 4300, 1750, 3000};
        SegmentTree segTree = new SegmentTree(regionConsumption);
        System.out.printf("  Total consumption (all 8 regions): %.0f L%n",
                          segTree.queryRange(0, 7));
        System.out.printf("  Consumption regions 2-5: %.0f L%n",
                          segTree.queryRange(2, 5));
        System.out.printf("  Consumption regions 0-3: %.0f L%n",
                          segTree.queryRange(0, 3));
        segTree.update(2, 1500); // region 2 consumption updated
        System.out.printf("  After updating region 2 to 1500L -> Total: %.0f L%n",
                          segTree.queryRange(0, 7));

        printSub("Fenwick Tree – Cumulative Water Distribution Statistics");
        FenwickTree fenwick = new FenwickTree(8);
        double[] dist = {500, 750, 300, 900, 450, 600, 820, 370};
        for (int i = 0; i < dist.length; i++) fenwick.update(i, dist[i]);
        System.out.printf("  Cumulative distribution [0..4]: %.0f L%n", fenwick.queryRange(0, 4));
        System.out.printf("  Cumulative distribution [3..7]: %.0f L%n", fenwick.queryRange(3, 7));
        System.out.printf("  Total distribution [0..7]:      %.0f L%n", fenwick.queryRange(0, 7));
        fenwick.update(2, 200); // add 200L to zone 2
        System.out.printf("  After +200L to zone 2 -> [0..7]: %.0f L%n", fenwick.queryRange(0, 7));

        // ---- MODULE 3 ----
        printHeader("MODULE 3: Graph Algorithms for Water Networks (CO3)");

        String[] nodes = {"Reservoir-A", "Reservoir-B", "Zone-1", "Zone-2", "Zone-3", "Zone-4"};
        WaterGraph wg = new WaterGraph(6, nodes);
        wg.addEdge(0, 2,  5);
        wg.addEdge(0, 3,  8);
        wg.addEdge(1, 3,  4);
        wg.addEdge(1, 4,  6);
        wg.addEdge(2, 3,  3);
        wg.addEdge(2, 5,  7);
        wg.addEdge(3, 4,  2);
        wg.addEdge(4, 5,  9);

        printSub("BFS – Reachable Water Distribution Zones");
        wg.bfs(0);
        wg.bfs(1);

        printSub("DFS – Pipeline Connectivity Check");
        wg.dfs(0);

        printSub("Kruskal's MST – Efficient Pipeline Network Design");
        wg.kruskalMST();

        // ---- MODULE 4 ----
        printHeader("MODULE 4: Shortest Path Optimization (CO4)");

        String[] spNodes = {"Reservoir-A", "Pump-Stn-1", "Pump-Stn-2", "Zone-North", "Zone-South"};
        ShortestPath sp = new ShortestPath(5, spNodes);
        sp.addEdge(0, 1, 10);
        sp.addEdge(0, 2, 15);
        sp.addEdge(1, 2,  5);
        sp.addEdge(1, 3, 20);
        sp.addEdge(2, 4, 10);
        sp.addEdge(3, 4,  5);

        printSub("Dijkstra's Algorithm – Optimal Water Distribution Paths");
        sp.dijkstra(0);

        printSub("Bellman-Ford – Networks with Varying Flow Costs");
        sp.bellmanFord(0);

        printSub("Floyd-Warshall – All-Reservoir Connectivity Analysis");
        sp.floydWarshall();

        printSub("Topological Sort – Water Maintenance Scheduling");
        String[] tasks = {"Inspect Pipes", "Flush System", "Chemical Test", "Repair Valves", "Restore Supply"};
        int[][] depEdges = {{0,1},{0,3},{1,2},{3,2},{2,4}};
        sp.topoSort(5, depEdges, tasks);

        // ---- MODULE 5 ----
        printHeader("MODULE 5: Advanced Sorting & Data Ranking (CO5)");

        WaterRecord[] records = {
            new WaterRecord(1, "Banjara Hills",  4500),
            new WaterRecord(2, "Jubilee Hills",  3200),
            new WaterRecord(3, "Secunderabad",   6100),
            new WaterRecord(4, "Kukatpally",     2800),
            new WaterRecord(5, "Hitech City",    5300),
            new WaterRecord(6, "Mehdipatnam",    1900),
        };

        SortingModule sorter = new SortingModule();

        printSub("Merge Sort – Sorted Water Usage Records (ascending)");
        WaterRecord[] ms = records.clone();
        sorter.mergeSort(ms, 0, ms.length - 1);
        sorter.print(ms, "Merge Sort Result");

        printSub("Quick Sort – High-Consumption Regions (descending)");
        WaterRecord[] qs = records.clone();
        sorter.quickSort(qs, 0, qs.length - 1);
        sorter.print(qs, "Quick Sort Result");

        printSub("Heap Sort – Critical Supply Zone Identification");
        WaterRecord[] hs = records.clone();
        sorter.heapSort(hs);
        sorter.print(hs, "Heap Sort Result (ascending)");

        printSub("Counting Sort – Pipeline ID Sorting");
        int[] pipelineIDs = {312, 105, 203, 101, 250, 150, 405, 320};
        System.out.print("    Original IDs: ");
        for (int id : pipelineIDs) System.out.print(id + " ");
        int[] sortedIDs = sorter.countingSort(pipelineIDs);
        System.out.print("\n    Sorted IDs:   ");
        for (int id : sortedIDs) System.out.print(id + " ");
        System.out.println();

        // ---- MODULE 6 ----
        printHeader("MODULE 6: Greedy Algorithms & Dynamic Programming (CO6)");

        GreedyDP gdp = new GreedyDP();

        printSub("Activity Selection – Maintenance Task Scheduling");
        List<GreedyDP.Task> maintenanceTasks = Arrays.asList(
            new GreedyDP.Task("Valve Inspection",      6,  8),
            new GreedyDP.Task("Pipeline Flushing",     8, 12),
            new GreedyDP.Task("Leak Detection Scan",   7, 10),
            new GreedyDP.Task("Chemical Treatment",   12, 14),
            new GreedyDP.Task("Pressure Test",        14, 16),
            new GreedyDP.Task("System Backup",        10, 12),
            new GreedyDP.Task("Filter Cleaning",      16, 18)
        );
        gdp.activitySelection(maintenanceTasks);

        printSub("Fractional Knapsack – Water Resource Optimization");
        List<GreedyDP.Resource> resources = Arrays.asList(
            new GreedyDP.Resource("Purified Water Supply",   80, 30),
            new GreedyDP.Resource("Emergency Tank Reserve",  50, 20),
            new GreedyDP.Resource("Groundwater Extraction",  60, 25),
            new GreedyDP.Resource("Treated Wastewater",      30, 15),
            new GreedyDP.Resource("Rainwater Harvesting",    40, 35)
        );
        gdp.fractionalKnapsack(resources, 60);

        printSub("0/1 Knapsack – Infrastructure Upgrades Within Budget");
        String[] upgrades = {
            "Smart Flow Meters", "AI Leak Detectors", "Solar-Powered Pumps",
            "Pipe Relining System", "SCADA Control Unit", "Pressure Regulators"
        };
        int[] upgradeCosts    = {20, 35, 50, 15, 45, 10};
        int[] upgradebenefits = {40, 65, 80, 25, 70, 18};
        gdp.knapsack01(upgrades, upgradeCosts, upgradebenefits, 80);

        printSub("Longest Increasing Subsequence – Water Demand Trend Analysis");
        int[] demandTrend = {120, 135, 130, 145, 140, 155, 170, 165, 180, 195};
        System.out.print("    Monthly Demand (kL): ");
        for (int d : demandTrend) System.out.print(d + " ");
        System.out.println();
        int lisLen = gdp.lis(demandTrend);
        System.out.println("    LIS Length (longest growth streak): " + lisLen);
        System.out.println("    Interpretation: Demand has been consistently growing for "
                           + lisLen + " months → Plan capacity expansion.");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  WaterGrid DSA-2 Project – All 6 Modules Complete!");
        System.out.println("=".repeat(60) + "\n");
    }
}