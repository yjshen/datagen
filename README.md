# datagen

Multithreaded data generator for testing.

## build

Build an include-all jar: `target/datagen-1.0-SNAPSHOT.jar`

```bash
mvn clean package
```

## run

You could run `io.spaceantime.App` directly from your IDE by adding parameters, or you could run the jar:

```bash
java -jar target/datagen-1.0-SNAPSHOT.jar io.spaceandtime.App [table_name] [start_id] [end_id] [num_threads] [jdbc_properties_file]
```

E.g.

```bash
java -cp target/datagen-1.0-SNAPSHOT.jar io.spaceandtime.App person 0 100000 10 ./dg.properties
```
