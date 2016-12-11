package com.linewx.law.parser;

import com.linewx.law.parser.cfg.ConfigurationHelper;
import com.linewx.law.parser.cfg.ParserConfiguration;
import com.linewx.law.parser.json.RuleJson;
import com.linewx.law.parser.state.ParseState;
import com.linewx.law.parser.state.ParseStateImpl;

import java.util.*;

/**
 * Created by luganlin on 11/16/16.
 */
public class ParseStateMachine {
    private Map<String, com.linewx.law.parser.state.ParseState> states = new HashMap<>();
    private Map<String, List<Transition>> transitions = new HashMap<>();

    private Boolean showTransition = false;

    public ParseStateMachine(RuleJson rule) {
        this(rule, null);
    }

    public ParseStateMachine(RuleJson rule, Properties properties) {
        ActionHandler actionHandler = new ActionHandler();
        actionHandler.configuration(properties);
        rule.getStates().forEach(oneState ->
                states.put(oneState.getId(), new ParseStateImpl(oneState, actionHandler))
        );

        rule.getTransitions().forEach(oneTransition -> {
                    if (!transitions.containsKey(oneTransition.getSource())) {
                        transitions.put(oneTransition.getSource(), new ArrayList<>());
                    }
                    transitions.get(oneTransition.getSource()).add(new Transition(oneTransition));
                }
        );
        this.showTransition = ConfigurationHelper.getBoolean(ParserConfiguration.SHOW_TRANSITION, properties);
    }

    public String transform(String state, ParseContext context) {
        List<Transition> stateTransitions = transitions.get(state);
        if (stateTransitions == null) {
            return state;
            //throw new RuntimeException("unknown state:" + state);
        }

        String nextState = state;

        for (Transition oneTransition: stateTransitions) {
            if (oneTransition.match(context)) {
                nextState = oneTransition.getTarget();
            }
        }
        return nextState;
    }

    public void run(ParseContext context, List<String> content) {
        for (String statement : content) {
            if (statement == null || statement.isEmpty()) {
                continue;
            }
            if (showTransition) {
                System.out.println(context.getCurrentStatement());
            }
            stepContext(context, statement);
            parse(context);
        }
    }

    void stepContext(ParseContext context, String statement) {
        if (statement == null || statement.isEmpty()) {
            return;
        }

        context.setPreStatement(context.getCurrentStatement());
        context.setCurrentStatement(statement);
    }

    void parse(ParseContext context) {
        if (context.getCurrentStatement() == null || context.getCurrentStatement().isEmpty()) {
            return;
        }
        ParseState currentState = states.get(context.getCurrentState());
        String nextStateName = transform(context.getCurrentState(), context);
        if (nextStateName == null) {
            throw new RuntimeException("state is null");
        } else {
            ParseState nextState = states.get(nextStateName);
            if (nextState == null) {
                throw new RuntimeException("unknown state " + nextStateName);
            } else {
                if (nextStateName.equals(currentState.getState())) {
                    currentState.onStay(context);
                } else {
                    currentState.onExit(context);
                    context.setCurrentState(nextStateName);
                    if (showTransition) {
                        System.out.println("------" + nextStateName + "---------");
                    }
                    nextState.onEntry(context);


                }
            }
        }
    }
}
