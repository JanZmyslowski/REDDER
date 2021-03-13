package pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech;

import com.google.gson.annotations.Expose;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class TextToSpeech {

    @Expose
    private String textToSpeak;

    @Expose
    private  List<Rule> rules = new ArrayList<>();

    public TextToSpeech() {
    }

    public TextToSpeech(String textToSpeak, List<Rule> rules) {
        this.textToSpeak = textToSpeak;
        this.rules = rules;
    }

    public String getTextToSpeak() {
        return textToSpeak;
    }

    public String getTextToSpeak(Object ... params) {
        return MessageFormat.format(textToSpeak,params);
    }

    public List<Rule> getRules() {
        return rules;
    }
}
