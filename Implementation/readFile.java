
import java.io.*;

public class readFile {
	/*public static void main(String[] args) throws Exception {

		File file = new File("c.txt");

		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		// use the command: sdiff -l -w 400 a.txt b.txt > c.txt 
		// where a.txt and b.txt are the files to be compared. c.txt contains the output of sdiff command. 
		// each line produced by sdiff contains a separator between the a.txt line and the corresponding b.txt line;
		// so, to split the line into two halves, we use a regeular expression. Basically, each separator has:
        //		 - six spaces followed by "(" or ")" or "\" or "/" or ">" or "<" or "|", then a tab or \n		
		// here is the regular regex pattern: "      [\(|<|>|\)|\||\\|\/][\t|\n]"
		// the next one is the way it is written in java: "      [\\(|<|>|\\)|\\||\\\\|\\/][\\t|\\n]"
		// Used this website to convert from regular exp to java regular exp: http://www.regexplanet.com/advanced/java/index.html
		
		String pattern = "      [\\(|<|>|\\)|\\||\\\\|\\/][\\t|\\n]";
		while ((st = br.readLine()) != null) {
			st = st + '\n'; //This is needed because readLine() strip the '\n' at the end of line
			String[] tokensVal = st.split(pattern);
			for (int i = 0; i < tokensVal.length; i++)
				System.out.println(tokensVal[i]);
		}
	}*/
}
