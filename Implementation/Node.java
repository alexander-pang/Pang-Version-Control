import java.util.ArrayList;

public class Node<T extends Comparable<T>> {
    private T elem;
    private ArrayList<Node<T>> children;
    private Node<T> parent;
    private Boolean isBranch;
    private String brName;
    private String message;

    public Node() {
        this.elem = null;
        this.children = new ArrayList<>();
        this.parent = null;
        this.isBranch = false;
        this.brName = null;
        this.message = null;

    }

    public Node(T elem, Node<T> parent) {
        this.elem = elem;
        this.children = new ArrayList<>();
        this.parent = parent;
        this.isBranch = false;
        this.brName = null;
        this.message = null;
    }

    ///// DO NOT EDIT THE ABOVE.

    //You are free to add more methods below.....
    public Node(Node<T> n) {
        this.elem = n.getElem();
        this.children = n.getChildren();
        this.parent = n.getParent();
        this.isBranch = n.getIsBranch();
        this.brName = n.getBrName();
        this.message = n.message;
    }

    public ArrayList<Node<T>> getChildren() {
        return this.children;
    }

    public Node<T> getParent(){
        return this.parent;
    }
    public void setParent(Node<T> parent){
        this.parent=parent;
    }

    public T getElem(){
        return this.elem;
    }

    public Boolean getIsBranch(){return this.isBranch;}

    public void setIsBranch(Boolean isBranch){ this.isBranch = isBranch; }

    public String getBrName() { return this.brName; }
    public void setBrName(String name) { this.brName = name; }

    public String getMessage() { return this.message; }
    public void setMessage(String m) { this.message = m; }

    public void newChild(T new_Data){
        Node<T> new_Node = new Node<>(new_Data, this);
        System.out.println(new_Node);
        this.children.add(new_Node);
    }
    public void addChild(Node<T> child){
        this.children.add(child);
    }

    public void removeChild(T data){
        for (Node<T> child:this.getChildren()){
            if (data.equals(child.getElem())){
                child.setParent(null);
                this.children.remove(child);
                System.out.println("Removed: " + child.getElem());
                return;
            }
        }
        System.out.println("Not found");
    }

   /* @Override
    public int hashCode(){
        if (this.elem == null){
            return 0;
        }
        if (this.elem == (Integer)this.elem){
            (String.valueOf(this.elem).length();
        }
        return ((String)this.elem).length();
    } */
    @Override
	public boolean equals(Object obj) {
        /*if (this.hashCode() != obj.hashCode()){
            return false;
        }*/
		if (obj.getClass() != this.getClass()){
		    return false;
        }
        Node<T> n = (Node<T>) obj;
        return this.elem.equals(n.elem);

	}

}