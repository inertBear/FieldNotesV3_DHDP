package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_FORMAT_SERVICE;

/**
 * Service to format fields for entry to the database
 */
public class FieldNoteFormatService extends DHDPService {
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    private DateFormat mDateTimeFormat;

    private FieldNoteFormatService(String name) {
        super(name);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mTimeFormat = new SimpleDateFormat("HH:mm:ss");
        mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteService.class)) {
            registry.register(FieldNoteFormatService.class, new FieldNoteFormatService(FIELDNOTES_FORMAT_SERVICE));
        }
    }

    /**
     * get date from millis
     *
     * @param timestampInMillis to retrieve date from
     * @return date as String
     */
    String toDateString(final long timestampInMillis) {
        return mDateFormat.format(new Date(timestampInMillis));

    }

    /**
     * get time from millis
     *
     * @param timestampInMillis to retrieve time from
     * @return time as String
     */
    String toTimeString(final long timestampInMillis) {
        return mTimeFormat.format(new Date(timestampInMillis));
    }

    Date toDateString(final String dateTimeString) {
        try {
            return mDateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid dateTimeString: " + dateTimeString);
        }
    }

    long toDateInMillis(final String dateTimeString) {
        return toDateString(dateTimeString).getTime();
    }

    String toGpsString(GpsCoord gps) {
        if (gps != null) {
            return String.valueOf(gps.getLattitude()) + ", " + String.valueOf(gps.getLongitude());
        }
        return "Not Provided";
    }
}
