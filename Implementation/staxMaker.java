import java.io.*;

public class staxMaker {
    private String file;
    private DAG D;

    public static void main(String[] args) throws IOException {
        DAG<String> D = new DAG<>();
        D.add(D.getSentinel().getElem(),"1.1");
        D.add("1.1", "1.2");
        D.add("1.2", "1.3");
        staxMaker s = new staxMaker("test", D);
        s.write();
    }

    public staxMaker(String file, DAG D) throws IOException {
        this.file = file;
        this.D = D;
    }

    public void write() throws IOException {
        File f = new File(this.file.split("\\.")[0] + ".xml");
        Node cur = this.D.getSentinel();
        f.delete();
        FileWriter fw = new FileWriter(this.file.split("\\.")[0] + ".xml", true); // true for appending option
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        pw.println("<?xml version=\"1.0\"?>");
        pw.println("<FileName name=\"" + this.file + "\">");
        while (cur.getChildren().size() > 0) {
            Node next = (Node)cur.getChildren().get(0); // will be main branch child
            pw.println("<Node Version =\"" + next.getElem() + "\">");
            pw.println("<Parent>" + next.getParent().getElem() + "</Parent>");
            pw.println("<Children>");
            for (Object child:next.getChildren()){
                Node c = (Node)child;
                pw.println("<Child>" + c.getElem() + "</Child>");
                System.out.println("CHILD: " + c.getElem());
                System.out.println("Its parent: " + c.getParent().getElem());
            }
            pw.println("</Children>");
            pw.println("</Node>");
            System.out.println(next.getElem() + " successfully appended into file");
            pw.flush();
            cur = next;
        }
        pw.println("</FileName>");
        pw.close();
        bw.close();
        fw.close();
    }
}
