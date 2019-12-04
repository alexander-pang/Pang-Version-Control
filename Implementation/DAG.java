public class DAG<T extends Comparable<T>> {
    private Node<T> sentinel;
    public DAG() {
        this.sentinel = new Node<>();
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

    public void add(T parent , T new_Data){
        if (!new_Data.equals(parent)) {
            if (parent==null) {
                if (search(new_Data)) {
                    //print(find(new_Data));
                    return;
                }
                this.sentinel.newChild(new_Data);
            } else
                if (search(new_Data)) {
                    for (Node child: find(parent).getChildren()){
                        if (child.getElem().equals(new_Data)){
                            return;
                        }
                    }
                    find(parent).addChild(find(new_Data));
                    }
                else find(parent).newChild(new_Data);
        }
        //print(find(new_Data));
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
            if(DFS(data, child) != null){
                return child;
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
    public static void main(String [] args){
        DAG d = new DAG();
        d.add(null, 5);
        d.add(null, 5);
        d.add(null, "Dog");
        d.add(null, 6);
        d.add(5, 5);
        d.add(5, 6);
        d.add(5, "Cat");
        //System.out.println(d.find(5).getChildren());
        //d.print_children(5);
        //d.printFirstLevel();
        d.remove(6);
        //d.printFirstLevel();
    }
}
