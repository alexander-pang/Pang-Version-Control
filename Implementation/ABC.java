
public class ABC {

    public static void main(String[] args) throws Exception {

    	System.out.println("Command line argument number is: "+args.length);
    	System.out.println("Command line arguments are:");
    	for(String s: args) {
        	
        		System.out.println(s);
        		
        		//if one option has an = sign, like for example W=12, you can get "W" and "12" each on its own string
        		//  by splitting the option. Here is how.... 
        		if(s.contains("=")) { 
        		String[] opt = s.split("=");
        		System.out.println("The previous option has an equal sign, its arguments are:");
        		for(String x: opt) {
        			System.out.println(x);
        		}
        		System.out.println();
        	}
    	}
    }
}