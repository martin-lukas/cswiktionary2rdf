package org.cswiktionary2rdf.utils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.security.MessageDigest;

public class Downloader {
    public static File downloadTo(Archive archive, String downloadDir) throws IOException {
        String archiveUrl = archive.getArchiveUrl();
        // save file has the same name as the archive, except the file extension
        File saveFile = prepareSaveFile(downloadDir + "\\" + archiveUrl.substring(archiveUrl.lastIndexOf("/") + 1));
        try {
            URL url = new URL(archive.getArchiveUrl());
            ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        } catch (MalformedURLException e) {
            System.err.println("The dump file URL is invalid.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("There was a problem during the download of the dump file.");
            e.printStackTrace();
        }
    
        return (isChecksumValid(saveFile, archive.getCorrectChecksum())) ? saveFile : null;
    }
    
    private static File prepareSaveFile(String saveFilePath) throws IOException {
        File saveFile = new File(saveFilePath);
        Files.deleteIfExists(saveFile.toPath());
        return saveFile;
    }
    
    private static boolean isChecksumValid(File saveFile, String correctChecksum) {
        boolean isChecksumValid = false;
        try {
            isChecksumValid = compareChecksums(saveFile, correctChecksum);
        } catch (Exception e) {
            System.err.println("Error when checking checksum values.");
            e.printStackTrace();
        }
        return isChecksumValid;
    }
    
    private static boolean compareChecksums(File file, String correctChecksum) throws Exception {
        String archiveChecksum = calculateSHA1(file);
        return archiveChecksum.equals(correctChecksum);
    }
    
    private static String calculateSHA1(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        InputStream fis = new FileInputStream(file);
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            n = fis.read(buffer);
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return byteArrayToHexString(digest.digest());
    }
    
    private static String byteArrayToHexString(byte[] b) {
        StringBuilder result = new StringBuilder();
        for (byte value : b) {
            result.append(Integer.toString((value & 0xff) + 0x100, 16).substring(1));
        }
        return result.toString();
    }
}
