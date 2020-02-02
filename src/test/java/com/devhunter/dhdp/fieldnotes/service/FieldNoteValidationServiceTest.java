package com.devhunter.dhdp.fieldnotes.service;

import com.devhunter.DHDPConnector4J.model.GpsCoord;
import com.devhunter.dhdp.fieldnotes.model.FieldNote;
import com.devhunter.dhdp.infrastructure.DHDPServiceRegistry;
import com.devhunter.dhdp.services.MySqlService;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

public class FieldNoteValidationServiceTest {
    private FieldNoteValidationService mService;

    @Before
    public void setup() {
        DHDPServiceRegistry registry = new DHDPServiceRegistry();
        MySqlService.initService(registry);
        FieldNoteValidationService.initService(registry);
        FieldNoteService.initService(registry);
        mService = registry.resolve(FieldNoteValidationService.class);
    }

    /**
     * test validate login success
     */
    @Test
    public void testLoginValidation() {
        mService.validateLogin("Username", "Password");
    }

    /**
     * test validate login failure - null username
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginValidateNullUsername() {
        mService.validateLogin(null, "Password");
    }

    /**
     * test validate login failure - no username
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginValidateNoUsername() {
        mService.validateLogin("", "Password");
    }

    /**
     * test validate login failure - null password
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginValidateNullPassword() {
        mService.validateLogin("Username", null);
    }

    /**
     * test validate login failure - no password
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoginValidateNoPassword() {
        mService.validateLogin("Username", "");
    }

    /**
     * test validate add note success
     */
    @Test
    public void testAddNoteValidate() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null username
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullUsername() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername(null)
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no username
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoUsername() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null project
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullProject() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject(null)
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no project
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoProject() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null billing
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullBilling() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType(null)
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no billing
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoBilling() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - non valid billing type
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNonValidBilling() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Non-valid-billing-type")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null description
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullDescription() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription(null)
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no description
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoDescription() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null Location
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullLocation() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation(null)
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no Location
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoLocation() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - non valid Location
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNonValidLocation() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Non-valid-location")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null wellname
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNullWellname() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Location")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname(null)
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - no wellname
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNoteNoWellname() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Location")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("")
                .setGPSCoords(GpsCoord.newBuilder()
                        .setLatitude(0)
                        .setLongitude(0)
                        .build())
                .build();

        mService.validateAddNote(fieldNote);
    }

    /**
     * test validate add note failure - null gps
     */
    @Test
    public void testAddNoteNullGps() {
        FieldNote fieldNote = FieldNote.newBuilder()
                .setUsername("Username")
                .setProject("Project")
                .setBillingType("Not Billable")
                .setDateStart(LocalDateTime.now())
                .setDateEnd(LocalDateTime.now())
                .setDescription("Description")
                .setLocation("Office")
                .setMileageStart(0)
                .setMileageEnd(1)
                .setWellname("Wellname")
                .setGPSCoords(null)
                .build();

        mService.validateAddNote(fieldNote);
    }
}