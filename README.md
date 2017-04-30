#Jackson Versioning
## Introduction
This is a Jackson module that let developer adds versioning annotations to the Plain Old Java Objects (POJO) models
that binds the JSON bodies. These annotations shall define the current latest version number of the POJO and 
the version when an attribute is deprecated or added. The goal is to make it easy to serialize and deserialize JSON
to/from a specific older version.

## Prerequisites
This module is indented to be compatible with Java 7 and Jackson 2.8 or later. We may support older versions of Jackson
but the module is only tested with Jackson 2.8 currently.

### Register module to Jackson
```java
public class JsonVersioningExample {
    protected ObjectMapper mapper; 
    public JsonVersioningExample() {
        // Register versioning module to the Jackson mapper
        mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
    }
    
    public String serialize(ModelPojo pojo) {
        // get the writer for serialization
        return mapper.writer().writeValueAsString(pojo);
    }
    
    public ModelPojo deserialize(String json) {
        // get the reader for deserialization
        return mapper.reader().forType(ModelPojo).readValue(pojo);
    }
    
}
```

## Use Cases
There are 2 different way to use the versions depends on where you want to attach the versions to.
* Version in the body: The json version is given in the attribute in the model POJO or in the JSON body.
* Version in context: The json version is given in the (de)serializing context inside the reader or writer.

You may want to let each POJO model to have their own independent versioning. Or, maybe you are designing
a REST API and the version number means the API version. The latter means all POJO models shares the same
latest API version. All POJO models' version would be bumped up even when only a few of them are changed.
Both of these use cases may be achieved by making versions in the body or in the context. But it should be
easier to make independent versioning "in the body" and the unified versioning "in the context". Examples of
these are shown below.

### Versioning in the body
The benefit of putting the versions in the body is that there is no need to use another data path to get the
version information. The versioning of each POJO model can be completely independent from each other.

```java
@JsonVersioned("1.0") // This annotation is required for versioned POJOs. The version here indicate the latest version number.
public class ModelPojo {
    // This is the version attribute. Should be in the JSON body to indicate the version as well.
    @JsonVersionProperty
    private String version;  // This attribute can also be a "Version" type.
    
    @JsonSince("0.8")       // this attribute is added in version 0.8
    private String newAttrSince08;
    
    @JsonUntil("0.6")       // This attribute is deprecated in version 0.6
    private String oldAttrBefore06;
    
    private String alwaysHere;
    
    // @JsonVersionProperty, @JsonSince, and @JsonUntil can also be on the getters instead.  
    // getters and optionally, setters
}
```
#### Specifying the serialized JSON's version
When serializing a POJO model, the target version can be set to the version property.
```java
public class JsonVersioningSerializingToVersion extends JsonVersioningExample {
    // ...   
    public String serialize(ModelPojo pojo, String toVersion) {
        // specify the version of the JSON 
        pojo.setVersion(toVersion);
        return mapper.writer().writeValueAsString(pojo);
    }
}
```
#### Specifying the deserializing JSON's version
When deserializing the JSON, the deserializer will look for the "version" attribute in the JSON body.
A few examples for valid jsons that can be deserialized into the ModelPojo. A JsonMappingException would be
thrown if an attribute shows up that does not conform with the version.
```json
{
  "version": "0.5",
  "oldAttrBefore06": "hello",
  "alwaysHere": "I am always here."
}
```
```json
{
  "version": "0.8",
  "newAttrSince08": "hello",
  "alwaysHere": "I am always here."
}
```
#### JSON version is default to be the Model version
When both serializing and deserializing, if the "version" attribute is not defined, the model's version given in the 
@JsonVersioned() annotation will be used. In other words, if there is no "version" attribute in the JSON body,
it is assumed that the JSON is in the latest version. If the "version" attribute is null in the POJO, it is
assumed that the POJO is to be serialized into JSON in the latest version.

