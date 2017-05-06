import org.apache.log4j.Logger;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by davlet on 5/4/17.
 */
public class Utility {
    public static void main(String[] args) throws Exception {
        MyLogger.resetLoggerConfiguration();
        Logger logger = Logger.getLogger("FileLogger");

        DepartmentUtility departmentUtility = new DepartmentUtility();
//        departmentUtility.addDepartment("new", "newjob", "newdesc");
//        departmentUtility.updateDepartment(1, "updated", "updatedjob", "updateddesc");
//        departmentUtility.deleteDepartment(1);
        departmentUtility.getAllDepartments();

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
                logger.debug("Synchronized xml file " + filename + " with database table 'department' successfully" + filename);
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

        OutputStream outputStream = new FileOutputStream(new File(toFile+".xml"));
        XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(
                new OutputStreamWriter(outputStream, "utf-8"));
        out.writeStartDocument();
        out.writeCharacters("\n");
        out.writeStartElement("departments");

        for (Department department: departmentList){
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

    private void synchronize_xml_with_dbtable(String xmlFile){

    }
}