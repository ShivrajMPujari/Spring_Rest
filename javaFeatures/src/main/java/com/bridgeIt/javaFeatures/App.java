package com.bridgeIt.javaFeatures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App 
{
    public static void main( String[] args )
    {
      Path path = Paths.get("/home/bridgelabz/Downloads/Sample.wav");
      try {
	byte[] fileByte =	Files.readAllBytes(path);
	
	} catch (IOException e) {
		e.printStackTrace();
	}
      
    }
}
