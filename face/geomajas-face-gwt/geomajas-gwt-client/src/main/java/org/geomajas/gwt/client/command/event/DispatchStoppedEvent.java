/*
 * This file is part of Geomajas, a component framework for building
 * rich Internet applications (RIA) with sophisticated capabilities for the
 * display, analysis and management of geographic information.
 * It is a building block that allows developers to add maps
 * and other geographic data capabilities to their web applications.
 *
 * Copyright 2008-2010 Geosparc, http://www.geosparc.com, Belgium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geomajas.gwt.client.command.event;

import org.geomajas.global.Api;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is thrown when the command dispatcher stops dispatching (no callbacks left).
 *
 * @author Jan De Moerloose
 */
@Api(allMethods = true)
public class DispatchStoppedEvent extends GwtEvent<DispatchStoppedHandler> {

	/** Handler type. */
	private static Type<DispatchStoppedHandler> TYPE;

	// -------------------------------------------------------------------------
	// Event implementation:
	// -------------------------------------------------------------------------

	/**
	 * Get the type associated with this event.
	 *
	 * @return returns the handler type
	 */
	public static Type<DispatchStoppedHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<DispatchStoppedHandler>();
		}
		return TYPE;
	}

	protected void dispatch(DispatchStoppedHandler handler) {
		handler.onDispatchStopped(this);
	}

	public final Type<DispatchStoppedHandler> getAssociatedType() {
		return TYPE;
	}

}