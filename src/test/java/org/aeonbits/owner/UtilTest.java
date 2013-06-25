/*
 * Copyright (c) 2013, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package org.aeonbits.owner;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import static org.aeonbits.owner.Util.ignore;
import static org.aeonbits.owner.Util.unreachable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests for the {@link Util} class as well utility methods used for test classes.
 *
 * @author Luigi R. Viggiano
 */
public class UtilTest {
    @Test(expected = UnsupportedOperationException.class)
    public void testConstructor() {
        new Util(){};
    }

    @Test
    public void testReverse() {
        Integer[] i = { 1, 2, 3, 4, 5};
        Integer[] result = Util.reverse(i);
        assertTrue(Arrays.equals(new Integer[]{1, 2, 3, 4, 5}, i));
        assertTrue(Arrays.equals(new Integer[]{5, 4, 3, 2, 1}, result));
    }

    @Test
    public void testIgnore() {
        Object result = ignore();
        assertNull(result);
    }

    @Test
    public void testUnreachable() {
        try {
            unreachable();
        } catch(AssertionError err) {
            assertEquals("this code should never be reached", err.getMessage());
        }
    }

    public static void save(File target, Properties p) throws IOException {
        target.getParentFile().mkdirs();
        p.store(new FileWriter(target), "saved for test");
    }

    public static void delete(File target) throws IOException {
        target.delete();
    }

    public static void saveJar(File file, String entryName, Properties props) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        props.store(out, "saved for test");
        InputStream input = new ByteArrayInputStream(out.toByteArray());
        JarOutputStream output = new JarOutputStream(
                new FileOutputStream(file));
        try {
            ZipEntry entry = new ZipEntry(entryName);
            output.putNextEntry(entry);
            byte[] buffer = new byte[4096];
            int size;
            while ((size = input.read(buffer)) != -1)
                output.write(buffer, 0, size);
        } finally {
            input.close();
            output.close();
        }
    }

    public static void debug(String format, Object... args) {
        if (Boolean.getBoolean("debug"))
            System.out.printf(format, args);
    }

    public static interface MyCloneable extends Cloneable {
        // for some stupid reason java.lang.Cloneable doesn't define this method...
        public Object clone() throws CloneNotSupportedException;
    }

    @SuppressWarnings("unchecked")
    public static <T extends MyCloneable> T[] newArray(int size, T cloneable) throws CloneNotSupportedException {
        Object array = Array.newInstance(cloneable.getClass(), size);
        Array.set(array, 0, cloneable);
        for (int i = 1; i < size; i++)
            Array.set(array, i, cloneable.clone());
        return (T[]) array;
    }

}
