package com.github.rntrp;

import java.security.Provider;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;
import java.util.Arrays;

class SecureRandomSynchronized extends SecureRandom {
    private static final SecureRandomSynchronizedSpi SPI = new SecureRandomSynchronizedSpi();
    private static final Provider PROVIDER = new Provider("Synchronized", 1d, "") {};

    private SecureRandomSynchronized(SecureRandomSynchronizedSpi spi) {
        super(spi, PROVIDER);
    }

    static SecureRandomSynchronized singletonSpi() {
        return new SecureRandomSynchronized(SPI);
    }

    static SecureRandomSynchronized newSpi() {
        return new SecureRandomSynchronized(new SecureRandomSynchronizedSpi());
    }

    private static class SecureRandomSynchronizedSpi extends SecureRandomSpi {
        @Override
        protected synchronized void engineSetSeed(byte[] seed) {
            Arrays.fill(seed, (byte) 42);
        }

        @Override
        protected synchronized void engineNextBytes(byte[] bytes) {
            Arrays.fill(bytes, (byte) 42);
        }

        @Override
        protected synchronized byte[] engineGenerateSeed(int numBytes) {
            byte[] bytes = new byte[numBytes];
            Arrays.fill(bytes, (byte) 42);
            return bytes;
        }
    }
}
