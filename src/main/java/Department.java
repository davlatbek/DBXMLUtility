/**
 * <h1>Represents Department table</h1>
 * Class Department represents Department table in the database, has
 * methods for getting and setting it's attributes
 * <p>
 *
 * @author  davlet
 * @version 1.0
 * @since   5/5/17
 */
public class Department {
    /**
     * Identificator of the record, Surrogate key
     */
    private int id;

    /**
     * Code string of department
     */
    private String DepCode;

    /**
     * Department Job
     */
    private String DepJob;

    /**
     * Description of the job
     */
    private String Description;

    /**
     * Creates new Department object with specified depCode, depJobe, description
     * @param depCode
     * @param depJob
     * @param description
     */
    public Department(String depCode, String depJob, String description) {
        DepCode = depCode;
        DepJob = depJob;
        Description = description;
    }

    /**
     * Gets id property of the Department object
     * @return int id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id of the Department object
     * @param id id of department
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets department code
     * @return String department code
     */
    public String getDepCode() {
        return DepCode;
    }

    /**
     * Sets department code
     * @param depCode department code
     */
    public void setDepCode(String depCode) {
        DepCode = depCode;
    }

    /**
     * Returns job name
     * @return String department job name
     */
    public String getDepJob() {
        return DepJob;
    }

    /**
     * Sets department job name
     * @param depJob department job name
     */
    public void setDepJob(String depJob) {
        DepJob = depJob;
    }

    /**
     * Gets description
     * @return String description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * Sets description
     * @param description description
     */
    public void setDescription(String description) {
        Description = description;
    }

    /**
     * Overridden Equals() functions
     * @param o object to compare to
     * @return <tt>true</tt> if equals, <tt>false</tt> - otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        if (!getDepCode().equals(that.getDepCode())) return false;
        if (!getDepJob().equals(that.getDepJob())) return false;
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
    }

    /**
     * Overridden hashCode() function
     * @return int hashCode of department object
     */
    @Override
    public int hashCode() {
        int result = getDepCode().hashCode();
        result = 31 * result + getDepJob().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
