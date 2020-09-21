# fieldr

An annotation driven field name and path generator.

## What does it do?

It generates field names of a POJO as constants. 
Fieldr is also capable of generating field name paths derived from class graphs.

## Why do you need it?

Whenever you want to access resources for which the name or the path 
of a POJOs' (or entitys') field name is required, having those names generated as
code is really helpful and much less error-prone as if you would copy it manually.

Fieldr is meant to be a pragmatic helper especially useful for raw mongodb queries.

## How does it work?

Simply annotate your POJOs with `@GenerateFieldNames` and let the annotation processor do the rest for you.

### Simple Example

Imagine you have a class `Facility` annotated with `@GenerateFieldNames`:

```
@GenerateFieldNames
public class Facility extends Parent {
    private String id;
    private String owner;
    private String name;
}
```

then fieldr will generate a class `Facility_` where you can access the fieldnames from:

```
@Test
public void testSimpleFieldNames() {
    assertEquals("id", Facility_.id);
    assertEquals("owner", Facility_.owner);
    assertEquals("name", Facility_.name);
}
``` 

### Example for field name paths

Imagine you have a `ContactPerson` linked from `Facility` and `ContactPersion` is linked to a `Location`:

```
@GenerateFieldNames
public class Facility extends Parent {
    ...
    private ContactPersion contactPerson;
}

@GenerateFieldNames
public class ContactPerson {
    private String name;
    private String email;
    private Location userLocation;
}
``` 

Then fieldr generates the field name paths derived from the class graph from you:

```
@Test
public void testFieldsPath() {
    assertEquals("contactPerson.email", Facility_.contactPerson.email);
    assertEquals("contactPerson.userLocation", Facility_.contactPerson.userLocation);
    assertEquals("contactPerson.userLocation.city", Facility_.contactPerson.userLocation.city);
}
``` 

### MongoDB `@Field` annotations

In Spring Data MongoDB, you can use `@Field` to define the field name as used in the database. 
Now if you want to query stuff, then you must provide the database field name instead of the POJO property name:

```
@GenerateFieldNames
public class Location {
    private String city;
    @Field("street_address")
    private String street;
}

```

Fieldr handles this for you:

```
@Test
public void testMongoDbFields() {
    assertEquals("contactPerson.userLocation.city", Facility_.contactPerson.userLocation.city);
    assertEquals("contactPerson.userLocation.street_address", Facility_.contactPerson.userLocation.street_address);
}
```

### Inheritance

Fieldr generates field names for all non-static, non-final fields from the current class and its ancestors. 

**Fieldr is currently only capable of generating superclass fields that are `protected`!**


## How to use

Add a compile time dependency to your maven pom:

```
<dependency>
    <groupId>com.github.foby</groupId>
    <artifactId>fieldr</artifactId>
    <version>0.1</version>
    <scope>compile</scope>
</dependency>
```

Start annotating you POJOs with `@GenerateFieldNames` and run

```
mvn clean compile
```

The generated field name classes should be available under `/target/generated-sources/annotations`.
**Note that you might need to configure your IDE to use this as an additional source folder**.

### Without maven

Enable annotation-processing directly in your IDE and add fieldr as an annotation processor.     

## Limitations

Fieldr is not an expression language and currently only supports path expressions separated with a `.` (dot).
Also, fieldr is an early development stage. Contribution very welcome!    
