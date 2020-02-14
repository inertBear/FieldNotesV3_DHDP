package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.dhdp.infrastructure.DHDPService;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.devhunter.dhdp.fieldnotes.FieldNotesConstants.FIELDNOTES_TIME_SERVICE;

public class FieldNoteTimeService extends DHDPService {
    private DateFormat mDateFormat;
    private DateFormat mTimeFormat;
    private DateFormat mDateTimeFormat;

    private FieldNoteTimeService(String name) {
        super(name);
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mTimeFormat = new SimpleDateFormat("HH:mm:ss");
        mDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static void initService(DHDPServiceRegistry registry) {
        if (!registry.containsService(FieldNoteService.class)) {
            registry.register(FieldNoteTimeService.class, new FieldNoteTimeService(FIELDNOTES_TIME_SERVICE));
        }
    }

    /**
     * get date from millis
     *
     * @param timestampInMillis to retrieve date from
     * @return date as String
     */
    String getDate(final long timestampInMillis) {
        return mDateFormat.format(new Date(timestampInMillis));

    }

    /**
     * get time from millis
     *
     * @param timestampInMillis to retrieve time from
     * @return time as String
     */
    String getTime(final long timestampInMillis) {
        return mTimeFormat.format(new Date(timestampInMillis));
    }

    Date getDate(final String dateTimeString) {
        try {
            return mDateTimeFormat.parse(dateTimeString);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid dateTimeString: " + dateTimeString);
        }
    }

    long getDateInMillis(final String dateTimeString) {
        return getDate(dateTimeString).getTime();
    }
}
