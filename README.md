 README
========

## Requisites
- Java (version 1.8.0_40)
- Maven (3.3.1) 
- Preferred IDE/Text editor
- Internet connection (To download dependencies)

## Basic commands
- `$ mvn clean compile test assembly:single`: run tests and create a jar file
- `$  java -jar target/data-loader-1.0-SNAPSHOT-jar-with-dependencies.jar "<PATH TO CSV FILE>"`: runs tool and load data

## Sample output

```
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
Processed 5000 records
Processed 10000 records
Processed 15000 records
Processed 20000 records
Processed 25000 records
Processed 30000 records
Processed 35000 records
Processed 40000 records
:
:
:
Total 1048572 records loaded
4 records failed to load
```

## Further improvements
- Parameterize connection details
- Batch insert will improve performance
