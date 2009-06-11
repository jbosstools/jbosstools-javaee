package org.jboss.tools.seam.core.test.project.facet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class TestUtils {

	public static String readFromFile(final IFile file)

	throws CoreException, IOException

	{
		TestCase.assertTrue(file.exists());

		final StringBuffer buf = new StringBuffer();
		final Reader r = new InputStreamReader(file.getContents());

		try {
			char[] chars = new char[1024];

			for (int count; (count = r.read(chars)) != -1;) {
				buf.append(chars, 0, count);
			}
		} finally {
			try {
				r.close();
			} catch (IOException e) {
			}
		}

		return buf.toString();
	}

	public static void assertEquals(final IFile file,
			final String expectedContents)

	throws CoreException, IOException

	{
		TestCase.assertEquals(readFromFile(file), expectedContents);
	}

	public static void assertFileContains(final IFile file, final String str)

	throws CoreException, IOException

	{
		TestCase.assertTrue(readFromFile(file).indexOf(str) != -1);
	}

}