package com.devhunter.dhdp.fieldnotes;

public class FieldNotesConstants {
    // SERVICE NAMES
    public static final String FIELDNOTES_SERVICE_NAME = "FIELDNOTES";
    public static final String FIELDNOTES_VALIDATION_SERVICE_NAME = "FieldNotesValidationService";

    // WORKFLOW NAME
    public static final String FIELDNOTES_WORKFLOW_NAME = "FieldNotesWorkflow";

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

    // Keys
    public static final String USERNAME_KEY = "USERNAME";
    public static final String PASSWORD_KEY = "PASSWORD";
    public static final String TOKEN_KEY = "TOKEN";
    public static final String NUMBER_AFFECTED_ROWS_KEY = "NUM_ROWS";
    public static final String TICKET_NUMBER_KEY = "TICKET_NUMBER";
    public static final String NEW_TICKET_KEY = "last_insert_id()";
    public static final String PROJECT_KEY = "PROJECT";
    public static final String WELLNAME_KEY = "WELLNAME";
    public static final String LOCATION_KEY = "LOCATION";
    public static final String BILLING_KEY = "BILLING";
    public static final String START_DATETIME_KEY = "START_DATETIME";
    public static final String END_DATETIME_KEY = "END_DATETIME";
    public static final String START_MILEAGE_KEY = "START_MILEAGE";
    public static final String END_MILEAGE_KEY = "END_MILEAGE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String GPS = "GPS";

    // Field restricted fields
    public static final String[] LOCATION_VALUES = {"Office", "Shop", "Field"};
    public static final String[] BILLING_VALUES = {"Billable", "Not Billable", "Turn-key"};

}
