import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;

public class User {
    private static final String PATH = "/home/mohammad/Desktop/User_Files/";
    private int id;
    private String directory;
    private int portListen;
    private int portDownload;
    private int portAllocationReceiver;
    private double totalSize;
    private Socket client;
    private HashSet<String> files = new HashSet<>();

    public User(int id){
        this.id = id;
        directory = PATH + id + "/";
        portListen = 4000 + id;
        portAllocationReceiver = 5000 + id;
        portDownload = 6000 + id;
        totalSize = id;
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

        ServerSocket allocationSocket = new ServerSocket(portAllocationReceiver);
        Socket client = allocationSocket.accept();
        DataInputStream in = new DataInputStream(client.getInputStream());
        String allocationFormat = in.readUTF();
        client.close();
        allocationSocket.close();

        System.out.println("Allocation received = " + allocationFormat);

        //TODO this part should send file to allocators


    }
}
