package module1;

import java.util.*;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.Microsoft;

/** HelloWorld
  * An application with two maps side-by-side zoomed in on different locations.
  * Author: UC San Diego Coursera Intermediate Programming team
  * @author Your name here
  * Date: July 17, 2015
  * */
public class HelloWorld extends PApplet
{
	/** Your goal: add code to display second map, zoom in, and customize the background.
	 * Feel free to copy and use this code, adding to it, modifying it, etc.  
	 * Don't forget the import lines above. */

	// You can ignore this.  It's to keep eclipse from reporting a warning
	private static final long serialVersionUID = 1L;

	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	// IF YOU ARE WORKING OFFLINE: Change the value of this variable to true
	//private static final boolean offline = false;
	
	/** The map we use to display our home town: La Jolla, CA */
	///UnfoldingMap map1;
	
	/** The map you will use to display your home town */ 
	//private UnfoldingMap map2;
	private UnfoldingMap map3;
	

	public void setup() {
		//size(800, 600, P2D);  // Set up the Applet window to be 800x600
		                      // The OPENGL argument indicates to use the 
		                      // Processing library's 2D drawing
		                      // You'll learn more about processing in Module 3

		// This sets the background color for the Applet.  
		// Play around with these numbers and see what happens!
		this.background(200, 200, 200);
		
		// Select a map provider
		//AbstractMapProvider provider = new Microsoft.RoadProvider();
		//AbstractMapProvider provider = new Google.GoogleTerrainProvider();
		// Set a zoom level
		//int zoomLevel = 15;
		int zoomLevel = 1;
		
//		if (offline) {
//			// If you are working offline, you need to use this provider 
//			// to work with the maps that are local on your computer.  
//			provider = new MBTilesMapProvider(mbTilesString);
//			// 3 is the maximum zoom level for working offline
//			zoomLevel = 3;
//		}
		
		// Create a new UnfoldingMap to be displayed in this window.  
		// The 2nd-5th arguments give the map's x, y, width and height
		// When you create your map we want you to play around with these 
		// arguments to get your second map in the right place.
		// The 6th argument specifies the map provider.  
		// There are several providers built-in.
		// Note if you are working offline you must use the MBTilesMapProvider
		///map1 = new UnfoldingMap(this, 50, 50, 350, 500, provider);

		// The next line zooms in and centers the map at 
	    // 32.9 (latitude) and -117.2 (longitude)
	    ///map1.zoomAndPanTo(zoomLevel, new Location(32.9f, -117.2f));
		
		// This line makes the map interactive
		///MapUtils.createDefaultEventDispatcher(this, map1);
		
		// TODO: Add code here that creates map2 
		// Then you'll modify draw() below
		
		  size(800, 600, P2D);
		  //map2 = new UnfoldingMap(this, 40, 50, 350, 500, new Microsoft.AerialProvider());
		 // MapUtils.createDefaultEventDispatcher(this, map2);
		  //map2.zoomAndPanTo(zoomLevel, new Location(32.9f, -117.2f));
		  
		  map3 = new UnfoldingMap(this, 50, 50, 700, 500, new Microsoft.AerialProvider());
		  MapUtils.createDefaultEventDispatcher(this, map3);
		  //map3.zoomAndPanTo(zoomLevel, new Location(39.939f, 32.887f));
		  map3.zoomAndPanTo(zoomLevel, new Location(0, 0));
		  
		  Location turkey = new Location(37.16, 37.03);
		  PointFeature valTr = new PointFeature(turkey);
		  valTr.addProperty("year", "2023");
		  
		 // Location japan = new Location(38.32, 142.36); location object could be created directly as a paramater in pointfeature
		  PointFeature valJpn = new PointFeature(new Location(38.32, 142.36));
		  valJpn.addProperty("year", "2011");
		  valJpn.addProperty("Casulties", "19,759 death");
		  
		  //SimplePointMarker val = new SimplePointMarker(valLoc);
		  //Location valLoc = new Location(-38.14, -73.03f);
		  PointFeature valEq = new PointFeature(new Location(-38.14, -73.03f));
		  valEq.addProperty("title", "Valdivia, Chile");
		  valEq.addProperty("magnitude", "9.5");
		  valEq.addProperty("date", "May 22, 1960");
		  valEq.addProperty("year", "1960");
		 // Marker valMk = new SimplePointMarker(valLoc, valEq.getProperties());
		  //map3.addMarker(val);
		 // map3.addMarker(valMk);
		  List<PointFeature> bigEqs = new ArrayList<PointFeature>();
		  bigEqs.add(valEq);
		  bigEqs.add(valTr);
		  bigEqs.add(valJpn);
		  List<Marker> markers = new ArrayList<Marker>();
		  for(PointFeature eq: bigEqs) {
			  markers.add(new SimplePointMarker(eq.getLocation(), eq.getProperties()));
		  }
		  //((Marker) markers).setColor(5);
		  int yellow = color(255,255,0);
		  int gray = color(150,150,150);
		  for(Marker mk : markers) {
			    String yearStr = (String) mk.getProperty("year");
			    int year = Integer.parseInt(yearStr);
			    if(year > 2020) {
			        mk.setColor(yellow);
			    }
			    else {
			        mk.setColor(gray);
			    }
			}
		  map3.addMarkers(markers);
	}

	/** Draw the Applet window.  */
	public void draw() {
		// So far we only draw map1...
		// TODO: Add code so that both maps are displayed
		//background(1);
		//map2.draw();
		map3.draw();
		//addKey();
	}

	
}
