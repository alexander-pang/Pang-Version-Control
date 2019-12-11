import java.io.*;

public class staxMaker {
    private String file;
    private DAG D;

    /*public static void main(String[] args) throws IOException {
        DAG<String> D = new DAG<>();
        D.add(D.getSentinel().getElem(),"1.1");
        D.add("1.1", "1.2");
        D.add("1.2", "1.3");
        D.add("1.2","1.2.1");
        staxMaker s = new staxMaker("test", D);
        s.write();
    }*/

    public staxMaker(String file, DAG D) throws IOException {
        this.file = file;
        this.D = D;
    }
    public void dfsWrite(Node version) throws IOException {
        FileWriter fw = new FileWriter(this.file.split("\\.")[0] + ".xml", true); // true for appending option
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("<Node Version =\"" + version.getElem() + "\">");
        pw.println("<Parent>" + version.getParent().getElem() + "</Parent>");
        String branch = "F";
        if (version.getIsBranch()) {
            branch = "T";
        }
        pw.println("<isBranch>" + branch + "</isBranch>");
        pw.println("<Children>");
        if (version.getChildren().size() >0) {
            for (Object child : version.getChildren()) {
                Node c = (Node) child;
                pw.println("<Child>" + c.getElem() + "</Child>");
                System.out.println("SELF: " + version.getElem());
                System.out.println("CHILD: " + c.getElem());
                System.out.println("Its parent: " + c.getParent().getElem());
            }
        }
        pw.println("</Children>");
        pw.println("</Node>");
        pw.close();
        bw.close();
        fw.close();
        for (Object child : version.getChildren()) {
            dfsWrite((Node) child);
        }
    }
    public void write() throws IOException {
        File f = new File(this.file.split("\\.")[0] + ".xml");
        Node cur = this.D.find("1.1");
        f.delete();
        FileWriter fw = new FileWriter(this.file.split("\\.")[0] + ".xml", true); // true for appending option
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("<?xml version=\"1.0\"?>");
        pw.println("<FileName name=\"" + this.file + "\">");
        pw.println("<current Version=\"" + this.D.currentVersion + "\"></current>");
        pw.close();
        bw.close();
        fw.close();
        dfsWrite(cur);
        fw = new FileWriter(this.file.split("\\.")[0] + ".xml", true); // true for appending option
        bw = new BufferedWriter(fw);
        pw = new PrintWriter(bw);
        System.out.println(cur.getElem() + " successfully appended into file");
        pw.flush();
        cur = cur;
        pw.println("</FileName>");
        pw.close();
        bw.close();
        fw.close();

    }}

