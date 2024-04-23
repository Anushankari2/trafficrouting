import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.util.stream.Collectors;


import java.util.*;

public class TrafficRouting extends Application {

    private Pane cityPane;
    private Map<String, Circle> cityCircles = new HashMap<>();
    private Map<String, Map<String, Double>> cityGraph = new HashMap<>();
    private Map<String, Label> cityLabels = new HashMap<>();

    public void start(Stage primaryStage) {
        cityPane = new Pane();
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));

        // Add circles representing cities to cityPane
        addCityCircle("madurai", 100, 100);
        addCityCircle("tiruchirappalli", 300, 200);
        addCityCircle("salem", 500, 100);
        addCityCircle("chennai", 400, 300);
        addCityCircle("coimbatore", 200, 400);

        // Add edges between cities (example connections)
        addEdge("madurai", "tiruchirappalli", 160.0);
        addEdge("madurai", "salem", 340.0);
        addEdge("madurai", "chennai", 450.0);
        addEdge("tiruchirappalli", "salem", 180.0);
        addEdge("tiruchirappalli", "chennai", 360.0);
        addEdge("salem", "chennai", 340.0);
        addEdge("chennai", "coimbatore", 350.0);

        // Display the cityPane
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic Routing");

        // User input controls
        TextField sourceField = new TextField();
        sourceField.setPromptText("Enter source city");
        TextField destinationField = new TextField();
        destinationField.setPromptText("Enter destination city");
        TextField waypointsField = new TextField();
        waypointsField.setPromptText("Enter intermediate cities (comma-separated)");
        Button findPathButton = new Button("Find Shortest Path");

        VBox inputBox = new VBox(10,
                new Label("Source City:"), sourceField,
                new Label("Destination City:"), destinationField,
                new Label("Waypoints:"), waypointsField,
                findPathButton);
        mainLayout.getChildren().addAll(inputBox, cityPane);

        findPathButton.setOnAction(e -> {
            String sourceCity = sourceField.getText().trim();
            String destinationCity = destinationField.getText().trim();
            String waypointsInput = waypointsField.getText().trim();

            List<String> waypoints = Arrays.asList(waypointsInput.split("\\s*,\\s*"));
            List<String> citiesToVisit = new ArrayList<>();
            citiesToVisit.add(sourceCity);
            citiesToVisit.addAll(waypoints);
            citiesToVisit.add(destinationCity);

            findAndDisplayShortestPath(citiesToVisit);
        });

        primaryStage.show();
    }

    private void addCityCircle(String cityName, double x, double y) {
        Circle circle = new Circle(x, y, 20, Color.BLUE);
        circle.setStroke(Color.BLACK);
        cityCircles.put(cityName, circle);
        cityPane.getChildren().add(circle);

        // Label the city node with its name
        Label nameLabel = new Label(cityName);
        nameLabel.setLayoutX(x - 15); // Adjust label position relative to circle
        nameLabel.setLayoutY(y + 25); // Adjust label position relative to circle
        cityLabels.put(cityName, nameLabel);
        cityPane.getChildren().add(nameLabel);

        // Initialize city graph with an empty map for connections (edges)
        cityGraph.put(cityName, new HashMap<>());
    }

    private void addEdge(String city1, String city2, double distance) {
        cityGraph.computeIfAbsent(city1, k -> new HashMap<>()).put(city2, distance);
        cityGraph.computeIfAbsent(city2, k -> new HashMap<>()).put(city1, distance);

        // Draw a line (edge) between the corresponding city circles
        Line pathLine = new Line(
                cityCircles.get(city1).getCenterX(),
                cityCircles.get(city1).getCenterY(),
                cityCircles.get(city2).getCenterX(),
                cityCircles.get(city2).getCenterY());
        cityPane.getChildren().add(0, pathLine); // Add line at the bottom (below circles)
    }

  private void findAndDisplayShortestPath(List<String> citiesToVisit) {
    // Remove any empty strings from the citiesToVisit list
    citiesToVisit = citiesToVisit.stream().filter(city -> !city.isEmpty()).collect(Collectors.toList());

    List<String> path = new ArrayList<>();

    if (citiesToVisit.size() > 1) {
        for (int i = 0; i < citiesToVisit.size() - 1; i++) {
            String sourceCity = citiesToVisit.get(i);
            String destinationCity = citiesToVisit.get(i + 1);
            List<String> shortestPath = dijkstraShortestPath(sourceCity, destinationCity);
            path.addAll(shortestPath);
            if (i < citiesToVisit.size() - 2) {
                path.remove(path.size() - 1); // Remove duplicate city (endpoint of previous path)
            }
        }
    } else {
        System.out.println("Please specify at least two cities to find a path.");
        return;
    }

    highlightShortestPath(path);
}


  private List<String> dijkstraShortestPath(String sourceCity, String destinationCity) {
    // Maps to store distances, predecessors, and visited status
    Map<String, Double> distances = new HashMap<>();
    Map<String, String> predecessors = new HashMap<>();
    Set<String> visited = new HashSet<>();

    // Initialize distances (source to itself is 0, others are infinity)
    distances.put(sourceCity, 0.0);
    for (String city : cityGraph.keySet()) {
        if (!city.equals(sourceCity)) {
            distances.put(city, Double.POSITIVE_INFINITY);
        }
    }

    // Priority queue to select the city with the smallest known distance
    PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(distances::get));

    pq.add(sourceCity);

    // Dijkstra's algorithm
    while (!pq.isEmpty()) {
        String current = pq.poll();
        if (visited.contains(current)) {
            continue;
        }
        visited.add(current);

        for (Map.Entry<String, Double> neighbor : cityGraph.get(current).entrySet()) {
            String next = neighbor.getKey();
            double distance = neighbor.getValue();
            double newDistance = distances.get(current) + distance;

            if (newDistance < distances.get(next)) {
                distances.put(next, newDistance);
                predecessors.put(next, current);
                pq.add(next);
            }
        }
    }

    // Build the shortest path
    List<String> shortestPath = new ArrayList<>();
    String step = destinationCity;
    while (predecessors.containsKey(step)) {
        shortestPath.add(0, step); // Insert at the beginning to reverse the path
        step = predecessors.get(step);
    }
    shortestPath.add(0, sourceCity); // Add the source city to the path

    return shortestPath;
}



   private void highlightShortestPath(List<String> path) {
    Set<String> shortestPathSet = new HashSet<>(path);

    for (int i = 0; i < path.size() - 1; i++) {
        String city1 = path.get(i);
        String city2 = path.get(i + 1);

        if (cityCircles.containsKey(city1) && cityCircles.containsKey(city2)) {
            Circle circle1 = cityCircles.get(city1);
            Circle circle2 = cityCircles.get(city2);

            Line pathLine = new Line(
                    circle1.getCenterX(),
                    circle1.getCenterY(),
                    circle2.getCenterX(),
                    circle2.getCenterY());

            if (shortestPathSet.contains(city1) && shortestPathSet.contains(city2)) {
                // Highlight shortest path in red
                pathLine.setStroke(Color.RED);
                pathLine.setStrokeWidth(3.0);
            } else {
                // Set other paths to grey
                pathLine.setStroke(Color.GREY);
                pathLine.setStrokeWidth(1.5);
            }

            cityPane.getChildren().add(pathLine);
        } else {
            System.out.println("City not found: " + city1 + " or " + city2);
        }
    }
}

    public static void main(String[] args) {
        launch(args);
    }
}
