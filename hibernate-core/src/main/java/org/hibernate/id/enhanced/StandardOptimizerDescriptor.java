/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.enhanced;

import org.hibernate.internal.util.StringHelper;

import org.jboss.logging.Logger;

/**
 * Enumeration of the standard Hibernate id generation optimizers.
 *
 * @author Steve Ebersole
 */
public enum StandardOptimizerDescriptor {
	/**
	 * Describes the optimizer for no optimization
	 */
	NONE( "none" ) {
		@Override
		public Optimizer constructInstance(Class returnClass, int incrementSize) {
			return new NoopOptimizer( returnClass, incrementSize );
		}
	},
	/**
	 * Describes the optimizer for using a custom "hilo" algorithm optimization
	 */
	HILO( "hilo" ) {
		@Override
		public Optimizer constructInstance(final Class returnClass, final int incrementSize) {
			return new HiLoOptimizer( returnClass, incrementSize );
		}
	},
	/**
	 * Describes the optimizer for using a custom "hilo" algorithm optimization, following the legacy
	 * Hibernate hilo algorithm
	 */
	LEGACY_HILO( "legacy-hilo" ) {
		@Override
		public Optimizer constructInstance(final Class returnClass, final int incrementSize) {
			return new LegacyHiLoAlgorithmOptimizer( returnClass, incrementSize );
		}
	},
	/**
	 * Describes the optimizer for use with tables/sequences that store the chunk information.  Here, specifically the
	 * hi value is stored in the database.
	 */
	POOLED( "pooled", true ) {
		@Override
		public Optimizer constructInstance(final Class returnClass, final int incrementSize) {
			return new PooledOptimizer( returnClass, incrementSize );
		}
	},
	/**
	 * Describes the optimizer for use with tables/sequences that store the chunk information.  Here, specifically the
	 * lo value is stored in the database.
	 */
	POOLED_LO( "pooled-lo", true ) {
		@Override
		public Optimizer constructInstance(final Class returnClass, final int incrementSize) {
			return new PooledLoOptimizer( returnClass, incrementSize );
		}
	},
	/**
	 * Describes the optimizer for use with tables/sequences that store the chunk information.  Here, specifically the
	 * lo value is stored in the database and ThreadLocal used to cache the generation state.
	 */
	POOLED_LOTL( "pooled-lotl", true ) {
		@Override
		public Optimizer constructInstance(final Class returnClass, final int incrementSize) {
			return new PooledLoThreadLocalOptimizer( returnClass, incrementSize );
		}
	};

	private static final Logger log = Logger.getLogger( StandardOptimizerDescriptor.class );

	private final String externalName;
	private final boolean isPooled;

	StandardOptimizerDescriptor(String externalName) {
		this( externalName, false );
	}

	StandardOptimizerDescriptor(String externalName, boolean pooled) {
		this.externalName = externalName;
		this.isPooled = pooled;
	}

	public String getExternalName() {
		return externalName;
	}

//	public Class<? extends Optimizer> getOptimizerClass() {
//		return optimizerClass;
//	}

	public abstract Optimizer constructInstance(Class returnClass, int incrementSize);

	public boolean isPooled() {
		return isPooled;
	}

	/**
	 * Interpret the incoming external name into the appropriate enum value
	 *
	 * @param externalName The external name
	 *
	 * @return The corresponding enum value; if no external name is supplied, {@link #NONE} is returned; if an
	 * unrecognized external name is supplied, {@code null} is returned
	 */
	public static StandardOptimizerDescriptor fromExternalName(String externalName) {
		if ( StringHelper.isEmpty( externalName ) ) {
			log.debug( "No optimizer specified, using NONE as default" );
			return NONE;
		}
		else if ( NONE.externalName.equals( externalName ) ) {
			return NONE;
		}
		else if ( HILO.externalName.equals( externalName ) ) {
			return HILO;
		}
		else if ( LEGACY_HILO.externalName.equals( externalName ) ) {
			return LEGACY_HILO;
		}
		else if ( POOLED.externalName.equals( externalName ) ) {
			return POOLED;
		}
		else if ( POOLED_LO.externalName.equals( externalName ) ) {
			return POOLED_LO;
		}
		else if ( POOLED_LOTL.externalName.equals( externalName ) ) {
			return POOLED_LOTL;
		}
		else {
			log.debugf( "Unknown optimizer key [%s]; returning null assuming Optimizer impl class name", externalName );
			return null;
		}
	}
}
