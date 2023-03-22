package com.web.helper;

import com.web.staticType.Types;

import java.util.Random;
import java.util.Scanner;

public class Helper {

    public static int checkUserType(String userID) {
        if (userID.length() == 8 &&
                (userID.startsWith("ATW") || userID.startsWith("VER") || userID.startsWith("OUT")) &&
                (userID.charAt(3) == 'C' || userID.charAt(3) == 'A')) {
            return userID.charAt(3) == 'C' ? Types.USER_TYPE_CUSTOMER : Types.USER_TYPE_ADMIN;
        }
        return 0;
    }

    public static int promptForCapacity(Scanner input) {
        System.out.println("---------------------------");
        System.out.println("Please enter the booking capacity:");
        return input.nextInt();
    }
}
