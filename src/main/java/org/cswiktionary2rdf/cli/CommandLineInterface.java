package org.cswiktionary2rdf.cli;

import org.cswiktionary2rdf.cli.tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Command line interface of the program. This is the main class. It launches all the possible tasks
 * based on the inputted command line arguments.
 *
 * @author Martin Lukáš
 */
public class CommandLineInterface {
    
    public static void main(String[] args) {
        LocalDateTime start = LocalDateTime.now().withNano(0);
        
        Task helpTask = new HelpTask();
        
        int arglen = args.length;
        
        if (arglen == 1) {
            if ("-d".equals(args[0])) {
                new DownloadTask().execute();
            } else if ("-h".equals(args[0])) {
                helpTask.execute();
            } else {
                System.err.println("Invalid command.\n");
                helpTask.execute();
            }
        } else if (arglen == 2) {
            if ("-d".equals(args[0])) {
                new DownloadTask(args[1]).execute();
            } else {
                System.err.println("Invalid command.\n");
                helpTask.execute();
            }
        } else if (arglen == 4) {
            if (args[0].equals("-e")) {
                ExtractTask extractTask = new ExtractTask(args[1], args[2], args[3]);
                extractTask.execute();
            } else {
                System.err.println("Invalid command.\n");
                helpTask.execute();
            }
        } else {
            System.err.println("Invalid command.\n");
            helpTask.execute();
        }
        // for debug
        Duration duration = Duration.between(start, LocalDateTime.now());
        System.out.println("Total time: "
                + duration.toMinutes() + " min "
                + (duration.toSeconds() - duration.toMinutes() * 60) + " sec.");
    }
}
