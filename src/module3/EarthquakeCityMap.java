package module3;

//Java utilities libraries
import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
import java.util.List;

//Processing library
import processing.core.PApplet;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;

//Parsing library
import parsing.ParseFeed;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Emre Kavak
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {

	// You can ignore this.  It's to keep eclipse from generating a warning.
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFLINE, change the value of this variable to true
	private static final boolean offline = false;
	
	// Less than this threshold is a light earthquake
	public static final float THRESHOLD_MODERATE = 5;
	// Less than this threshold is a minor earthquake
	public static final float THRESHOLD_MEDIUM = 4;
	// My configuration
	public static final float THRESHOLD_LIGHT = 3;
	
    int white = color(255, 228, 228);
    int darkred = color(153, 0, 0);
    int lightred = color(255, 102, 102);
    int verylightred = color(241, 169, 160);

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// The map
	private UnfoldingMap map;
	
	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";

	
	public void setup() {
		size(960, 800, OPENGL);

		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 700, 500, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom"; 	// Same feed, saved Aug 7, 2015, for working offline
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 750, 700, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
			//earthquakesURL = "2.5_week.atom";
		}
		
	    map.zoomToLevel(2);
	    MapUtils.createDefaultEventDispatcher(this, map);	
			
	    // The List you will populate with new SimplePointMarkers
	    List<Marker> markers = new ArrayList<Marker>();

	    //Use provided parser to collect properties for each earthquake
	    //PointFeatures have a getLocation method
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	  
	    //loop for creating simple point markers object from point feature objects
	    for(PointFeature eq : earthquakes) {
	    	markers.add(createMarker(eq));
	    }

		  
	    // Add the markers to the map so that they are displayed
	    map.addMarkers(markers);

	}
		
	private SimplePointMarker createMarker(PointFeature feature)
	{  
		SimplePointMarker marker = new SimplePointMarker(feature.getLocation());
		
		
		Object magObj = feature.getProperty("magnitude");
		float mag = Float.parseFloat(magObj.toString());
		//System.out.println(mag);
		System.out.println("mag: " + mag + "location: " + feature.getLocation());
		
		// Here is an example of how to use Processing's color method to generate 
	    // an int that represents the color yellow.  

		
	    if(mag>THRESHOLD_MODERATE) {
	    	marker.setColor(darkred);
	    }else if(mag>THRESHOLD_MEDIUM) {
	    	marker.setColor(lightred);
	    }else if(mag>THRESHOLD_LIGHT) {
	    	marker.setColor(verylightred);
	    }
	    else {
	    	marker.setColor(white);
	    }
	    
	    // Finally return the marker
	    return marker;
	}
	
	public void draw() {
	    background(4);
	    map.draw();
	    addKey();
	}

	// helper method to draw key in GUI
	// TODO: Implement this method to draw the key
	private void addKey() 
	{	
		fill(225); //255 is white
		rect(5, 50, 180, 415);
		
		fill(10);
		textSize(22); 
		text("Eartquake Keys", 15, 125); 
		textSize(16); 
		text("+5.0 Magnitude", 40, 180); 
		text("+4.0 Magnitude", 40, 240);
		text("+3.0 Magnitude", 40, 300);
		text("Below 3.0", 40, 360);
		//rectMode(CORNER);
		
		fill(darkred);
		ellipse(15, 175, 15, 15);
		fill(lightred);
		ellipse(15, 235, 15, 15);
		fill(verylightred);
		ellipse(15, 295, 15, 15);
		fill(white);
		ellipse(15, 355, 15, 15);
		
		// Remember you can use Processing's graphics methods here
	
	}
}
