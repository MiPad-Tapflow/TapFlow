package com.xiaomi.mslgrdp.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/* loaded from: classes5.dex */
public class RDPFileParser {
    private static final int MAX_ERRORS = 20;
    private static final int MAX_LINES = 500;
    private HashMap<String, Object> options;

    public RDPFileParser() {
        init();
    }

    public RDPFileParser(String filename) throws IOException {
        init();
        parse(filename);
    }

    private void init() {
        this.options = new HashMap<>();
    }

    public void parse(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        int errors = 0;
        int lines = 0;
        while (true) {
            String line = br.readLine();
            if (line != null) {
                lines++;
                boolean ok = false;
                if (errors > 20 || lines > MAX_LINES) {
                    break;
                }
                String[] fields = line.split(":", 3);
                if (fields.length == 3) {
                    if (fields[1].equals("s")) {
                        this.options.put(fields[0].toLowerCase(Locale.ENGLISH), fields[2]);
                        ok = true;
                    } else if (fields[1].equals("i")) {
                        try {
                            Integer i = Integer.valueOf(Integer.parseInt(fields[2]));
                            this.options.put(fields[0].toLowerCase(Locale.ENGLISH), i);
                            ok = true;
                        } catch (NumberFormatException e) {
                        }
                    } else if (fields[1].equals("b")) {
                        ok = true;
                    }
                }
                if (!ok) {
                    errors++;
                }
            } else {
                br.close();
                return;
            }
        }
        br.close();
        throw new IOException("Parsing limits exceeded");
    }

    public String getString(String optionName) {
        if (this.options.get(optionName) instanceof String) {
            return (String) this.options.get(optionName);
        }
        return null;
    }

    public Integer getInteger(String optionName) {
        if (this.options.get(optionName) instanceof Integer) {
            return (Integer) this.options.get(optionName);
        }
        return null;
    }
}
