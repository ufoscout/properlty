# Properlty - Simple configuration library with placeholders resolution and no magic!

Properlty provides a simple way to configure an application from multiple sources — built in resources, system
properties, property files, environment variables, and whatever else you like.

Some features:

- Recursive placeholders resolution
- Only 30Kb and no external dependencies
- Java and Kotlin versions


Getting Started
---------------

1. To get started, add Properlty dependency to your project.
 
For Kotlin:
```xml
    <dependency>
        <groupId>com.ufoscout.properlty</groupId>
        <artifactId>properlty-kotlin</artifactId>
        <version>1.3.0</version>
    </dependency>
```

For Java:
```xml
    <dependency>
        <groupId>com.ufoscout.properlty</groupId>
        <artifactId>properlty</artifactId>
        <version>1.3.0</version>
    </dependency>
```

2. Define some properties. You can use placeholders, for example, in `config.properties`:

```properties
    server.port=9090
    server.host=127.0.0.1
    server.url=http://${server.host}:${server.port}/
```
    
3. Build a Properlty object that loads properties:

Kotlin:
```kotlin
    val properlty = Properlty.builder()
            .add("classpath:config.properties") // loads from the classpath
            .build()
```
    
Java:
```java
    final Properlty properlty = Properlty.builder()
            .add("classpath:classpath:config.properties")  // loads from the classpath
            .build();
```

4. Look up properties by key:

Kotlin:
```kotlin
    val port: Int = properlty.getInt("server.port", 8080); // returns 9090
    val serverUrl = properlty["server.url"] // returns http://127.0.0.1:9090/
    val defaultVal = properlty["unknownKey", "defaultValue"] // returns defaultValue
```

Java:
```java
    int port = properlty.getInt("server.port", 8080); // returns 9090
    
    // the Java API uses Optional for methods that could not produce a valid result.
    // In this case the Optional contains the Strind 'http://127.0.0.1:9090/' 
    Optional<String> serverUrl = properlty.get("server.url") 
    String defaultVal = properlty.get("unknownKey", "defaultValue") // returns defaultValue
```

(All examples are in Kotlin from now on, btw Java code is exactly the same)

Readers
-------
In Properlty a "reader" is whatever source of properties. 
By default, Properlty offers readers to load properties from:

* Java property files in the file system and in the classpath
* Java system properties
* Environment variables
* Programmatically typed properties

Custom properties readers can be created implementing the `com.ufoscout.properlty.reader.Reader` interface. 


Placeholders resolution
-----------------------
Properlty is able to resolve placeholders recursively. For example:

fileOne.properties:
```properties
    server.url=http://${${environment}.server.host}:${server.port}
    server.port=8080
```

fileTwo.properties:
```properties
    environment=PROD
    PROD.server.host=10.10.10.10
    DEV.server.host=127.0.0.1
```

```kotlin
    val properlty = Properlty.builder()
            .add("./fileOne.properties") // loads from the file system
            .add("./fileTwo.properties") // loads from the file system
            .build()
            
    println(properlty["server.url"]) // this prints 'http://10.10.10.10:8080'
```

By default `${` and `}` delimiters are used. Custom delimiters can be easily defined:

```kotlin
    val properlty = Properlty.builder()
            .delimiters("%(", ")") // using %( and ) as delimiters
            .add( bla bla bla)
```


Readers priority
----------------
Readers are evaluated in the order they are added. So in case of key conflicts, the first added has the highest priority.

For example:

fileOne.properties:
```properties
    server.url=urlFromOne
```

fileTwo.properties:
```properties
    server.url=urlFromTwo
```

```kotlin
    val properlty = Properlty.builder()
            .add(SystemPropertiesReader()) // loads the Java system properties
            .add("classpath:fileOne.properties") // loads from the classpath
            .add("file:./fileTwo.properties") // loads from the file system
            .build()
    
    // this prints 'urlFromOne'
    println(properlty["server.url"])
```

BTW, due to the fact that we used `SystemPropertiesReader()` before the file readers, if the "server.url" system property is specified at runtime, it will override the other values.

In addition, it is possible to specify a custom priority:

```kotlin
    val properlty = Properlty.builder()
            .add(SystemPropertiesReader()) // loads the Java system properties
            .add("classpath:fileOne.properties") // loads from the classpath
            
            // load the properties from the file system and specify their priority
            .add(resourcePath = "file:./fileTwo.properties", priority = Default.HIGHEST_PRIORITY) 
            .build()
            
    // this prints 'urlFromTwo'
    println(properlty["server.url"]) 
```

The deafult priority is 100. The highest priority is 0.


Real life example
-----------------
A typical real life configuration would look like:

```kotlin
    val properlty = Properlty.builder()
    
            // Add a not mandatory reader, resource not found exceptions are ignored.
            // Here I am adding properties present only during testing.
            .add(resourcePath = "classpath:test.properties", ignoreNotFound = true)
                                                                       
             // Load the Java system properties                                          
            .add(SystemPropertiesReader())
            
             // Load the Environment variables
            .add(EnvironmentVariablesReader())
            
             // load the Environment variables and convert their keys 
             // from JAVA_HOME=XXX to java.home=XXX
             // This could be desired to override default properties
            .add(ToLowerCaseAndDotKeyReader(EnvironmentVariablesReader()))
            
            // load a resource from the classpath
            .add("classpath:default.properties")
            
             // load a file the file system
            .add("file:./config/config.properties")
            
            // build the properlty object 
            .build()
```

Properlty API
-------------
Properlty has a straightforward API that hopefully does not need detailed documentation.

Some examples:

```kotlin
    val properlty = Properlty.builder()
            .add("classpath:test.properties")
            .build()

    // get a String. "defaultValue" is returned if the key is not found
    val oneString = properlty["key", "defaultValue"]

    // get an array from the comma separated tokens of the property value
    val oneArray = properlty.getArray("key", ",")

    // get an Enum
    val oneEnum = properlty.getEnum<NeedSomebodyToLove>("key")

    // get a BigDecimal by applying a transformation function to the returned String value
    val oneBigDecimal = properlty["key", { BigDecimal(it) }]

    // get a list of BigDecimal. The property value is split in tokens using the default list 
    // separator (a comma) then the transformation function is applied to each token
    val oneListOfBigDecimals = properlty.getList("key") {BigDecimal(it)} 
```