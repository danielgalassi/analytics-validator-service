/**
 * 
 */
package org.validator.engine;

import java.io.File;

/**
 * @author danielgalassi@gmail.com
 *
 */
public interface Test {
	public void		execute(File rpd);
	public void		reset();
	public String	getName();
	public String	getResultFile();
}
