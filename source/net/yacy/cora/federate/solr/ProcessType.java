/**
 *  ProcessType
 *  Copyright 2013 by Michael Peter Christen
 *  First released 02.01.2013
 */
 
/*
This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful, but without any warranty; without even the implied warranty of merchantability or fitness for a particular purpose. See the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License along with this program in the file lgpl21.txt. If not, see <http://www.gnu.org/licenses/>.
*/



package net.yacy.cora.federate.solr;

/**
 * this enum class is used to define (post-) process steps that are attached at the solr dataset in the field process_s
 */
public enum ProcessType {

    CITATION, UNIQUE;
    
}
