public class Commit {


    // I think combining all methods together later would be better
    // because we don't need to call DAG revisionTree all the time then
    // but I will suppose we are not at this moment

    public DAG reviTree;    // revision tree
    public Node currFile;   // current working file

   public void commit(DAG reviTree, Node currFile) {
       if (currFile.getChildren().size() !=0 ) {
           branch(reviTree, currFile);
       }
   }

   public void branch(DAG reviTree, Node currFile) {
       //currFile.revisionNumber.setNextRN();
   }

}
