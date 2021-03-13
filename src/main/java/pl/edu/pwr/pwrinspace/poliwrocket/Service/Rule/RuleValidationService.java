package pl.edu.pwr.pwrinspace.poliwrocket.Service.Rule;

import jdk.jshell.JShell;
import jdk.jshell.SnippetEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Speech.Rule;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RuleValidationService {

    private final JShell js;

    public RuleValidationService() {
        js = JShell.create();
    }

    private boolean checkExpressionResult(List<SnippetEvent> snippetEvents) {
        AtomicBoolean returnValue = new AtomicBoolean(true);
        snippetEvents.forEach(snip -> {
            if (snip.status() == jdk.jshell.Snippet.Status.VALID
                    && !Boolean.parseBoolean(snip.value())
                    && returnValue.get()) {
                returnValue.set(false);
            }
        });
        return returnValue.get();
    }

    public boolean validate(Rule rule, Object ... values) {

        if(rule.getValidated() < Math.abs(rule.getValidationTimes())) {
            String expression = MessageFormat.format(rule.getCondition(),values);
            return makeValidationReturn(checkExpressionResult(js.eval(js.sourceCodeAnalysis().analyzeCompletion(expression).source())),rule);
        }
        return false;
    }

    private boolean makeValidationReturn(boolean checkResult, Rule rule) {
        if(checkResult) {
            rule.setValidated(rule.getValidated()+1);
        }
        return checkResult;
    }
}
