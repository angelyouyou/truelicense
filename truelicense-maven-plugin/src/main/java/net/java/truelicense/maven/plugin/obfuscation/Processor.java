/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.maven.plugin.obfuscation;

import de.schlichtherle.truezip.file.TFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.java.truelicense.obfuscate.Obfuscate;
import org.objectweb.asm.ClassVisitor;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Set;

/**
 * Runs the obfuscation based on the configuration parameters provided to its
 * constructor(s).
 *
 * @author Christian Schlichtherle
 */
public final class Processor implements Runnable {

    /**
     * The default value of the {@code maxBytes} property, which is {@value}.
     *
     * @see #maxBytes()
     */
    public static final int DEFAULT_MAX_BYTES = 64 * 1024;

    /**
     * The default value of the {@code obfuscateAll} property, which is {@value}.
     *
     * @see #obfuscateAll()
     */
    public static final boolean DEFAULT_OBFUSCATE_ALL = false;

    /**
     * The default value of the {@code methodNameFormat} property, which is {@value}.
     *
     * @see #methodNameFormat()
     */
    public static final String DEFAULT_METHOD_NAME_FORMAT = "_%s#%d";

    /**
     * The default value of the {@code internStrings} property, which is {@value}.
     *
     * @see #internStrings()
     */
    public static final boolean DEFAULT_INTERN_STRINGS = true;

    private final Set<String> constantStrings = new HashSet<String>();

    private final Logger logger;
    private final TFile directory;
    private final int maxBytes;
    private final boolean obfuscateAll;
    private final String methodNameFormat;
    private final boolean internStrings;

    /** Returns a new builder for a processor. */
    public static Builder builder() { return new Builder(); }

    Processor(final Builder b) {
        this.logger = requireNonNull(b.logger);
        this.directory = b.directory instanceof TFile
                ? (TFile) b.directory
                : new TFile(b.directory);
        if (0 >= (this.maxBytes = nonNullOr(b.maxBytes, DEFAULT_MAX_BYTES)))
            throw new IllegalArgumentException();
        this.obfuscateAll = nonNullOr(b.obfuscateAll, DEFAULT_OBFUSCATE_ALL);
        this.methodNameFormat = nonNullOr(b.methodNameFormat, DEFAULT_METHOD_NAME_FORMAT);
        this.internStrings = nonNullOr(b.internStrings, DEFAULT_INTERN_STRINGS);
    }

    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    private static <T> T requireNonNull(@Nullable T t) {
        if (null == t) throw new NullPointerException();
        return t;
    }

    private static <T> T nonNullOr(@Nullable T value, T def) {
        return null != value ? value : def;
    }

    /**
     * Returns the logger to use.
     * Any errors should be logged at the error level to enable subsequent
     * checking.
     */
    public Logger logger() { return logger; }

    /** Returns the directory to scan for class files to process. */
    public File directory() { return directory; }

    /** Returns the maximum allowed size of a class file in bytes. */
    public int maxBytes() { return maxBytes;  }

    /**
     * Returns {@code true} if all constant string values shall get obfuscated.
     * Otherwise only constant string values of fields annotated with
     * {@link Obfuscate} shall get obfuscated.
     *
     * @return Whether or not all constant string values shall get obfuscated.
     */
    public boolean obfuscateAll() { return obfuscateAll; }

    /**
     * Returns the methodName for synthesized method names.
     * This a methodName string for the class {@link Formatter}.
     * It's first parameter is a string identifier for the obfuscation stage
     * and its second parameter is an integer index for the synthesized method.
     */
    public String methodNameFormat() { return methodNameFormat; }

    /**
     * Returns whether or not a call to <code>java.lang.String.intern()</code>
     * shall get added when computing the original constant string values again.
     * Use this to preserve the identity relation of constant string values if
     * required.
     */
    public boolean internStrings() { return internStrings; }

    /**
     * Returns the set of constant strings to obfuscate.
     * The returned set is only used when {@link #obfuscateAll} is
     * {@code false} and is modifiable so as to exchange the set between
     * different processing paths.
     *
     * @return The set of constant strings to obfuscate.
     */
    @SuppressWarnings("ReturnOfCollectionOrArrayField")
    Set<String> constantStrings() { return constantStrings; }

    Logger logger(String subject) {
        return new SubjectLogger(logger(), subject);
    }

    String methodName(String stage, int index) {
        return new Formatter().format(methodNameFormat(), stage, index).toString();
    }

    /** Runs the obfuscation processor. */
    @Override public void run() {
        firstPass().run();
        secondPass().run();
    }

    Runnable firstPass() { return new FirstPass(this); }
    Runnable secondPass() { return new SecondPass(this); }

    ClassVisitor collector() { return new Collector(this); }

    ClassVisitor obfuscator(ClassVisitor cv) {
        return new Obfuscator(this, cv);
    }

    ClassVisitor merger(ClassVisitor cv, String prefix) {
        return new Merger(this, prefix, cv);
    }

    public static final class Builder {

        @Nullable Logger logger;
        @Nullable File directory;
        @Nullable Integer maxBytes;
        @Nullable Boolean obfuscateAll;
        @Nullable String methodNameFormat;
        @Nullable Boolean internStrings;

        Builder() { }

        public Builder logger(final Logger logger) {
            this.logger = logger;
            return this;
        }

        public Builder directory(final File directory) {
            this.directory = directory;
            return this;
        }

        public Builder maxBytes(final Integer maxBytes) {
            this.maxBytes = maxBytes;
            return this;
        }

        public Builder obfuscateAll(final Boolean obfuscateAll) {
            this.obfuscateAll = obfuscateAll;
            return this;
        }

        public Builder methodNameFormat(final String methodNameFormat) {
            this.methodNameFormat = methodNameFormat;
            return this;
        }

        public Builder internStrings(final Boolean internStrings) {
            this.internStrings = internStrings;
            return this;
        }

        public Processor build() { return new Processor(this); }
    }
}