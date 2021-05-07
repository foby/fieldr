package com.github.foby.fieldr.user;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Field;

public class Parent {

    protected Instant createdDate = Instant.now();

    protected Instant lastModifiedDate;

    @Field("customer_id")
    protected double customerId;

}