### Versioning in the context
The Jackson data-binding provides a way to set configuration attributes in the serializing and deserializing 
context. This module can utilize that can set the serializing and deserializing version in the context which
will be used dynamically by all POJO models.
This provides a way to indicate the JSON version "out-of-bound". It can be useful when we need to get the
JSON version in a different way. For example, when developing a REST API, the API version may be specified
in the path; ex: "GET /api/v1.0/cars" and "GET /api/v2.0/cars" shall response with the car model in version
1.0 and 2.0 respectively. In this case, the versions don't have to be in the JSON body.

```java
// somewhere in the common code, the unified API version is defined
public class MyApi {
    public static final String API_VERSION = "1.0";
}

@JsonVersioned(MyApi.API_VERSION) // Still need this annotation but use the common API version 
public class ModelPojo {
    // Don't need to version attribute anymore
     
    @JsonSince("0.8")       // this attribute is added in version 0.8
    private String newAttrSince08;
    
    @JsonUntil("0.6")       // This attribute is deprecated in version 0.6
    private String oldAttrBefore06;
    
    private String alwaysHere;
}
```

#### Specifying the JSON's version in context
The JSON version can be defined as an attribute to the Jackson reader or writer. The attribute name is defined
in Version.JsonVersionConfigDeserializing and Version.JsonVersionConfigSerializing (**Note the different names**)
variables. The value can be a *String* or a *Version* object for bettwe performance.
```java
public class JsonVersioningExample {
    protected ObjectReader reader;
    protected ObjectWriter writer;
    
    public JsonVersioningExample() {
        // Register versioning module to the Jackson mapper
        ObjectWriter mapper = new ObjectMapper().registerModule(new JsonVersioningModule());
        reader = mapper.reader(); // reader is initialized without json version, the POJO model version will be used.
        writer = mapper.writer(); // writer is initialized without json version, the POJO model version will be used.
    }

    public void setDeserializingJsonVersion(String jsonVersion) {
        // reader is replaced by a new reader instance that will have this attribute set.
        reader = reader.withAttribute(
                    Version.JsonVersionConfigDeserializing, 
                    Version.fromString(jsonVersion));
    }
    public void setSerializingJsonVersion(String jsonVersion) {
        // reader is replaced by a new reader instance that will have this attribute set.
        writer = writer.withAttribute(
                    Version.JsonVersionConfigSerializing, 
                    Version.fromString(jsonVersion));
    }
    
    public String serialize(ModelPojo pojo) {
        // using the same writer to serialize pojos with the same target JSON version
        return writer.writeValueAsString(pojo);
    }
    
    public String serialize(ModelPojo pojo, String jsonVersion) {
        // Or make a new writer with the new version setting
        return writer
                .withAttribute(Version.JsonVersionConfigSerializing, jsonVersion)
                .writeValueAsString(pojo);
    }

    public ModelPojo deserialize(String json) {
        // using the same reader to deserialize JSON in the same version
        return reader
                .forType(ModelPojo)
                .readValue(pojo);
    } 

    public ModelPojo deserialize(String json, String jsonVersion) {
        // Or make a new reader with the new version setting
        return reader
                .withAttribute(Version.JsonVersionConfigDeserializing, jsonVersion)
                .forType(ModelPojo)
                .readValue(pojo);
    }
}
```
# Notes
1. **Json version property overrides the version in context.** Something it may be beneficial to use both versioning 
in body and in context. But developer needs to be careful because when the version number in the model or json attribute
is defined, the version number in the context is ignored.
1. **JSON version defaults to the POJO model's latest version.** If no version attribute is defined in model or in json and
no version attribute is defined in the context, the POJO model's latest version number defined in @JsonVersioned annotation 
is used as the JSON version.
1. **Versioned POJO can be used as an attribute of another POJO** The versioning will still work; 
1. Be careful when you use versioning "in context" but don't have unified versioning on POJO models. The version
in context cannot be changed during the same serializing or deserializing call. **You are better off just have
an unified version number for all POJOs if you plan to use versioning "in context".**
