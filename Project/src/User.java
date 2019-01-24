import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * all the networks operations are implemented in this class
 */
public class User {
    private static final String PATH = System.getProperty("user.home")+"/Desktop/User_Files/";
    // returns the directory
    private int id;
    private String directory;
    private int portListen;             // the permanent up socket port
    private int portDownload;           // listens for downloading requests
    private int portAllocationReceiver; // listens for allocations formats
    private double totalSize;           //the total storage of peer
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

    /**
     *
     * @return id of the peer
     */
    public int getId(){
        return this.id;
    }

    /**
     *
     * @return portListen
     */
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

    /**
     * when a new file is allocated to this peer we add the size of that file
     * when a file is deleted its size will be subtracted
     * @param size
     */
    public void addTotalSize(double size){
        totalSize += size;
    }

    /**
     * method for uploading a file
     * @param fileName  name of the requested file
     * @param partitions number of partitions which file should be divided into
     * @param path path of the uploaded file
     * @throws IOException
     */
    public void upload(String fileName, int partitions, String path) throws IOException{
        files.add(fileName);
        String filePath = path + fileName;
        //System.out.println("Uploading ...");
        try {
            client = new Socket("localhost", 8888);         //requests for uploading
            // admin listens to 8888
        } catch (UnknownHostException e){} catch (IOException e){}
        //System.out.println("User connected");


        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        String request = "upload " + filePath + " " + partitions + " " + getId();    //e.g. upload /.../readme.txt 3 id
        out.writeUTF(request);

        // now the user should receive the allocation format to send the partitions
        ServerSocket allocationSocket = new ServerSocket(portAllocationReceiver);
        Socket client = allocationSocket.accept();
        DataInputStream in = new DataInputStream(client.getInputStream());
        String allocationFormat = in.readUTF();
        client.close();
        allocationSocket.close();

        //System.out.println("Allocation received = " + allocationFormat);

        long fileSize = new File(filePath).length();
        long[] size = new long[partitions];
        long sum = 0;

        //examples of how we partition the files
        //12,3 -> 4,4,4
        //11,3 -> 4,4,3
        //10,3 -> 4,4,2
        for (int i = 0; i < partitions - 1; i++){
            size[i] = (long)Math.ceil(fileSize/partitions);
            sum += size[i];
        }
        size[partitions - 1] = fileSize - sum;

        String[] id_part = allocationFormat.split(",");
        int[] idFixed = new int[id_part.length];        //the id which the partition should be sent to
        int[] partFixed = new int[id_part.length];      //which partition should be sent
        for (int i = 0; i < id_part.length; i++){
            idFixed[i] = Integer.parseInt(id_part[i].split(":")[1]);
            partFixed[i] = Integer.parseInt(id_part[i].split(":")[0]);
        }

        for (int i = 0; i < id_part.length; i++){  // sending partition size to peers
            try {
                Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
                DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());
                String info = "upload " + (fileName + "." +  partFixed[i]) + " " + size[partFixed[i]-1];
                //upload fileName.1 (partition 1) with size = size[]
                outTmp.writeUTF(info);

                socketTmp.close();
                outTmp.close();
            } catch (IOException e){}
        }

        File file = new File(path + fileName);
        byte[] fileContent = null;
        try {
            fileContent = Files.readAllBytes(file.toPath());            //cast the file to a array of bytes
        } catch (IOException e){}
        for (int i = 0; i < id_part.length; i++) {                      // sending partitions to peers
            Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
            DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());

            long offset = 0;
            for(int j = 1; j < partFixed[i]; j++)
                offset += size[j-1];

            int len = (int)size[partFixed[i]-1];
            System.out.println("len = " + len);
            outTmp.write(fileContent, (int)offset, len);                //dividing the file


