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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * Utility class for extracting db to xml and syncing xml file to database table
 */
public class Utility {
    private DBConnection db = null;
    private Logger logger = Logger.getLogger("FileLogger");

    /**
     * Creates Utility object and sets connection
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Utility() throws SQLException, IOException, ClassNotFoundException {
        db = DBConnection.getInstance();
        db.setConnection();
        logger.debug("Set connection with database");
    }

    public static void main(String[] args) throws Exception {
        MyLogger.resetLoggerConfiguration();
        Logger logger = Logger.getLogger("FileLogger");
        Utility utilityObj = new Utility();
        String command = args[0];
        String filename = args[1];

        switch (command) {
            case "extract":
                utilityObj.extract_to_xml(filename);
                break;
            case "sync":
                utilityObj.synchronize_xml_with_dbtable(filename);
                break;
            default:
                logger.error("Command is not valid. Enter extract or sync and specify filename! Example: <extract dbxml>");
                throw new Exception("Error: Command is not valid!");
        }

    }

    /**
     * Extracts database table to specified file
     * @param toFile
     * @throws IOException
     * @throws XMLStreamException
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws TransformerException
     */
    private void extract_to_xml(String toFile) throws IOException, XMLStreamException, SQLException, ClassNotFoundException, TransformerException {
        DepartmentUtility departmentUtility = new DepartmentUtility(this.db);
        List<Department> departmentList = departmentUtility.getAllDepartments();
        departmentUtility.db.connection.close();

        OutputStream outputStream = new FileOutputStream(new File(toFile + ".xml"));
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(outputStream, "utf-8"));

        logger.debug("Started writing to specified XML file: " + toFile);
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
        logger.debug("Finished writing database table to XML file " + toFile);
        logger.debug("Extracted database table 'department' to XML file '" + toFile + "' successfully");
    }

    /**
     * Synchronizes xml file xmlFile's content with database table
     * @param xmlFile
     * @throws Exception
     */
    private void synchronize_xml_with_dbtable(String xmlFile) throws Exception {
        File inputXML = new File(xmlFile);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputXML);
        document.getDocumentElement().normalize();

        //get natural keys from db
        HashSet<NaturalKey> naturalKeysOfDatabase = new LinkedHashSet<>();
        DepartmentUtility departmentUtility = new DepartmentUtility(this.db);
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
                element.getElementsByTagName("DepCode").item(0).getTextContent(),                     element.getElementsByTagName("DepJob").item(0).getTextContent(),                      element.getElementsByTagName("Description").item(0).getTextContent());

            hashMapDepartments.put(new NaturalKey(department.getDepCode(), department.getDepJob()), department);

            if (naturalKeysOfXML.contains(new NaturalKey(department.getDepCode(), department.getDepJob()))) {
                logger.error("Error! Two identical records in XML file!");
                throw new Exception("Error! Two identical records in XML file!");
            } else {
                naturalKeysOfXML.add(new NaturalKey(department.getDepCode(), department.getDepJob()));
            }
        }

        HashSet<NaturalKey> notPresentedInXmlForDeleting = this.getKeysDiffNotPresentedInXml(naturalKeysOfDatabase, naturalKeysOfXML);
        HashSet<NaturalKey> notPresInDbForAdding = this.getKeysDiffNotPresentedInDb(naturalKeysOfDatabase, naturalKeysOfXML);
        naturalKeysOfXML.addAll(naturalKeysOfDatabase);
        naturalKeysOfXML.removeAll(notPresInDbForAdding);
        naturalKeysOfXML.removeAll(notPresentedInXmlForDeleting);

        this.syncInOneTransaction(notPresInDbForAdding, naturalKeysOfXML, notPresentedInXmlForDeleting, hashMapDepartments);
    }

    /**
     * Deletes keys of records not presented in xml file
     * @param keysDb
     * @param keysXml
     * @return
     */
    private HashSet<NaturalKey> getKeysDiffNotPresentedInXml(HashSet<NaturalKey> keysDb, HashSet<NaturalKey> keysXml) {
        HashSet<NaturalKey> keysNotPresentedInXml = new HashSet<>(keysDb);
        keysNotPresentedInXml.removeAll(keysXml);
        return keysNotPresentedInXml;
    }

    /**
     * Gets keys not presented in database table, but present in xml file
     * @param keysDb
     * @param keysXml
     * @return
     */
    private HashSet<NaturalKey> getKeysDiffNotPresentedInDb(HashSet<NaturalKey> keysDb, HashSet<NaturalKey> keysXml) {
        HashSet<NaturalKey> keysNotPresentedInDb = new HashSet<>(keysXml);
        keysNotPresentedInDb.removeAll(keysDb);
        return keysNotPresentedInDb;
    }

    /**
     * Synchronize all records from xml file in one transaction
     * @param addKeys
     * @param updateKeys
     * @param deleteKeys
     * @param departmentHashMap
     * @throws SQLException
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void syncInOneTransaction(HashSet<NaturalKey> addKeys, HashSet<NaturalKey> updateKeys, HashSet<NaturalKey> deleteKeys, HashMap<NaturalKey, Department> departmentHashMap) throws SQLException, IOException, ClassNotFoundException {
        this.db.setConnection();
        this.db.connection.setAutoCommit(false);
        DepartmentUtility departmentUtility = new DepartmentUtility(this.db);

        PreparedStatement preparedStatementToDelete = null;
        if (deleteKeys.size() != 0) {
            preparedStatementToDelete = departmentUtility.deleteDepartmentByNaturalKeys(deleteKeys);
        }

        PreparedStatement preparedStatementToAdd = null;
        List<Department> departmentListToAdd = new ArrayList<>();
        for (NaturalKey addKey : addKeys){
            departmentListToAdd.add(departmentHashMap.get(addKey));
        }
        if (addKeys.size() != 0){
            preparedStatementToAdd = departmentUtility.addDepartmentList(departmentListToAdd);
        }

        PreparedStatement preparedStatementToUpdate = null;
        List<Department> depListToUpdate = new ArrayList<>();
        for (NaturalKey updateKey : updateKeys){
            depListToUpdate.add(departmentHashMap.get(updateKey));
        }
        if (updateKeys.size() != 0){
            preparedStatementToUpdate = departmentUtility.updateDepartment(depListToUpdate);
        }

        try {
            if (deleteKeys.size() != 0) {
                assert preparedStatementToDelete != null;
                preparedStatementToDelete.execute();
            }
            if (addKeys.size() != 0) {
                assert preparedStatementToAdd != null;
                preparedStatementToAdd.execute();
            }
            if (updateKeys.size() != 0){
                assert preparedStatementToUpdate != null;
                preparedStatementToUpdate.executeBatch();
            }
            this.db.connection.commit();
            logger.debug("SYNC: Deleted departments from db not presented in XML file");
            logger.debug("SYNC: Added new departments from XML file to db");
            logger.debug("SYNC: Updated records from XML file with db");
            logger.debug("SYNC: Synchronized XML file with database successfully");
        } catch (Exception e){
            this.db.connection.rollback();
            logger.error("SYNC FAILED, ROLLBACK: Couldn't sync XML file with database table");
        } finally {
            this.db.connection.close();
            logger.debug("SYNC: Connection with database closed successfully");
        }
    }
}