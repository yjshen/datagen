# datagen

Multithreaded data generator for testing.

## build

```bash
mvn clean package
```

## run

```bash
java -jar target/datagen-1.0-SNAPSHOT.jar io.spaceandtime.App [table_name] [start_id] [end_id] [num_threads] [jdbc_properties_file]
```

E.g.

```bash
java -cp target/datagen-1.0-SNAPSHOT.jar io.spaceandtime.App person 0 100000 10 ./dg.properties
```
