import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by davlet on 5/5/17.
 */
class DepartmentUtility {
    private Logger logger = MyLogger.getInstance();
    protected DBConnection db = null;

    DepartmentUtility() throws SQLException, IOException, ClassNotFoundException {
        db = DBConnection.getInstance();
        db.setConnection();
    }

    List<Department> getAllDepartments() throws SQLException, IOException, ClassNotFoundException {
        List<Department> departments = new ArrayList<Department>();
        Statement statement = db.connection.createStatement();
        String sqlQuery;
        sqlQuery = "select * from department";
        ResultSet rs = statement.executeQuery(sqlQuery);

        while (rs.next()){
            int id = rs.getInt("ID");
            String depCode = rs.getString("DepCode");
            String depJob = rs.getString("DepJob");
            String desc = rs.getString("Description");
            Department department = new Department(id, depCode, depJob, desc);
            departments.add(department);
        }
        rs.close();
        statement.close();
        return departments;
    }

    void addDepartment(Department newDep) throws SQLException {
        db.connection.setAutoCommit(false);
        String addQuery = "insert into dbtoxml.department " +
                "(DepCode, DepJob, Description) value (?, ?, ?)";
        PreparedStatement preparedStatement = db.connection.prepareStatement(addQuery);
        preparedStatement.setString(1, newDep.getDepCode());
        preparedStatement.setString(2, newDep.getDepJob());
        preparedStatement.setString(3, newDep.getDescription());
        preparedStatement.executeUpdate();
        //transaction end
        db.connection.commit();
        logger.debug("Added new department with natural key "  + newDep.getDepCode() + " " + newDep.getDepJob());
    }


    void addDepartment(String DepCode, String DepJob, String Description) throws SQLException, IOException, ClassNotFoundException {
        //transaction start
        db.connection.setAutoCommit(false);
        String addQuery = "insert into dbtoxml.department " +
                "(DepCode, DepJob, Description) value (?, ?, ?)";
        PreparedStatement preparedStatement = db.connection.prepareStatement(addQuery);
        preparedStatement.setString(1, DepCode);
        preparedStatement.setString(2, DepJob);
        preparedStatement.setString(3, Description);
        preparedStatement.executeUpdate();
        //transaction end
        db.connection.commit();
        logger.debug("Added new department with id");
    }

    boolean updateDepartment(String DepCode, String DepJob, String Description) throws SQLException {
        db.connection.setAutoCommit(false);
        String updateQuery = "UPDATE dbtoxml.department SET DepCode = ?,"
                + " DepJob = ?, Description = ? WHERE (DepCode, DepJob) IN ((?, ?))";
        PreparedStatement preparedStatement = db.connection.prepareStatement(updateQuery);
        preparedStatement.setString(1, DepCode);
        preparedStatement.setString(2, DepJob);
        preparedStatement.setString(3, Description);
        preparedStatement.setString(4, DepCode);
        preparedStatement.setString(5, DepJob);
        preparedStatement.executeUpdate();
        db.connection.commit();
        logger.debug("Updated department with keys " + DepCode + " " + DepJob);
        return true;
    }

    boolean deleteDepartment(int id) throws SQLException {
        db.connection.setAutoCommit(false);
        String deleteQuery = "DELETE FROM dbtoxml.department " + "WHERE ID = ?";
        PreparedStatement preparedStatement = db.connection.prepareStatement(deleteQuery);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        db.connection.commit();
        logger.debug("Deleted department with id = " + id);
        return true;
    }

    boolean deleteDepartmentByNaturalKey(NaturalKey key) throws SQLException {
        db.connection.setAutoCommit(false);
        String deleteQuery = "DELETE FROM dbtoxml.department WHERE (DepCode, DepJob) IN ((?,?))";
        PreparedStatement preparedStatement = db.connection.prepareStatement(deleteQuery);
        preparedStatement.setString(1, key.getDepCode());
        preparedStatement.setString(2, key.getDepJob());
        preparedStatement.executeUpdate();
        db.connection.commit();
        logger.debug("Deleted department with natural key = " + key.getDepCode() + " " + key.getDepJob());
        return true;
    }
}
