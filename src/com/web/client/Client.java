package com.web.client;

//import Logger.Logger;
import DataModel.MovieModel;
import com.web.service.WebInterface;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.Scanner;


public class Client {
    public static final int CUSTOMER_BOOK_MOVIE = 1;
    public static final int CUSTOMER_GET_BOOKING_SCHEDULE = 2;
    public static final int CUSTOMER_CANCEL_MOVIE = 3;
    public static final int CUSTOMER_SWAP_MOVIE = 4;
    public static final int CUSTOMER_LOGOUT = 5;

    public static final int ADMIN_ADD_MOVIE =1 ;
    public static final int ADMIN_REMOVE_MOVIE = 2;
    public static final int ADMIN_LIST_MOVIE_AVAILABILITY = 3;
    public static final int ADMIN_BOOK_MOVIE = 4;
    public static final int ADMIN_GET_BOOKING_SCHEDULE = 5;
    public static final int ADMIN_CANCEL_EVENT = 6;
    public static final int ADMIN_SWAP_EVENT = 7;
    public static final int ADMIN_LOGOUT = 8;

    public static final int SERVER_ATWATER = 2964;
    public static final int SERVER_VERDUN = 2965;
    public static final int SERVER_OUTRAMONT = 2966;
    public static final String MOVIE_MANAGEMENT_REGISTERED_NAME = "MOVIE_MANAGEMENT";

    public static final int USER_TYPE_CUSTOMER = 1;
    public static final int USER_TYPE_ADMIN = 2;

    static Scanner input;
    public static Service atwaterService;
    public static Service verdunService;
    public static Service outramontService;
    private static WebInterface obj;

    public static void main(String[] args) throws Exception {
        URL atwaterURL = new URL("http://localhost:8080/atwater?wsdl");
        QName atwaterlQName = new QName("http://implementation.service.web.com/", "MovieManagementService");
        atwaterService = Service.create(atwaterURL, atwaterlQName);

        URL verdunURL = new URL("http://localhost:8080/verdun?wsdl");
        QName verdunQName = new QName("http://implementation.service.web.com/", "MovieManagementService");
        verdunService = Service.create(verdunURL, verdunQName);

        URL outramontURL = new URL("http://localhost:8080/outramont?wsdl");
        QName outramontQName = new QName("http://implementation.service.web.com/", "MovieManagementService");
        outramontService = Service.create(outramontURL, outramontQName);
        init();
    }

