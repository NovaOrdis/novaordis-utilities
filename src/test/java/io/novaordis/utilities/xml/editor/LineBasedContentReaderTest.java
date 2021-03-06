/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.utilities.xml.editor;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/11/16
 */
public class LineBasedContentReaderTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(LineBasedContentReaderTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void emptyReader() throws Exception {

        LineBasedContent content = new LineBasedContent();
        assertEquals(0, content.getLineCount());

        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[4];
        assertZero(buffer, 0, 4);

        // we're already at the end of stream
        count = r.read(buffer, 0, 4);
        assertEquals(-1, count);
    }

    @Test
    public void read() throws Exception {

        LineBasedContent content = new LineBasedContent("test\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[1024];
        assertZero(buffer, 0, buffer.length);

        count = r.read(buffer, 0, 1);
        assertEquals('t', buffer[0]);
        assertZero(buffer, 1);
        assertEquals(1, count);

        count = r.read(buffer, 1, 1000);
        assertEquals('e', buffer[1]);
        assertEquals('s', buffer[2]);
        assertEquals('t', buffer[3]);
        assertEquals('\n', buffer[4]);
        assertZero(buffer, 5);
        assertEquals(4, count);

        count = r.read(buffer, 5, 1000);
        assertEquals(-1, count);
    }

    @Test
    public void read_MultipleLines_ExactNumberOfLines() throws Exception {

        LineBasedContent content = new LineBasedContent("test\ntest\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[1024];
        assertZero(buffer, 0);

        count = r.read(buffer, 0, 10);
        String s = new String(buffer, 0, 10);
        assertEquals("test\ntest\n", s);
        assertEquals(10, count);
        assertZero(buffer, 10);

        count = r.read(buffer, 0, 1000);
        assertEquals(-1, count);
        s = new String(buffer, 0, 10);
        assertEquals("test\ntest\n", s);
        assertZero(buffer, 10);
    }

    @Test
    public void read_MultipleLines_NotExactNumberOfLines() throws Exception {

        LineBasedContent content = new LineBasedContent("test\ntest\ntest\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[1024];
        assertZero(buffer, 0);

        count = r.read(buffer, 0, 12);
        String s = new String(buffer, 0, 12);
        assertEquals("test\ntest\nte", s);
        assertEquals(12, count);
        assertZero(buffer, 12);

        count = r.read(buffer, 0, 1000);
        assertEquals(3, count);
        s = new String(buffer, 0, 3);
        assertEquals("st\n", s);

        assertEquals(-1, r.getCurrentLine());
        assertEquals(-1, r.getCurrentPositionInLine());

        assertEquals(-1, r.read(buffer, 0, 1));
    }

    @Test
    public void read_AllCharactersAreOnTheFirstLine() throws Exception {

        LineBasedContent content = new LineBasedContent("test\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[5];
        assertZero(buffer, 0, buffer.length);

        count = r.read(buffer, 0, 2);
        assertEquals(2, count);
        assertEquals('t', buffer[0]);
        assertEquals('e', buffer[1]);
        assertZero(buffer, 2);

        assertEquals(0, r.getCurrentLine());
        assertEquals(2, r.getCurrentPositionInLine());

        count = r.read(buffer, 2, 3);
        assertEquals(3, count);
        assertEquals('t', buffer[0]);
        assertEquals('e', buffer[1]);
        assertEquals('s', buffer[2]);
        assertEquals('t', buffer[3]);
        assertEquals('\n', buffer[4]);

        assertEquals(-1, r.getCurrentLine());
        assertEquals(-1, r.getCurrentPositionInLine());

        // we reached end of stream
        count = r.read(buffer, 0, 1);
        assertEquals(-1, count);

        // the buffer is not changed
        assertEquals('t', buffer[0]);
        assertEquals('e', buffer[1]);
        assertEquals('s', buffer[2]);
        assertEquals('t', buffer[3]);
        assertEquals('\n', buffer[4]);
    }

    @Test
    public void subsequentReadAfterWeReachEndOfStream() throws Exception {

        LineBasedContent content = new LineBasedContent("test\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[1024];
        assertZero(buffer, 0);

        count = r.read(buffer, 0, 1000);
        assertEquals(5, count);
        assertEquals("test\n", new String(buffer, 0, 5));

        assertEquals(-1, r.read(buffer, 0, 1000));
        assertEquals("test\n", new String(buffer, 0, 5));

        assertEquals(-1, r.read(buffer, 0, 1000));
        assertEquals("test\n", new String(buffer, 0, 5));
    }

    @Test
    public void read_LenLargerThanBufferCapacity() throws Exception {

        LineBasedContent content = new LineBasedContent("test\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        char[] buffer = new char[2];
        assertZero(buffer, 0, buffer.length);

        // we are asking to read more than the buffer can hold, a StringReader responds with IndexOutOfBoundsException

        try {

            r.read(buffer, 0, 3);
            fail("should have thrown exception");
        }
        catch (IndexOutOfBoundsException e) {

            log.info(e.getMessage());
        }
    }

    // close() ---------------------------------------------------------------------------------------------------------

    @Test
    public void close() throws Exception {

        LineBasedContent content = new LineBasedContent("test\n");
        LineBasedContentReader r = new LineBasedContentReader(content);

        int count;
        char[] buffer = new char[1024];
        assertZero(buffer, 0);

        count = r.read(buffer, 0, 2);
        String s = new String(buffer, 0, 2);
        assertEquals("te", s);
        assertEquals(2, count);
        assertZero(buffer, 2);

        r.close();

        count = r.read(buffer, 0, 100);
        assertEquals(-1, count);

        //
        // make sure we don't change the origin LineBaseContent
        //

        assertEquals(1, content.getLineCount());
        Line line = content.get(0);
        assertNotNull(line);
        assertEquals("test", line.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void assertZero(char[] cbuf, int from, int len) {

        for(int i = from; i < from + len; i ++) {

            if (cbuf[i] != 0) {
                fail("we encountered non-zero value on position " + i);
            }
        }
    }

    /**
     * Asserts that the array elements are all zero, from the index 'from' to the end.
     */
    private void assertZero(char[] cbuf, int from) {

        assertZero(cbuf, from, cbuf.length - from);
    }


    // Inner classes ---------------------------------------------------------------------------------------------------

}
