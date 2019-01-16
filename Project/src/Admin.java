import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Admin is a singleton class
 */
public class Admin{


    public static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<String[]> files = new ArrayList<>(); // [allocation format][file name][id of uploader]
    private static int redundancy = 2;


    public Admin(){
        Thread t = new Server();
        t.start();
    }
    //    public static Admin getInstance() throws IOException{
//        if (single_instance == null)
//            single_instance = new Admin();
//
//        return single_instance;
//    }



    public void addUser(User user){
        users.add(user);
    }

    public User getUser(int id){
        for(User user : users)
            if (user.getId() == id)
                return user;

        return null;
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

    public static String allocation(String path, int n) throws FileNotFoundException {
        String[] ss = path.split("/");
        int splitter = ss.length;
        String fileName = ss[splitter-1];
        String idUploader = ss[splitter-2];

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
        allocation = allocation.substring(0, allocation.length() - 1); // removing last ','
        String[] info = {allocation, fileName, idUploader};
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

                    String allocation = Admin.allocation(fileDirectory, partition);


                    String[] ss = fileDirectory.split("/");
                    String idUploader = ss[ss.length-2];
                    int portAllocation = 5000 + Integer.parseInt(idUploader);

                    System.out.println(allocation);
                    Socket allocationSender = new Socket("localhost", portAllocation);
                    DataOutputStream out = new DataOutputStream(allocationSender.getOutputStream());
                    out.writeUTF(allocation);
                    out.close();


                }
            } catch (IOException e){}





            try {
                socket.close();
            }
            catch (IOException e) {}
        }
    }

}

