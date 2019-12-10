import javax.xml.stream.XMLStreamException;
import java.io.*;
import java.nio.channels.FileChannel;

public class checkout {
    private String fileName;
    public String version;
    private DAG D;
    public Node point;

    public checkout(DAG D, String fileName, String version) throws IOException {
        this.fileName = fileName;
        this.version = version;
        this.D = D;
        point = this.D.getTail();
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        //Runtime.getRuntime().exec("cp " + fileName + "_last.txt " + "temp.txt").waitFor();
        File source = new File(fileName.split("\\.")[0] + "_last." + fileName.split("\\.")[1]);
        File dest = new File("temp.txt");
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;
        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }finally{
            sourceChannel.close();
            destChannel.close();
        }
    }

    /*public static void main(String[] args) throws InterruptedException, XMLStreamException, IOException {
        DAG<String> D = new DAG<>();
        D.add(D.getSentinel().getElem(),"first");
        D.add("first", "ffff");
        D.add("ffff", "asdf");
        D.add("asdf", "last");
        D.setTail(D.find("last"));
        checkout c = new checkout(D, "file.txt", "ffff");
        c.generate();
    }*/


    public void generate() throws IOException, XMLStreamException {
            // Runs the command line to overwrite file with latest (fileName_last.txt)
            //System.out.println("Run command");
            //System.setProperty("user.dir", System.getProperty("user.dir") + "/Implementation");
        //System.out.println("Point: " + point.getElem() + " - Target: " + version );
            //BufferedReader br = new BufferedReader(new FileReader(fileName.split("\\.")[0] + "_last." + fileName.split("\\.")[1]));
            //String line;
            /*System.out.println("\nNow printing " + fileName.split("\\.")[0] + "_last." + fileName.split("\\.")[1]);
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
            BufferedReader c = new BufferedReader(new FileReader("temp.txt"));
            System.out.println("\nNow printing temp.txt: ");
            while ((line = c.readLine()) != null) {
                System.out.println(line);
            }
            BufferedReader z = new BufferedReader(new FileReader("current.patch"));
            System.out.println("\nNow printing current.patch: ");
            while ((line = z.readLine()) != null) {
                System.out.println(line);
            }*/
            System.out.println("\nPoint: " + point.getElem());
            // parser.getData() is the patch file for the version
            String toVersion = (String)point.getParent().getElem();
        if (point.getChildren().size() > 1) {
            for (Object ch : point.getChildren()) {
                Node child = (Node) ch;
                if (D.DFS(version, child) != null) {
                    System.out.println("found version: " + D.DFS(version,child).getElem());
                    System.out.println("setting toversion: " + child.getElem());
                    toVersion = (String)child.getElem();
                    System.out.println("to version"+toVersion);
                    //return;
                }
            }
            point = point.getParent();
        } else if (point.getElem().toString().split("\\.").length > 2){
            if(point.getChildren().size() == 1) {
                Node temp = (Node) point.getChildren().get(0);
                toVersion = (String)temp.getElem();
                //System.out.println("Set not main point = " + point.getElem() );
            }
        }
        patchParser parser = new patchParser(fileName, toVersion);
            try {
                // Write the patch data to temp patch file
                FileWriter fw = new FileWriter("current.patch");
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter pw = new PrintWriter(bw);
                //System.out.println("\n The new data: \n" + parser.getData());
                pw.print(parser.getData());
                pw.flush();

                pw.close();
                bw.close();
                fw.close();

                // Command line to apply patches then change point to next node
                //Process p =
                System.out.println("Applying patch: " + toVersion + " now on temp.txt...");
                //Runtime.getRuntime().exec("patch temp.txt current.patch").waitFor();
                Runtime r = Runtime.getRuntime();
                Process p = r.exec("patch temp.txt current.patch  "); // Here we execute the command
                p.waitFor();

                /*if (point.getChildren().size() > 1) {
                    for (Object ch : point.getChildren()) {
                        Node child = (Node) ch;
                        if (D.DFS(version, child) != null) {
                            System.out.println("seting point to child: " + child.getElem());
                            point = child;
                            //System.out.println("Set child point = " + point.getElem());
                            return;
                        }
                    }
                    point = point.getParent();
                } else if (point.getElem().toString().split("\\.").length > 2){
                    if(point.getChildren().size() == 1) {
                        point = (Node) point.getChildren().get(0);
                        System.out.println("Set not main point = " + point.getElem() );
                    }
                    else if (point.getChildren().size() == 0){
                        return;
                    }

                } else {
                    point = point.getParent();
                    System.out.println("Set main point = " + point.getElem() );
                }*/
                point = D.find(toVersion);

            } catch (Exception e) {
                System.out.println(e);
            }

            System.out.println("Successful patch writes...");

            /*try {
                // attach a file to FileWriter
                FileWriter fw = new FileWriter(fileName);
                String str = "File Handling in Java using " +
                        " FileWriter and FileReader";
                fw.write(str);
                //close the file
                fw.close();
            }catch(Exception e){System.out.println(e);}
            System.out.println("Success...");*/



    }

    // write the content in file
    // Accept a string




}
