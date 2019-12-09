
//This program shows how to execute a command using your operating system, like Windows or Mac. 
// Notice that in this program when the program execute the command, we catch the output 
//    on the command line and save it (BufferedReader b). You can use that to print the output 
//   on java terminal, or you might write the data to a file, like a patch file, or you might
//   write some of it to an XML file. This is just a general framework for you to use.
// 
// This program is edited and taken from: https://stackoverflow.com/questions/792024/how-to-execute-system-commands-linux-bsd-using-java

// I am executing a diff command, print the output, and save the output of diff in string called output and write it to a file.

import java.io.*;

public class commands {

	public static void main(String args[]) throws InterruptedException, IOException {
		Runtime r = Runtime.getRuntime();
		//You might need to change -u option for Windows
		Process p = r.exec("diff -u a.txt b.txt  "); // Here we execute the command
		p.waitFor();
		BufferedReader b = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		String output = "";// Would like to grap all the lines and save them in a single string called
							// output.
		while ((line = b.readLine()) != null) {
			System.out.println(line);
			output = output + line + "\n";
		}
		b.close();

		// Here we write the string containing all the output appeared on the terminal to a file called c.patch

		BufferedWriter writer = null;
		writer = new BufferedWriter(new FileWriter("c.patch"));
		writer.write(output);

		writer.close();

	}

}
