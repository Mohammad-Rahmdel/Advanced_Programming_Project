import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
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

    public int getPortDownload() { return portDownload; }

    public String getDirectory(){
        return directory;
    }

    public double getTotalSize(){
        return totalSize;
    }

    public void addTotalSize(double size){
        totalSize += size;
    }

    public void upload(String fileName, int partitions, String path) throws IOException{
        files.add(fileName);
        String filePath = path + fileName;
        System.out.println("Uploading ...");
        try {
            client = new Socket("localhost", 8888);
        } catch (UnknownHostException e){} catch (IOException e){}
        System.out.println("User connected");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        String request = "upload " + filePath + " " + partitions + " " + getId();    //e.g. upload /.../readme.txt 3 id
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

        File file = new File(path + fileName);
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


    public void download(String fileName) throws IOException{
        System.out.println("Downloading ...");
        try {
            client = new Socket("localhost", 8888);
        } catch (UnknownHostException e){} catch (IOException e){}
        System.out.println("User connected");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        String request = "download " + fileName + " " + id;            //e.g. download readme.txt 3
        out.writeUTF(request);

        //receives how many parts it should receive
        ServerSocket portSocket = new ServerSocket(12345);
        Socket client = portSocket.accept();
        DataInputStream in = new DataInputStream(client.getInputStream());
        String answer = in.readUTF();
        client.close();
        portSocket.close();
        out.close();

        if(answer.equals("failed")){
            System.out.println("Downloading failed. Requested file is corrupt");

            //TODO SEND MESSAGE FAILED
        }
        else {
            //TODO SEND MESSAGE SUCCESS
            int noParts = Integer.parseInt(answer);
            System.out.println("Number of parts = " + noParts);

            int totalSize = 0;
            ArrayList<byte[]> partitions = new ArrayList<>();

            System.out.println("New socket for receiving on " + (portDownload));

            ServerSocket downloadSSocket = new ServerSocket(portDownload);
            Socket downloadSocket = null;
            DataInputStream downloadStream = null;

            for(int i = 0; i < noParts; i++){


                downloadSocket = downloadSSocket.accept();
                System.out.println("Connection***************************");
                downloadStream = new DataInputStream(downloadSocket.getInputStream());
                String size = downloadStream.readUTF();
                int partitionSize = Integer.parseInt(size);


                System.out.println("packet size is = " + partitionSize);



                byte[] receive = new byte[partitionSize];
                downloadStream.readFully(receive);
                totalSize += receive.length - 1;
                partitions.add(receive);

                System.out.println("packet number " + receive[0] + " received");


            }

            System.out.println("total size = " + totalSize);

            downloadSSocket.close();
            downloadSocket.close();
            downloadStream.close();

            System.out.println("All sockets closed");


            byte[] mergedFile = new byte[totalSize];
            int index = 0;
            for (int t = 1; t <= partitions.size(); t++){  // merging in correct order
                for (byte[] x : partitions){
                    if((int) x[0] == t){
                        System.out.println("packet number " + t + " merged");
                        for(int p = 1; p < x.length; p++){
                            mergedFile[index] = x[p];
                            index++;
                        }
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(directory + fileName)) {
                fos.write(mergedFile);
            } catch (IOException e){}
        }

    }

}


class UploadListener extends Thread{
    private ServerSocket uploadListenerSocket;
    private int port;
    private String directory;
    private User user;
    private Socket socketUp;

    public UploadListener(User user){
        this.user = user;
        this.port = user.getPortListen();
        this.directory = user.getDirectory();


        try {
            uploadListenerSocket = new ServerSocket(port);
        } catch (IOException e) {}
        socketUp = null;
    }

    public void run(){


        while (true){

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

                    user.addTotalSize((double)fileSize);

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
                    user.addTotalSize(-sizeDeletingFile);
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
                else if(request.startsWith("send")){

                    int idRequester = Integer.parseInt(request.split(" ")[1]);
                    String fileInfo = request.split(" ")[2];

                    System.out.println("peer " + user.getId() + " to " + idRequester + " this: " + fileInfo);

                    int lastIndex = fileInfo.split("\\.").length - 1;
                    int partNumber = Integer.parseInt(fileInfo.split("\\.")[lastIndex]);


                    File file = new File(directory + fileInfo);
                    byte[] fileContent = null;
                    try {
                        fileContent = Files.readAllBytes(file.toPath());
                    } catch (IOException e){}

                    System.out.println("sending size ... 1");

                    byte[] fileContent2 = new byte[fileContent.length + 1];
                    fileContent2[0] = (byte) partNumber; // offset
                    for (int k = 1; k <= fileContent.length; k++)
                        fileContent2[k] = fileContent[k-1];


                    System.out.println("sending size ... 2");

                    System.out.println("Socket on port = " + (6000 + idRequester));
                    //sending partition's size
                    Socket socketSend = new Socket("localhost", 6000 + idRequester);

                    System.out.println("sending size ... 3");

                    DataOutputStream outStream = new DataOutputStream(socketSend.getOutputStream());
                    String size = String.valueOf(fileContent2.length);
                    outStream.writeUTF(size);

                    System.out.println("size sent = " + size);

                    // offset<0> + data<1-end>
                    System.out.println("partition " + partNumber + " sending... to " + (6000 + idRequester));
                    outStream.write(fileContent2);
                    System.out.println("partition " + partNumber + " sent to " + (6000 + idRequester ));


                    socketSend.close();
                    outStream.close();

                }
            } catch (IOException e){}

            try {
                socketUp.close();
            }
            catch (IOException e) {}
        }
    }

}




class DownloadListener extends Thread{

    private byte[] data;
    private User user;

    DownloadListener(User user){
        this.user = user;
    }

    public byte[] getData(){
        return data;
    }
    public int getSize(){
        return data.length - 1;
    }

    public void run(){
        try {
            ServerSocket sizeSSocket = new ServerSocket(12346);
            Socket sizeSocket = sizeSSocket.accept();
            DataInputStream sizeStream = new DataInputStream(sizeSocket.getInputStream());
            String size = sizeStream.readUTF();
            int partitionSize = Integer.parseInt(size);

            sizeSocket.close();
            sizeSSocket.close();
            sizeStream.close();

            System.out.println("packet size = " + partitionSize);
            ServerSocket downloadSSocket = new ServerSocket(user.getPortDownload());
            System.out.println("port download receiver = " + user.getPortDownload());
            Socket downloadSocket = downloadSSocket.accept();
            DataInputStream downloadStream = new DataInputStream(downloadSocket.getInputStream());


            byte[] receive = new byte[partitionSize];
            data = receive;
            downloadStream.readFully(receive);
            //partitions.add(receive);

            System.out.println("packet number " + receive[0] + " received");


            downloadSSocket.close();
            downloadSocket.close();
            downloadStream.close();

        } catch (IOException e){}

    }
}

