package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	private List<Marker> airportList;
	List<Marker> routeList;
	private List<Marker> cityMarkers;
	
	private float mapMargin = 0.02f; // Relative margin (2% of the window size)
	private float leftPanelWidth = 0.3f; // Width of the left panel (30% of the canvas width)
	
	public void setup() {
		// setting up PApplet
		int canvasWidth = (int) (displayWidth * 0.55);
		int canvasHeight = (int) (displayHeight * 0.55);
		size(canvasWidth, canvasHeight, OPENGL);
				
		// setting up map and default events
		float mapX = width * (leftPanelWidth + mapMargin);
		float mapY = height * mapMargin;
		float mapWidth = width * (1 - leftPanelWidth - 2 * mapMargin);
		float mapHeight = height * (1 - 2 * mapMargin);
				
		map = new UnfoldingMap(this, mapX, mapY, mapWidth, mapHeight);
		// setting up PAppler
		//size(800,600, OPENGL);
		
		// setting up map and default events
		//map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		List<Feature> cities = GeoJSONReader.loadData(this, "city-data.json");
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			System.out.println(sl.getProperties());
			
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
		
		// Count routes for each airport
		HashMap<Integer, Integer> airportRouteCounts = new HashMap<Integer, Integer>();
		for (ShapeFeature route : routes) {
			int source = Integer.parseInt((String) route.getProperty("source"));
			int dest = Integer.parseInt((String) route.getProperty("destination"));

			if (airportRouteCounts.containsKey(source)) {
				airportRouteCounts.put(source, airportRouteCounts.get(source) + 1);
			} else {
				airportRouteCounts.put(source, 1);
			}

			if (airportRouteCounts.containsKey(dest)) {
				airportRouteCounts.put(dest, airportRouteCounts.get(dest) + 1);
			} else {
				airportRouteCounts.put(dest, 1);
			}
		}
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		map.addMarkers(cityMarkers);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	

}
