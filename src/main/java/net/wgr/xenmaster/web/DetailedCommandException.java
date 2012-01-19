/*
 * Made by Wannes 'W' De Smet
 * (c) 2011 Wannes De Smet
 * All rights reserved.
 * 
 */
package net.wgr.xenmaster.web;

import net.wgr.lang.I18N;
import net.wgr.wcp.command.CommandException;

/**
 * 
 * @created Jan 19, 2012
 * @author double-u
 */
public class DetailedCommandException extends CommandException {

    protected String title;
    protected String name;

    public DetailedCommandException(String commandName, String errorName) {
        super((I18N.hasText(errorName + "_MESSAGE") ? I18N.getText(errorName + "_MESSAGE") : errorName), commandName);

        this.name = errorName;
        this.title = (I18N.hasText(errorName) ? I18N.getText(errorName) : errorName);
    }
}
