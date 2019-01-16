import java.sql.Timestamp;

public class File {
    private String fileName;
    private String extention;
    private double fileSize;
    private int noPartitions;
    private String distro;
    private String owner;
    private Timestamp created;
    private Timestamp lastAccess;

    public void setFileName(String name){
        this.fileName = name;
    }

    public void setExtention(String extention) {
        this.extention = extention;
    }

    public void setFileSize(double size){
        this.fileSize = size;
    }

    public void setNoPartitions(int noPartitions) {
        this.noPartitions = noPartitions;
    }

    public void setDistro(String distro) {
        this.distro = distro;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreated(Timestamp timestamp) {
        this.created = timestamp;
    }

    public void setLastAccess(Timestamp timestamp) {
        this.lastAccess = timestamp;
    }
}
