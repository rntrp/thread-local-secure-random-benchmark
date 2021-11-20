package com.github.rntrp;

import org.openjdk.jmh.annotations.*;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(warmups = 1, value = 1)
@Warmup(iterations = 20, batchSize = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(iterations = 100, batchSize = 10, time = 1, timeUnit = TimeUnit.MICROSECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(10)
@SuppressWarnings("unused")
public class SecureRandomNextBytesBenchmark {
    // Depletes entropy pool too fast and is too slow anyway
    // Uncomment @Benchmark annotation to see the effects
    // @Benchmark
    public byte[] newRef(DefaultState state, OutputState os) {
        new SecureRandom().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] staticRef(DefaultState state, OutputState os) {
        state.secureRandom.nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] threadLocal(DefaultState state, OutputState os) {
        state.secureRandomThreadLocal.get().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] preInit(PreInitState ps, DefaultState prop, OutputState os) {
        // DefaultState necessary for setting the system properties once per Benchmark
        ps.secureRandom.nextBytes(os.buffer);
        return os.buffer;
    }

    @State(Scope.Thread)
    public static class OutputState {
        private byte[] buffer;
        @Setup
        public void setup() {
            buffer = new byte[4];
        }
        @TearDown
        public void tearDown() {
            buffer = null;
        }
    }

    @State(Scope.Benchmark)
    public static class DefaultState {
        // TODO: Better way? @Fork.jvmArgs?
        @Param({"default", "file:/dev/random", "file:/dev/urandom"})
        public String arg;

        // effectively static/singleton via @State(Scope.Benchmark)
        private SecureRandom secureRandom;
        private ThreadLocal<SecureRandom> secureRandomThreadLocal;

        @Setup
        public void setup() {
            if (!"default".equals(arg)) {
                System.setProperty("java.security.egd", arg);
            }
            secureRandom = new SecureRandom();
            secureRandomThreadLocal = ThreadLocal.withInitial(SecureRandom::new);
        }

        @TearDown
        public void tearDown() {
            System.clearProperty("java.security.egd");
        }
    }

    @State(Scope.Thread)
    public static class PreInitState {
        private SecureRandom secureRandom;
        @Setup
        public void setup() {
            secureRandom = new SecureRandom();
        }
    }
}
