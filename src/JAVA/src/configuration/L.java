package configuration;

public class L {
	/*		Loglevel explaination:
	 * 		0... log nothing, well, almost nothing, just the important stuff!
	 * 		increase for more output...
	 */
	
	
	
	
	public static int level = 0;
	
	public static void log(String stringToLog, int logLevel){
		if(logLevel<=level){
		System.out.println(stringToLog);
		}
		
		
	}

	public static void logErr(String stringToLog, int logLevel){
		if(logLevel<=level){
		System.err.println(stringToLog);
		}
		
	}
	
	
	

}
