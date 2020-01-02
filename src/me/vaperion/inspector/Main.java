package me.vaperion.inspector;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;

public class Main {

    public static void main(String[] args) {
        OptionParser parser = new OptionParser();
        OptionSpec<String> urlParam = parser.accepts("url").withRequiredArg().required().ofType(String.class);
        OptionSpec<File> cacheParam = parser.accepts("cache").withRequiredArg().required().ofType(File.class);
        OptionSpec<File> outputParam = parser.accepts("output").withRequiredArg().required().ofType(File.class);
        OptionSpec<Boolean> silentParam = parser.accepts("silent").withOptionalArg().ofType(Boolean.class).defaultsTo(false);

        OptionSet options;

        try {
            options = parser.parse(args);
        } catch (OptionException ex) {
            System.out.println("program --url <url> --cache <path> --output <path> --silent <true/false>");
            System.out.println(ex.getMessage());
            System.exit(1);
            return;
        }

        File inputFile = options.valueOf(cacheParam);
        File outputFile = options.valueOf(outputParam);

        try {
            if (inputFile.exists()) inputFile.delete();
            URL url = new URL(options.valueOf(urlParam));
            try (InputStream is = url.openStream()) {
                Files.copy(is, inputFile.toPath());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1337);
        }

        try {
            new Inspector(inputFile, outputFile, options.valueOf(silentParam));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

}