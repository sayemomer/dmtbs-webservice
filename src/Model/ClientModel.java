package Model;

public class ClientModel {
    public static final String CLIENT_TYPE_ADMIN = "MOVIE_ADMIN";
    public static final String CLIENT_TYPE_CUSTOMER = "CUSTOMER";
    public static final String CLIENT_SERVER_ATWATER = "ATWATER";
    public static final String CLIENT_SERVER_VERDUN = "VERDUN";
    public static final String CLIENT_SERVER_OUTRAMONT = "OUTRAMONT";
    private String clientType;
    private String clientID;
    private String clientServer;

    public ClientModel(String clientID) {
        this.clientID = clientID;
        this.clientType = detectClientType();
        this.clientServer = detectClientServer();
    }

    private String detectClientServer() {
        if (clientID.substring(0, 3).equalsIgnoreCase("ATW")) {
            return CLIENT_SERVER_ATWATER;
        } else if (clientID.substring(0, 3).equalsIgnoreCase("VER")) {
            return CLIENT_SERVER_VERDUN;
        } else {
            return CLIENT_SERVER_OUTRAMONT;
        }
    }

    private String detectClientType() {
        if (clientID.substring(3, 4).equalsIgnoreCase("A")) {
            return CLIENT_TYPE_ADMIN;
        } else {
            return CLIENT_TYPE_CUSTOMER;
        }
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientServer() {
        return clientServer;
    }

    public void setClientServer(String clientServer) {
        this.clientServer = clientServer;
    }

    @Override
    public String toString() {
        return getClientType() + "(" + getClientID() + ") on " + getClientServer() + " Server.";
    }
}