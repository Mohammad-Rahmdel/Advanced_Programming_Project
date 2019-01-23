import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Admin is a singleton class
 */
public class Admin{


    public static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<String[]> files = new ArrayList<>(); // [allocation format][file name][id of uploader][directory]
    private static int redundancy = 2;


    public Admin(){
        Thread t = new Server(this);
        t.start();
    }
    //    public static Admin getInstance() throws IOException{
//        if (single_instance == null)
//            single_instance = new Admin();
//
//        return single_instance;
//    }


    public ArrayList<String[]> getFiles(){
        return files;
    }


    public void addUser(User user){
        users.add(user);
    }

    public static boolean userExists(int id){
        for (User u : users)
            if (u.getId() == id)
                return true;

        return false;
    }

    public User getUser(int id){
        for(User user : users)
            if (user.getId() == id)
                return user;

        return null;
    }

    public boolean hasFile(String fileName){
        for (String[] x : files){
            if (x[1].equals(fileName))
                return true;
        }
        return false;
    }


    public static String downloadManager(String fileName){
        String allocation = "";
        for(String[] x: files) {
            if (x[1].equals(fileName))
                allocation = x[0];
        }
        System.out.println("alloc = " + allocation);
        String[] split = allocation.split(",");
        int numberOfParts = split.length / redundancy;
        int cnt = 0;
        String downloadFormat = "";
        System.out.println("N = " + numberOfParts);
        for (int i = 1; i <= numberOfParts; i++){
            boolean flag = true;
            for (String y : split){
                if ((flag) && Integer.parseInt(y.split(":")[0]) == i){ // part i.th not found yet
                    if(userExists(Integer.parseInt(y.split(":")[1]))){
                        downloadFormat += y + ",";
                        cnt++;
                        flag = false;
                    }
                }
            }
        }

        downloadFormat = downloadFormat.substring(0,downloadFormat.length() - 1);
        if (cnt == numberOfParts)
            return "" + numberOfParts + "#" + downloadFormat;   // e.g. 3#1:2,2:3,3:5
        else
            return "failed";
    }


    public int hasAccess(int id, String name){
        int status = 1; // Wrong file name
        int foundId = -1;
        for(String[] x : files){
            if(x[1].equals(name)){
                status = 2; // name ok. id doesn't have access
                foundId = Integer.parseInt(x[2]);
                break;
            }
        }
        if(status == 2){ // if file name is ok
            if(id == foundId)
                status = 3; // this id has access
        }
        return status;
    }

    public static String allocation(String path, int n, String id) throws FileNotFoundException {
        String[] ss = path.split("/");
        String directory = "";
        for(int i = 0; i < ss.length - 1; i++){
            directory += ss[i] + "/";
        }

        int splitter = ss.length;
        String fileName = ss[splitter-1];
        String idUploader = id;//ss[splitter-2];

        System.out.println("path = " + path);
        System.out.println("name = " + fileName);
        System.out.println("ID = " + idUploader);

        long fileSize = new File(path).length();
        System.out.println("size of file = " + fileSize);


        String allocation = "";
        ArrayList<User> sortedUsersBySize = sortUsers(users);
        int usersNo = sortedUsersBySize.size();

        int index = 0;
        for(int i = 1; i <= n; i++){
            for(int j = 1; j <= redundancy; j++){
                if(index == sortedUsersBySize.size())
                    index = 0;
                allocation += (i + ":" + sortedUsersBySize.get(index).getId() + ",");

                index++;
            }
        }
        allocation = allocation.substring(0, allocation.length() - 1); // omitting last ','
        String[] info = {allocation, fileName, idUploader, directory};
        files.add(info);
        return allocation;
    }

    public static ArrayList<User> sortUsers(ArrayList<User> users){
        users.sort(Comparator.comparing(a -> a.getTotalSize()));    // sorting by size using lambda expression
        return users;
    }


