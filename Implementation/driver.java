import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.SplittableRandom;

public class driver {
    private DAG<String> D;
    //private String currentVersion;
    //private String fileName;

    public driver(String[] args) throws IOException, XMLStreamException {
        makeDag(args);
    }

    public DAG<String> getDag() {
        return this.D;
    }

    public void setCurrentVersion(String s) {
        this.D.currentVersion = s;
    }

    public String getCurrentVersion() {
        return this.D.currentVersion;
    }

    public static void main(String[] args) throws IOException, XMLStreamException, InterruptedException {
        //DAG dig = new DAG();
        //System.out.println("This is empty");
        //dig.printGraphEdges();
        //System.out.println("end...");
        driver dr = new driver(args);
        /*System.out.println("*******");
        dr.getDag().printGraphEdges();
        System.out.println("*******");*/

        //dr.setCurrentVersion(dr.getDag().getTail().getElem());
        System.out.println("Current: " + dr.getDag().currentVersion);
        dr.getDag().printGraphEdges();
        System.out.println("Current Version: " + dr.getCurrentVersion());
        dr.drive(args);
        System.out.println("Current Version: " + dr.getCurrentVersion());
    }

    public void makeDag(String[] args) throws IOException, XMLStreamException {
        //File file = new File(args[1]);
        String root = args[1].split("\\.")[0];
        File xmlFile = new File(root + ".xml");
        if (xmlFile.exists()) {
            System.out.println("YES!!!");
            stax_parser sp = new stax_parser(args[1]);
            this.D = sp.parser(xmlFile, args[1]);
        }
        else this.D = new DAG<>(args[1]);
        System.out.println("NO!!!");
    }

    public void drive(String[] args) throws InterruptedException, XMLStreamException, IOException {
        System.out.println(args[0]);
        if (args[0].equals("commit")) {
            //call commit
            commit(args);
        }
        if (args[0].equals("checkout")) {
            System.out.println("running it");
            String filename = args[1];
            String version = args[2];
            checkout c = new checkout(D, filename, version);
            System.out.println("Point: " + c.point.getElem() + " - Target: " + version );
            while(!c.point.getElem().equals(version)) {
                c.generate();
                System.out.println("Point: " + c.point.getElem() + " - Target: " + version );
            }
            File source = new File("temp.txt");
            File dest = new File(filename);
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
            //call checkout with args
            this.D.currentVersion = version;
        }
        if (args[0].equals("merge")) {
            Object filename = args[1];
            Object version = args[2];
            //call merge with args
        }
        String filename = args[1];
        staxMaker s = new staxMaker(filename,this.D);
        s.write();


    }

