import java.util.Random;
import java.util.Scanner;

public class ShortestPath {
    private static int numOfNodes;
    private static int numOfEdges;
    private static int edgeMinCost;
    private static int edgeMaxCost;

    public static void main(String[] args) {
        int[][] x = generateGraph();
        printGraph(x);
        printDistances(x);
    }

    private static int[][] generateGraph() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter number of nodes: ");
            numOfNodes = scanner.nextInt();

            System.out.print("Enter number of edges: ");
            numOfEdges = scanner.nextInt();

            if ((numOfEdges < numOfNodes - 1) || (numOfEdges > numOfNodes*(numOfNodes-1) / 2)) {
                System.out.println("Invalid edge number, try again.");
            }
            else {
                System.out.print("Enter minimum cost of edges: ");
                edgeMinCost = scanner.nextInt();

                System.out.print("Enter maximum cost of edges: ");
                edgeMaxCost = scanner.nextInt();
                break;
            }
        }

        int[][] graph = new int[numOfNodes][numOfNodes];
        int[] visited = new int[numOfNodes];
        while (true) {
            generateEdges(graph);
            checkConnected(0, visited,graph);
            if (checkIfOneEdgeBetweenTwoNodes(graph)) {
                int counter = 0;
                for (int i = 0; i < visited.length; i++) {
                    if (visited[i] == 1) {
                        counter++;
                    }
                }
                if (counter == numOfNodes) {
                    break;
                }
            }
            else {
                eraseGraph(graph);
            }
        }

        int [] edgeCosts = generateEdgeCosts();
        int edgeCostCounter = 0;

        for (int i = 0; i < numOfNodes; i++) {
            for (int j = 0; j < numOfNodes; j++) {
                if (i == j) {
                    graph[i][j] = 0; // diagonal values are 0.
                }
                else if(graph[i][j] == 99) {
                    graph[i][j] = edgeCosts[edgeCostCounter];
                    edgeCostCounter++;
                }
            }
        }
        return graph;
    }

    private static void generateEdges(int[][] arr) {
        Random random = new Random();
        int counter = 0;
        while (counter != numOfEdges) {
            int node1 = random.nextInt(numOfNodes);
            int node2 = random.nextInt(numOfNodes);

            if (arr[node1][node2] != 99 && (node1 != node2)) {
                arr[node1][node2] = 99; // initially, the generated edges are given with the value of 99.
                counter++;
            }
        }
    }

    // Checks if there are not more than one edge between two nodes
    private static boolean checkIfOneEdgeBetweenTwoNodes(int[][] graph) {
        for (int i = 0; i < graph[0].length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j] != 0 && graph[j][i] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private static void checkConnected(int i, int[] visited, int[][] graph) {
        visited[i] = 1;
        for (int j = 0; j < numOfNodes; j++) {
            if (visited[j] != 1 && graph[i][j] != 0) {
                checkConnected(j, visited, graph);
            }
        }
    }

    // Erases the graph if the previous created graph does not meet with the requirements
    private static void eraseGraph(int[][] graph) {
        for (int i = 0; i < graph[0].length; i++) {
            for (int j = 0; j < graph.length; j++) {
                graph[i][j] = 0;
            }
        }
    }

    private static int[] generateEdgeCosts() {
        int[] edgeCosts = new int[numOfEdges];
        Random random = new Random();
        for(int i = 0; i < numOfEdges; i++) {
            int edgeCost = edgeMinCost + random.nextInt(edgeMaxCost - edgeMinCost+1);
            edgeCosts[i] = edgeCost;
        }
        return edgeCosts;
    }

    private static void printGraph(int[][] arr) {
        System.out.println();
        System.out.println("Adjacency Matrix: ");
        System.out.print("[ ");
        for (int i = 0; i < arr[0].length; i++) {
            for (int j = 0; j < arr.length; j++) {
                if (arr[i][j] == 0) {
                    System.out.print(" * ");
                }
                else {
                    System.out.print(" "+arr[i][j] + " ");
                }
            }
            if (i == arr[0].length - 1) {
                System.out.print("]");
            }
            System.out.println();
            System.out.print("  ");
        }
    }

    private static void printDistances(int[][] graph) {
        int[] dist = bellmanFord(graph);
        int target = chooseTarget(graph);
        if (dist.length == 1) {
            System.out.println();
            return;
        }
        System.out.println("Bellman-Ford, Cost of shortest path:");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("Node "+i+" to Target Node "+target+": "+dist[i]);
        }
    }

    private static int[] bellmanFord(int[][] graph) {
        int[] distance = new int[numOfNodes];
        int[] path = new int[numOfNodes];
        int[] emptyArray = new int[1];

        for (int i = 0; i < distance.length; i++) {
            distance[i] = Integer.MAX_VALUE;
        }

        int target = chooseTarget(graph);

        for (int s = 0; s < numOfNodes; s++) {
            if (s == target) {
                path[s] = 0;
                continue;
            }
            else {
                distance[s] = 0;
                for (int i = 0; i < numOfNodes -1; i++) {
                    for (int k = 0; k < graph[0].length; k++) {
                        for (int j = 0; j < graph.length; j++) {
                            if (graph[k][j] != 0 && distance[k] != Integer.MAX_VALUE) {
                                if (distance[k] + graph[k][j] < distance[j]) {
                                    distance[j] = distance[k] + graph[k][j];
                                }
                            }
                        }
                    }
                }
                if (isNegativeCycle(graph, distance)) {
                    System.out.println("Negative Cycle Exists!");
                    return emptyArray;
                }
                path[s] = distance[target];
            }
            for (int i = 0; i < distance.length; i++) {
                distance[i] = Integer.MAX_VALUE;
            }
        }
        return path;
    }

    private static int chooseTarget(int[][] graph) {
        int[] edgeCounters = new int[graph.length];
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[0].length; j++) {
                if (graph[i][j] != 0) {
                    edgeCounters[i]++;
                }
            }
        }
        int min = edgeCounters[0];
        int index = 0;
        for (int i = 0; i < edgeCounters.length; i++) {
            if (edgeCounters[i] < min) {
                min = edgeCounters[i];
                index = i;
            }
        }
        return index;
    }

    private static boolean isNegativeCycle(int[][] graph, int[] distance) {
        for (int k = 0; k < graph[0].length; k++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[k][j] != 0 && distance[k] != Integer.MAX_VALUE) {
                    if (distance[k] + graph[k][j] < distance[j]) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
