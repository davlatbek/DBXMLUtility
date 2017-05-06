/**
 * Created by davlet on 5/5/17.
 */
public class Department {
    private int id;
    private String DepCode;
    private String DepJob;
    private String Description;

    public Department(String depCode, String depJob, String description) {
        DepCode = depCode;
        DepJob = depJob;
        Description = description;
    }

    public Department(int id, String depCode, String depJob, String description) {
        this.id = id;
        DepCode = depCode;
        DepJob = depJob;
        Description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepCode() {
        return DepCode;
    }

    public void setDepCode(String depCode) {
        DepCode = depCode;
    }

    public String getDepJob() {
        return DepJob;
    }

    public void setDepJob(String depJob) {
        DepJob = depJob;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        if (!getDepCode().equals(that.getDepCode())) return false;
        if (!getDepJob().equals(that.getDepJob())) return false;
        return getDescription() != null ? getDescription().equals(that.getDescription()) : that.getDescription() == null;
    }

    @Override
    public int hashCode() {
        int result = getDepCode().hashCode();
        result = 31 * result + getDepJob().hashCode();
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }

/*    public boolean equals(Object object){
        if (object == null)
            return false;
        if (!(object instanceof Department))
            return false;
        Department department = (Department) object;
        return this.DepCode.equals(department.DepCode) & this.DepJob.equals(department.DepJob);
    }

    public int hashcode(){
        return this.DepCode.hashCode() * DepJob.hashCode();
    }*/
}
