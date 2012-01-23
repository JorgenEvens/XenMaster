/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.wgr.lang.I18N;
import net.wgr.wcp.command.CommandException;
import net.wgr.xenmaster.api.NamedEntity;
import net.wgr.xenmaster.controller.BadAPICallException;

/**
 * 
 * @created Jan 19, 2012
 * @author double-u
 */
public class DetailedCommandException extends CommandException {

    protected String title;
    protected String name;
    // Uppercase alfanumeric + underscores, must contain at least one underscore
    protected final static Pattern ERROR_NAME = Pattern.compile("[A-Z0-9_]+_[A-Z0-9_]+");

    public DetailedCommandException(String commandName, BadAPICallException ex) {
        super(ex.getErrorName(), commandName);

        this.name = ex.getErrorName();
        String betterName = getNestedErrorName(ex.getErrorDescription());
        if (betterName == null) {
            betterName = ex.getErrorName();
        }
        this.message = getMessage(betterName);
        this.title = getTitle(betterName);
        
        if (ex.getOrigin() instanceof NamedEntity) {
            NamedEntity ne = (NamedEntity) ex.getOrigin();
            this.title = String.format(this.title, ne.getName(), ne.getDescription());
            this.message = String.format(this.message, ne.getName(), ne.getDescription());
        }
    }

    protected final String getMessage(String errorName) {
        return (I18N.hasText(errorName + "_MESSAGE") ? I18N.getText(errorName + "_MESSAGE") : errorName);
    }
    
    protected final String getTitle(String errorName) {
        return (I18N.hasText(errorName) ? I18N.getText(errorName) : errorName);
    }

    /**
     * XAPI error messages are user nor machine friendly
     * @param name original name
     * @return deepest error name
     */
    protected final String getNestedErrorName(String name) {
        Matcher matcher = ERROR_NAME.matcher(name);

        String lastMatch = null;
        while (matcher.find()) {
            String match = matcher.group();
            if (match.length() > 2) {
                lastMatch = match;
            }
        }

        return lastMatch;
    }
}
