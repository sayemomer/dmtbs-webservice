package main;

import com.web.webcontroller.implementation.MovieManagement;

import javax.xml.ws.Endpoint;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(new ServerInstanceRunnable("ATW", args));
        executor.submit(new ServerInstanceRunnable("VER", args));
        executor.submit(new ServerInstanceRunnable("OUT", args));
        executor.shutdown();
    }

    private static class ServerInstanceRunnable implements Runnable {
        private final String serverName;
        private final String[] args;

        public ServerInstanceRunnable(String serverName, String[] args) {
            this.serverName = serverName;
            this.args = args;
        }

        @Override
        public void run() {
            try {
                ServerInstance serverInstance = new ServerInstance(serverName, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class ServerInstance {

        private String serverID;
        private String serverName;
        private String serverEndpoint;
        private int serverUdpPort;

        public ServerInstance(String serverID, String[] args) throws Exception {
            this.serverID = serverID;
            switch (serverID) {
                case "ATW":
                    serverName = MovieManagement.MOVIE_SERVER_ATWATER;
                    serverUdpPort = MovieManagement.Atwater_Server_Port;
                    serverEndpoint = "http://localhost:8080/atwater";
                    break;
                case "VER":
                    serverName = MovieManagement.MOVIE_SERVER_VERDUN;
                    serverUdpPort = MovieManagement.Verdun_Server_Port;
                    serverEndpoint = "http://localhost:8080/verdun";
                    break;
                case "OUT":
                    serverName = MovieManagement.MOVIE_SERVER_OUTRAMONT;
                    serverUdpPort = MovieManagement.Outramont_Server_Port;
                    serverEndpoint = "http://localhost:8080/outramont";
                    break;
            }
            try {
                System.out.println(serverName + " Server Started...");
                //Logger.serverLog(serverID, " Server Started...")

                MovieManagement service = new MovieManagement(serverID, serverName);

                Endpoint endpoint = Endpoint.publish(serverEndpoint, service);

                System.out.println(serverName + " Server is Up & Running");
                //Logger.serverLog(serverID, " Server is Up & Running");

//            addTestData(server);
            Runnable task = () -> {
                listenForRequest(service, serverUdpPort, serverName, serverID);
            };
            Thread thread = new Thread(task);
            thread.start();

            } catch (Exception e) {
//            System.err.println("Exception: " + e);
                e.printStackTrace(System.out);
                //Logger.serverLog(serverID, "Exception: " + e);
            }

//        System.out.println(serverName + " Server Shutting down");
//        Logger.serverLog(serverID, " Server Shutting down");

        }

        private static void listenForRequest(MovieManagement obj, int serverUdpPort, String serverName, String serverID) {
            DatagramSocket aSocket = null;
            String sendingResult = "";
            try {
                aSocket = new DatagramSocket(serverUdpPort);
                byte[] buffer = new byte[1000];
                System.out.println(serverName + " UDP Server Started at port " + aSocket.getLocalPort() + " ............");
                //Logger.serverLog(serverID, " UDP Server Started at port " + aSocket.getLocalPort());
                while (true) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    String sentence = new String(request.getData(), 0,
                            request.getLength());
                    String[] parts = sentence.split(";");
                    String method = parts[0];
                    String customerID = parts[1];
                    String eventType = parts[2];
                    String eventID = parts[3];
                    if (method.equalsIgnoreCase("removeEvent")) {
                        //Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                        String result = obj.removeMovieUDP(eventID, eventType, customerID);
                        sendingResult = result + ";";
                    } else if (method.equalsIgnoreCase("listEventAvailability")) {
                        //Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventType: " + eventType + " ", " ...");
                        String result = obj.listMovieAvailabilityUDP(eventType);
                        sendingResult = result + ";";
                    } else if (method.equalsIgnoreCase("bookEvent")) {
                       // Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                        String result = obj.bookMovie(customerID, eventID, eventType);
                        sendingResult = result + ";";
                    } else if (method.equalsIgnoreCase("cancelEvent")) {
                       // Logger.serverLog(serverID, customerID, " UDP request received " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", " ...");
                        String result = obj.cancelMovie(customerID, eventID, eventType);
                        sendingResult = result + ";";
                    }
                    sendingResult = sendingResult.trim();
                    byte[] sendData = sendingResult.getBytes();
                    DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                            request.getPort());
                    aSocket.send(reply);
                   // Logger.serverLog(serverID, customerID, " UDP reply sent " + method + " ", " eventID: " + eventID + " eventType: " + eventType + " ", sendingResult);
                }
            } catch (SocketException e) {
                System.err.println("SocketException: " + e);
                e.printStackTrace(System.out);
            } catch (IOException e) {
                System.err.println("IOException: " + e);
                e.printStackTrace(System.out);
            } finally {
                if (aSocket != null)
                    aSocket.close();
            }
        }

    }
}
