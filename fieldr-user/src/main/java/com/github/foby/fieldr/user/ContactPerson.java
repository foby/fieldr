package com.github.foby.fieldr.user;

import org.springframework.data.mongodb.core.mapping.Field;

import com.github.foby.fieldr.GenerateFieldNames;
import com.github.foby.fieldr.user.sub.Location;

@GenerateFieldNames
public class ContactPerson {

    private String name;

    private String email;

    @Field("user_location")
    private Location userLocation;
}
