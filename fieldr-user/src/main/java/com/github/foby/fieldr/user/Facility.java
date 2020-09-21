package com.github.foby.fieldr.user;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import com.github.foby.fieldr.GenerateFieldNames;

@GenerateFieldNames
public class Facility extends Parent implements Serializable {

    @Id
    private String id;

    @Field("user_id")
    private String userId;

    private String name;

    @Field("contact_person")
    private ContactPerson contactPerson;

}
