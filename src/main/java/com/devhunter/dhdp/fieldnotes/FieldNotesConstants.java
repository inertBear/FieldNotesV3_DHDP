package com.devhunter.dhdp.fieldnotes;

public class FieldNotesConstants {

    //MySQL Database
    public static final String DB_SERVER = "fieldnotes-rhl-restore.ckzbugsctcko.us-west-2.rds.amazonaws.com";
    public static final String DB_USERNAME = "FNAdmin";
    public static final String DB_PASSWORD = "Nashv1ll3$";
    public static final String DB_DATABASE = "FieldNotes_RHL";
    public static final int DB_PORT = 3306;
    // Login Table
    public static final String LOGIN_TABLE = "MASTER_LOGIN";
    public static final String USERNAME_COLUMN = "Username";
    public static final String PASSWORD_COLUMN = "Password";
    public static final String TOKEN_COLUMN = "UserToken";
    // Data Table
    public static final String TICKET_NUMBER_COLUMN = "ticketNumber";
    public static final String USER_COLUMN = "userName";
    public static final String WELLNAME_COLUMN = "wellName";
    public static final String DATE_START_COLUMN = "dateStart";
    public static final String TIME_START_COLUMN = "timeStart";
    public static final String MILEAGE_START_COLUMN = "mileageStart";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String MILEAGE_END_COLUMN = "mileageEnd";
    public static final String DATE_END_COLUMN = "dateEnd";
    public static final String TIME_END_COLUMN = "timeEnd";
    public static final String PROJECT_NUMBER_COLUMN = "projectNumber";
    public static final String LOCATION_COLUMN = "location";
    public static final String GPS_COLUMN = "gps";
    public static final String BILLING_COLUMN = "billing";
    // JSON User tags
    public static final String USERNAME_TAG = "UserName";
    public static final String PASSWORD_TAG = "UserPassword";
}
