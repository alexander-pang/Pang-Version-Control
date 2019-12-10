import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DAG<T extends Comparable<T>> {
    private Node<T> sentinel;
    public Node<T> tail;
    private String fileName;
    public String currentVersion;
    public DAG(String fileName) {
        this.sentinel = new Node<>();
        this.tail = sentinel;
        this.fileName = fileName;
        this.currentVersion = null;
    }

    public Node<T> getTail() { return this.tail; }
    public void setTail(Node<T> node) throws IOException {
        this.tail = node;
    }

    public Node<T> getSentinel(){
        return this.sentinel;
    }

    public void printFirstLevel(){
        System.out.println("Sentinel");
        for (Node<T> child: this.sentinel.getChildren()){
            System.out.println("Level 1:");
            System.out.println("Element: " + child.getElem());
            System.out.println("Parent: " + child.getParent().getElem());
            print_children(child);
            for (Node<T> c: child.getChildren()){
                System.out.println("Level 2:");
                System.out.println("Element: " + c.getElem());
                System.out.println("Parent: " + c.getParent().getElem());
                print_children(c);
            }
        }
    }
    public void print(Node<T> n){
        System.out.println("Element: " + n.getElem());
        System.out.println("Parent: " + n.getParent().getElem());
        print_children(n);
    }


    public void add(T parent , T newVersion) throws IOException {
        if (!newVersion.equals(parent)) {
            if (parent == null) {
                if (search(newVersion)) {
                    //print(find(newVersion));
                    return;
                }
                this.sentinel.newChild(newVersion);
                if (newVersion.toString().split("\\.").length == 2) {
                    this.setTail(find(newVersion));
                }
            } else {
                if (search(newVersion)) {
                    for (Node child : find(parent).getChildren()) {
                        if (child.getElem().equals(newVersion)) {
                            return;
                        }
                    }
                    find(parent).addChild(find(newVersion));
                } else {
                    find(parent).newChild(newVersion);
                    if (newVersion.toString().split("\\.").length == 2) {
                        this.setTail(find(newVersion));
                    }
                }
            }
            print(find(newVersion));
            if (find(parent) != null && find(parent).getChildren().size() > 1){
                find(newVersion).setIsBranch(true);
            }
        }
    }
    public String calcVersion(Node<T> current){
        if (current.getChildren().size() == 0){
            if(current.getIsBranch()){
                String s = (String)current.getElem();
                String version = s + ".1";
                return version;
            }else{
                String s = (String)current.getElem();
                //System.out.println(s);
                String[] v = s.split("\\.");
                //for (int i=0; i<v.length;i++) {
                //    System.out.println(v[i]);
                //}
                int ver = Integer.parseInt(v[v.length-1]);
                ver++;
                String ve = Integer.toString(ver);
                v[v.length-1] = ve;
                String version = String.join(".",v);
                return version;
            }
        }else if(current.getChildren().size() == 1){
            if(current.getIsBranch()){
                String s = (String)current.getElem();
                String[] v = s.split("\\.");
                int ver = Integer.parseInt(v[v.length-1]);
                ver++;
                String ve = Integer.toString(ver);
                v[v.length-1] = ve;
                String version = String.join(".",v);
                return version;
            }else{
                String s = (String)current.getElem();
                String version = s + ".1";
                return version;
            }
        }else if(current.getChildren().size() > 1){
            ArrayList<Node<T>> L = current.getChildren();
            String s = (String)L.get(L.size() - 1).getElem();
            String[] v = s.split("\\.");
            int ver = Integer.parseInt(v[v.length-1]);
            ver++;
            String ve = Integer.toString(ver);
            v[v.length - 1] = ve;
            String version = String.join(".",v);
            return version;
        }return (String)current.getElem();
    }
    public void remove(T data){
        if (search(data)){
            find(data).getParent().removeChild(data);
        }
        DFS_Remove(data, this.sentinel);
        return;
    }

    public void DFS_Remove(T data, Node<T> current){
        if (current != this.sentinel) {
            //System.out.println("Current: " + current.getElem());
            if (current.getElem() == null) {
                return;
            }
        }
        for (Node<T> child: current.getChildren()){
            if (child.getElem().equals(data)){
                current.removeChild(data);
            }
            else DFS_Remove(data, child);
        }
        return;
    }

    public boolean search(T data){
        if (this.sentinel.getChildren().size()==0){
            return false;
        }
        //System.out.println("\nSearch for: " + data);
        return DFS(data, this.sentinel)!=null;
    }

    public Node<T> DFS(T data, Node<T> current){
        if (current != this.sentinel) {
            //System.out.println("Current: " + current.getElem());
            if (current.getElem() == null) {
                return null;
            }
            if (current.getElem().equals(data)) {
                //System.out.println("Match!");
                return current;
            }
        }
        for (Node<T> child: current.getChildren()){

            Node<T> temp = DFS(data, child);
            if(temp != null ){
                return temp;
            }
        }
        return null;
    }

    public void print_children(Node<T> n){
        System.out.println("Children: ");
        for (Node<T> child: n.getChildren()) {
            System.out.println(child.getElem());
        }
        System.out.println();
    }

    protected Node<T> find(T data){
        if (this.sentinel.getChildren().size()==0){
            return null;
        }
        return DFS(data, this.sentinel);
    }

    //The method returns an arrayList of edges as Pair<T,T>.
    // Note that when the sentinel node is the parent, it returns Pair<null,T>

    public ArrayList<Pair<T,T>> getEdgesArray() {

        //// In here I assume that getChildren() will return ArrayList. If you implementation
        ///     returns a linkedList, you need to change ArrayList in the next line to LinkedList;
        ////     and if your getChildren() returns other types of List, just change it to that
        ArrayList<Node<T>> graph_nodes = new ArrayList<>();
        graph_nodes.addAll(this.sentinel.getChildren());

        for (int i=0; i<graph_nodes.size(); i++) {
            if(graph_nodes.get(i).getChildren().size()!=0)
                graph_nodes.addAll(graph_nodes.get(i).getChildren());
        }

        //Add the nodes to a set to eliminate duplications
        Set<Node<T>> graph_set = new HashSet<Node<T>>();
        for (Node<T> someNode: graph_nodes){
            graph_set.add( someNode);
        }

        //now, visit the nodes in the graph_set and add their edges to the array list edgeArray
        ArrayList<Pair<T,T>> edgeArray = new ArrayList<>();
        for(Node<T> n: graph_set) {
            if(n.getParent() == this.sentinel)
                edgeArray.add(new Pair( n.getElem(), null));//adding parent edge
            else edgeArray.add(new Pair( n.getElem(), n.getParent().getElem()));
            for(Node<T> c: n.getChildren())
                edgeArray.add(new Pair(n.getElem(), c.getElem()));
        }

        return edgeArray;
    }

    ///////////////////////////////////// CODE TO PRINT EDGE OF THE GRAPH ///////////////////////////////////

    public void printGraphEdges() {

        if(this.sentinel.getChildren().size() == 0)
            return;

        ArrayList<Node<T>> graph_nodes = new ArrayList<>();
        graph_nodes.addAll(this.sentinel.getChildren());

        for (int i=0; i<graph_nodes.size(); i++) {
            if(graph_nodes.get(i).getChildren().size()!=0)
                graph_nodes.addAll(graph_nodes.get(i).getChildren());
        }

        //Add the nodes to a set to eliminate duplications
        Set<Node<T>> graph_set = new HashSet<Node<T>>();
        for (Node<T> someNode: graph_nodes){
            graph_set.add( someNode);
        }

        //Start printing edges: (x , y) means the edge direction is from x --> y.
        for(Node<T> someNode: graph_set) {
            //print edge to parent edge first
            if(someNode.getParent() == this.sentinel) {
                System.out.println("PARENT of "+someNode.getElem().toString()+" is  root/sentinel");
            }
            else System.out.println("PARENT of "+ someNode.getElem().toString() +" is "+ someNode.getParent().getElem().toString() );
            //print children edges
            for(Node<T> c: someNode.getChildren()) {
                System.out.println("("+someNode.getElem().toString()+", "+c.getElem().toString()+")");
            }
        }
    }
    ///////////////////////////////////////////////////////////////////////////////


   /* public static void main(String [] args){
        DAG d = new DAG();
        d.add(null, "1.2");
        d.add("1.2","1.3");
        d.add("1.2","1.2.1");
        d.add("1.2.1","1.2.1.1");
        //d.add("1.1","1.1.1");

        //d.add("1.1","1.2");
        //d.add("1.1","1.1.2");
        Node test = d.find("1.2.1.1");
        //test.setIsBranch(true);
        //ArrayList<Node> W = test.getChildren();
        //Node chil = W.get(W.size() - 1);
        //chil.setIsBranch(true);
        String s = d.calcVersion(test);
        System.out.println(s);
        //DAG d = new DAG();
        //d.add(null, 5);
        ////   d.add(null, 5);
        //d.add(null, "Dog");
        //d.add(5, 6);
        //d.add(5, 4);
        //d.add(5, 6);
        //  d.add(5, "Cat");
        //d.add(6, 7);
        //d.add(4, 8);
        //d.printGraphEdges();

        //d.remove(6);
        //d.printGraphEdges();

       *//*  DAG d = new DAG();
        d.add(null, 5);
        //d.add(null, 5);
        // d.add(null, "Dog");
        d.add(null, 6);
        d.add(5, 4);
        d.add(4, 6);
        d.printGraphEdges();
        *//*
        //   d.add(5, "Cat");
        //System.out.println(d.find(5).getChildren());
        //d.print_children(5);
        //d.printFirstLevel();
        //   d.remove(6);
        //d.printFirstLevel();
    }*/
}