            socketTmp.close();
            outTmp.close();
        }
        System.out.println("All files Sent");

    }


    /**
     * method for handling download request
     * @param fileName = fileName requested for download
     * @throws IOException
     */
    public void download(String fileName) throws IOException{
        //System.out.println("Downloading ...");
        try {
            client = new Socket("localhost", 8888);
        } catch (UnknownHostException e){} catch (IOException e){}
        //System.out.println("User connected");

        DataOutputStream out = new DataOutputStream(client.getOutputStream());
        String request = "download " + fileName + " " + id;            //e.g. download readme.txt 3
        out.writeUTF(request);      //send download request to admin

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
            // fail occurs when all of the owners of an partitions are not in the network anymore.
        }
        else {
            int noParts = Integer.parseInt(answer);         //number of parts which should ber received
            //System.out.println("Number of parts = " + noParts);

            int totalSize = 0;
            ArrayList<byte[]> partitions = new ArrayList<>();

            //System.out.println("New socket for receiving on " + (portDownload));

            ServerSocket downloadSSocket = new ServerSocket(portDownload);
            Socket downloadSocket = null;
            DataInputStream downloadStream = null;

            for(int i = 0; i < noParts; i++){


                downloadSocket = downloadSSocket.accept();
                //System.out.println("Connection***************************");
                downloadStream = new DataInputStream(downloadSocket.getInputStream());
                String size = downloadStream.readUTF();         // size of partition
                int partitionSize = Integer.parseInt(size);

                //System.out.println("packet size is = " + partitionSize);

                byte[] receive = new byte[partitionSize];
                downloadStream.readFully(receive);      //receiving the partition
                totalSize += receive.length - 1;        //updates total size
                partitions.add(receive);

                //System.out.println("packet number " + receive[0] + " received");

            }

            //System.out.println("total size = " + totalSize);

            downloadSSocket.close();
            downloadSocket.close();
            downloadStream.close();

            System.out.println("All sockets closed");


            // now merge all partitions according to their place
            byte[] mergedFile = new byte[totalSize];
            int index = 0;
            for (int t = 1; t <= partitions.size(); t++){  // merging in the correct order
                for (byte[] x : partitions){
                    if((int) x[0] == t){
                        //System.out.println("packet number " + t + " merged");
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


/**
 * a thread which listens to upload - rename - delete requests
 */
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
                if (request.startsWith("upload")){                              //upload command
                    String fileName = request.split(" ")[1];
                    long fileSize = Long.parseLong(request.split(" ")[2]);

                    user.addTotalSize((double)fileSize);

                    //System.out.println(port + ": fileSize = " + fileSize);
                    //System.out.println(port + ": fileName = " + fileName);


                    in.close();
                    socketUp.close();
                    socketUp = uploadListenerSocket.accept();
                    byte[] fileContent = new byte[(int)fileSize];           //receives the partitions with size = fileSize
                    in = new DataInputStream(socketUp.getInputStream());

                    in.readFully(fileContent);
                    //System.out.println(port + " file received");

                    try (FileOutputStream fos = new FileOutputStream(directory + fileName)) {
                        fos.write(fileContent);
                    } catch (IOException e){}
                }
                else if(request.startsWith("delete")){ // delete request
                    String fileName = request.split(" ")[1];
                    //System.out.println(port + " delete request = " + request);
                    //System.out.println(port + ": " + directory + fileName);
                    File f = new File(directory + fileName);
                    double sizeDeletingFile = f.length();                   //for updating total size
                    //System.out.println(port + ": size = " + sizeDeletingFile);
                    f.delete();             //delete file
                    System.out.println("Deleted successfully");
                    user.addTotalSize(-sizeDeletingFile);
                }
                else if(request.startsWith("rename")){      //rename request
                    String fileName = request.split(" ")[1];
                    //System.out.println(port + " name = " + fileName);
                    String[] split = fileName.split("\\.");

                    String index = split[split.length - 1];

                    String fileNewName = request.split(" ")[2];
                    File file_old = new File(directory + fileName);
                    File file_new = new File(directory + fileNewName + "." + index);
                    file_old.renameTo(file_new);

                }
                else if(request.startsWith("send")){            //send for download request

                    int idRequester = Integer.parseInt(request.split(" ")[1]);
                    String fileInfo = request.split(" ")[2];

                    //System.out.println("peer " + user.getId() + " to " + idRequester + " this: " + fileInfo);

                    int lastIndex = fileInfo.split("\\.").length - 1;
                    int partNumber = Integer.parseInt(fileInfo.split("\\.")[lastIndex]);


                    File file = new File(directory + fileInfo);     //creating the file to send
                    byte[] fileContent = null;
                    try {
                        fileContent = Files.readAllBytes(file.toPath());      //converting file to bytes
                    } catch (IOException e){}

                    //System.out.println("sending size ... 1");

                    byte[] fileContent2 = new byte[fileContent.length + 1];
                    fileContent2[0] = (byte) partNumber;                    // offset
                    for (int k = 1; k <= fileContent.length; k++)
                        fileContent2[k] = fileContent[k-1];                 // data


                    //System.out.println("sending size ... 2");

                    //System.out.println("Socket on port = " + (6000 + idRequester));
                    //sending partition's size
                    Socket socketSend = new Socket("localhost", 6000 + idRequester);

                    //System.out.println("sending size ... 3");

                    DataOutputStream outStream = new DataOutputStream(socketSend.getOutputStream());
                    String size = String.valueOf(fileContent2.length);              //sending partition's size
                    outStream.writeUTF(size);

                    //System.out.println("size sent = " + size);

                    // offset<0> + data<1-end>
                    //System.out.println("partition " + partNumber + " sending... to " + (6000 + idRequester));
                    outStream.write(fileContent2);          //sending partition
                    //System.out.println("partition " + partNumber + " sent to " + (6000 + idRequester ));


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

