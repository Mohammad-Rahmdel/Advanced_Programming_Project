import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class RunGUI extends Thread{
    private static ArrayList<Integer> ids = new ArrayList<>();
    private static HashMap<Integer, Graphix> guis = new HashMap<>();

    private ServerSocket serverSocket;
    private int publicPort = 9999;
    Admin admin;

    public RunGUI(Admin admin){
        this.admin = admin;
        this.start();
    }

    public static boolean isValid(int id){
        for(Integer i : ids)
            if(i == id)
                return true;
        return false;
    }

    public void run(){
        Scanner scanner = new Scanner(System.in);
        Socket socket;

        try {
            serverSocket = new ServerSocket(publicPort);
        } catch (IOException e) {}


        while (true){
            socket = null;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {}
            System.out.println("GUI Listens ...");

            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());

                String input = in.readUTF();
                System.out.println("message received |RUN_GUI| = " + input);

                if(input.startsWith("upload")){                    //e.g. upload readme.txt 3 (directory) (parts)
                    String fileName = input.split(" ")[1];
                    int id = Integer.parseInt(input.split(" ")[2]);
                    if(isValid(id)){
                        int partition = Integer.parseInt(input.split(" ")[4]);
                        //String path = "/home/mohammad/Desktop/User_Files/";
                        String path = input.split(" ")[3];
                        //System.out.print("message = " + input);
                        (admin.getUser(id)).upload(fileName, partition, path);
                    }
                    else
                        System.out.println("This ID doesn't exist");
                }
                else if(input.startsWith("rename")){ // rename readme.txt 2 (new Name)
                    String fileName = input.split(" ")[1];
                    int id = Integer.parseInt(input.split(" ")[2]);
                    String newName = input.split(" ")[3];
                    String response;
                    if(isValid(id)){
                        int status = admin.hasAccess(id, fileName);
                        if(status == 1)
                            response = "This file doesn't exist!";
                        else if(status == 2)
                            response = "You cannot rename this file!";
                        else {
                            //System.out.println("Enter the new name: ");
                            //input = scanner.nextLine();
                            response = "File renamed successfully";
                            admin.rename(fileName, newName);
                            System.out.print("message = " + input);
                        }

                        Socket responseSocket = null;
                        DataOutputStream out = null;
                        System.out.println("rename = null");

                        while (responseSocket == null){
                            try {
                                responseSocket = new Socket("localhost", 12000 + id);
                                //System.out.println("rename Socket Created");
                                out = new DataOutputStream(responseSocket.getOutputStream());
                                //System.out.println("rename Stream Created");
                            } catch (UnknownHostException e){
                                //System.out.println("rename Stream error1 = " + e);
                            } catch (IOException e){
                                //System.out.println("rename Stream error2 = " + e);
                            }
                        }

                        try {
                            System.out.println("RESPONSE = " + response);
                            out.writeUTF(response);
                            out.close();
                            responseSocket.close();
                        } catch (IOException e){
                            System.out.println("sending error = " + e);
                        }

                    }
                    else
                        System.out.println("This ID doesn't exist");
                }
                else if(input.startsWith("delete")){ // delete readme.txt 2
                    String fileName = input.split(" ")[1];
                    int id = Integer.parseInt(input.split(" ")[2]);
                    String response;
                    if(isValid(id)){
                        int status = admin.hasAccess(id, fileName);
                        if(status == 1)
                            response = "This file doesn't exist!";
                        else if(status == 2)
                            response = "You cannot delete this file!";
                        else {
                            response = "File deleted successfully";
                            admin.delete(fileName);
                        }



                        Socket responseSocket = null;
                        DataOutputStream out = null;
                        while (responseSocket == null) {
                            try {
                                responseSocket = new Socket("localhost", 12000 + id);
                                System.out.println("delete response = " + response);
                                out = new DataOutputStream(responseSocket.getOutputStream());
                            } catch (IOException e) {
                               // System.out.println("error = " + e);
                            }
                        }

                        try {
                            out.writeUTF(response);
                            out.close();
                            responseSocket.close();
                        } catch (IOException e){}



                    }
                    else
                        System.out.println("This ID doesn't exist");
                }
                else if(input.startsWith("download")){ // download readme.txt 3
                    String fileName = input.split(" ")[1];
                    int id = Integer.parseInt(input.split(" ")[2]);
                    String response = "";
                    if(isValid(id)){
                        if (admin.hasFile(fileName)) {
                            (admin.getUser(id)).download(fileName);
                            response = "File downloaded successfully";
                        }
                        else
                            response = "This file doesn't exist";


                        //TODO SEND RESPONSE


                    }
                    else
                        System.out.println("This ID doesn't exist");
                }
                else if(input.equals("end")) {
                    break;
                }



            } catch (IOException e){}
        }


        try {
            socket.close();
        }
        catch (IOException e) {}
    }



    public static void main(String[] args) throws IOException {
        Admin admin = new Admin();
        RunGUI runGUI = new RunGUI(admin);

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("finish")){
            if(input.startsWith("new user")){
                int id = Integer.parseInt(input.split(" ")[2]);
                if ( (id < 1000) && (id > 0) ) {
                    admin.addUser(new User(id));
                    ids.add(id);
                    guis.put(id,new Graphix(id));
                    //Graphix tmp = guis.get(id);
                }
                else {
                    System.out.println("Invalid ID");
                }

            }
            else {
                System.out.println("Invalid Input!");
            }
            input = scanner.nextLine();
        }
    }
}


