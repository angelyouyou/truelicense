/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.core

import java.util.Calendar._
import java.util.Date
import javax.security.auth.x500.X500Principal

import org.truelicense.api._
import org.truelicense.api.io.{Store, Transformation}
import org.truelicense.it.core.io.IdentityTransformation
import org.truelicense.obfuscate._

/** @author Christian Schlichtherle */
trait TestContext {

  val managementContext: LicenseManagementContext[ObfuscatedString]

  final lazy val vendorContext = managementContext.vendor

  def vendorManager: LicenseVendorManager
  def chainedVendorManager: LicenseVendorManager

  final lazy val consumerContext = managementContext.consumer

  final def consumerManager(): LicenseConsumerManager = consumerManager(store)
  def consumerManager(store: Store): LicenseConsumerManager
  def chainedConsumerManager(parent: LicenseConsumerManager, store: Store): LicenseConsumerManager
  def ftpConsumerManager(parent: LicenseConsumerManager, store: Store): LicenseConsumerManager

  def license = {
    val now = new Date
    val me = new X500Principal("CN=Christian Schlichtherle")
    val license = managementContext.license
    import license._
    setSubject("subject")
    setHolder(me)
    setIssuer(me)
    setIssued(now)
    setNotBefore(now)
    setInfo("info")
    setExtra(extraData)
    license
  }

  def extraData: AnyRef = { // must be AnyRef to enable overriding and returning a bean instead.
    // The XmlEncoder used with V1 format license keys supports only standard
    // Java collections by default, so I cannot use collection.JavaConverters
    // here because it would create a custom implementation.
    val map = new java.util.HashMap[String, String]
    map.put("message", "This is some private extra data!")
    map
  }

  final def datePlusDays(date: Date, days: Int) = {
    val cal = getInstance
    import cal._
    setTime(date)
    assert(isLenient)
    add(DATE, days)
    getTime
  }

  def store: Store = vendorContext.memoryStore
  def codec = vendorManager.parameters.codec
  def transformation: Transformation = IdentityTransformation
  def protection(password: ObfuscatedString) = managementContext protection password
}

/** @author Christian Schlichtherle */
object TestContext {
  def test1234 = new ObfuscatedString(Array[Long](0x545a955d0e30826cl, 0x3453ccaa499e6bael)) /* => "test1234" */
}