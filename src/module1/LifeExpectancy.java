package module1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class LifeExpectancy extends PApplet {
	
	UnfoldingMap map;
	static Map<String, Float> lifeExpByCountry;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	public void setup() {
		size(900, 800, OPENGL);
		map = new UnfoldingMap(this, 50, 50, 800, 700, new Google.GoogleMapProvider());
		MapUtils.createDefaultEventDispatcher(this, map);
		
		lifeExpByCountry = loadLifeExpectancyFromCSV("C:\\Users\\emrek\\UCSDUnfoldingMaps\\data\\LifeExpectancyWorldBank.csv");
		
		countries = GeoJSONReader.loadData(this, "C:\\Users\\emrek\\UCSDUnfoldingMaps\\data\\countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		map.addMarkers(countryMarkers);
		shadeCountries();
	}
	
	private Map<String, Float> loadLifeExpectancyFromCSV(String filename) {
		
		Map<String, Float> lifeExpMap = new HashMap<String, Float>();
		String[] rows = loadStrings(filename);
		for(String row: rows) {
			String[] columns = row.split(",");
			for(int i = 2; i < columns.length; i++) {
				 if(columns[i].toUpperCase().equals(columns[i]) && columns[i].length() == 3 ) {
					 if(!columns[i+1].equals("..")) {
						 float value = Float.parseFloat(columns[i+1]);						 
						 lifeExpMap.put(columns[i], value);					 
					 }
				 }				 
			 }
			//float value = Float.parseFloat(columns[5]);
			//lifeExpMap.put(columns[4], value);
		}
		return lifeExpMap;
	}
	
	private void shadeCountries() {
		for(Marker marker : countryMarkers) {
			String countryId = marker.getId();
			if(lifeExpByCountry.containsKey(countryId)) {
				float lifeExp = lifeExpByCountry.get(countryId);
				int colorLevel = (int) map(lifeExp, 40, 90, 10, 255);
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			}
			else {
				marker.setColor(color(150, 150, 150));
			}
		}
	}

	public void draw() {
		map.draw();
	}
	/*public static void main(String[] args) {
		
		try {
			System.out.println(lifeExpByCountry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Test");
	}*/
	
}	
