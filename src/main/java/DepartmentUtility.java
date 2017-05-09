import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by davlet on 5/5/17.
 */
class DepartmentUtility implements Callable {
    private Logger logger = MyLogger.getInstance();
    protected DBConnection db = null;

    DepartmentUtility(DBConnection dbConnection) throws SQLException, IOException, ClassNotFoundException {
        db = dbConnection;
    }

    public List<Department> getAllDepartments() throws SQLException, IOException, ClassNotFoundException {
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

    public void addDepartment(Department newDep) throws SQLException {
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

    public void addDepartment(String DepCode, String DepJob, String Description) throws SQLException, IOException, ClassNotFoundException {
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

    public boolean updateDepartment(String DepCode, String DepJob, String Description) throws SQLException {
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

    public boolean deleteDepartment(int id) throws SQLException {
        db.connection.setAutoCommit(false);
        String deleteQuery = "DELETE FROM dbtoxml.department " + "WHERE ID = ?";
        PreparedStatement preparedStatement = db.connection.prepareStatement(deleteQuery);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
        db.connection.commit();
        logger.debug("Deleted department with id = " + id);
        return true;
    }

    public boolean deleteDepartmentByNaturalKey(NaturalKey key) throws SQLException {
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

    public PreparedStatement deleteDepartmentByNaturalKeys(HashSet<NaturalKey> keysForDeletion) throws SQLException {
        StringBuilder keysToDeleteParams = new StringBuilder();
        for (NaturalKey naturalKey : keysForDeletion){
            keysToDeleteParams.append("(?, ?),");
        }

        String deleteQuery = "DELETE FROM dbtoxml.department WHERE (DepCode, DepJob) IN (" + keysToDeleteParams.deleteCharAt(keysToDeleteParams.length() - 1) + ")";
        PreparedStatement preparedStatement = db.connection.prepareStatement(deleteQuery);

        int index = 1;
        for (NaturalKey key : keysForDeletion){
            preparedStatement.setString(index++, key.getDepCode());
            preparedStatement.setString(index++, key.getDepJob());
        }
        return preparedStatement;
    }

    public PreparedStatement addDepartmentList(List<Department> depList) throws SQLException {
        StringBuilder keysToAddParam = new StringBuilder();
        for (int i = 0; i < depList.size(); i++){
            keysToAddParam.append("(?,?,?),");
        }

        String addQuery = "insert into dbtoxml.department " +
                "(DepCode, DepJob, Description) VALUES " + keysToAddParam.deleteCharAt(keysToAddParam.length() - 1) + ";";
        PreparedStatement preparedStatement = db.connection.prepareStatement(addQuery);

        int index = 1;
        for (Department dep : depList){
            preparedStatement.setString(index++, dep.getDepCode());
            preparedStatement.setString(index++, dep.getDepJob());
            preparedStatement.setString(index++, dep.getDescription());
        }
        return preparedStatement;
    }

    public PreparedStatement updateDepartment(List<Department> departmentList) throws SQLException {
        String updateQuery = "UPDATE dbtoxml.department SET DepCode = ?,"
                + " DepJob = ?, Description = ? WHERE (DepCode, DepJob) IN ((?,?))";
        PreparedStatement preparedStatement = db.connection.prepareStatement(updateQuery);

        for (Department dep: departmentList){
            preparedStatement.setString(1, dep.getDepCode());
            preparedStatement.setString(2, dep.getDepJob());
            preparedStatement.setString(3, dep.getDescription());
            preparedStatement.setString(4, dep.getDepCode());
            preparedStatement.setString(5, dep.getDepJob());
            preparedStatement.addBatch();
        }
        return preparedStatement;
    }
}
