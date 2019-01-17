import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Run {

    private static ArrayList<Integer> ids = new ArrayList<>();

    public static boolean isValid(int id){
        for(Integer i : ids)
            if(i == id)
                return true;
        return false;
    }

    public static void main(String[] args) throws IOException {

        Admin admin = new Admin();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("finish")){

            if(input.startsWith("new user")){
                int id = Integer.parseInt(input.split(" ")[2]);
                if ( (id < 1000) && (id > 0) ) {
                    admin.addUser(new User(id));
                    ids.add(id);
                }
                else
                    System.out.println("Invalid ID");

            }
            else if(input.startsWith("open user")){
                int id = Integer.parseInt(input.split(" ")[2]);
                // TODO
                // opens GUI
            }
            else if(input.startsWith("upload")){                    //e.g. upload readme.txt 3
                String fileName = input.split(" ")[1];
                int id = Integer.parseInt(input.split(" ")[2]);
                if(isValid(id)){
                    System.out.println("Enter the number of partitions: ");
                    input = scanner.nextLine();
                    int partition = Integer.parseInt(input);

                    (admin.getUser(id)).upload(fileName, partition);
                }
                else
                    System.out.println("This ID doesn't exist");
            }
            else if(input.startsWith("rename")){ // rename readme.txt 2
                String fileName = input.split(" ")[1];
                int id = Integer.parseInt(input.split(" ")[2]);
                if(isValid(id)){
                    int status = admin.hasAccess(id, fileName);
                    if(status == 1)
                        System.out.println("This file doesn't exist!");
                    else if(status == 2)
                        System.out.println("You cannot rename this file!");
                    else {
                        System.out.println("Enter the new name: ");
                        input = scanner.nextLine();
                        admin.rename(fileName, input);
                    }
                }
                else
                    System.out.println("This ID doesn't exist");
            }
            else if(input.startsWith("delete")){ // delete readme.txt 2
                String fileName = input.split(" ")[1];
                int id = Integer.parseInt(input.split(" ")[2]);
                if(isValid(id)){
                    int status = admin.hasAccess(id, fileName);
                    if(status == 1)
                        System.out.println("This file doesn't exist!");
                    else if(status == 2)
                        System.out.println("You cannot delete this file!");
                    else {
                        admin.delete(fileName);
                    }
                }
                else
                    System.out.println("This ID doesn't exist");
            }
            else if(input.startsWith("download")){ // download readme.txt 3
                String fileName = input.split(" ")[1];
                int id = Integer.parseInt(input.split(" ")[2]);
                if(isValid(id)){
                    if (admin.hasFile(fileName))
                        (admin.getUser(id)).download(fileName);
                    else
                        System.out.println("This file doesn't exist");
                }
                else
                    System.out.println("This ID doesn't exist");
            }
            else {
                System.out.println("Invalid Input!");
            }
            input = scanner.nextLine();
        }
    }
}
