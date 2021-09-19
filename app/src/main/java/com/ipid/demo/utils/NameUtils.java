package com.ipid.demo.utils;

import static com.ipid.demo.constants.Constants.EMPTY_STRING;
import static com.ipid.demo.constants.Constants.SPACE;
import static com.ipid.demo.constants.Constants.UNDERSCORE;

import java.util.Locale;

public class NameUtils {
    /**
     * Returns the first name given the full name
     *
     * @param name
     * @return
     */
    public static String getFirstName(String name) {
        // Cater for non-member names from contact list with first name only
        if (name.split("\\w+").length == 1) {
            return name;
        }

        if(name.split("\\w+").length > 1) {
            return name.substring(0, name.lastIndexOf(SPACE));
        } else {
            return name;
        }
    }

    /**
     * Returns the last name given the full name
     *
     * @param name
     * @return
     */
    public static String getLastName(String name) {
        // Cater for non-member names from contact list with first name only
        if (name.split("\\w+").length == 1) {
            return EMPTY_STRING;
        }

        if (name.split("\\w+").length > 1) {
            return name.substring(name.lastIndexOf(SPACE) + 1);
        } else {
            return name;
        }
    }

    public static String getFullName(String firstName, String lastName) {
        return firstName + SPACE + lastName;
    }

    public static String getDrawableFlagName(String countryName) {
        String flagName = countryName.toLowerCase().replace(SPACE, UNDERSCORE);
        return flagName;
    }
}
