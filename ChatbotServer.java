import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatbotServer {
    private static final int PORT = 12346;
    private static CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running and waiting for connections...");

            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String serverMessage = scanner.nextLine();
                    broadcast("[Server]: " + serverMessage, null);
                }
            }).start();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender && client.isConnected()) {
                client.sendMessage(message);
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private boolean connected = true;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                out.println("Enter your username:");
                username = in.readLine();
                System.out.println("User " + username + " connected.");
                broadcast(username + " joined the chat.", null);

                String inputLine;
                while (connected && (inputLine = in.readLine()) != null) {
                    inputLine = inputLine.trim();
                    String response = getBotResponse(inputLine);
                    
                    if (response != null) {
                        out.println("[Bot]: " + response);
                        System.out.println("[" + username + "] to Bot: " + inputLine);
                        System.out.println("Bot to [" + username + "]: " + response);
                        
                        if (inputLine.equalsIgnoreCase("bye") || inputLine.equalsIgnoreCase("goodbye") || inputLine.equalsIgnoreCase("byee")) {
                            connected = false;
                            break;
                        }
                        continue;
                    }
                    
                    System.out.println("[" + username + "]: " + inputLine);
                    broadcast("[" + username + "]: " + inputLine, this);
                }

                clients.remove(this);
                broadcast(username + " left the chat.", null);
                System.out.println("User " + username + " disconnected.");
            } catch (IOException e) {
                System.out.println("User " + username + " disconnected unexpectedly.");
            } finally {
                connected = false;
                try {
                    if (in != null) in.close();
                    if (out != null) out.close();
                    if (clientSocket != null) clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private String getBotResponse(String message) {
            String lower = message.toLowerCase();
            
            if (lower.equals("hey") || lower.equals("hi") || lower.equals("hello")) {
                return "Hello " + username + "! How can I help?";
            } else if (lower.equals("bye") || lower.equals("goodbye") || lower.equals("byee")) {
                return "Goodbye, " + username + "!";
            } else if (lower.contains("capital of india")) {
                return "The capital of India is New Delhi.";
            } else if (lower.contains("capital of france")) {
                return "The capital of France is Paris.";
            } else if (lower.contains("capital of usa") || lower.contains("capital of america")) {
                return "The capital of the USA is Washington, D.C.";
            } else if (lower.contains("capital of japan")) {
                return "The capital of Japan is Tokyo.";
            } else if (lower.contains("capital of china")) {
                return "The capital of China is Beijing.";
            } else if (lower.contains("who is the president of india")) {
                return "The President of India is Droupadi Murmu.";
            } else if (lower.contains("who is the prime minister of india")) {
                return "The Prime Minister of India is Narendra Modi.";
            } else if (lower.contains("largest planet")) {
                return "Jupiter is the largest planet in our solar system.";
            } else if (lower.contains("smallest planet")) {
                return "Mercury is the smallest planet in our solar system.";
            } else if (lower.contains("speed of light")) {
                return "The speed of light is approximately 299,792 kilometers per second.";
            } else if (lower.contains("who invented computer")) {
                return "Charles Babbage is considered the father of the computer.";
            } else if (lower.contains("who invented telephone")) {
                return "Alexander Graham Bell is credited with inventing the telephone.";
            } else if (lower.contains("tallest mountain")) {
                return "Mount Everest is the tallest mountain in the world at 8,849 meters.";
            } else if (lower.contains("longest river")) {
                return "The Nile River is considered the longest river in the world.";
            } else if (lower.contains("largest ocean")) {
                return "The Pacific Ocean is the largest ocean on Earth.";
            } else if (lower.contains("how many continents")) {
                return "There are 7 continents: Asia, Africa, North America, South America, Antarctica, Europe, and Australia.";
            } else if (lower.contains("what is java")) {
                return "Java is a high-level, object-oriented programming language developed by Sun Microsystems.";
            } else if (lower.contains("what is python")) {
                return "Python is a high-level, interpreted programming language known for its simplicity and readability.";
            } else if (lower.contains("father of nation india")) {
                return "Mahatma Gandhi is known as the Father of the Nation in India.";
            } else if (lower.contains("independence day india")) {
                return "India celebrates Independence Day on August 15th.";
            } else if (lower.contains("boiling point of water")) {
                return "The boiling point of water is 100 degrees Celsius or 212 degrees Fahrenheit at sea level.";
            } else if (lower.contains("freezing point of water")) {
                return "The freezing point of water is 0 degrees Celsius or 32 degrees Fahrenheit.";
            } else if (lower.contains("help") || lower.equals("?")) {
                return "Ask me about capitals, planets, rivers, mountains, famous people, programming languages, or general knowledge!";
            }
            
            return null;
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        public boolean isConnected() {
            return connected;
        }
    }
}
