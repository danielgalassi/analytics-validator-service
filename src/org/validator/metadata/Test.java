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

	/**
	 * Getter method for the test name
	 * @return the name of the test
	 */
	public String getName();

	/**
	 * Asserts metadata status
	 * @param repository a trimmed OBIEE repository file in XUML format
	 * @param result path where test results are saved to
	 */
	public void assertMetadata(Repository repository, String result);

}
