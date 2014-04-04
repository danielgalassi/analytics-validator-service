/**
 * 
 */
package org.validator.metadata;


/**
 * Interface that all test classes need to implement.
 * The <code>ValidatorEngine</code> calls these methods.
 * @author danielgalassi@gmail.com
 *
 */
public interface Test {

	public String getName();
	public void assertMetadata(Metadata repository, String result);

}
