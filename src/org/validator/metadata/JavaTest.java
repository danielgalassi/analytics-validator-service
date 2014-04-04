/**
 * 
 */
package org.validator.metadata;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author daniel
 *
 */
public class JavaTest extends ClassLoader implements Test {

	String name;
	Test test = null;
	Class t = null;

	public JavaTest(InputStream s) {
		ClassLoader classLoader = JavaTest.class.getClassLoader();
		try {

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int data = s.read();

			while(data != -1){
				buffer.write(data);
				data = s.read();
			}

			s.close();
			byte[] classData = buffer.toByteArray();
			System.out.println(1);
			try {
				defineClass("test/MyJavaTest", classData, 0, classData.length);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println(3);
			try {
				defineClass("test.MyJavaTest", classData, 0, classData.length);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			System.out.println(2);
			System.out.println("Interfaces: " + t.getInterfaces()[0].toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.validator.metadata.Test#getName()
	 */
	@Override
	public String getName() {
		try {
			test = (Test) t.newInstance();
			name = test.getName();
			System.out.println(name);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return name;
	}

	/* (non-Javadoc)
	 * @see org.validator.metadata.Test#assertMetadata(org.validator.metadata.Metadata, java.lang.String)
	 */
	@Override
	public void assertMetadata(Metadata repository, String result) {
		test.assertMetadata(null, "hey hey LBJ");
	}

}
