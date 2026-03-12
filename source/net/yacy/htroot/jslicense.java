// jslicense.java
// -----------------------
// (C) 2009 by Michael Peter Christen; mc@yacy.net, Frankfurt a. M., Germany
// first published 07.04.2005 on http://yacy.net
//
// This File is contributed by luc
//
// $LastChangedDate$
// $LastChangedRevision$
// $LastChangedBy$
//
/*
This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but without any warranty; without even the implied warranty of merchantability or fitness for a particular purpose. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

package net.yacy.htroot;

import net.yacy.cora.protocol.RequestHeader;
import net.yacy.server.serverObjects;
import net.yacy.server.serverSwitch;

/**
 * Produces YaCy JavaScript license information page (sse jslicense.html).
 * @author luc
 *
 */
public class jslicense {

    /**
     * @param header request headers
     * @param post post parameters
	 * @param env server environment
	 */
    public static serverObjects respond(final RequestHeader header, final serverObjects post, final serverSwitch env) {
        return new serverObjects();
    }
}
