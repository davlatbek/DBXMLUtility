/**
 * Created by davlet on 5/6/17.
 */
public class NaturalKey {
    private String DepCode;
    private String DepJob;

    public NaturalKey(String depCode, String depJob) {
        DepCode = depCode;
        DepJob = depJob;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NaturalKey that = (NaturalKey) o;

        if (getDepCode() != null ? !getDepCode().equals(that.getDepCode()) : that.getDepCode() != null) return false;
        return getDepJob() != null ? getDepJob().equals(that.getDepJob()) : that.getDepJob() == null;
    }

    @Override
    public int hashCode() {
        int result = getDepCode() != null ? getDepCode().hashCode() : 0;
        result = 31 * result + (getDepJob() != null ? getDepJob().hashCode() : 0);
        return result;
    }
}
