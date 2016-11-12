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

import io.novaordis.utilities.Files;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/10/16
 */
public class InLineXmlEditorTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(InLineXmlEditorTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------


    private File scratchDirectory;

    @Before
    public void before() throws Exception {

        String projectBaseDirName = System.getProperty("basedir");
        scratchDirectory = new File(projectBaseDirName, "target/test-scratch");
        assertTrue(scratchDirectory.isDirectory());
    }

    @After
    public void after() throws Exception {

        //
        // scratch directory cleanup
        //

        assertTrue(io.novaordis.utilities.Files.rmdir(scratchDirectory, false));
    }

    // preconditions fail ----------------------------------------------------------------------------------------------

    @Test
    public void fileDoesNotExist() throws Exception {

        File doesNotExist = new File("/I/am/sure/this/file/does/not/exist.xml");

        try {
            new InLineXmlEditor(doesNotExist);
            fail("should throw exception");
        }
        catch(IOException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertEquals("file /I/am/sure/this/file/does/not/exist.xml does not exist", msg);
        }
    }

    @Test
    public void fileCannotBeWritten() throws Exception {

        File sampleFile =
                new File(System.getProperty("basedir"), "src/test/resources/data/xml/file-that-cannot-be-written.xml");

        assertTrue(sampleFile.isFile());
        assertTrue(sampleFile.canRead());
        assertFalse(sampleFile.canWrite());

        try {
            new InLineXmlEditor(sampleFile);
            fail("should throw exception");
        }
        catch(IOException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.matches("^file .*src/test/resources/data/xml/file-that-cannot-be-written\\.xml cannot be written"));
        }
    }

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        String content =
                "<test>\n" +
                "   <something/>\n" +
                "</test>";

        File f = new File(scratchDirectory, "test.xml");
        assertTrue(Files.write(f, content));

        InLineXmlEditor editor = new InLineXmlEditor(f);

        assertEquals(3, editor.getLineCount());
        assertFalse(editor.isDirty());
        assertEquals(new File(scratchDirectory, "test.xml"), editor.getFile());
    }

    // save ------------------------------------------------------------------------------------------------------------

    @Test
    public void save_NotDirty() throws Exception {

        File xmlFile = new File(scratchDirectory, "test.xml");
        Files.write(xmlFile, "<root/>");

        InLineXmlEditor editor = new InLineXmlEditor(xmlFile);

        assertEquals("<root/>", Files.read(xmlFile));

        assertFalse(editor.isDirty());

        //
        // change the file on disk, then save. Saving should not have any effect
        //

        Files.write(xmlFile, "<blah/>");

        assertEquals("<blah/>", Files.read(xmlFile));

        //
        // save, nothing should happen
        //

        editor.save();

        assertEquals("<blah/>", Files.read(xmlFile));
    }

    @Test
    public void save_Dirty() throws Exception {

        File xmlFile = new File(scratchDirectory, "test.xml");
        Files.write(xmlFile, "<root><a>?</a></root>");

        InLineXmlEditor editor = new InLineXmlEditor(xmlFile);
        assertFalse(editor.isDirty());

        //
        // change the file on disk, then save. It should overwrite.
        //

        Files.write(xmlFile, "<blah/>");
        assertEquals("<blah/>", Files.read(xmlFile));

        assertTrue(editor.set("/root/a", "!"));

        editor.save();

        assertEquals("<root><a>!</a></root>", Files.read(xmlFile));
    }

    // end to end ------------------------------------------------------------------------------------------------------

    @Test
    public void simpleXmlFileEndToEnd() throws Exception {

        File origFile = new File(System.getProperty("basedir"), "src/test/resources/data/xml/simple.xml");

        assertTrue(origFile.isFile());

        //
        // copy it in the test area
        //

        File sampleFile = new File(scratchDirectory, "simple-copy.xml");

        assertTrue(Files.cp(origFile, sampleFile));

        InLineXmlEditor editor = new InLineXmlEditor(sampleFile);

        assertFalse(editor.isDirty());
        assertEquals(3, editor.getLineCount());

        //
        // attempt to overwrite a value with the same
        //

        assertFalse(editor.set("/example/color", "red"));
        assertFalse(editor.isDirty());

        //
        // attempt to overwrite a value with a different one
        //

        assertTrue(editor.set("/example/color", "blue"));
        assertTrue(editor.isDirty());

        editor.save();

        assertFalse(editor.isDirty());

        //
        // make sure the change went to disk
        //

        InLineXmlEditor editor2 = new InLineXmlEditor(sampleFile);

        String s = editor2.get("/example/color");

        assertEquals("blue", s);
    }

    @Test
    public void endToEnd() throws Exception {

        File origFile = new File(System.getProperty("basedir"), "src/test/resources/data/xml/pom-sample.xml");

        assertTrue(origFile.isFile());

        //
        // copy it in the test area
        //

        File sampleFile = new File(scratchDirectory, "pom-sample.xml");

        assertTrue(Files.cp(origFile, sampleFile));

        InLineXmlEditor editor = new InLineXmlEditor(sampleFile);

        assertFalse(editor.isDirty());
        assertEquals(28, editor.getLineCount());

        //
        // attempt to overwrite a value with the same
        //

        assertFalse(editor.set("/project/version", "1.0.0-SNAPSHOT-1"));
        assertFalse(editor.isDirty());

        //
        // attempt to overwrite a value with a different one
        //

        assertTrue(editor.set("/project/version", "1.0.0-SNAPSHOT-2"));
        assertTrue(editor.isDirty());

        editor.save();

        assertFalse(editor.isDirty());

        //
        // make sure the change went to disk
        //

        InLineXmlEditor editor2 = new InLineXmlEditor(sampleFile);

        String s = editor2.get("/project/version");

        assertEquals("1.0.0-SNAPSHOT-2", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}
