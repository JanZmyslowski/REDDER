package pl.edu.pwr.pwrinspace.poliwrocket.Service.Speech;


import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech.TextToSpeechDictionary;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Rule.RuleValidationService;

import javax.speech.Central;
import javax.speech.EngineException;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextToSpeechService implements InvalidationListener {

    private Synthesizer synthesizer;

    private final RuleValidationService ruleValidationService;

    private final TextToSpeechDictionary textToSpeechDictionary;

    public TextToSpeechService(RuleValidationService ruleValidationService, TextToSpeechDictionary textToSpeechDictionary) {
        this.ruleValidationService = ruleValidationService;
        this.textToSpeechDictionary = textToSpeechDictionary;
        try {
            // Set property as Kevin Dictionary
            System.setProperty(
                    "freetts.voices",
                    "com.sun.speech.freetts.en.us"
                            + ".cmu_us_kal.KevinVoiceDirectory");

            // Register Engine
            Central.registerEngineCentral(
                    "com.sun.speech.freetts"
                            + ".jsapi.FreeTTSEngineCentral");

            // Create a Synthesizer
            synthesizer
                    = Central.createSynthesizer(
                    new SynthesizerModeDesc(Locale.US));

            // Allocate synthesizer
            synthesizer.allocate();

            // Resume Synthesizer
            synthesizer.resume();

            // Speaks the given text
            // until the queue is empty.
            synthesizer.waitEngineState(
                    Synthesizer.QUEUE_EMPTY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void speak(String textToSpeak) {
        if(synthesizer != null) {
            synthesizer.speakPlainText(textToSpeak, null);
        }
    }

    public void deallocate() {
        if(synthesizer != null) {
            try {
                synthesizer.deallocate();

            } catch (EngineException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void invalidated(Observable observable) {
        var sensor = (Sensor) observable;

        if(sensor.isBoolean() && sensor.getPreviousValue() == sensor.getValue()) {
            return;
        }

        var speechList = textToSpeechDictionary.getSpeechByTrigger(sensor.getName());
        if(speechList != null && !speechList.isEmpty()) {
            speechList.forEach(speechObject -> {
                AtomicBoolean valid = new AtomicBoolean(false);
                speechObject.getRules().forEach(rule -> {
                    if(ruleValidationService.validate(rule,Double.toString(sensor.getValue()),Double.toString(sensor.getPreviousValue()))
                            && !valid.get()) {
                        valid.set(true);
                    }
                });
                if(valid.get()){
                    speak(speechObject.getTextToSpeak(Double.toString(sensor.getValue())));
                }
            });
        }
    }
}
