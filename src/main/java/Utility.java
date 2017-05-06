import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by davlet on 5/4/17.
 */
public class Utility {
    public static void main(String[] args) throws Exception {
        MyLogger.resetLoggerConfiguration();
        Logger logger = Logger.getLogger("FileLogger");

        Utility utilityObj = new Utility();
        String command = args[0];
        String filename = args[1];

        switch (command) {
            case "extract":
                utilityObj.extract_to_xml(filename);
                logger.debug("Extracted database table 'department' successfully to xml file " + filename);
                break;
            case "sync":
                utilityObj.synchronize_xml_with_dbtable(filename);
                logger.debug("Synchronized xml file '" + filename + "' with database table 'department' successfully");
                break;
            default:
                logger.error("Command is not valid. Enter extract or sync and specify filename! Example: <extract dbxml>");
                throw new Exception("Error: Command is not valid!");
        }

    }

    private void extract_to_xml(String toFile) throws IOException, XMLStreamException, SQLException, ClassNotFoundException, TransformerException {
        Logger logger = Logger.getLogger("FileLogger");

        DepartmentUtility departmentUtility = new DepartmentUtility();
        List<Department> departmentList = departmentUtility.getAllDepartments();
        departmentUtility.db.connection.close();

        OutputStream outputStream = new FileOutputStream(new File(toFile + ".xml"));
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(outputStream, "utf-8"));
        out.writeStartDocument();
        out.writeCharacters("\n");
        out.writeStartElement("departments");

        for (Department department : departmentList) {
            out.writeCharacters("\n");
            out.writeStartElement("department");

            out.writeCharacters("\n");
            out.writeStartElement("DepCode");
            out.writeCharacters(department.getDepCode());
            out.writeEndElement();

            out.writeCharacters("\n");
            out.writeStartElement("DepJob");
            out.writeCharacters(department.getDepJob());
            out.writeEndElement();

            out.writeCharacters("\n");
            out.writeStartElement("Description");
            out.writeCharacters(department.getDescription());
            out.writeEndElement();

            out.writeEndElement();
        }
        out.writeCharacters("\n");
        out.writeEndElement();
        out.writeCharacters("\n");
        out.writeEndDocument();
        out.close();
        logger.debug("Finished writing database table to xml file");
    }

    protected void synchronize_xml_with_dbtable(String xmlFile) throws Exception {
        File inputXML = new File(xmlFile);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputXML);
        document.getDocumentElement().normalize();

        //get natural keys from db
        HashSet<NaturalKey> naturalKeysOfDatabase = new LinkedHashSet<>();
        DepartmentUtility departmentUtility = new DepartmentUtility();
        List<Department> departmentList = departmentUtility.getAllDepartments();
        for (Department department : departmentList) {
            naturalKeysOfDatabase.add(new NaturalKey(department.getDepCode(), department.getDepJob()));
        }

        //getting all records from xml to hashmap with key = NaturalKey class object, value = Department
        HashMap<NaturalKey, Department> hashMapDepartments = new LinkedHashMap<NaturalKey, Department>();
        HashSet<NaturalKey> naturalKeysOfXML = new LinkedHashSet<>();

        NodeList depList = document.getElementsByTagName("department");
        for (int i = 0; i < depList.getLength(); i++) {
            Department department;
            Node node = depList.item(i);
            Element element = (Element) node;
            department = new Department(
                    element.getElementsByTagName("DepCode").item(0).getTextContent(), element.getElementsByTagName("DepJob").item(0).getTextContent(), element.getElementsByTagName("Description").item(0).getTextContent());

            hashMapDepartments.put(new NaturalKey(department.getDepCode(), department.getDepJob()), department);

            if (naturalKeysOfXML.contains(new NaturalKey(department.getDepCode(), department.getDepJob()))) {
                System.out.println("Error! Two identical records in XML file!");
                throw new Exception("Error! Two identical records in XML file!");
            } else {
                naturalKeysOfXML.add(new NaturalKey(department.getDepCode(), department.getDepJob()));
            }
        }

        //not presented in xml, delete from db
        HashSet<NaturalKey> notPresentedInXmlForDeleting = this.getKeysDiffNotPresentedInXml(naturalKeysOfDatabase, naturalKeysOfXML);
        for (NaturalKey key : notPresentedInXmlForDeleting) {
            departmentUtility.deleteDepartmentByNaturalKey(key);
        }
        System.out.println("Deleted " + notPresentedInXmlForDeleting.size() + " records not presented in xml file from db");

        //not presented in db, add them from xml
        HashSet<NaturalKey> notPresInDbForAdding = this.getKeysDiffNotPresentedInDb(naturalKeysOfDatabase, naturalKeysOfXML);
        for (NaturalKey naturalKey : notPresInDbForAdding) {
            departmentUtility.addDepartment(hashMapDepartments.get(naturalKey));
        }
        System.out.println("Added " + notPresInDbForAdding.size() + " records not presented in db from xml file");

        //if record with key in db, update it
        naturalKeysOfXML.addAll(naturalKeysOfDatabase);
        naturalKeysOfXML.removeAll(notPresInDbForAdding);
        naturalKeysOfXML.removeAll(notPresentedInXmlForDeleting);
        for (NaturalKey key : naturalKeysOfXML){
            departmentUtility.updateDepartment(key.getDepCode(), key.getDepJob(), hashMapDepartments.get(key).getDescription());
        }
        System.out.println("Updated " + naturalKeysOfXML.size() + " records in db from xml file");

        departmentUtility.db.connection.close();
    }

    //delete records not presented in xml file
    protected HashSet<NaturalKey> getKeysDiffNotPresentedInXml(HashSet<NaturalKey> keysDb, HashSet<NaturalKey> keysXml) {
        HashSet<NaturalKey> keysNotPresentedInXml = new HashSet<>(keysDb);
        keysNotPresentedInXml.removeAll(keysXml);
        return keysNotPresentedInXml;
    }

    //add to db new records
    protected HashSet<NaturalKey> getKeysDiffNotPresentedInDb(HashSet<NaturalKey> keysDb, HashSet<NaturalKey> keysXml) {
        HashSet<NaturalKey> keysNotPresentedInDb = new HashSet<>(keysXml);
        keysNotPresentedInDb.removeAll(keysDb);
        return keysNotPresentedInDb;
    }
}