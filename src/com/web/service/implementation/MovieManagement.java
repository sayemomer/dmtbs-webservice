package com.web.service.implementation;

//import DataModel.EventModel;
//import Logger.Logger;

import com.web.service.WebInterface;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

@WebService(endpointInterface = "com.web.service.WebInterface")

@SOAPBinding(style = SOAPBinding.Style.RPC)
public class MovieManagement  implements WebInterface {

    private String serverID;
    private String serverName;

    public static final int Atwater_Server_Port = 8888;
    public static final int Verdun_Server_Port = 7777;
    public static final int Outramont_Server_Port = 6666;
    public static final String MOVIE_SERVER_ATWATER = "Atwater";
    public static final String MOVIE_SERVER_VERDUN = "Verdun";
    public static final String MOVIE_SERVER_OUTRAMONT = "Outramont";

    public MovieManagement(String serverID, String serverName){
        super();
        this.serverID = serverID;
        this.serverName = serverName;
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
    public String addEvent(String eventID, String eventType, int bookingCapacity){
        return null;
    }

    @Override
    public String removeEvent(String eventID, String eventType)  {
        return null;
    }

    @Override
    public String listEventAvailability(String eventType) {
        return null;
    }

    @Override
    public String bookEvent(String customerID, String eventID, String eventType) {
        return null;
    }

    @Override
    public String getBookingSchedule(String customerID) {
        return null;
    }

    @Override
    public String cancelEvent(String customerID, String eventID, String eventType) {
        return null;
    }

    @Override
    public String swapEvent(String customerID, String newEventID, String newEventType, String oldEventID, String oldEventType) {
        return null;
    }
}