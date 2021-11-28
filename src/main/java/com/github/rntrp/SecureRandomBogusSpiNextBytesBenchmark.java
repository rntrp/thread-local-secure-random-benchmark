package com.github.rntrp;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@Fork(warmups = 1, value = 1)
@Warmup(iterations = 20, batchSize = 100, time = 10, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(iterations = 100, batchSize = 100, time = 10, timeUnit = TimeUnit.MICROSECONDS)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Threads(Threads.MAX)
@SuppressWarnings("unused")
public class SecureRandomBogusSpiNextBytesBenchmark {
    @Benchmark
    public byte[] newRefConcurrent(OutputState os) {
        new SecureRandomConcurrent().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] newRefSynchronized(OutputState os) {
        SecureRandomSynchronized.newSpi().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] newRefSynchronizedSingletonSpi(OutputState os) {
        SecureRandomSynchronized.singletonSpi().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] staticRefConcurrent(SecureRandomState srs, OutputState os) {
        srs.concurrent.nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] staticRefSynchronized(SecureRandomState srs, OutputState os) {
        srs.sync.nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] staticRefSynchronizedSingletonSpi(SecureRandomState srs, OutputState os) {
        srs.syncSingletonSpi.nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] threadLocalConcurrent(SecureRandomState srs, OutputState os) {
        srs.concurrentThreadLocal.get().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] threadLocalSynchronized(SecureRandomState srs, OutputState os) {
        srs.syncThreadLocal.get().nextBytes(os.buffer);
        return os.buffer;
    }

    @Benchmark
    public byte[] threadLocalSynchronizedSingletonSpi(SecureRandomState srs, OutputState os) {
        srs.syncSingletonSpiThreadLocal.get().nextBytes(os.buffer);
        return os.buffer;
    }

    @State(Scope.Benchmark)
    public static class SecureRandomState {
        private SecureRandomConcurrent concurrent;
        private SecureRandomSynchronized sync;
        private SecureRandomSynchronized syncSingletonSpi;
        private ThreadLocal<SecureRandomConcurrent> concurrentThreadLocal;
        private ThreadLocal<SecureRandomSynchronized> syncThreadLocal;
        private ThreadLocal<SecureRandomSynchronized> syncSingletonSpiThreadLocal;

        @Setup
        public void setup() {
            concurrent = new SecureRandomConcurrent();
            sync = SecureRandomSynchronized.newSpi();
            syncSingletonSpi = SecureRandomSynchronized.singletonSpi();
            concurrentThreadLocal = ThreadLocal.withInitial(SecureRandomConcurrent::new);
            syncThreadLocal = ThreadLocal.withInitial(SecureRandomSynchronized::newSpi);
            syncSingletonSpiThreadLocal = ThreadLocal.withInitial(SecureRandomSynchronized::singletonSpi);
        }
    }

    @State(Scope.Thread)
    public static class OutputState {
        private byte[] buffer;
        @Setup
        public void setup() {
            buffer = new byte[128];
        }
        @TearDown
        public void tearDown() {
            buffer = null;
        }
    }
}
