import java.io.IOException;
import java.util.Scanner;

public class Run {

    public static void main(String[] args) throws IOException {

        Admin admin = new Admin();

        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.equals("finish")){

            if(input.startsWith("new user")){
                int id = Integer.parseInt(input.split(" ")[2]);
                if ( (id < 999) && (id > 0) )
                    admin.addUser(new User(id));
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
                System.out.println("Enter the number of partitions: ");
                input = scanner.nextLine();
                int partition = Integer.parseInt(input);

                (admin.getUser(id)).upload(fileName, partition);
            }
            else {
                System.out.println("Invalid Input!");
            }
            input = scanner.nextLine();
        }
    }
}
