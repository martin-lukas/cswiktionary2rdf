package org.cswiktionary2rdf.downloading;

import org.cswiktionary2rdf.cli.tasks.DownloadTask;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.Assertion;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import java.io.File;

import static org.junit.Assert.*;

public class DownloadTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
    
    @Test
    public void downloadArchiveTest() {
        // TODO: change download test so it doesn't have to download so much data (maybe a sample
        assert false;
//        DownloadTask downloadTask = new DownloadTask();
//        exit.expectSystemExitWithStatus(1);
//        exit.checkAssertionAfterwards(new Assertion() {
//            public void checkAssertion() {
//                assertNotNull(downloadTask.getDumpFile());
//            }
//        });
//        downloadTask.execute();
    }
}