    public void commit(String[] args) throws IOException, XMLStreamException, InterruptedException {
        System.out.println(D.currentVersion);
        if (D.currentVersion == null){
            // System.out.println("running it");
            String filename = args[1];
            String version = "1.1";
            D.add(null, version);
            FileWriter fw = new FileWriter("temp.txt");
            fw.close();
            File source = new File("temp.txt");
            File dest = new File("temp2.txt");
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
            source = new File(filename);
            dest = new File("temp.txt");
            sourceChannel = null;
            destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
            //this.setCurrentVersion(version);
            Runtime r = Runtime.getRuntime();
            //You might need to change -u option for Windows
            Process p = r.exec("diff -u temp.txt temp2.txt  "); // Here we execute the command
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
            writer = new BufferedWriter(new FileWriter("current.patch"));
            writer.write(output);

            writer.close();
            write(D.currentVersion, filename);
            this.setCurrentVersion(version);
        }
        else {
            String version = this.D.calcVersion(D.find(D.currentVersion));
            if (version.split("\\.").length != 2) { // if it is not in main branch
                // Just diff temp and file
                String filename = args[1];
                checkout c = new checkout(D, filename, D.currentVersion);
                while(!c.point.getElem().equals(D.currentVersion)) {
                    System.out.println("Point: " + c.point.getElem() + " - Target: " + version );
                    c.generate();
                }
                Runtime r = Runtime.getRuntime();
                //You might need to change -u option for Windows
                Process p = r.exec("diff -u temp.txt " + filename); // Here we execute the command
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
                writer = new BufferedWriter(new FileWriter("current.patch"));
                writer.write(output);
                System.out.println(version + " " + D.currentVersion);
                writer.close();
                write(version, filename);
                D.add(D.currentVersion, version);
                this.setCurrentVersion(version);

            } else {
                // System.out.println("running it");
                String filename = args[1];
                checkout c = new checkout(D, filename, D.currentVersion);
                while(!c.point.getElem().equals(D.currentVersion)) {
                    System.out.println("Point: " + c.point.getElem() + " - Target: " + D.currentVersion );
                    c.generate();
                }
                File source = new File("temp.txt");
                File dest = new File("temp2.txt");
                FileChannel sourceChannel = null;
                FileChannel destChannel = null;
                try {
                    sourceChannel = new FileInputStream(source).getChannel();
                    destChannel = new FileOutputStream(dest).getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                } finally {
                    sourceChannel.close();
                    destChannel.close();
                }
                source = new File(filename);
                dest = new File("temp.txt");
                sourceChannel = null;
                destChannel = null;
                try {
                    sourceChannel = new FileInputStream(source).getChannel();
                    destChannel = new FileOutputStream(dest).getChannel();
                    destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
                } finally {
                    sourceChannel.close();
                    destChannel.close();
                }
                //this.setCurrentVersion(version);
            if (D.find(getCurrentVersion()).getChildren().size() == 0) {
                    Runtime r = Runtime.getRuntime();
                    //You might need to change -u option for Windows
                    Process p = r.exec("diff -u temp.txt temp2.txt  "); // Here we execute the command
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
                    //String version = this.D.calcVersion(D.find(D.currentVersion));
                    System.out.println(version + " " + D.currentVersion);
                    BufferedWriter writer = null;
                    writer = new BufferedWriter(new FileWriter("current.patch"));
                    writer.write(output);
                    writer.close();

                }
                write(D.currentVersion, filename);
                D.add(D.currentVersion, version);
                this.setCurrentVersion(version);
            }
        }
        String version = this.D.calcVersion(D.find(D.currentVersion));
        System.out.println(version + " " + D.currentVersion);
        System.out.println("VERSION SPLIT: " + version.split("\\.").length);
        if (version.split("\\.").length== 2){
            File source = new File(args[1]);
            File dest = new File(args[1].split("\\.")[0] + "_last." + args[1].split("\\.")[1]);
            FileChannel sourceChannel = null;
            FileChannel destChannel = null;
            try {
                sourceChannel = new FileInputStream(source).getChannel();
                destChannel = new FileOutputStream(dest).getChannel();
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } finally {
                sourceChannel.close();
                destChannel.close();
            }
            //this.getDag().setTail(this.getDag().find(version));
        }

    }

    public void write(String version, String fileName) throws IOException {
        File q = new File("."+fileName);
        //Node cur = this.getDag().getSentinel();
        //f.delete();
        //System.out.println(q);
        //System.out.println(q.exists());
        if (!q.exists()){
            //System.out.println("guefsbhdfkjnefsbhdkjfn");
            FileWriter fw = new FileWriter("."+fileName); // true for appending option
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("<?xml version=\"1.0\"?>");
            pw.println("<FileName name=\"" + fileName + "\">");
            pw.println("</FileName>");
            pw.close();
            bw.close();
            fw.close();
            return;
        }

        BufferedReader br = new BufferedReader(new FileReader("."+fileName));
        String data = "";
        ArrayList<String> L = new ArrayList<>();
        String line;
        //System.out.println("\nNow printing ");
        while ((line = br.readLine()) != null) {
            L.add(line);
            //System.out.println(line);
        }
        br.close();
        String end = L.get(L.size()-1);
        L.remove(L.size()-1);

        br = new BufferedReader(new FileReader("current.patch"));
        while ((line = br.readLine()) != null) {
            data += line+"\n";
            //System.out.println(line);
        }
        br.close();

        L.add("<superSecretString Version = \"" + version + "\">" + data + "</superSecretString>");
        L.add(end);
        File f = new File("."+fileName);
        f.delete();
        FileWriter fw = new FileWriter("."+fileName, true); // true for appending option
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        //pw.println("<?xml version=\"1.0\"?>");
        for (String l : L){
            pw.println(l);
        }
        pw.close();
        bw.close();
        fw.close();
    }
}
