import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatbotClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12346;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to the chat server!");

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread readerThread = new Thread(new MessageReader(in));
            readerThread.setDaemon(true);
            readerThread.start();

            Scanner scanner = new Scanner(System.in);
            String userInput;
            while (scanner.hasNextLine()) {
                userInput = scanner.nextLine();
                out.println(userInput);
            }

            scanner.close();
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class MessageReader implements Runnable {
        private BufferedReader in;

        public MessageReader(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            try {
                String serverResponse;
                while ((serverResponse = in.readLine()) != null) {
                    System.out.println(serverResponse);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}
