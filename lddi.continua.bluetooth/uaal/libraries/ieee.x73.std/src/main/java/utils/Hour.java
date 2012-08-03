package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Hour {
	
	public static String getActualTime(){
		Date fechaActual = new Date();
		SimpleDateFormat formato = new SimpleDateFormat("H:mm:ss:SSS");
		return( formato.format(fechaActual));
		
		
	}
		
}
