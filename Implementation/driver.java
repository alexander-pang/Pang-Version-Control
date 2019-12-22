import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.SplittableRandom;

public class driver {
    private DAG<String> D;
    //private String currentVersion;
    //private String fileName;
    public driver(){ }
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
        System.out.println("Command line argument number is: "+args.length);
        System.out.println("Command line arguments are:");
        String[] sArray = new String[6];

        for(int i = 0;i<args.length;i++) {
            if (i == 0){
                if (args[0].equals("help")) {
                    driver d = new driver();
                    d.printCommands();
                    return;
                }
                sArray[0] = args[0];
            } else if (args[i].equals("-rev")) {
                i++;
                if (sArray[2] == null) {
                    sArray[2] = args[i];
                } else {
                    sArray[3] = args[i];
                }

            } else if (args[i].equals("-br")){
                i++;
                //String s = dr.findVersion(args[i]);
                if (sArray[2] == null) {
                    sArray[2] = args[i];
                } else {
                    sArray[3] = args[i];
                }

            } else if (args[i].equals("-f")) {
                i++;
                sArray[1] = args[i];
            } else if (args[i].equals("-m")) {
                i++;
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                String str = String.format("Message: ");
                System.out.println(str.trim());
                String input = in.readLine();
                sArray[4] = input;
                System.out.println(input);
            } else if (args[i].equals("-list")){
                i++;
                sArray[1] = args[i];
            }
            //System.out.println(args[i]);
        }
        for(int i = 0; i<sArray.length;i++){
            System.out.println(sArray[i]);
        }

        //DAG dig = new DAG();
        //System.out.println("This is empty");
        //dig.printGraphEdges();
        //System.out.println("end...");
        driver dr = new driver(sArray);

        /*System.out.println("*******");
        dr.getDag().printGraphEdges();
        System.out.println("*******");*/

        //dr.setCurrentVersion(dr.getDag().getTail().getElem());
        System.out.println("Current: " + dr.getDag().currentVersion);
        dr.getDag().printGraphEdges();
        System.out.println("Current Version: " + dr.getCurrentVersion());
        dr.drive(sArray);
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
    public void printDiff(String args) throws IOException, InterruptedException {
        String root = args.split("\\.")[0];
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("diff -u " + args + " " + root + "_last.txt " ); // Here we execute the command
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
    }

    public void drive(String[] args) throws InterruptedException, XMLStreamException, IOException {
        System.out.println(args[0]);
        if (args[0].equals("commit")) {
            commit(args);
        }
        if (args[0].equals("diff")){
            printDiff(args[1]);
        }
        if(args[0].equals("branch")){
            D.printBranches(D.getSentinel().getChildren().get(0));
        }
        if (args[0].equals("checkout")) {
            checkOut(args);
        }
        if (args[0].equals("merge")) {
            merge(args);
        }
        if (args[0].equals("rename")) {
            rename(args);
        }

        String filename = args[1];
        D.printGraphEdges();
        staxMaker s = new staxMaker(filename,this.D);
        s.write();

    }

    public void printCommands(){
        System.out.println("Key: -f = filename : -m = option if commit message is wanted : -rev = revision number : -br = branch name : () = optional");
        System.out.println("\nCommiting: java -jar oosd-group-project.jar commit -f fileName (-br branchName) (-m)");
        System.out.println("\nCheckout: java -jar oosd-group-project.jar checkout [-rev revisionNum | -br branchName] -f fileName");
        System.out.println("\nMerge: java -jar oosd-group-project.jar merge f file [-rev revision | -br branchName] [-rev revision | -br branchName]  (-m)");
        System.out.println("\nRename: java -jar oosd-group-project.jar rename -f fileName [-br oldBranchName | -rev revisionNum] -br newBranchName");
        System.out.println("\nPrint Diff: java -jar oosd-group-project.jar diff -f fileName");
        System.out.println("\nPrint Branches: java -jar oosd-group-project.jar branch -list fileName");
    }

