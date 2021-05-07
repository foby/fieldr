package com.github.foby.fieldr.user;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.foby.fieldr.user.generated.Facility_;

public class FieldGenerationTest {

    @Test
    public void testMongoIdFieldMongoStyle() {
        assertEquals("_id", Facility_.id);
    }

    @Test
    public void testFieldsPath() {
        assertEquals("contact_person.email", Facility_.contactPerson.email);
        assertEquals("contact_person.user_location", Facility_.contactPerson.userLocation.toString());
        assertEquals("contact_person.user_location.city", Facility_.contactPerson.userLocation.city);
    }

    @Test
    public void testParentFieldsPickedUp() {
        assertEquals("createdDate", Facility_.createdDate);
        assertEquals("lastModifiedDate", Facility_.lastModifiedDate);
    }

    @Test
    public void testMongodbReferences() {
        assertEquals("$createdDate", Facility_.$createdDate);
        assertEquals("$contact_person.name", Facility_.$contactPerson.name);
        assertEquals("customer_id", Facility_.customerId);
    }

    @Test
    public void testConstants() {
        assertEquals("createdDate", Facility_.CREATED_DATE);
        assertEquals("customer_id", Facility_.CUSTOMER_ID);
    }
}
