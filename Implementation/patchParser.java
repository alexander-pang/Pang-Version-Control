// Java Code to implement StAX parser
// From: https://www.geeksforgeeks.org/stax-xml-parser-java/
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.Iterator;
//import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

/*
This program will currently just parse out the version you tell it to get.
So, the driver will have to recursively call this for each node it needs to get the patch file for
 */

public class patchParser
{
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        patchParser p = new patchParser("file.txt", "asdf");
        System.out.println("FINAL RESULT:\n" + p.getData());

    }
    private static boolean diffFile, FileName;
    private String data = "";

    public patchParser(String fileName, String version) throws FileNotFoundException, XMLStreamException {
        // Create a File object with appropriate xml file name
        //System.out.println("Working Directory = " + System.getProperty("user.dir"));
        File file = new File("." + fileName);
        //System.out.println("VERSION: " + version);
        // Function for accessing the data
        parser(file, version, fileName);
    }

    public String getData(){return this.data;}
    public void addData(String d){this.data += d;}

    private void parser(File file, String version, String fileName) throws FileNotFoundException,
            XMLStreamException
    {
        // Variables to make sure whether a element
        // in the xml is being accessed or not
        // if false that means elements is
        // not been used currently , if true the element or the
        // tag is being used currently
        diffFile = FileName = false;

        // Instance of the class which helps on reading tags
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // Initializing the handler to access the tags in the XML file
        XMLEventReader eventReader =
                factory.createXMLEventReader(new FileReader(file));

        // Checking the availabilty of the next tag
        while (eventReader.hasNext())
        {
            // Event is actually the tag . It is of 3 types
            // <name> = StartEvent
            // </name> = EndEvent
            // data between the StartEvent and the EndEvent
            // which is Characters Event
            XMLEvent event = eventReader.nextEvent();

            // This will trigger when the tag is of type <...>
            if (event.isStartElement())
            {
                StartElement element = (StartElement)event;

                // Iterator for accessing the metadeta related
                // the tag started.
                // Here, it would name of the company
                //Iterator<Attribute> iterator = element.getAttributes();
                //System.out.println("Starting a new interator!");
                /*while (iterator.hasNext())
                {
                    Attribute attribute = iterator.next();
                    QName name = attribute.getName();
                    String value = attribute.getValue();
                    //System.out.println(name+" = " + value);
                    if (value.equals(version)){
                        //System.out.println("HEY! I FOUND " + value);
                    }
                }*/

                // Checking which tag needs to be opened for reading.
                // If the tag matches then the boolean of that tag
                // is set to be true.

                if (!element.getName().toString().equalsIgnoreCase("start")) {
                    String attribute = element.getAttributes().next().getValue();
                    if (element.getName().toString().equalsIgnoreCase("superSecretString") && attribute.equals(version)) {
                        System.out.println("Heres the diff " + attribute);
                        diffFile = true;
                    }
                    if (element.getName().toString().equalsIgnoreCase("FileName") && attribute.equals(fileName)) {
                        System.out.println("Heres the file " + attribute);
                        FileName = true;
                    }
                }

            }

            // This will be triggered when the tag is of type </...>
            if (event.isEndElement())
            {
                EndElement element = (EndElement) event;
                // Checking which tag needs to be closed after reading.
                // If the tag matches then the boolean of that tag is
                // set to be false.
                if (element.getName().toString().equalsIgnoreCase("superSecretString"))
                {
                    //System.out.println("SSS: " + element.getName());
                    diffFile = false;
                }
                if (element.getName().toString().equalsIgnoreCase("FileName"))
                {
                    //System.out.println("FN: " + element.getName());
                    FileName = false;
                }
            }

            // Triggered when there is data after the tag which is
            // currently opened.
            if (event.isCharacters())
            {
                // Depending upon the tag opened the data is retrieved .
                Characters element = (Characters) event;
                if (diffFile && FileName)
                {
                    //System.out.println("Event: " + event);
                    // This is the actual patch information
                    //System.out.println("BOTH STUFF!!!!" + element.getData());
                    //System.out.println("Adding: " + element.getData());
                    this.addData(element.getData());
                }

            }
        }
    }
}
