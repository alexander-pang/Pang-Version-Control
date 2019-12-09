import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class driver {
    private DAG<String> D;
    private String currentVersion;
    //private String fileName;

    public driver(String[] args) throws FileNotFoundException, XMLStreamException {
        makeDag(args);
    }

    public DAG<String> getDag() {
        return this.D;
    }

    public void setCurrentVersion(String s) {
        this.currentVersion = s;
    }

    public String getCurrentVersion() {
        return this.currentVersion;
    }

    public static void main(String[] args) throws IOException, XMLStreamException, InterruptedException {
        driver dr = new driver(args);
        dr.setCurrentVersion(dr.getDag().getTail().getElem());
        dr.getDag().printGraphEdges();
        System.out.println("Current Version: " + dr.getCurrentVersion());
        dr.drive(args);
        System.out.println("Current Version: " + dr.getCurrentVersion());
    }

    public void makeDag(String[] args) throws FileNotFoundException, XMLStreamException {
        File file = new File(args[1]);
        boolean exists = file.exists();
        stax_parser sp = new stax_parser(args[1]);
        String root = args[1].split("\\.")[0];
        File xmlFile = new File(root + ".xml");
        this.D = sp.parser(xmlFile, args[1]);
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
            c.generate();
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
            this.setCurrentVersion(version);
            //call checkout with args
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

        if (currentVersion.split("\\.").length != 2) { // if it is not in main branch
            // Just diff temp and file
            String filename = args[1];
            String version = this.D.calcVersion(D.find(currentVersion));
            checkout c = new checkout(D, filename, currentVersion);
            c.generate();
            Runtime.getRuntime().exec("diff -u temp.txt " + filename + " > current.patch");
            write(version, filename);
            D.add(this.currentVersion, version);
            this.setCurrentVersion(version);

        } else {
            // System.out.println("running it");
            String filename = args[1];
            String version = this.D.calcVersion(D.find(currentVersion));
            checkout c = new checkout(D, filename, currentVersion);
            c.generate();
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
            Runtime.getRuntime().exec("diff -u temp.txt temp2.txt > current.patch");
            write(version, filename);
            D.add(this.currentVersion, version);
            this.setCurrentVersion(version);
        }
    }

    public void write(String version, String fileName) throws IOException {
        File q = new File(fileName);
        //Node cur = this.getDag().getSentinel();
        //f.delete();

        if (q.exists()){
            FileWriter fw = new FileWriter(fileName.split("\\.")[0] + ".xml", true); // true for appending option
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            pw.println("<?xml version=\"1.0\"?>");
            pw.println("<FileName name=\"" + fileName + "\">");
            pw.println("</FileName>");
            pw.close();
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

        L.add("<superSecretString Version = \"" + currentVersion + "\">" + data + "</superSecretString>");
        L.add(end);
        File f = new File("."+fileName);
        f.delete();
        FileWriter fw = new FileWriter("."+fileName, true); // true for appending option
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        //pw.println("<?xml version=\"1.0\"?>");
        for (String l : L){
            pw.println(l.trim());
        }
        pw.close();
        bw.close();
        fw.close();
    }
}
