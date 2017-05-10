/**
 * Class NaturalKey represents natural key (depcode, depjobe) in database
 * <p>
 *
 * @author  davlet
 * @version 1.0
 * @since   5/5/17
 */
public class NaturalKey {
    /**
     * Department code string
     */
    private String DepCode;

    /**
     * Department job name
     */
    private String DepJob;

    /**
     * Creates NaturalKey object with specified depCode, depJob
     * @param depCode
     * @param depJob
     */
    public NaturalKey(String depCode, String depJob) {
        DepCode = depCode;
        DepJob = depJob;
    }

    /**
     * Gets department code
     * @return String
     */
    public String getDepCode() {
        return DepCode;
    }

    /**
     * Sets department code
     * @param depCode
     */
    public void setDepCode(String depCode) {
        DepCode = depCode;
    }

    /**
     * Gets department job name
     * @return String
     */
    public String getDepJob() {
        return DepJob;
    }

    /**
     * Sets department job name
     * @param depJob
     */
    public void setDepJob(String depJob) {

        DepJob = depJob;
    }

    /**
     * Overridden equals function
     * @param o Object to compare to
     * @return <tt>true</tt> if equals, <tt>false</tt> otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NaturalKey that = (NaturalKey) o;

        if (getDepCode() != null ? !getDepCode().equals(that.getDepCode()) : that.getDepCode() != null) return false;
        return getDepJob() != null ? getDepJob().equals(that.getDepJob()) : that.getDepJob() == null;
    }

    /**
     * Overridden hashCode function
     * @return int hashcode value
     */
    @Override
    public int hashCode() {
        int result = getDepCode() != null ? getDepCode().hashCode() : 0;
        result = 31 * result + (getDepJob() != null ? getDepJob().hashCode() : 0);
        return result;
    }
}