    public static void init() throws Exception {
        input = new Scanner(System.in);
        String userID;
        System.out.println("*************************************");
        System.out.println("*************************************");
        System.out.println("Please Enter your UserID(For Concurrency test enter 'ConTest'):");
        userID = input.next().trim().toUpperCase();
//        if (userID.equalsIgnoreCase("ConTest")) {
//            startConcurrencyTest();
//        } else {
//        Logger.clientLog(userID, " login attempt");
            switch (checkUserType(userID)) {
                case USER_TYPE_CUSTOMER:
                    try {
                        System.out.println("Customer Login successful (" + userID + ")");
//                    Logger.clientLog(userID, " Customer Login successful");
                        customer(userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case USER_TYPE_ADMIN:
                    try {
                        System.out.println("Manager Login successful (" + userID + ")");
//                    Logger.clientLog(userID, " Manager Login successful");
                        admin(userID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    System.out.println("!!UserID is not in correct format");
//                Logger.clientLog(userID, " UserID is not in correct format");
//                Logger.deleteALogFile(userID);
                    init();
            }
//        }
    }

//    private static void startConcurrencyTest() throws Exception {
//        System.out.println("Concurrency Test Starting for BookEvent");
//        System.out.println("Connecting Montreal Server...");
//        String eventType = EventModel.CONFERENCES;
//        String eventID = "MTLE101020";
//        WebInterface servant = montrealService.getPort(WebInterface.class);
//        System.out.println("adding " + eventID + " " + eventType + " with capacity 2 to Montreal Server...");
//        String response = servant.addEvent(eventID, eventType, 2);
//        System.out.println(response);
//        Runnable task1 = () -> {
//            String customerID = "MTLC2345";
////            System.out.println("Connecting Montreal Server for " + customerID);
//            String res = servant.bookEvent(customerID, eventID, eventType);
//            System.out.println("Booking response for " + customerID + " " + res);
//        };
//        Runnable task2 = () -> {
//            String customerID = "MTLC3456";
////            System.out.println("Connecting Montreal Server for " + customerID);
//            String res = servant.bookEvent(customerID, eventID, eventType);
//            System.out.println("Booking response for " + customerID + " " + res);
//        };
//        Runnable task3 = () -> {
//            String customerID = "MTLC4567";
////            System.out.println("Connecting Montreal Server for " + customerID);
//            String res = servant.bookEvent(customerID, eventID, eventType);
//            System.out.println("Booking response for " + customerID + " " + res);
//        };
//        Runnable task4 = () -> {
////            System.out.println("Connecting Montreal Server for " + customerID);
//            String res = servant.cancelEvent("MTLC2345", eventID, eventType);
//            System.out.println("Canceling response for MTLC2345" + " " + res);
//
//            res = servant.cancelEvent("MTLC3456", eventID, eventType);
//            System.out.println("Canceling response for MTLC3456" + " " + res);
//
//            res = servant.cancelEvent("MTLC4567", eventID, eventType);
//            System.out.println("Canceling response for MTLC4567" + " " + res);
//        };
//
//        Runnable task5 = () -> {
////            System.out.println("Connecting Montreal Server for " + customerID);
//            String res = servant.removeEvent(eventID, eventType);
//            System.out.println("removeEvent response for " + eventID + " " + res);
//        };
//
//        Thread thread1 = new Thread(task1);
//        Thread thread2 = new Thread(task2);
//        Thread thread3 = new Thread(task3);
//        Thread thread4 = new Thread(task4);
//        Thread thread5 = new Thread(task5);
////        synchronized (thread1) {
//        thread1.start();
//        thread2.start();
//        thread3.start();
////        }
//        thread1.join();
//        thread2.join();
//        thread3.join();
//
//        //cancelling the event for clients
//        thread4.start();
//        thread4.join();
////        if (!thread1.isAlive() && !thread2.isAlive() && !thread3.isAlive() && !thread4.isAlive() && !thread5.isAlive()) {
//        System.out.println("Concurrency Test Finished for BookEvent");
//        thread5.start();
//        thread5.join();
//        init();
////        }
//    }

    private static String getServerID(String userID) {
        String branchAcronym = userID.substring(0, 3);
        if (branchAcronym.equalsIgnoreCase("ATW")) {
            obj = atwaterService.getPort(WebInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("VER")) {
            obj = verdunService.getPort(WebInterface.class);
            return branchAcronym;
        } else if (branchAcronym.equalsIgnoreCase("OUT")) {
            obj = outramontService.getPort(WebInterface.class);
            return branchAcronym;
        }
        return "1";
    }

    private static int checkUserType(String userID) {
        if (userID.length() == 8) {
            if (userID.substring(0, 3).equalsIgnoreCase("ATW") ||
                    userID.substring(0, 3).equalsIgnoreCase("VER") ||
                    userID.substring(0, 3).equalsIgnoreCase("OUT")) {
                if (userID.substring(3, 4).equalsIgnoreCase("C")) {
                    return USER_TYPE_CUSTOMER;
                } else if (userID.substring(3, 4).equalsIgnoreCase("A")) {
                    return USER_TYPE_ADMIN;
                }
            }
        }
        return 0;
    }

    private static void customer(String customerID) throws Exception {
        String serverID = getServerID(customerID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        printMenu(USER_TYPE_CUSTOMER);
        int menuSelection = input.nextInt();
        String movieType;
        String movieID;
        String serverResponse;
        switch (menuSelection) {
            case CUSTOMER_BOOK_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to bookEvent");
                serverResponse = obj.bookMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case CUSTOMER_GET_BOOKING_SCHEDULE:
               // Logger.clientLog(customerID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " null ", serverResponse);
                break;
            case CUSTOMER_CANCEL_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to cancelEvent");
                serverResponse = obj.cancelMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " bookEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case CUSTOMER_SWAP_MOVIE:
                System.out.println("Please Enter the OLD movie to be replaced");
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                System.out.println("Please Enter the NEW movie to be replaced");
                String newMovieType = promptForMovieType();
                String newMovieID = promptForMovieID();
                //Logger.clientLog(customerID, " attempting to swapEvent");
                serverResponse = obj.swapMovie(customerID, newMovieID, newMovieType, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(customerID, " swapEvent", " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case CUSTOMER_LOGOUT:
                repeat = false;
                //Logger.clientLog(customerID, " attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            customer(customerID);
        }
    }

    private static void admin(String movieAdminID) throws Exception {
        String serverID = getServerID(movieAdminID);
        if (serverID.equals("1")) {
            init();
        }
        boolean repeat = true;
        printMenu(USER_TYPE_ADMIN);
        String customerID;
        String movieType;
        String movieID;
        String serverResponse;
        int capacity;
        int menuSelection = input.nextInt();
        switch (menuSelection) {
            case ADMIN_ADD_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                capacity = promptForCapacity();
               // Logger.clientLog(eventManagerID, " attempting to addEvent");
                serverResponse = obj.addMovie(movieID, movieType, capacity);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " addEvent", " eventID: " + eventID + " eventType: " + eventType + " eventCapacity: " + capacity + " ", serverResponse);
                break;
            case ADMIN_REMOVE_MOVIE:
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to removeEvent");
                serverResponse = obj.removeMovie(movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " removeEvent", " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_LIST_MOVIE_AVAILABILITY:
                movieType = promptForMovieType();
                //Logger.clientLog(eventManagerID, " attempting to listEventAvailability");
                serverResponse = obj.listMovieAvailability(movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " listEventAvailability", " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_BOOK_MOVIE:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to bookEvent");
                serverResponse = obj.bookMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " bookEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_GET_BOOKING_SCHEDULE:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                //Logger.clientLog(eventManagerID, " attempting to getBookingSchedule");
                serverResponse = obj.getBookingSchedule(customerID);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " getBookingSchedule", " customerID: " + customerID + " ", serverResponse);
                break;
            case ADMIN_CANCEL_EVENT:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to cancelEvent");
                serverResponse = obj.cancelMovie(customerID, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " cancelEvent", " customerID: " + customerID + " eventID: " + eventID + " eventType: " + eventType + " ", serverResponse);
                break;
            case ADMIN_SWAP_EVENT:
                customerID = askForCustomerIDFromManager(movieAdminID.substring(0, 3));
                System.out.println("Please Enter the OLD movie to be swapped");
                movieType = promptForMovieType();
                movieID = promptForMovieID();
                System.out.println("Please Enter the NEW movie to be swapped");
                String newMovieType = promptForMovieType();
                String newMovieID = promptForMovieID();
                //Logger.clientLog(eventManagerID, " attempting to swapEvent");
                serverResponse = obj.swapMovie(customerID, newMovieID, newMovieType, movieID, movieType);
                System.out.println(serverResponse);
                //Logger.clientLog(eventManagerID, " swapEvent", " customerID: " + customerID + " oldEventID: " + eventID + " oldEventType: " + eventType + " newEventID: " + newEventID + " newEventType: " + newEventType + " ", serverResponse);
                break;
            case ADMIN_LOGOUT:
                repeat = false;
                //Logger.clientLog(eventManagerID, "attempting to Logout");
                init();
                break;
        }
        if (repeat) {
            admin(movieAdminID);
        }
    }

    private static String askForCustomerIDFromManager(String branchAcronym) {
        System.out.println("Please enter a customerID(Within " + branchAcronym + " Server):");
        String userID = input.next().trim().toUpperCase();
        if (checkUserType(userID) != USER_TYPE_CUSTOMER || !userID.substring(0, 3).equals(branchAcronym)) {
            return askForCustomerIDFromManager(branchAcronym);
        } else {
            return userID;
        }
    }

    private static void printMenu(int userType) {
        System.out.println("*************************************");
        System.out.println("Please choose an option below:");
        if (userType == USER_TYPE_CUSTOMER) {
            System.out.println("1.Book Event");
            System.out.println("2.Get Booking Schedule");
            System.out.println("3.Cancel Event");
            System.out.println("4.Swap Event");
            System.out.println("5.Logout");
        } else if (userType == USER_TYPE_ADMIN) {
            System.out.println("1.Add Event");
            System.out.println("2.Remove Event");
            System.out.println("3.List Event Availability");
            System.out.println("4.Book Event");
            System.out.println("5.Get Booking Schedule");
            System.out.println("6.Cancel Event");
            System.out.println("7.Swap Event");
            System.out.println("8.Logout");
        }
    }

    private static String promptForMovieType() {
        System.out.println("*************************************");
        System.out.println("Please choose an eventType below:");
        System.out.println("1.Conferences");
        System.out.println("2.Seminars");
        System.out.println("3.Trade Shows");
        switch (input.nextInt()) {
            case 1:
                return MovieModel.AVATAR;
            case 2:
                return MovieModel.AVENGER;
            case 3:
                return MovieModel.TITANIC;
        }
        return promptForMovieType();
    }

    private static String promptForMovieID() {
        System.out.println("*************************************");
        System.out.println("Please enter the EventID (e.g MTLM190120)");
        String eventID = input.next().trim().toUpperCase();
        if (eventID.length() == 10) {
            if (eventID.substring(0, 3).equalsIgnoreCase("MTL") ||
                    eventID.substring(0, 3).equalsIgnoreCase("SHE") ||
                    eventID.substring(0, 3).equalsIgnoreCase("QUE")) {
                if (eventID.substring(3, 4).equalsIgnoreCase("M") ||
                        eventID.substring(3, 4).equalsIgnoreCase("A") ||
                        eventID.substring(3, 4).equalsIgnoreCase("E")) {
                    return eventID;
                }
            }
        }
        return promptForMovieID();
    }

    private static int promptForCapacity() {
        System.out.println("*************************************");
        System.out.println("Please enter the booking capacity:");
        return input.nextInt();
    }
}
