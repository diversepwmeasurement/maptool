/*
 *  This software copyright by various authors including the RPTools.net
 *  development team, and licensed under the LGPL Version 3 or, at your
 *  option, any later version.
 *
 *  Portions of this software were originally covered under the Apache
 *  Software License, Version 1.1 or Version 2.0.
 *
 *  See the file LICENSE elsewhere in this distribution for license details.
 */

package net.rptools.maptool.client.functions;

import java.math.BigDecimal;
import java.util.List;

import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.language.I18N;
import net.rptools.maptool.model.InitiativeList;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.InitiativeList.TokenInitiative;
import net.rptools.parser.Parser;
import net.rptools.parser.ParserException;
import net.rptools.parser.function.AbstractFunction;

/**
 * Add token to initiative
 * 
 * @author Jay
 */
public class TokenAddToInitiativeFunction extends AbstractFunction {

    /** Handle adding one, all, all PCs or all NPC tokens. */
	private TokenAddToInitiativeFunction() {
		super(0, 3, "addToInitiative");
	}
	
    /** singleton instance of this function */
    private final static TokenAddToInitiativeFunction instance = new TokenAddToInitiativeFunction();

    /** @return singleton instance */
	public static TokenAddToInitiativeFunction getInstance() { return instance; }	
	
	/**
	 * @see net.rptools.parser.function.AbstractFunction#childEvaluate(net.rptools.parser.Parser, java.lang.String, java.util.List)
	 */
	@Override
	public Object childEvaluate(Parser parser, String functionName, List<Object> args) throws ParserException {
	    InitiativeList list = MapTool.getFrame().getCurrentZoneRenderer().getZone().getInitiativeList();
	    Token token = AbstractTokenAccessorFunction.getTarget(parser, args, -1);
        if (!MapTool.getParser().isMacroTrusted()) {
        	if (!MapTool.getFrame().getInitiativePanel().hasOwnerPermission(token)) {
        		String message = I18N.getText("macro.function.initiative.gmOnly", functionName);
        		if (MapTool.getFrame().getInitiativePanel().isOwnerPermissions()) message = 
        			I18N.getText("macro.function.initiative.gmOrOwner", functionName);
        		throw new ParserException(message);
        	} // endif
        }
	    boolean allowDuplicates = false;
	    if (!args.isEmpty()) { 
	        allowDuplicates = TokenInitFunction.getBooleanValue(args.get(0));
	        args.remove(0);
	    } // endif
	    String state = null;
	    if (!args.isEmpty()) {
	        state = args.get(0).toString();
	        args.remove(0);
	    } // endif

	    // insert the token if needed
	    TokenInitiative ti = null;
	    if (allowDuplicates || list.indexOf(token).isEmpty()) {
	        ti = list.insertToken(-1, token);
	        if (state != null) ti.setState(state);
	    } else {
	        TokenInitFunction.getInstance().setTokenValue(token, state);
	    } // endif
	    return ti != null ? BigDecimal.ONE : BigDecimal.ZERO;
	}
}
