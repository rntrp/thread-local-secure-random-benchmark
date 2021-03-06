package com.github.rntrp.threadlocalsecurerandombenchmark.bogusspi;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.Arrays;

public class SecureRandomConcurrent extends SecureRandom {
    private static final SecureRandomConcurrentSpi SPI = new SecureRandomConcurrentSpi();
    private static final Provider PROVIDER = new Provider("Concurrent", 1d, "") {};

    private SecureRandomConcurrent() {
        super(SPI, PROVIDER);
    }

    public static SecureRandomConcurrent newInstance() {
        return new SecureRandomConcurrent();
    }

    private static class SecureRandomConcurrentSpi extends SecureRandomSpi {
        @Override
        protected void engineSetSeed(byte[] seed) {
            Arrays.fill(seed, (byte) 42);
        }

        @Override
        protected void engineNextBytes(byte[] bytes) {
            Arrays.fill(bytes, (byte) 42);
        }

        @Override
        protected byte[] engineGenerateSeed(int numBytes) {
            byte[] bytes = new byte[numBytes];
            Arrays.fill(bytes, (byte) 42);
            return bytes;
        }
    }
}
