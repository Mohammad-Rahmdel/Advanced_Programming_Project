import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Admin is a singleton class
 */
public class Admin{


    public ArrayList<User> users = new ArrayList<>();
    private int redundancy = 2;


    public Admin() throws IOException {
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

//    public static void main(String[] args) throws IOException{
//        try {
//            Thread t = new Server();
//            t.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


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

                    System.out.println("file = " + fileDirectory);
                    System.out.println("partitions = " + partition);
                }
            } catch (IOException e){}





            try {
                socket.close();
            }
            catch (IOException e) {}
        }
    }

}

