/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.it.v2.xml

import org.junit.runner._
import org.scalatest.junit._
import org.truelicense.it.core.CodecTestSuite
import org.truelicense.it.v2.commons.V2CompressionTestContext

/** @author Christian Schlichtherle */
@RunWith(classOf[JUnitRunner])
class V2XmlCompressionTest
extends CodecTestSuite
   with V2XmlTestContext
   with V2CompressionTestContext
