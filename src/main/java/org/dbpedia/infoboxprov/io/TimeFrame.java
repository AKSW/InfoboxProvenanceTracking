package org.dbpedia.infoboxprov.io;

import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Date;

/**
 * Class for handling the timestamps inputed from the user
 * 
 * @author daniel
 */

public class TimeFrame {
	

	private Date[] timeFrame = null;
	
	public TimeFrame(String earlier, String later) {
		
	try {	
		
    String newDate =new SimpleDateFormat("yyyy-MM-dd").format(new Date());	
		 
	Date extractLater = new SimpleDateFormat("yyyy-MM-dd")
				.parse(later);
	Date extractEarlier = new SimpleDateFormat("yyyy-MM-dd")
				.parse(earlier);
	Date currentDate = new  SimpleDateFormat("yyyy-MM-dd")
				.parse(newDate);
	Date foundation = new SimpleDateFormat("yyyy-MM-dd").
				parse("2001-01-02");
		
		if(extractEarlier.after(extractLater )){
			throw new DateTimeException ("Earlier timestamp has to be earlier when later timestamp");
		}
		
		if(extractEarlier.before(foundation) ){
			extractEarlier = foundation;
			System.out.println("Set earlier timestamp to Wikipedia foundation date 2001-01-02");
		}
		
		if(extractLater.after(new  SimpleDateFormat("yyyy-MM-dd").parse(newDate)) ){
			extractLater = currentDate;
			System.out.println("Set later timestamp to current date");
		}
		
		
		if(extractLater.before(foundation)){
			throw new DateTimeException ("Later timestamp has to be later when Wikipedia foundation date 2001-01-02");
		}
		
		
		if(extractEarlier.after(currentDate)){
			throw new DateTimeException ("Earlier timestamp has to be earlier when current Date");
		}
		
		
		if( !(extractEarlier.equals(foundation)&&
			extractLater.equals(currentDate) )	){
		
			timeFrame = new Date[] { extractEarlier,extractLater };
		}
		
	} catch (java.text.ParseException | DateTimeException  e) {
		System.out.println(e.getMessage());
		System.exit(1);
	}
	
	}
	
	public Date[] getDateArray() {
		return timeFrame;
	}
	
	
	
}
