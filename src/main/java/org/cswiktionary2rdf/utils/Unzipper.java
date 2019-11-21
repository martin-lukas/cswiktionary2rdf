package org.cswiktionary2rdf.utils;

import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Unzipper {
    
    public static File unzip(File zippedFile) {
        String zippedFilename = zippedFile.getAbsolutePath();
        File unzippedFile = new File(zippedFilename.substring(0, zippedFilename.lastIndexOf(".")));
        
        try (FileInputStream fileIn = new FileInputStream(zippedFile)) {
            BufferedInputStream bufferIn = new BufferedInputStream(fileIn);
            CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bufferIn);
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(unzippedFile),
                    StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
            }
            return unzippedFile;
        } catch (FileNotFoundException e) {
            System.err.println("The archive file can't be unzipped because it can't be found.");
            e.printStackTrace();
        } catch (CompressorException e) {
            System.err.println("Error during the decompression.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error during writing decompressed content into file.");
            e.printStackTrace();
        }
        return null;
    }
}
