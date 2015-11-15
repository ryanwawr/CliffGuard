/* ================================================================
 * JSQLParser : java based sql parser 
 * ================================================================
 *
 * Project Info:  http://jsqlparser.sourceforge.net
 * Project Lead:  Leonardo Francalanci (leoonardoo@yahoo.it);
 *
 * (C) Copyright 2004, by Leonardo Francalanci
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.relationalcloud.tsqlparser.statement.select;

import com.relationalcloud.tsqlparser.visitors.recursive.RecursiveRewriterVisitor;
import com.relationalcloud.tsqlparser.visitors.recursive.RecursiveVisitor;

/**
 * An index of a column in expressions as "GROUP BY 1" or as "ORDER BY 1,2"
 */
public class ColumnIndex implements ColumnReference {
	private int index;
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void accept(ColumnReferenceVisitor columnReferenceVisitor) {
		columnReferenceVisitor.visit(this);
	}

	public String toString() {
		return ""+index;
	}

	@Override
	public void accept(RecursiveVisitor v) {
		v.visitBegin(this);
		v.visitEnd(this);
	}
	
	@Override
	public Object accept(RecursiveRewriterVisitor v) {
		v.visitBegin(this);
		return v.visitEnd(this);
	}
}
