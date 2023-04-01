package myJava;

//import processing.core.PApplet;
import processing.core.*;

public class sun extends PApplet {
	
	private String url = "https://media.architecturaldigest.com/photos/5af4aed7da68792ef45e50a4/master/w_3865,h_2576,c_limit/16%20Nacpan.jpg" ;
	
	PImage photo; //Declaring a new 
	
	
	public void setup()
	{
		System.setProperty("https.protocols", "TLSv1.1,TLSv1.2");

		size(400, 400); //canvas size
		background(255);
		stroke(0); //set pen color
		photo = loadImage(url, "jpg");
		photo.resize(0,height); //width will be decided according to height and height is already size of the canvas
		image(photo, -170, 0); //
		
		 if (photo == null) {
			 System.err.println("Failed to load image. Please check the URL or file path.");
			 exit(); // Exit the sketch if the image could not be loaded
		 }
		 
	}
	
	public void draw()
	{	
		int[] color = sunColorSec(second()); //Calculate color code for sun, and passed second() built-in method as a paramater 
		fill(color[0], color[1], color[2]);
		ellipse(width-100, height/4, width/5, height/5); //draw sun
	}
	
	public int[] sunColorSec(float seconds)
	{
		int[] rgb = new int[3];
		//Scale the brightness of the yellow based on the seconds. 30 seconds
		//is black. 0 seconds is bright yellow.
		float diffFrom30 = Math.abs(30-seconds);
		
		float ratio = diffFrom30/30;
		rgb[0] = (int)(255*ratio);
		rgb[1] = (int)(255*ratio);
		rgb[2] = 0;
		
		return rgb;
	}
	
	
}