    public void commit(String[] args) throws IOException, XMLStreamException, InterruptedException {
        System.out.println(D.currentVersion);
        System.out.println(args.toString());
        if (D.currentVersion == null){
            // System.out.println("running it");
            String filename = args[1];
            System.out.println(filename);
            String version = "1.1";
            D.add(null, version);
            // set the commit message
            if (args[4] != null) {
                D.find(version).setMessage(args[4]);
            }
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
            D.currentVersion = version;
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
                // set the commit message
                if (args[4] != null) {
                    D.find(version).setMessage(args[4]);
                }
                this.setCurrentVersion(version);

            } else {
                System.out.println("Adding a new tail");
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
                // set the commit message
                if (args[4] != null) {
                    D.find(version).setMessage(args[4]);
                }
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

        // set the branch name
        // if our parameter was branch name
        System.out.println(args[2]);
        if (!(D.search(args[2]))) {
            D.find(D.currentVersion).setBrName(args[2]);
            System.out.println("This is our branch name~~~~~~~~~~~~");
            System.out.println(D.find(D.currentVersion).getBrName());
        }

    }

    public void checkOut(String[] args) throws IOException, XMLStreamException {
        System.out.println("running it");
        String filename = args[1];
        String version = args[2];
        if ((!D.search(args[2])) && ((D.findWBr(args[2],D.getSentinel()) != null))) {
            //System.out.println(D.findWBr(args[2]).getElem());
            version = (String) D.findWBr(args[2],D.getSentinel()).getElem();
        }
        System.out.println("Checkout version -- should be revision number!!!!!!");
        System.out.println(version);
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

    public void merge(String args[]) throws IOException, XMLStreamException, InterruptedException {
        String file = args[1];
        String version1 = args[2];
        if ((!D.search(args[2])) && ((D.findWBr(args[2],D.getSentinel()) != null))) {
            //System.out.println(D.findWBr(args[2]).getElem());
            version1 = (String) D.findWBr(args[2],D.getSentinel()).getElem();
        }
        String version2 = args[3];
        if ((!D.search(args[3])) && ((D.findWBr(args[3],D.getSentinel()) != null))) {
            //System.out.println(D.findWBr(args[2]).getElem());
            version2 = (String) D.findWBr(args[3],D.getSentinel()).getElem();
        }
        System.out.println("finding: " + version1);
        checkout c = new checkout(D, file, version1);
        System.out.println("Point: " + c.point.getElem() + " - Target: " + version1 );
        while(!c.point.getElem().equals(version1)) {
            c.generate();
            System.out.println("Point: " + c.point.getElem() + " - Target: " + version1 );
        }            File source = new File("temp.txt");
        File dest = new File("a.txt");
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
        System.out.println("finding" + version2);

        checkout e = new checkout(D, file, version2);
        System.out.println("Point: " + e.point.getElem() + " - Target: " + version2 );
        while(!e.point.getElem().equals(version2)) {
            e.generate();
            System.out.println("Point: " + e.point.getElem() + " - Target: " + version2 );
        }            source = new File("temp.txt");
        dest = new File("b.txt");
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
        Runtime r = Runtime.getRuntime();
        Process p = r.exec("sdiff -l -w 400 a.txt b.txt  "); // Here we execute the command
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
        writer = new BufferedWriter(new FileWriter("c.txt"));
        writer.write(output);
        writer.close();
        new merge(args[1]);
        String mergeTo;
        String other;
        if (version1.split("\\.").length < version2.split("\\.").length){
            mergeTo = version1;
            other = version2;
        }else{
            mergeTo = version2;
            other = version1;
        }
        //String cur = D.currentVersion;
        D.currentVersion = mergeTo;
        commit(args);
        //D.currentVersion = cur;
        D.find(other).addChild(D.find(D.currentVersion));
        Node something = D.find(other);
        //D.add(other, D.currentVersion);
        //call merge with args
    }

    public void rename(String[] args) {
        String version = args[2];
        if ((!D.search(args[2])) && ((D.findWBr(args[2],D.getSentinel()) != null))) {
            //System.out.println(D.findWBr(args[2]).getElem());
            version = (String) D.findWBr(args[2],D.getSentinel()).getElem();
        }
        System.out.println("Checkout version -- should be revision number!!!!!!");
        System.out.println(version);

        if (D.findWBr(args[3],D.getSentinel()) != null) {
            System.out.println("Newname, " + args[3] + ", already exists. Cannot rename with this name.");
        } else if (!(D.search(version))) {
            System.out.println(args[2]+ " doesn't exist. Cannot rename nonexistent one.");
        } else {
            D.find(version).setBrName(args[3]);
            System.out.println("This is new branch name.");
            System.out.println(D.find(version).getBrName());
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
