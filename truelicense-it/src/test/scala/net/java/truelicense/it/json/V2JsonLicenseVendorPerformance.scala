/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.it.json

import net.java.truelicense.it.LicenseVendorPerformance

/** @author Christian Schlichtherle */
object V2JsonLicenseVendorPerformance
extends LicenseVendorPerformance with V2JsonTestContext {
  def main(args: Array[String]) = call ()
}