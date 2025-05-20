package main.com.pyratron.pugmatt.bedrockconnect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import main.com.pyratron.pugmatt.bedrockconnect.BedrockConnect;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Language {
    public final String DEFAULT_PATH = "language.json";

    private HashMap<String, HashMap<String,String>> elements;

    public Language(String languageFile) {
        elements = new HashMap<>();

        try {
            // Load default language elements
            JSONObject defaultLang = loadDefault();

            for (Object wording : defaultLang.keySet()) {
                elements.put((String) wording, (HashMap<String,String>) defaultLang.get(wording));
            }

            // If a custom language file is defined, overwrite any default wording elements with the ones that exist in this file
            if(languageFile != null) {
                JSONObject customLang = loadCustom(languageFile);

                for (Object wording : customLang.keySet()) {
                    if(!elements.containsKey(wording)) continue;
                    HashMap<String,String> set = elements.get(wording);
                    HashMap<String,String> newSet = (HashMap<String,String>) customLang.get(wording);

                    for (Object el : newSet.keySet()) {
                        set.put((String) el, newSet.get(el));
                    }

                    elements.put((String) wording, set);
                }

                BedrockConnect.logger.info("Loaded custom language elements");
                BedrockConnect.logger.debug("Language data: " + elements.toString());
            }
        } catch (Exception e) {
            BedrockConnect.logger.error("Error loading language file", e);
            System.exit(1);
        }
    }

    private JSONObject loadDefault() throws IOException {
        InputStream stream = BedrockConnect.class.getClassLoader().getResourceAsStream(DEFAULT_PATH);

        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> langMap = mapper.readValue(stream, Map.class);

        return new JSONObject(langMap);
    }

    private JSONObject loadCustom(String file) throws IOException, ParseException {
        return (JSONObject) new JSONParser().parse(new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8")));
    }

    public String getWording(String rootKey, String key) {
        return elements.containsKey(rootKey) && elements.get(rootKey).containsKey(key) ? elements.get(rootKey).get(key).toString() : "N/A";
    }
}
