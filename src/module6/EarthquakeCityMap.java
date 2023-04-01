package module6;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PImage;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	//Show City Around Earthquakes Variables
	int numberofQuakesAround;
	double averageMag;
	String mostRecent;
	PImage photo; //Declaring a new 
	
	//private String url = "https://media.architecturaldigest.com/photos/5af4aed7da68792ef45e50a4/master/w_3865,h_2576,c_limit/16%20Nacpan.jpg";
	private String url = "https://res.klook.com/image/upload/Mobile/City/qyxhb8q4t2efvxpa7ew8.jpg";
	
	//private String url = "istanbul.jpg";
	//private String localImageFileName = "istanbul.png";
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(1000, 750, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 225, 25, 750, 700, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 225, 25, 750, 700, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		for(Feature city : cities) {
			//System.out.println("Test purpose: " + city.getStringProperty("name"));
			if(city.getStringProperty("name").equalsIgnoreCase("Istanbul")) {
				city.addProperty("photo", "/UCSDUnfoldingMaps/data/images/istanbul.png");
				System.out.println("Url Has been attached succesfully.");
			}
		}
		System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    //printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    /*for(Marker m: quakeMarkers) {
	    	System.out.println(m);
	    }*/
	    sortAndPrint(20);
	    
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
		if (lastClicked != null && lastClicked instanceof CityMarker) {
	        addCityPopup((CityMarker) lastClicked);
	    }
	}
	
	/*private boolean checkIfCity() {
		boolean result = false;
		for(Marker m: cityMarkers) {
			if(lastClicked==m) {
				result = true;
			}
		}
		return result;
	}*/
	
	private void sortAndPrint(int numToPrint) {
		Object[] myQuakes = quakeMarkers.toArray();
		//Insertion Sort
		for(int i=1; i<myQuakes.length; i++) {
			EarthquakeMarker current = (EarthquakeMarker)myQuakes[i];
			EarthquakeMarker previous = (EarthquakeMarker)myQuakes[i-1];

			int check = i;
			while(check>0 && ((current.compareTo(previous))>0)) {
				/*If left is greater than right, it will swap to most left side where is the value most great*/
				swap(myQuakes, check, check-1);
				check--;
				current = (EarthquakeMarker)myQuakes[check];
				//To avoid index out of bounds crash 
				if(check==0) {
					previous = (EarthquakeMarker)myQuakes[check];
				}else {
					previous = (EarthquakeMarker)myQuakes[check-1];
				}
			}
		}//2nd condition to check length of the array
		for(int print=0; print<numToPrint && print<myQuakes.length; print++) {
			System.out.println((EarthquakeMarker)myQuakes[print]);
		}
	}
	
	private void swap(Object[] myQuakes, int first, int second) {
		EarthquakeMarker temp = (EarthquakeMarker) myQuakes[first];
		myQuakes[first] = myQuakes[second];
		myQuakes[second] = temp;
	}


	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			if (lastClicked == null) {
				checkCitiesForClick();
				//Calling other methods
				//important to check if the click was made on the object
				if (lastClicked != null && lastClicked instanceof CityMarker) {
					activateCity((CityMarker)lastClicked);
				}
			}
		}
		/*if(checkIfCity()==true) {
			System.out.println("now we are talking");
			addCityPopup();
		}*/
	}


	/*The difference in my implementation in module 5 is:
	 *I iterate over the markers inside mouseClicked method 
	 *when i find I set the lastClicked and hide all the markers
	 *and in my helper methods I only check the around of the
	 *city or quakes and if they are in the thread zone I set them 
	 *to unhidden
	 *In terms of readability my code is easier but UCS is more complex and well structured.*/
	
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	/*In this helper method both click and thread zone is checked*/
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) > quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}		
	}
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 5;
		int ybase = 40;
		
		rect(xbase, ybase, 210, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		
	}
	
	//City popup menu
	private void addCityPopup(CityMarker cityMarker) {
	    // Customize the popup content with the city's information
	    fill(255, 250, 240);
	    int xbase = 5;
	    int ybase = 315;

	    rect(xbase, ybase, 210, 180);

	    fill(0);
	    textAlign(LEFT, CENTER);
	    textSize(12);
	    text("City: " + cityMarker.getCity(), xbase + 10, ybase + 20);
	    text("Country: " + cityMarker.getCountry(), xbase + 10, ybase + 40);
	    text("Population: " + cityMarker.getPopulation(), xbase + 10, ybase + 60);
	 
	    text("EarhQuakes around: " + numberofQuakesAround, xbase+10, ybase+80);
	    text("Avg Mag: " + averageMag, xbase+10, ybase+100);
	    if(mostRecent=="No Quake") {
	    	text("Most Recent: " + mostRecent, xbase+10, ybase+120);
	    }else {
	    	text("Most Recent: ", xbase+10, ybase+120);
	    	text(mostRecent, xbase+10, ybase+140);
	    }
	    
	    //photo = loadImage(url, "jpg");
	    //String absoluteFilePath = sketchPath(url);
	    //photo = loadLocalImage(url);
	    
	    photo = loadImage(url, "jpg");
	    //photo = loadImage(localImageFileName);
	    
	    if (photo == null) {
	        System.err.println("Failed to load image. Please check the file path.");
	        exit(); // Exit the sketch if the image could not be loaded
	    } else {
	    	//photo = loadImage(photo, "png");
	        photo.resize(210, 200);
	        image(photo, xbase, ybase+200);
	    }
	}
	
	private void activateCity(CityMarker clickCity) {
		System.out.println("test in activatecity");
		this.numberofQuakesAround= findQuakesNumber(clickCity);
		this.averageMag = findtheAverage(clickCity);
		this.mostRecent = findtheRecent(clickCity);
	}
	
	private String findtheRecent(CityMarker cityMarker) {
		System.out.println("testin findtherecent");
		ArrayList<EarthquakeMarker> quakes = findQuakes(cityMarker);
		if(quakes==null) return null;
		EarthquakeMarker[] quakesArray = new EarthquakeMarker[quakes.size()];
        quakes.toArray(quakesArray);
        
		if(quakesArray.length==1) {
			return quakesArray[0].getTitle().split(",")[0];
		}else if(quakesArray.length==0) {
			return "No Quake";
		}
		//Insertion Sort
		for(int i=1; i<quakesArray.length; i++) {
			EarthquakeMarker current = (EarthquakeMarker)quakesArray[i];
			EarthquakeMarker previous = (EarthquakeMarker)quakesArray[i-1];

			int check = i;
			while(check>0 && ((current.compareToDate(previous))>0)) {
				/*If left is greater than right, it will swap to most left side where is the value most great*/
				swap(quakesArray, check, check-1);
				check--;
				current = (EarthquakeMarker)quakesArray[check];
				//To avoid index out of bounds crash 
				if(check==0) {
					previous = (EarthquakeMarker)quakesArray[check];
				}else {
					previous = (EarthquakeMarker)quakesArray[check-1];
				}
			}
		}//2nd condition to check length of the array
		return quakesArray[0].getTitle().split(",")[0];

	}


	private double findtheAverage(CityMarker cityMarker) {
		ArrayList<EarthquakeMarker> quakes = findQuakes(cityMarker);
		if(quakes.isEmpty()) {return 0.0;}
		float sum=0;
		for(EarthquakeMarker m : quakes) {
			sum += m.getMagnitude();
		}
		 // Format the double value with 3 decimal places
	    String formattedAverage = String.format("%.3f", (double)sum / quakes.size());
	    // Parse the formatted string back to a double
	    return Double.parseDouble(formattedAverage);
	}


	private int findQuakesNumber(CityMarker cityMarker) {
		int nofQuakes=0;
		ArrayList<EarthquakeMarker> quakes = findQuakes(cityMarker);
		nofQuakes = quakes.size();
		System.out.println(nofQuakes);
		if(nofQuakes>0) {
			return nofQuakes;
		}else {
			return 0;
		}
		
	}
	
	private ArrayList<EarthquakeMarker> findQuakes(CityMarker cityMarker) {
		ArrayList<EarthquakeMarker> quakes = new ArrayList<EarthquakeMarker>();
		//if(quakes.size()==0) {return null;}
		int count=0;
		for (Marker cqks : quakeMarkers) {
			EarthquakeMarker quakeMarker = (EarthquakeMarker)cqks;
			if (quakeMarker.getDistanceTo(cityMarker.getLocation()) < quakeMarker.threatCircle()) {
				quakes.add(quakeMarker);
				count++;
			}
		}
		return quakes;
	}
	


	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
