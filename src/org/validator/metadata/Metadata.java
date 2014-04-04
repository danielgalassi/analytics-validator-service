/**
 * 
 */
package org.validator.metadata;

import java.io.File;

/**
 * @author danielgalassi@gmail.com
 *
 */
public interface Metadata {

	public boolean available();
	public File toFile();
}
