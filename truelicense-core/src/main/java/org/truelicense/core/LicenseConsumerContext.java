/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.core.auth.Authentication;
import org.truelicense.core.crypto.Encryption;
import org.truelicense.core.io.Source;
import org.truelicense.core.io.Store;
import org.truelicense.core.util.Builder;
import org.truelicense.core.util.Injection;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;
import java.io.File;

/**
 * A derived context for license consumer applications.
 * Use this context to configure a {@link LicenseConsumerManager} with the
 * required parameters.
 * For a demonstration of this API, please use the TrueLicense Maven Archetype
 * to generate a sample project - even if you don't use Maven to build your
 * software product.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseConsumerContext
extends LicenseApplicationContext {

    /**
     * Returns a builder for
     * {@linkplain LicenseConsumerManager license consumer managers}.
     * Call its {@link ManagerBuilder#build} method to obtain a configured
     * license consumer manager.
     */
    ManagerBuilder managerBuilder();

    /**
     * A builder for
     * {@linkplain LicenseConsumerManager license consumer managers}.
     * Call {@link #build} to obtain a configured license consumer manager.
     *
     * @author Christian Schlichtherle
     */
    interface ManagerBuilder
    extends Builder<LicenseConsumerManager>, Injection<ManagerBuilder> {

        /**
         * Sets the parent license consumer manager.
         * A parent license consumer manager is required to configure a
         * non-zero {@linkplain #ftpDays free trial period} (FTP).
         * The parent license consumer manager will be tried first whenever a
         * {@linkplain LicenseConsumerManager life cycle management method}
         * is executed, e.g. when verifying a license key.
         *
         * @return {@code this}.
         */
        ManagerBuilder parent(LicenseConsumerManager parent);

        /**
         * Returns a builder for the parent license consumer manager.
         * Call its {@link ManagerBuilder#inject} method to build and inject
         * the configured parent license consumer manager into this builder and
         * return it.
         * <p>
         * A parent license consumer manager is required to configure a
         * non-zero {@linkplain #ftpDays free trial period} (FTP).
         * The parent license consumer manager will be tried first whenever a
         * {@linkplain LicenseConsumerManager life cycle management method}
         * is executed, e.g. when verifying a license key.
         *
         * @see #parent(LicenseConsumerManager)
         */
        ManagerBuilder parentBuilder();

        /**
         * Sets the free trial period (FTP) in days (the 24 hour equivalent).
         * If this is zero, then no FTP is configured.
         * Otherwise, the {@linkplain #keyStore key store} needs to have a
         * password configured for the private key entry and a
         * {@linkplain #parent parent license consumer manager}
         * needs to be configured for the regular license keys.
         *
         * @return {@code this}.
         */
        ManagerBuilder ftpDays(int ftpDays);

        /**
         * Sets the authentication.
         *
         * @return {@code this}.
         */
        ManagerBuilder authentication(Authentication authentication);

        /**
         * Returns an injection for a key store based authentication.
         * Call its {@link Injection#inject} method to build and inject the
         * configured authentication into this builder and return it.
         * <p>
         * The keystore needs to have a key password configured if and only if
         * the license consumer manager to build defines a non-zero
         * {@linkplain #ftpDays free trial period} (FTP).
         *
         * @see #authentication(Authentication)
         */
        KsbaInjection<ManagerBuilder> keyStore();

        /**
         * Sets the encryption.
         * An encryption needs to be configured if no
         * {@linkplain #parent parent license consumer manager} is configured.
         * Otherwise, the encryption gets inherited from the parent license
         * consumer manager.
         *
         * @return {@code this}.
         */
        ManagerBuilder encryption(Encryption encryption);

        /**
         * Returns an injection for a password based encryption (PBE).
         * Call its {@link Injection#inject} method to build and inject the
         * configured encryption into this builder and return it.
         * <p>
         * PBE parameters need to be configured if no
         * {@linkplain #parent parent license consumer manager} is configured.
         * Otherwise, the PBE parameters get inherited from the parent license
         * consumer manager.
         *
         * @see #encryption(Encryption)
         */
        PbeInjection<ManagerBuilder> pbe();

        /**
         * Stores the license key in the given store.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeIn(Store store);

        /**
         * Stores the license key in the given file.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInFile(File file);

        /**
         * Stores the license key in the system preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInSystemNode(Class<?> classInPackage);

        /**
         * Stores the license keys in the user preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInUserNode(Class<?> classInPackage);
    }
}