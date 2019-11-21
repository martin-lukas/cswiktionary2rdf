package org.cswiktionary2rdf.cli.tasks;

import org.cswiktionary2rdf.utils.Archive;
import org.cswiktionary2rdf.utils.Downloader;
import org.cswiktionary2rdf.utils.Locator;
import org.cswiktionary2rdf.utils.Unzipper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;

public class DownloadTask implements Task {
    private String downloadDir;
    private File dumpFile;
    
    public DownloadTask() {
        this.downloadDir = new File(System.getProperty("user.home")
                + System.getProperty("file.separator") + "Downloads").getAbsolutePath();
    }
    
    public DownloadTask(String downloadDir) {
        try {
            if (new File(downloadDir).isDirectory()) {
                this.downloadDir = downloadDir;
            } else {
                throw new NotDirectoryException(downloadDir);
            }
        } catch (NotDirectoryException e) {
            System.err.println("The path provided is not a directory.");
            e.printStackTrace();
        }
    }
    
    @Override
    public void execute() {
        System.out.println("Searching for the newest cs.wiktionary.org dump archive...");
        Archive archive = null;
        try {
            archive = Locator.locateArchive();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (archive == null) {
            System.err.println("Couldn't locate the newest dump file online.");
            System.exit(-1);
        }
        
        System.out.println("Downloading the file...");
        File downloadedFile = null;
        try {
            downloadedFile = Downloader.downloadTo(archive, downloadDir);
        } catch (IOException e) {
            System.out.println("Attempt to delete file resulted in exception. Override unsuccessful.");
            e.printStackTrace();
        }
        if (downloadedFile == null) {
            System.err.println("The downloading of the dump archive wasn't successful.");
            System.exit(-1);
        }
        System.out.println("Download successful.");
        
        System.out.println("Unzipping dump archive file...");
        this.dumpFile = Unzipper.unzip(downloadedFile);
        if (dumpFile == null) {
            System.err.println("The unizpping wasn't successful.");
            System.exit(-1);
        }
        System.out.println("Unzipping successful.");
        
        // remove the archive
        try {
            Files.deleteIfExists(downloadedFile.toPath());
        } catch (IOException ex) {
            System.err.println("Couldn't delete the dump archive after unzipping.");
            ex.printStackTrace();
        }
        
        // for testing purposes
        System.exit(1);
    }
    
    public File getDumpFile() {
        return dumpFile;
    }
}
