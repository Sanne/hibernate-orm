/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

//$
package org.hibernate.test.annotations.idmanytoone.alphabetical;
import java.math.BigInteger;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Service {
    @Id
    private BigInteger idpk;
}