    public void delete(String fileName){
        int y = 0;
        String[] info = null;
        for (String[] x: files){
            if(x[1].equals(fileName)){
                info = x;
                files.remove(y);
                System.out.println("removed from admin");
                break;
            }
            y++;
        }
        String[] id_part = info[0].split(",");
        int[] idFixed = new int[id_part.length];
        int[] partFixed = new int[id_part.length];
        for (int i = 0; i < id_part.length; i++){
            idFixed[i] = Integer.parseInt(id_part[i].split(":")[1]);
            partFixed[i] = Integer.parseInt(id_part[i].split(":")[0]);
        }
        for (int i = 0; i < id_part.length; i++){ // send delete command
            try {
                Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
                DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());
                String command = "delete " + (fileName + "." +  partFixed[i]);
                outTmp.writeUTF(command);

                socketTmp.close();
                outTmp.close();
            } catch (IOException e){}
        }
    }

    public void rename(String fileName, String newName){
        String[] info = null;
        for (String[] x: files){
            if(x[1].equals(fileName)){
                info = x;
                x[1] = newName;
                break;
            }
        }
        String[] id_part = info[0].split(",");
        int[] idFixed = new int[id_part.length];
        int[] partFixed = new int[id_part.length];
        for (int i = 0; i < id_part.length; i++){
            idFixed[i] = Integer.parseInt(id_part[i].split(":")[1]);
            partFixed[i] = Integer.parseInt(id_part[i].split(":")[0]);
        }
        for (int i = 0; i < id_part.length; i++){ // send rename command
            try {
                Socket socketTmp = new Socket("localhost", 4000 + idFixed[i]);
                DataOutputStream outTmp = new DataOutputStream(socketTmp.getOutputStream());
                String command = "rename " + (fileName + "." +  partFixed[i]) + " " + newName;
                outTmp.writeUTF(command);

                socketTmp.close();
                outTmp.close();
            } catch (IOException e){}
        }
    }

}

class Server extends Thread{
    private ServerSocket serverSocket;
    private int publicPort = 8888;
    Admin admin;

    public Server(Admin admin){
        this.admin = admin;
    }

    public void run(){
        try {
            serverSocket = new ServerSocket(publicPort);
        } catch (IOException e) {}

        while (true){
            Socket socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {}
            System.out.println("new Thread Created");

            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                String request = in.readUTF();

                if (request.startsWith("upload")){
                    String fileDirectory = request.split(" ")[1];
                    int partition = Integer.parseInt(request.split(" ")[2]);

                    String idUploader = request.split(" ")[3];

                    String allocation = Admin.allocation(fileDirectory, partition, idUploader);


                    int portAllocation = 5000 + Integer.parseInt(idUploader);

                    System.out.println(allocation);
                    Socket allocationSender = new Socket("localhost", portAllocation);
                    DataOutputStream out = new DataOutputStream(allocationSender.getOutputStream());
                    out.writeUTF(allocation);
                    out.close();


                }
                else if(request.startsWith("download")){
                    String fileName = request.split(" ")[1];
                    int id = Integer.parseInt(request.split(" ")[2]);
                    String answer = Admin.downloadManager(fileName);
                    System.out.println("download answer = " + answer);
                    Socket downloadResponse = new Socket("localhost", 12345);
                    DataOutputStream out = new DataOutputStream(downloadResponse.getOutputStream());
                    out.writeUTF(answer.split("#")[0]);
                    out.close();

                    String[] downloadFormat = (answer.split("#")[1]).split(",");
                    for (int i = 0; i < downloadFormat.length; i++){ // informs peers to send the specific partition
                        int peerId = Integer.parseInt(downloadFormat[i].split(":")[1]);
                        int partNo = Integer.parseInt(downloadFormat[i].split(":")[0]);
                        String message = "send " + id + " " + fileName +
                                "." + partNo; // send to id partNo of fileName.partNo

                        Socket sendInfo = new Socket("localhost", 4000 + peerId); // -> portListen
                        DataOutputStream out2 = new DataOutputStream(sendInfo.getOutputStream());
                        out2.writeUTF(message);
                        out2.close();

                    }

                }
                else if(request.startsWith("send_list")){
                    int portN = 13000 + Integer.parseInt(request.split(" ")[1]);
                    Socket socketList = null;
                    DataOutputStream outList = null;
                    try {
                        socketList = new Socket("localhost", portN);
                        outList = new DataOutputStream(socketList.getOutputStream());
                    } catch (UnknownHostException e){} catch (IOException e){}
                    String answer = "";


                    ArrayList<String[]> listOfFiles = admin.getFiles();
                    for (String[] f: listOfFiles){
                        answer += f[1] + " " + f[3] + " " + f[0] + " " + f[2] + "@"; //name directory allocation owner
                    }
                    if(answer.length() > 0)
                        answer = answer.substring(0,answer.length() - 1); //omitting last *
                    try {
                        outList.writeUTF(answer);
                        outList.close();
                        socketList.close();
                    } catch (IOException e){}
                }
            } catch (IOException e){}





            try {
                socket.close();
            }
            catch (IOException e) {}
        }
    }

}