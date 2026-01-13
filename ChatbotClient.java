import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatbotClient {
    //set hardcoded values fr server port number and address
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12346;

    public static void main(String[] args) {
        try {
            //create a socket and setup the input output for the server
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the chat server!");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a thread to handle incoming messages
            new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            
            Scanner scanner = new Scanner(System.in);
            String userInput;
            while (true) {
                //taking user input here
                userInput = scanner.nextLine();
                out.println(userInput);
            }
           
        } catch (IOException e) {
            //if an error occurs, handle it here
            e.printStackTrace();
        }
    }
}