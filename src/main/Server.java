package main;

import com.web.service.implementation.MovieManagement;

import javax.xml.ws.Endpoint;
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
//            Runnable task = () -> {
//                listenForRequest(service, serverUdpPort, serverName, serverID);
//            };
//            Thread thread = new Thread(task);
//            thread.start();

            } catch (Exception e) {
//            System.err.println("Exception: " + e);
                e.printStackTrace(System.out);
                //Logger.serverLog(serverID, "Exception: " + e);
            }

//        System.out.println(serverName + " Server Shutting down");
//        Logger.serverLog(serverID, " Server Shutting down");

        }

    }
}
