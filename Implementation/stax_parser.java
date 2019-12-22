// Java Code to implement StAX parser
// From: https://www.geeksforgeeks.org/stax-xml-parser-java/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.util.Iterator;
//import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

public class stax_parser {

    private static boolean fileName, current, node, brname, parent, child, children, message;
    private String data, file, version;
    private String cur_Par,cur_Ver,cur_Mes,cur_BrN = "";

    public stax_parser(String name) throws FileNotFoundException, XMLStreamException {
        //System.out.println("VERSION: " + version);
        // Function for accessing the data
        this.data = "";
        this.setFile(name);

    }

    /*public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        stax_parser p = new stax_parser("file1.txt");
        System.out.println("FINAL RESULT: \n" + p.getData());
    }*/

    public String getData() {
        return this.data;
    }
    public void setData(String d) {
        this.data = d;
    }
    public void addData(String s){this.data += s + "\n";}

    public String getFile(){return this.file;}
    public void setFile(String name){this.file = name;}

    public String getVersion(){return this.version;}
    public void setVersion(String name){this.version = name;}

    public DAG parser(File file, String name) throws IOException, XMLStreamException {
        DAG<String> D = new DAG<>(name);
        D.add(null, "1.1");
        // Variables to make sure whether a element
        // in the xml is being accessed or not
        // if false that means elements is
        // not been used currently , if true the element or the
        // tag is being used currently
        fileName = node = brname =parent = child = children = current = message = false;

        // Instance of the class which helps on reading tags
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // Initializing the handler to access the tags in the XML file
        XMLEventReader eventReader =
                factory.createXMLEventReader(new FileReader(file));

        // Checking the availabilty of the next tag
        while (eventReader.hasNext()) {
            // Event is actually the tag . It is of 3 types
            // <name> = StartEvent
            // </name> = EndEvent
            // data between the StartEvent and the EndEvent
            // which is Characters Event
            XMLEvent event = eventReader.nextEvent();

            // This will trigger when the tag is of type <...>
            if (event.isStartElement()) {
                StartElement element = (StartElement) event;

                // Iterator for accessing the metadeta related
                // the tag started.
                // Here, it would name of the company
                /*Iterator<Attribute> iterator = element.getAttributes();
                //System.out.println("Starting a new interator!");
                while (iterator.hasNext())
                {
                    Attribute attribute = iterator.next();
                    QName n = attribute.getName();
                    String value = attribute.getValue();
                    System.out.println(n+" = " + value);
                    if (value.equals(name)){
                        System.out.println("HEY! I FOUND " + value);
                    }
                }*/

                // Checking which tag needs to be opened for reading.
                // If the tag matches then the boolean of that tag
                // is set to be true.

                if (!element.getName().toString().equalsIgnoreCase("start") && element.getAttributes().hasNext()) {
                    String attribute = element.getAttributes().next().getValue();
                    if (element.getName().toString().equalsIgnoreCase("FileName") && attribute.equals(name)) {
                        System.out.println("Start of file: " + attribute);
                        //System.out.println(element);
                        this.addData("File: " + attribute);
                        fileName = true;
                    }
                    if (element.getName().toString().equalsIgnoreCase("Node") && fileName) {
                        System.out.println("Start of version: " + attribute);
                        //this.setVersion(attribute);
                        this.addData("Version: " + attribute);
                        cur_Par = cur_Ver = null;
                        cur_Ver = attribute;
                        System.out.println("Attribute: " + attribute);
                        System.out.println(cur_Ver + cur_Par);
                        node = true;
                    }
                    if (element.getName().toString().equalsIgnoreCase("current") && fileName) {
                        System.out.println("Start of current: " + attribute);
                        //this.setVersion(attribute);
                        this.addData("Version: " + attribute);
                        System.out.println("Attribute: " + attribute);
                        D.currentVersion = attribute;
                        node = true;
                    }

                }
                if (element.getName().toString().equalsIgnoreCase("Parent") && fileName) {
                    System.out.println("Start of parent:");
                    parent = true;
                }
                if (element.getName().toString().equalsIgnoreCase("BrName") && fileName) {
                    System.out.println("Start of brname:");
                    brname = true;
                }
                if (element.getName().toString().equalsIgnoreCase("Message") && fileName) {
                    System.out.println("Start of Message:");
                    message = true;
                }
                if (element.getName().toString().equalsIgnoreCase("Children") && fileName) {
                    System.out.println("Start of children:");
                    children = true;
                }
                if (element.getName().toString().equalsIgnoreCase("Child") && fileName) {
                    System.out.println("Start of child:");
                    child = true;
                }


            }

            // This will be triggered when the tag is of type </...>
            if (event.isEndElement()) {
                EndElement element = (EndElement) event;

                // Checking which tag needs to be closed after reading.
                // If the tag matches then the boolean of that tag is
                // set to be false.
                if (element.getName().toString().equalsIgnoreCase("FileName") && fileName) {
                    System.out.println("End of file: " + element.getName());
                    fileName = false;
                }
                if (element.getName().toString().equalsIgnoreCase("Node") && fileName) {
                    System.out.println("End of version: " + element.getName());
                    node = false;
                    // If end of a version, add the node created to our DAG
                    System.out.println(cur_Ver + cur_Par);
                    //System.out.println("THIS IS CUR_PAR: " + cur_Par + ": " + cur_Par.getClass());
                    if(cur_Ver.equals("1.1")){
                        D.add(null, cur_Ver);
                        D.find(cur_Ver).setMessage(cur_Mes);
                        D.find(cur_Ver).setBrName(cur_BrN);
                    } else {
                        D.add(cur_Par, cur_Ver);
                        D.find(cur_Ver).setMessage(cur_Mes);
                        D.find(cur_Ver).setBrName(cur_BrN);
                    }

                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    //!!!!!!!!!!!!!!!!! ADD THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    System.out.println("\n######## Graph after adding " + cur_Ver + " to " + cur_Par + "##########");
                    D.printGraphEdges();
                    System.out.println("#######################################");
                }

                if (element.getName().toString().equalsIgnoreCase("BrName") && fileName) {
                    System.out.println("End of BRN: " + element.getName());
                    brname = false;
                }
                if (element.getName().toString().equalsIgnoreCase("Parent") && fileName) {
                    System.out.println("End of parent:");
                    parent = false;
                }
                if (element.getName().toString().equalsIgnoreCase("Message") && fileName) {
                    System.out.println("End of Message:");
                    message = false;
                }
                if (element.getName().toString().equalsIgnoreCase("Children") && fileName) {
                    System.out.println("End of children:");
                    children = false;
                }
                if (element.getName().toString().equalsIgnoreCase("Child") && fileName) {
                    System.out.println("End of child:");
                    child = false;
                }
            }

            // Triggered when there is data after the tag which is
            // currently opened.
            if (event.isCharacters()) {
                // Depending upon the tag opened the data is retrieved .
                Characters element = (Characters) event;
                if (fileName) {
                    if (child) {
                        if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                            System.out.println("\tCHILD!!!!*******" + element.getData() + "*******");
                            D.add(cur_Ver, element.getData());
                            this.addData("Add Parent: " + cur_Ver + " Child: " + element.getData());
                            //return;
                        }
                    } else {
                        if (parent) {
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tPARENT!!!!***" + element.getData() + "***");
                                this.addData("Parent: " + element.getData());
                                // Add the parent to our tracking
                                cur_Par = element.getData();
                                System.out.println(cur_Ver + cur_Par);
                                //return;
                            }
                        }
                        if (message) {
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tMessage!!!!***" + element.getData() + "***");
                                this.addData("Message: " + element.getData());
                                // Add the parent to our tracking
                                cur_Mes = element.getData();
                                System.out.println(cur_Mes);
                                //return;
                            }
                        }
                        if (brname) {
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tBrName!!!!***" + element.getData() + "***");
                                this.addData("BrName: " + element.getData());
                                // Add the parent to our tracking
                                cur_BrN = element.getData();
                                System.out.println(cur_BrN);
                                //return;
                            }
                        }
                        /*else if (children) {
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tCHILDREN!!!!*****" + element.getData() + "*****");
                                this.addData(element.getData());
                                //return;
                            }
                        } else if (node) {
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tVERSION!!**" + element.getData() + "**");
                                //System.out.println(element.getData().trim().equals(""));
                                this.addData("Version: " + element.getData());
                                //return;
                            }
                        } else {
                            // This is the actual patch information
                            if (!element.getData().replaceAll("[\\n\\t ]", "").equals("")) {
                                System.out.println("\tFILE!!!!*" + element.getData() + "*");
                                this.addData("File: : " + element.getData());
                                //return;
                            }
                        }*/
                    }
                }
            }
        }
        return D;
    }
}
