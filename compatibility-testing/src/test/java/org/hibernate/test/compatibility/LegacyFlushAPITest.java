/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.compatibility;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.internal.log.DeprecationLogger;

import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * WARNING: because of a bug in the Gradle/IDEA integration, running
 * this test from IDEA will not use the intended dependencies, which
 * will have the test fail.
 * Verify changes invoking Gradle directly to workaround.
 *
 */
public class LegacyFlushAPITest extends BaseNonConfigCoreFunctionalTestCase {

	// TODO

}
