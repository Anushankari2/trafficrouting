# trafficrouting
java project with ui/ux implementation and using djikstra"s algorithm to efficiently route traffic also has an additional functionality to consider intermediate cities in the path.
To find the path djikstra's algorithm will be applied for source->intermediate and intermediate->destination.


1. Graphical Representation Setup:
   - Implement a graphical user interface (GUI) using JavaFX to display cities (nodes) and roads (edges) as a visual representation of the road network.
   - Allow users to interactively add cities to the graph and specify connections (roads) between them.

2. Input Gathering:
   - Provide user input fields to specify a source city, a destination city, and optional intermediate cities (waypoints) for route calculation.
   - Validate user input to ensure selected cities exist within the graph.

3. Applying Dijkstra's Algorithm:
   - Implement Dijkstra's algorithm to compute the shortest path from the source city to each intermediate city and from each intermediate city to the destination city.
   - Use an adjacency list representation of the graph to efficiently calculate shortest paths based on edge weights (e.g., road distances or travel times).

4. Path Calculation and Highlighting:
   - Compute the shortest paths using Dijkstra's algorithm for the specified route (source -> intermediate -> destination).
   - Store the computed shortest paths and highlight them visually on the map using JavaFX, such as changing the color or thickness of road segments corresponding to the shortest path.

5. User Interaction and Visualization:
   - Display the graphical representation of cities and roads within the JavaFX application.
   - Provide interactive features for users to select source, destination, and intermediate cities via dropdown menus or text input fields.
   - Upon route calculation, visually highlight the shortest path on the map to provide a clear visualization of the recommended route.

6. Error Handling and User Feedback:
   - Implement error handling to notify users of invalid inputs (e.g., non-existent cities or disconnected graph components).
   - Display informative messages or tooltips to guide users through the input and route selection process.
