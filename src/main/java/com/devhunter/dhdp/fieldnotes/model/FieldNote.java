package com.devhunter.dhdp.fieldnotes.model;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;

/**
 * Models a FieldNotes request.
 * models transfer between a DHDPRequest -> FieldNotesService
 */
public class FieldNote extends DHDPRequestBody {
    private String mUsername;
    private String mProject;
    private String mWellname;
    private String mDescription;
    private String mLocation;
    private GpsCoord mGps;
    private String mBilling;
    private long mStartTimeStampMillis;
    private long mEndTimeStampMillis;
    private int mMileageStart;
    private int mMileageEnd;

    private FieldNote(FieldNote.Builder builder) {
        mUsername = builder.username;
        mProject = builder.project;
        mWellname = builder.wellname;
        mDescription = builder.description;
        mLocation = builder.location;
        mBilling = builder.billing;
        mStartTimeStampMillis = builder.startTimeStampMillis;
        mEndTimeStampMillis = builder.endTimeStampMillis;
        mMileageStart = builder.mileageStart;
        mMileageEnd = builder.mileageEnd;
        mGps = builder.gps;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getUsername() {
        return mUsername;
    }

    public String getProject() {
        return mProject;
    }

    public String getWellname() {
        return mWellname;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getLocation() {
        return mLocation;
    }

    public GpsCoord getGps() {
        return mGps;
    }

    public String getBillingType() {
        return mBilling;
    }

    public long getStartTimestampMillis() {
        return mStartTimeStampMillis;
    }

    public boolean hasStartTimeStamp() {
        return mStartTimeStampMillis != 0L;
    }

    public long getEndTimestampMillis() {
        return mEndTimeStampMillis;
    }

    public boolean hasEndTimeStamp() {
        return mEndTimeStampMillis != 0L;
    }

    public int getMileageStart() {
        return mMileageStart;
    }

    public int getMileageEnd() {
        return mMileageEnd;
    }

    public static class Builder {

        private String username;
        private String project;
        private String wellname;
        private String description;
        private String location;
        private GpsCoord gps;
        private String billing;
        private long startTimeStampMillis;
        private long endTimeStampMillis;
        private int mileageStart;
        private int mileageEnd;

        public Builder setUsername(final String username) {
            this.username = username;
            return this;
        }

        public Builder setProject(final String project) {
            this.project = project;
            return this;
        }

        public Builder setWellname(final String wellname) {
            this.wellname = wellname;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setLocation(final String location) {
            this.location = location;
            return this;
        }

        public Builder setGPSCoords(final GpsCoord gps) {
            this.gps = gps;
            return this;
        }

        public Builder setBillingType(final String billing) {
            this.billing = billing;
            return this;
        }

        public Builder setStartTimeStampMillis(final long startTimestamp) {
            this.startTimeStampMillis = startTimestamp;
            return this;
        }

        public Builder setEndTimestampMillis(final long endTimestamp) {
            this.endTimeStampMillis = endTimestamp;
            return this;
        }

        public Builder setMileageStart(final int startMileage) {
            this.mileageStart = startMileage;
            return this;
        }

        public Builder setMileageEnd(final int endMileage) {
            this.mileageEnd = endMileage;
            return this;
        }

        public FieldNote build() {
            return new FieldNote(this);
        }
    }
}
