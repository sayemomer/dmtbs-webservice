package com.web.webcontroller.implementation;

//import DataModel.EventModel;
//import Logger.Logger;

import Model.ClientModel;
import Model.MovieModel;
import com.web.webcontroller.ControllerInterface;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@WebService(endpointInterface = "com.web.webcontroller.ControllerInterface")

@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MovieManagement  implements ControllerInterface {

    private String serverID;
    private String serverName;

    public static final int Atwater_Server_Port = 8888;
    public static final int Verdun_Server_Port = 7777;
    public static final int Outramont_Server_Port = 6666;
    public static final String MOVIE_SERVER_ATWATER = "Atwater";
    public static final String MOVIE_SERVER_VERDUN = "Verdun";
    public static final String MOVIE_SERVER_OUTRAMONT = "Outramont";

    private Map<String, Map<String, MovieModel>> allMovies;
    private Map<String, Map<String, List<String>>> clientEvents;

    private Map<String, ClientModel> serverClients;

    public MovieManagement(String serverID, String serverName){
        super();
        this.serverID = serverID;
        this.serverName = serverName;
        allMovies = new ConcurrentHashMap<>();
        allMovies.put(MovieModel.AVATAR, new ConcurrentHashMap<>());
        allMovies.put(MovieModel.AVENGER, new ConcurrentHashMap<>());
        allMovies.put(MovieModel.TITANIC, new ConcurrentHashMap<>());
        clientEvents = new ConcurrentHashMap<>();
        serverClients = new ConcurrentHashMap<>();
    }

    private static int getServerPort(String branchAcronym) {
        if (branchAcronym.equalsIgnoreCase("ATW")) {
            return Atwater_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("VER")) {
            return Verdun_Server_Port;
        } else if (branchAcronym.equalsIgnoreCase("OUT")) {
            return Outramont_Server_Port;
        }
        return 1;
    }

    @Override
    public String addMovie(String movieID, String movieType, int bookingCapacity) {
        String response;
        if (isEventOfThisServer(movieID)) {
            if (showExists(movieType, movieID)) {
                if (allMovies.get(movieType).get(movieID).getMovieCapacity() <= bookingCapacity) {
                    allMovies.get(movieType).get(movieID).setMovieCapacity(bookingCapacity);
                    response = "Success: Show " + movieID + " Capacity increased to " + bookingCapacity;
//                    try {
//                        //Logger.serverLog(serverID, "null", " CORBA addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return response;
                } else {
                    response = "Failed: Show Already Exists, Cannot Decrease Booking Capacity";
//                    try {
//                       // Logger.serverLog(serverID, "null", " CORBA addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return response;
                }
            } else {
                MovieModel movie = new MovieModel(movieType, movieID, bookingCapacity);
                Map<String, MovieModel> MovieHashMap = allMovies.get(movieType);
                MovieHashMap.put(movieID, movie);
                allMovies.put(movieType, MovieHashMap);
                response = "Success: Show " + movieID + " added successfully";
//                try {
//                    Logger.serverLog(serverID, "null", " CORBA addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
        } else {
            response = "Failed: Cannot Add Show to servers other than " + serverName;
//            try {
//                Logger.serverLog(serverID, "null", " CORBA addEvent ", " eventID: " + eventID + " eventType: " + eventType + " bookingCapacity " + bookingCapacity + " ", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }
    }

    @Override
    public String removeMovie(String movieID, String movieType) {
        String response;
        if (isEventOfThisServer(movieID)) {
            if (showExists(movieType, movieID)) {
                List<String> registeredClients = allMovies.get(movieType).get(movieID).getRegisteredClientIDs();
                allMovies.get(movieType).remove(movieID);
                addCustomersToNextSameEvent(movieID, movieType, registeredClients);
                response = "Success: Event " + movieID + " Removed Successfully";
//                try {
//                    Logger.serverLog(serverID, "null", " CORBA removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            } else {
                response = "Failed: Event " + movieID + " Does Not Exist";
//                try {
//                    Logger.serverLog(serverID, "null", " CORBA removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
        } else {
            response = "Failed: Cannot Remove Event from servers other than " + serverName;
//            try {
//                Logger.serverLog(serverID, "null", " CORBA removeEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }
    }

    @Override
    public String listMovieAvailability(String movieType) {
        String response;
        Map<String, MovieModel> movies = allMovies.get(movieType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName).append(" Server ").append(movieType).append(":\n");
        if (movies.size() == 0) {
            builder.append("No show of Type ").append(movieType).append("\n");
        } else {
            for (MovieModel movie :
                    movies.values()) {
                builder.append(movie.toString()).append(" || ");
            }
        }
        builder.append("\n=====================================\n");
        String otherServer1, otherServer2;
        if (serverID.equals("ATW")) {
            otherServer1 = sendUDPMessage(Verdun_Server_Port, "listEventAvailability", "null", movieType, "null");
            otherServer2 = sendUDPMessage(Outramont_Server_Port, "listEventAvailability", "null", movieType, "null");
        } else if (serverID.equals("VER")) {
            otherServer1 = sendUDPMessage(Atwater_Server_Port, "listEventAvailability", "null", movieType, "null");
            otherServer2 = sendUDPMessage(Outramont_Server_Port, "listEventAvailability", "null", movieType, "null");
        } else {
            otherServer1 = sendUDPMessage(Atwater_Server_Port, "listEventAvailability", "null", movieType, "null");
            otherServer2 = sendUDPMessage(Verdun_Server_Port, "listEventAvailability", "null", movieType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        response = builder.toString();
//        try {
//            Logger.serverLog(serverID, "null", " CORBA listEventAvailability ", " eventType: " + eventType + " ", response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return response;
    }

    @Override
    public String bookMovie(String customerID, String movieID, String movieType) {
        String response;
        checkClientExists(customerID);
        if (isEventOfThisServer(movieID)) {
            MovieModel bookedMovie = allMovies.get(movieType).get(movieID);
            if (bookedMovie == null) {
                response = "Failed: movie " + movieID + " Does not exists";
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
            if (!bookedMovie.isFull()) {
                if (clientEvents.containsKey(customerID)) {
                    if (clientEvents.get(customerID).containsKey(movieType)) {
                        if (!clientHasMovie(customerID, movieType, movieID)) {
                            if (isCustomerOfThisServer(customerID))
                                clientEvents.get(customerID).get(movieType).add(movieID);
                        } else {
                            response = "Failed: movie " + movieType + " Already Booked";
//                            try {
//                                Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
                            return response;
                        }
                    } else {
                        if (isCustomerOfThisServer(customerID))
                            addMovieTypeAndMovie(customerID, movieType, movieID);
                    }
                } else {
                    if (isCustomerOfThisServer(customerID))
                        addCustomerAndMovie(customerID, movieType, movieID);
                }
                if (allMovies.get(movieType).get(movieID).addRegisteredClientID(customerID) == MovieModel.ADD_SUCCESS) {
                    response = "Success: Movie " + movieID + " Booked Successfully";
                } else if (allMovies.get(movieType).get(movieID).addRegisteredClientID(customerID) == MovieModel.EVENT_FULL) {
                    response = "Failed: Event " + movieID + " is Full";
                } else {
                    response = "Failed: Cannot Add You To Event " + movieID;
                }
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            } else {
                response = "Failed: Event " + movieID + " is Full";
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
        } else {
            if (clientHasMovie(customerID, movieType, movieID)) {
                String serverResponse = "Failed: Movie " + movieID + " Already Booked";
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return serverResponse;
            }
            if (!exceedWeeklyLimit(customerID, movieID.substring(4))) {
                String serverResponse = sendUDPMessage(getServerPort(movieID.substring(0, 3)), "bookEvent", customerID, movieType, movieID);
                if (serverResponse.startsWith("Success:")) {
                    if (clientEvents.get(customerID).containsKey(movieType)) {
                        clientEvents.get(customerID).get(movieType).add(movieID);
                    } else {
                        List<String> temp = new ArrayList<>();
                        temp.add(movieID);
                        clientEvents.get(customerID).put(movieType, temp);
                    }
                }
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return serverResponse;
            } else {
                response = "Failed: You Cannot Book Movie in Other Servers For This Week(Max Weekly Limit = 3)";
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA bookEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
        }
    }

    @Override
    public String getBookingSchedule(String customerID) {
        String response;
        if (!checkClientExists(customerID)) {
            response = "Booking Schedule Empty For " + customerID;
//            try {
//                Logger.serverLog(serverID, customerID, " CORBA getBookingSchedule ", "null", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }
        Map<String, List<String>> movies = clientEvents.get(customerID);
        if (movies.size() == 0) {
            response = "Booking Schedule Empty For " + customerID;
//            try {
//                Logger.serverLog(serverID, customerID, " CORBA getBookingSchedule ", "null", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }
        StringBuilder builder = new StringBuilder();
        for (String movieType :
                movies.keySet()) {
            builder.append(movieType).append(":\n");
            for (String eventID :
                    movies.get(movieType)) {
                builder.append(eventID).append(" ||");
            }
            builder.append("\n=====================================\n");
        }
        response = builder.toString();
//        try {
//            Logger.serverLog(serverID, customerID, " CORBA getBookingSchedule ", "null", response);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return response;
    }

    @Override
    public String cancelMovie(String customerID, String movieID, String movieType) {
        String response;
        if (isEventOfThisServer(movieID)) {
            if (isCustomerOfThisServer(customerID)) {
                if (!checkClientExists(customerID)) {
                    response = "Failed: You " + customerID + " Are Not Registered in " + movieID;
//                    try {
//                        Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return response;
                } else {
                    if (removeMovieIfExists(customerID, movieType, movieID)) {
                        allMovies.get(movieType).get(movieID).removeRegisteredClientID(customerID);
                        response = "Success: Event " + movieID + " Canceled for " + customerID;
//                        try {
//                            Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        return response;
                    } else {
                        response = "Failed: You " + customerID + " Are Not Registered in " + movieID;
//                        try {
//                            Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        return response;
                    }
                }
            } else {
                if (allMovies.get(movieType).get(movieID).removeRegisteredClientID(customerID)) {
                    response = "Success: Event " + movieID + " Canceled for " + customerID;
//                    try {
//                        Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return response;
                } else {
                    response = "Failed: You " + customerID + " Are Not Registered in " + movieID;
//                    try {
//                        Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    return response;
                }
            }
        } else {
            if (isCustomerOfThisServer(customerID)) {
                if (checkClientExists(customerID)) {
                    if (removeMovieIfExists(customerID, movieType, movieID)) {
                        response = sendUDPMessage(getServerPort(movieID.substring(0, 3)), "cancelMovie", customerID, movieType, movieID);
//                        try {
//                            Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        return response;
                    }
                }
            }
            response = "Failed: You " + customerID + " Are Not Registered in " + movieID;
//            try {
//                Logger.serverLog(serverID, customerID, " CORBA cancelEvent ", " eventID: " + eventID + " eventType: " + eventType + " ", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        }
    }

    @Override
    public String swapMovie(String customerID, String newMovieID, String newMovieType, String oldMovieID, String oldMovieType) {
        String response;
        if (!checkClientExists(customerID)) {
            response = "Failed: You " + customerID + " Are Not Registered in " + oldMovieID;
//            try {
//                Logger.serverLog(serverID, customerID, " CORBA swapEvent ", " oldEventID: " + oldEventID + " oldEventType: " + oldEventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return response;
        } else {
            if (clientHasMovie(customerID, oldMovieType, oldMovieID)) {
                String bookResp = "Failed: did not send book request for your newMovie " + newMovieID;
                String cancelResp = "Failed: did not send cancel request for your oldMovie " + oldMovieID;
                synchronized (this) {
                    if (onTheSameWeek(newMovieID.substring(4), oldMovieID) && exceedWeeklyLimit(customerID, newMovieID.substring(4))) {
                        cancelResp = cancelMovie(customerID, oldMovieID, oldMovieType);
                        if (cancelResp.startsWith("Success:")) {
                            bookResp = bookMovie(customerID, newMovieID, newMovieType);
                        }
                    } else {
                        bookResp = bookMovie(customerID, newMovieID, newMovieType);
                        if (bookResp.startsWith("Success:")) {
                            cancelResp = cancelMovie(customerID, oldMovieID, oldMovieType);
                        }
                    }
                }
                if (bookResp.startsWith("Success:") && cancelResp.startsWith("Success:")) {
                    response = "Success: Event " + oldMovieID + " swapped with " + newMovieID;
                } else if (bookResp.startsWith("Success:") && cancelResp.startsWith("Failed:")) {
                    cancelMovie(customerID, newMovieID, newMovieType);
                    response = "Failed: Your oldMovie " + oldMovieID + " Could not be Canceled reason: " + cancelResp;
                } else if (bookResp.startsWith("Failed:") && cancelResp.startsWith("Success:")) {
                    //hope this won't happen, but just in case.
                    String resp1 = bookMovie(customerID, oldMovieID, oldMovieType);
                    response = "Failed: Your newMovie " + newMovieID + " Could not be Booked reason: " + bookResp + " And your old event Rolling back: " + resp1;
                } else {
                    response = "Failed: on Both newMovie " + newMovieID + " Booking reason: " + bookResp + " and oldMovie " + oldMovieID + " Canceling reason: " + cancelResp;
                }
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA swapEvent ", " oldEventID: " + oldEventID + " oldEventType: " + oldEventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            } else {
                response = "Failed: You " + customerID + " Are Not Registered in " + oldMovieID;
//                try {
//                    Logger.serverLog(serverID, customerID, " CORBA swapEvent ", " oldEventID: " + oldEventID + " oldEventType: " + oldEventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", response);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                return response;
            }
        }
    }

    /**
     * for udp calls only
     *
     * @param oldNewMovieID
     * @param movieType
     * @param customerID
     * @return
     */
    public String removeMovieUDP(String oldNewMovieID, String movieType, String customerID) {
        String oldMovieID, newMovieID;
        String[] parts = oldNewMovieID.split(":");
        oldMovieID = parts[0];
        newMovieID = parts[1];
        if (!checkClientExists(customerID)) {
            return "Failed: You " + customerID + " Are Not Registered in " + oldMovieID;
        } else {
            if (removeMovieIfExists(customerID, movieType, oldMovieID)) {
                if (!newMovieID.equalsIgnoreCase("null")) {
                    bookMovie(customerID, newMovieID, movieType);
                }
                return "Success: Event " + oldMovieID + " Was Removed from " + customerID + " Schedule";
            } else {
                return "Failed: You " + customerID + " Are Not Registered in " + oldMovieID;
            }
        }
    }

    /**
     * for UDP calls only
     *
     * @param movieType
     * @return
     */
    public String listMovieAvailabilityUDP(String movieType) {
        Map<String, MovieModel> movies = allMovies.get(movieType);
        StringBuilder builder = new StringBuilder();
        builder.append(serverName).append(" Server ").append(movieType).append(":\n");
        if (movies.size() == 0) {
            builder.append("No Events of Type ").append(movieType);
        } else {
            for (MovieModel movie :
                    movies.values()) {
                builder.append(movie.toString()).append(" || ");
            }
        }
        builder.append("\n=====================================\n");
        return builder.toString();
    }

    private String sendUDPMessage(int serverPort, String method, String customerID, String movieType, String movieId) {
        DatagramSocket aSocket = null;
        String result = "";
        String dataFromClient = method + ";" + customerID + ";" + movieType + ";" + movieId;
//        try {
//            Logger.serverLog(serverID, customerID, " UDP request sent " + method + " ", " eventID: " + eventId + " eventType: " + eventType + " ", " ... ");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {
            aSocket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            aSocket.receive(reply);
            result = new String(reply.getData()).trim();
            String[] parts = result.split(";");
            result = parts[0];
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }
//        try {
//            Logger.serverLog(serverID, customerID, " UDP reply received" + method + " ", " eventID: " + eventId + " eventType: " + eventType + " ", result);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return result;

    }

    private String getNextSameMovie(Set<String> keySet, String movieType, String oldMovieID) {
        List<String> sortedIDs = new ArrayList<>(keySet);
        sortedIDs.add(oldMovieID);
        sortedIDs.sort((ID1, ID2) -> {
            Integer timeSlot1 = 0;
            switch (ID1.substring(3, 4).toUpperCase()) {
                case "M":
                    timeSlot1 = 1;
                    break;
                case "A":
                    timeSlot1 = 2;
                    break;
                case "E":
                    timeSlot1 = 3;
                    break;
            }
            int timeSlot2 = 0;
            switch (ID2.substring(3, 4).toUpperCase()) {
                case "M":
                    timeSlot2 = 1;
                    break;
                case "A":
                    timeSlot2 = 2;
                    break;
                case "E":
                    timeSlot2 = 3;
                    break;
            }
            Integer date1 = Integer.parseInt(ID1.substring(8, 10) + ID1.substring(6, 8) + ID1.substring(4, 6));
            Integer date2 = Integer.parseInt(ID2.substring(8, 10) + ID2.substring(6, 8) + ID2.substring(4, 6));
            int dateCompare = date1.compareTo(date2);
            int timeSlotCompare = timeSlot1.compareTo(timeSlot2);
            if (dateCompare == 0) {
                return ((timeSlotCompare == 0) ? dateCompare : timeSlotCompare);
            } else {
                return dateCompare;
            }
        });
        int index = sortedIDs.indexOf(oldMovieID) + 1;
        for (int i = index; i < sortedIDs.size(); i++) {
            if (!allMovies.get(movieType).get(sortedIDs.get(i)).isFull()) {
                return sortedIDs.get(i);
            }
        }
        return "Failed";
    }

    private boolean exceedWeeklyLimit(String customerID, String movieDate) {
        int limit = 0;
        for (int i = 0; i < 3; i++) {
            List<String> registeredIDs = new ArrayList<>();
            switch (i) {
                case 0:
                    if (clientEvents.get(customerID).containsKey(MovieModel.AVATAR)) {
                        registeredIDs = clientEvents.get(customerID).get(MovieModel.AVATAR);
                    }
                    break;
                case 1:
                    if (clientEvents.get(customerID).containsKey(MovieModel.AVENGER)) {
                        registeredIDs = clientEvents.get(customerID).get(MovieModel.AVENGER);
                    }
                    break;
                case 2:
                    if (clientEvents.get(customerID).containsKey(MovieModel.TITANIC)) {
                        registeredIDs = clientEvents.get(customerID).get(MovieModel.TITANIC);
                    }
                    break;
            }
            for (String eventID :
                    registeredIDs) {
                if (onTheSameWeek(movieDate, eventID) && !isEventOfThisServer(eventID)) {
                    limit++;
                }
                if (limit == 3)
                    return true;
            }
        }
        return false;
    }

    private void addCustomersToNextSameEvent(String oldEventID, String eventType, List<String> registeredClients) {
        for (String customerID :
                registeredClients) {
            if (customerID.substring(0, 3).equals(serverID)) {
                removeMovieIfExists(customerID, eventType, oldEventID);
            }
            //                if (res.startsWith("Success:")) {
            //                    tryToBookNextSameEvent(customerID, eventType, oldEventID);
            //                } else {
            //                    String response = "Acquiring nextSameEvent for Client (" + customerID + "):" + res;
            //                    try {
            //                        Logger.serverLog(serverID, customerID, " addCustomersToNextSameEvent ", " oldEventID: " + oldEventID + " eventType: " + eventType + " ", response);
            //                    } catch (IOException e) {
            //                        e.printStackTrace();
            //                    }
            //                }

            tryToBookNextSameEvent(customerID, eventType, oldEventID);
        }
    }

    private void tryToBookNextSameEvent(String customerID, String movieType, String oldMovieID) {
        String response;
        String nextSameEventResult = getNextSameMovie(allMovies.get(movieType).keySet(), movieType, oldMovieID);
        if (nextSameEventResult.equals("Failed")) {
            if (!customerID.substring(0, 3).equals(serverID)) {
                sendUDPMessage(getServerPort(customerID.substring(0, 3)), "removeEvent", customerID, movieType, oldMovieID + ":null");
            }
            response = "Acquiring nextSameEvent for Client (" + customerID + "):" + nextSameEventResult;
//            try {
//                Logger.serverLog(serverID, customerID, " addCustomersToNextSameEvent ", " oldEventID: " + oldEventID + " eventType: " + eventType + " ", response);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } else {
            if (customerID.substring(0, 3).equals(serverID)) {
                bookMovie(customerID, nextSameEventResult, movieType);
            } else {
                String oldNewEventID = oldMovieID + ":" + nextSameEventResult;
                sendUDPMessage(getServerPort(customerID.substring(0, 3)), "removeEvent", customerID, movieType, oldNewEventID);
            }
        }
    }

    private synchronized boolean showExists(String movieType, String movieID) {
        return allMovies.get(movieType).containsKey(movieID);
    }

    private synchronized boolean isEventOfThisServer(String eventID) {
        return MovieModel.detectMovieServer(eventID).equals(serverName);
    }

    private synchronized boolean checkClientExists(String customerID) {
        if (!serverClients.containsKey(customerID)) {
            addNewCustomerToClients(customerID);
            return false;
        } else {
            return true;
        }
    }

    private synchronized boolean clientHasMovie(String customerID, String movieType, String movieID) {
        if (clientEvents.get(customerID).containsKey(movieType)) {
            return clientEvents.get(customerID).get(movieType).contains(movieID);
        } else {
            return false;
        }
    }

    private boolean removeMovieIfExists(String customerID, String movieType, String movieID) {
        if (clientEvents.get(customerID).containsKey(movieType)) {
            return clientEvents.get(customerID).get(movieType).remove(movieID);
        } else {
            return false;
        }
    }

    private synchronized void addCustomerAndMovie(String customerID, String movieType, String movieID) {
        Map<String, List<String>> temp = new ConcurrentHashMap<>();
        List<String> temp2 = new ArrayList<>();
        temp2.add(movieID);
        temp.put(movieType, temp2);
        clientEvents.put(customerID, temp);
    }

    private synchronized void addMovieTypeAndMovie(String customerID, String movieType, String movieID) {
        List<String> temp = new ArrayList<>();
        temp.add(movieID);
        clientEvents.get(customerID).put(movieID, temp);
    }

    private boolean isCustomerOfThisServer(String customerID) {
        return customerID.substring(0, 3).equals(serverID);
    }

    private boolean onTheSameWeek(String newEventDate, String eventID) {
        if (eventID.substring(6, 8).equals(newEventDate.substring(2, 4)) && eventID.substring(8, 10).equals(newEventDate.substring(4, 6))) {
            int day1 = Integer.parseInt(eventID.substring(4, 6));
            int day2 = Integer.parseInt(newEventDate.substring(0, 2));
            if (day1 % 7 == 0) {
                day1--;
            }
            if (day2 % 7 == 0) {
                day2--;
            }
            int week1 = day1 / 7;
            int week2 = day2 / 7;
//                    int diff = Math.abs(day2 - day1);
            return week1 == week2;
        } else {
            return false;
        }
    }

    public Map<String, Map<String, MovieModel>> getAllEvents() {
        return allMovies;
    }

    public Map<String, Map<String, List<String>>> getClientEvents() {
        return clientEvents;
    }

    public Map<String, ClientModel> getServerClients() {
        return serverClients;
    }

    public void addNewMovie(String movieID, String movieType, int capacity) {
        MovieModel sampleConf = new MovieModel(movieType, movieID, capacity);
        allMovies.get(movieType).put(movieID, sampleConf);
    }

    public void addNewCustomerToClients(String customerID) {
        ClientModel newCustomer = new ClientModel(customerID);
        serverClients.put(newCustomer.getClientID(), newCustomer);
        Map<String, List<String>> emptyEvents = new ConcurrentHashMap<>();
        emptyEvents.put(MovieModel.AVATAR, new ArrayList<>());
        emptyEvents.put(MovieModel.AVENGER, new ArrayList<>());
        emptyEvents.put(MovieModel.TITANIC, new ArrayList<>());
        clientEvents.put(newCustomer.getClientID(), new ConcurrentHashMap<>());
    }
}