# thread-local-secure-random-benchmark
Measuring performance of Java SecureRandom.nextBytes() under various conditions

## Usage
Build `bench.jar`:
```bash
mvn clean verify
```
Execute it:
```bash
java -jar /path/to/bench.jar
```

## TODOs
* Find out if the Provider affects the duration.
* Is SecureRandom seeded lazily? Do we need to call a `nextXYZ` method in `@Setup` before delegating the instance to the benchmark?
* Further stabilize the results (std dev ist still huge).
