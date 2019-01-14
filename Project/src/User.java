import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class User {
    private static final String PATH = "/Desktop/User_Files/";
    private int id;
    private String directory;
    private int portListen;
    private int portDownload;
    private double totalSize;
    private Socket client;
    private HashSet<String> files = new HashSet<>();

    public User(int id){
        this.id = id;
        directory = PATH + id + "/";
        portListen = 4000 + id;
        portDownload = 6000 + id;
        totalSize = 0;
    }

    public int getId(){
        return this.id;
    }

    public double getTotalSize(){
        return totalSize;
    }

    public void upload(String fileName, int partitions) throws IOException{
        files.add(fileName);
        String filePath = directory + fileName;
        System.out.println("Uploading ...");
        try {
            client = new Socket("localhost", 8888);
        } catch (UnknownHostException e){} catch (IOException e){}
        System.out.println("User connected");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        String request = "upload " + filePath + " " + partitions;            //e.g. upload /.../readme.txt 3
        out.writeUTF(request);



    }
}
