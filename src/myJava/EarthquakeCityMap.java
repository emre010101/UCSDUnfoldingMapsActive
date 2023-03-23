package myJava;
import processing.core.*;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.data.PointFeature;
import java.util.ArrayList;


public class EarthquakeCityMap extends PApplet {

	private UnfoldingMap map;
	
	public void setup() {
		size (950, 600, OPENGL);
		map = new UnfoldingMap(this, 150, 50, 700, 500, new Microsoft.AerialProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		Location valLoc = new Location(-38.14f, -73.03f);
		PointFeature valEq = new PointFeature(valLoc);
		valEq.addProperty("title", "Valdivia, Chile");
		valEq.addProperty("magnitude", "7.2");
		valEq.addProperty("date", "Oct 23, 2011");
		valEq.addProperty("year", "2011");
		
		Location vanLoc = new Location(38.75f, 43.36f);
		PointFeature vanEq = new PointFeature(vanLoc);
		vanEq.addProperty("title", "Van, Turkey");
		vanEq.addProperty("magnitude", "9.5");
		vanEq.addProperty("date", "May 22, 1960");
		vanEq.addProperty("year", "1960");
		
		ArrayList<PointFeature> bigEqs = new ArrayList<PointFeature>();
		bigEqs.add(valEq);
		bigEqs.add(vanEq);
		
		ArrayList<Marker> markers = new ArrayList<Marker>();
		for (PointFeature eq: bigEqs) {
			markers.add(new SimplePointMarker(eq.getLocation(), eq.getProperties()));
		}
		
		int yellow = color(255, 255, 0);
		int gray = color(150, 150, 150);
		


		//Marker valMK = new SimplePointMarker(valLoc, valEq.getProperties());
		map.addMarkers(markers);
		
		for (Marker mk : markers) {
			if ((int) mk.getProperty("year") > 2000) {
				mk.setColor(yellow);
			}
			else {
				mk.setColor(gray);
			}
		}
		
		
	}
	
	public void draw() {
		background(230);
		map.draw();
		//addKey();
	}
	

}


