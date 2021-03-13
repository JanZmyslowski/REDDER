package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;

import java.util.HashMap;
import java.util.List;

public class TextToSpeechDictionary extends BaseSaveModel {

    @Expose
    private HashMap<String, List<TextToSpeech>> speechRules = new HashMap<>();

    public TextToSpeechDictionary() {
        super("./config/", "speechConfig.json");
    }

    public List<TextToSpeech> getSpeechByTrigger(String key) {
        return speechRules.get(key);
    }

    private void add(String key, List<TextToSpeech> value) {
        speechRules.put(key, value);
    }

    public static TextToSpeechDictionary defaultModel() {
        TextToSpeechDictionary textToSpeechDictionary = new TextToSpeechDictionary();
        Rule rule = new Rule("{0} > 500",1);
        Rule rule2 = new Rule("{0} > 1500",1);
        TextToSpeech sp = new TextToSpeech("Reached altitude {0} meters.", List.of(rule,rule2));
        Rule apogee = new Rule("{0} < {1}",1);
        TextToSpeech sp2 = new TextToSpeech("Apogee detected on {0} meters.", List.of(apogee));
        textToSpeechDictionary.add("Altitude",List.of(sp,sp2));

        return textToSpeechDictionary;
    }
}
