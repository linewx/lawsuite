package com.linewx.law.parser.state;

import com.linewx.law.parser.ActionHandler;
import com.linewx.law.parser.ParseContext;
import com.linewx.law.parser.json.ActionJson;
import com.linewx.law.parser.json.StateJson;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by luganlin on 11/16/16.
 */
public class ParseStateImpl implements ParseState{
    private String state;
    private ActionHandler actionHandler;

    private List<ActionJson> onEntryActions;
    private List<ActionJson> onStayActions;
    private List<ActionJson> onExitActions;
    private List<ActionJson> onEntryLineAction;
    private List<ActionJson> onExitLineAction;

    public ParseStateImpl(StateJson state, ActionHandler actionHandler) {
        this.state = state.getId();
        this.actionHandler = actionHandler;

        this.onEntryActions = state.getOnEntry() != null ?  state.getOnEntry() : new LinkedList<>();
        this.onStayActions =  state.getOnStay() != null ?  state.getOnStay() : new LinkedList<>();
        this.onExitActions =  state.getOnExit() != null ?  state.getOnExit() : new LinkedList<>();
        this.onEntryLineAction =  state.getOnEntryLine() != null ?  state.getOnEntryLine() : new LinkedList<>();
        this.onExitActions =  state.getOnExitLine() != null ?  state.getOnExitLine() : new LinkedList<>();
        this.onExitLineAction =  state.getOnExitLine() != null ?  state.getOnExitLine() : new LinkedList<>();
    }



    @Override
    public String getState() {
        return state;
    }

    @Override
    public void onEntry(ParseContext context) {
        this.onEntryActions.forEach(action ->
            actionHandler.execute(context, action.getAction(), action.getParameters())
        );

        this.onEntryLine(context);
    }

    @Override
    public void onExit(ParseContext context) {
        this.onExitLine(context);

        this.onExitActions.forEach(action ->
                actionHandler.execute(context, action.getAction(), action.getParameters())
        );

    }

    @Override
    public void onStay(ParseContext context) {
        this.onEntryLine(context);

        this.onStayActions.forEach(action ->
                actionHandler.execute(context, action.getAction(), action.getParameters())
        );

        this.onExitLine(context);


    }

    @Override
    public void onEntryLine(ParseContext context) {
        this.onEntryLineAction.forEach(action ->
                actionHandler.execute(context, action.getAction(), action.getParameters())
        );
    }

    @Override
    public void onExitLine(ParseContext context) {
        this.onExitLineAction.forEach(action ->
                actionHandler.execute(context, action.getAction(), action.getParameters())
        );
    }

    @Override
    public String transform(ParseContext context) {
        return null;
    }
}
