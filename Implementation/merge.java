
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class merge {
	public merge(String fileName) throws IOException {
		File file = new File("c.txt");
		List<String> readText = Files.readAllLines(Paths.get("c.txt"));

		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

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

		List<String> real = new ArrayList<>();
		while ((st = br.readLine()) != null) {
			st = st + '\n'; //This is needed because readLine() strip the '\n' at the end of line
			String[] tokensVal = st.split(pattern);
			for (int i = 0; i < tokensVal.length; i++) {
				tokensVal[i] = tokensVal[i].trim();
			}
			//	List<String> zl = new ArrayList<String>();
			List<String> al = new ArrayList<String>(Arrays.asList(tokensVal));
			//	zl = Arrays.asList(tokensVal);
			al.removeIf(""::equals);
			if (al.size() == 2) {
				String str = String.format("Press 1 to choose: %s or Press 2 to choose: %s", al.get(0), al.get(1));
				//System.out.println(str.trim());
				String input = in.readLine();
				if (input.equals("1")) {
					real.add(al.get(0));
				} else {
					real.add(al.get(1));
				}
			} else if (al.size() == 1) {
				real.add(al.get(0));
			}
		}

		File f = new File(fileName);
		f.delete();
		FileWriter fw = new FileWriter(fileName, true); // true for appending option
		BufferedWriter bw = new BufferedWriter(fw);
		PrintWriter pw = new PrintWriter(bw);
		//pw.println("<?xml version=\"1.0\"?>");
		//System.out.println("Merge DATA!!!!!!: \n");
		for (String line : real) {
			//System.out.println(line);
			pw.println(line);
		}
		pw.close();
		bw.close();
		fw.close();
	}
}