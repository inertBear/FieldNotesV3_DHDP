package com.devhunter.dhdp.fieldnotes.model;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.DHDPConnector4J.request.DHDPRequestBody;

import java.time.LocalDateTime;

/**
 * Models an internal response to a FieldNotes request. Acts as a medium for
 * transfer between the db query resultSet and the DHDPResponse
 */
public class FieldNote extends DHDPRequestBody {
    private String mUsername;
    private String mProject;
    private String mWellname;
    private String mDescription;
    private String mLocation;
    private GpsCoord mGps;
    private String mBilling;
    private LocalDateTime mStartTimeStamp;
    private LocalDateTime mEndTimeStamp;
    private int mMileageStart;
    private int mMileageEnd;

    private FieldNote(FieldNote.Builder builder) {
        mUsername = builder.username;
        mProject = builder.project;
        mWellname = builder.wellname;
        mDescription = builder.description;
        mLocation = builder.location;
        mBilling = builder.billing;
        mStartTimeStamp = builder.startTimeStamp;
        mEndTimeStamp = builder.endTimeStamp;
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

    public String getBilling() {
        return mBilling;
    }

    public LocalDateTime getStartTimestamp() {
        return mStartTimeStamp;
    }

    public LocalDateTime getEndTimestamp() {
        return mEndTimeStamp;
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
        private LocalDateTime startTimeStamp;
        private LocalDateTime endTimeStamp;
        private int mileageStart;
        private int mileageEnd;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setProject(String project) {
            this.project = project;
            return this;
        }

        public Builder setWellname(String wellname) {
            this.wellname = wellname;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setGPSCoords(GpsCoord gps) {
            this.gps = gps;
            return this;
        }

        public Builder setBillingType(String billing) {
            this.billing = billing;
            return this;
        }

        public Builder setDateStart(LocalDateTime startTimestamp) {
            this.startTimeStamp = startTimestamp;
            return this;
        }

        public Builder setDateEnd(LocalDateTime endTimestamp) {
            this.endTimeStamp = endTimestamp;
            return this;
        }

        public Builder setMileageStart(int startMileage) {
            this.mileageStart = startMileage;
            return this;
        }

        public Builder setMileageEnd(int endMileage) {
            this.mileageEnd = endMileage;
            return this;
        }

        public FieldNote build() {
            return new FieldNote(this);
        }
    }
}
