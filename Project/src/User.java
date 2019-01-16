import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
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

        Thread t = new UploadListener(this);
        t.start();
    }

    public int getId(){
        return this.id;
    }

    public int getPortListen(){
        return portListen;
    }

    public String getDirectory(){
        return directory;
    }

    public double getTotalSize(){
        return totalSize;
    }

    public void addTotolSize(double size){
        totalSize += size;
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

        long fileSize = new File(filePath).length();
        long[] size = new long[partitions];
        long sum = 0;

        //12,3 -> 4,4,4
        //11,3 -> 4,4,3
        //10,3 -> 4,4,2
        for (int i = 0; i < partitions - 1; i++){
            size[i] = (long)Math.ceil(fileSize/partitions);
            sum += size[i];
        }
        size[partitions - 1] = fileSize - sum;

        String[] id_part = allocationFormat.split(",");
        int[] idFixed = new int[id_part.length];
        int[] partFixed = new int[id_part.length];
        for (int i = 0; i < id_part.length; i++){
            idFixed[i] = Integer.parseInt(id_part[i].split(":")[1]);
            partFixed[i] = Integer.parseInt(id_part[i].split(":")[0]);
        }

        for (int i = 0; i < id_part.length; i++){  // sending->file size
            try {
                Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
                DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());
                String info = "upload " + (fileName + "." +  partFixed[i]) + " " + size[partFixed[i]-1];
                outTmp.writeUTF(info);

                socketTmp.close();
                outTmp.close();
            } catch (IOException e){}
        }

        File file = new File(directory + fileName);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e){}
        for (int i = 0; i < id_part.length; i++) {  // sending partitions
            Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
            DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());

            long offset = 0;
            for(int j = 1; j < partFixed[i]; j++)
                offset += size[j-1];

            int len = (int)size[partFixed[i]-1];
            System.out.println("len = " + len);
            outTmp.write(fileContent, (int)offset, len);

            socketTmp.close();
            outTmp.close();
        }
        System.out.println("All files Sent");

    }



}


class UploadListener extends Thread{
    private ServerSocket uploadListenerSocket;
    private int port;
    private String directory;
    private User user;

    public UploadListener(User user){
        this.user = user;
        this.port = user.getPortListen();
        this.directory = user.getDirectory();
    }

    public void run(){
        try {
            uploadListenerSocket = new ServerSocket(port);
        } catch (IOException e) {}

        while (true){
            Socket socketUp = null;
            try {
                socketUp = uploadListenerSocket.accept();
            } catch (IOException e) {}
            //System.out.println("");
            try {
                DataInputStream in = new DataInputStream(socketUp.getInputStream());
                String request = in.readUTF();
                if (request.startsWith("upload")){
                    String fileName = request.split(" ")[1];
                    long fileSize = Long.parseLong(request.split(" ")[2]);

                    user.addTotolSize((double)fileSize);

                    System.out.println(port + ": fileSize = " + fileSize);
                    System.out.println(port + ": fileName = " + fileName);


                    in.close();
                    socketUp.close();
                    socketUp = uploadListenerSocket.accept();
                    byte[] fileContent = new byte[(int)fileSize];
                    in = new DataInputStream(socketUp.getInputStream());

                    in.readFully(fileContent);
                    System.out.println(port + " file received");

                    try (FileOutputStream fos = new FileOutputStream(directory + fileName)) {
                        fos.write(fileContent);
                    } catch (IOException e){}
                }
                else if(request.startsWith("delete")){ // delete
                    String fileName = request.split(" ")[1];
                    System.out.println(port + " delete request = " + request);
                    System.out.println(port + ": " + directory + fileName);
                    File f = new File(directory + fileName);
                    double sizeDeletingFile = f.length();
                    System.out.println(port + ": size = " + sizeDeletingFile);
                    f.delete();
                    System.out.println("Deleted successfully");
                    user.addTotolSize(-sizeDeletingFile);
                }
                else if(request.startsWith("rename")){
                    String fileName = request.split(" ")[1];
                    System.out.println(port + " name = " + fileName);
                    String[] split = fileName.split("\\.");

                    String index = split[split.length - 1];

                    String fileNewName = request.split(" ")[2];
                    File file_old = new File(directory + fileName);
                    File file_new = new File(directory + fileNewName + "." + index);
                    file_old.renameTo(file_new);

                }
            } catch (IOException e){}

            try {
                socketUp.close();
            }
            catch (IOException e) {}
        }
    }

}
