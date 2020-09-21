package com.github.foby.fieldr.user.sub;

import org.springframework.data.mongodb.core.mapping.Field;

import com.github.foby.fieldr.GenerateFieldNames;

@GenerateFieldNames
public class Location {

    public String city;

    @Field("my_street")
    private String street;
}
