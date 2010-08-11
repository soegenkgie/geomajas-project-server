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

package org.geomajas.gwt.client.controller;

import org.geomajas.geometry.Coordinate;
import org.geomajas.global.Api;
import org.geomajas.gwt.client.spatial.WorldViewTransformer;
import org.geomajas.gwt.client.util.GwtEventUtil;
import org.geomajas.gwt.client.widget.MapWidget;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

/**
 * <p>
 * Abstraction of the <code>GraphicsController</code> that implements all methods as empty methods. By using this as a
 * starting point for your own controllers, you don't have to clutter your code with empty methods that you don't use
 * anyway.
 * </p>
 * <p>
 * What makes this class special is that it provides a few protected methods for easily acquiring information from the
 * mouse events. You can for example get the event's position, or target DOM element.
 * </p>
 * 
 * @author Pieter De Graef
 * @since 1.6.0
 */
@Api(allMethods = true)
public abstract class AbstractGraphicsController implements GraphicsController {

	private int offsetX;

	private int offsetY;

	protected MapWidget mapWidget;

	protected AbstractGraphicsController(MapWidget mapWidget) {
		this.mapWidget = mapWidget;
	}

	// -------------------------------------------------------------------------
	// Empty implementations of all the event handler functions:
	// -------------------------------------------------------------------------

	public void onMouseDown(MouseDownEvent event) {
	}

	public void onMouseUp(MouseUpEvent event) {
	}

	public void onMouseMove(MouseMoveEvent event) {
	}

	public void onMouseOut(MouseOutEvent event) {
	}

	public void onMouseOver(MouseOverEvent event) {
	}

	public void onMouseWheel(MouseWheelEvent event) {
	}

	public void onDoubleClick(DoubleClickEvent event) {
	}

	// -------------------------------------------------------------------------
	// GraphicsController implementation:
	// -------------------------------------------------------------------------

	public void onActivate() {
	}

	public void onDeactivate() {
	}

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	/**
	 * An offset along the X-axis expressed in pixels for event coordinates. Used when controllers are placed on
	 * specific elements that have such an offset as compared to the origin of the map. Event from such elements have
	 * X,Y coordinates relative from their own position, but need this extra offset so that we can still calculate the
	 * correct screen and world position.
	 * 
	 * @since 1.8.0
	 */
	public int getOffsetX() {
		return offsetX;
	}

	/**
	 * An offset along the X-axis expressed in pixels for event coordinates. Used when controllers are placed on
	 * specific elements that have such an offset as compared to the origin of the map. Event from such elements have
	 * X,Y coordinates relative from their own position, but need this extra offset so that we can still calculate the
	 * correct screen and world position.
	 * 
	 * @param offsetX
	 *            Set the actual offset value in pixels.
	 * 
	 * @since 1.8.0
	 */
	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	/**
	 * An offset along the Y-axis expressed in pixels for event coordinates. Used when controllers are placed on
	 * specific elements that have such an offset as compared to the origin of the map. Event from such elements have
	 * X,Y coordinates relative from their own position, but need this extra offset so that we can still calculate the
	 * correct screen and world position.
	 * 
	 * @since 1.8.0
	 */
	public int getOffsetY() {
		return offsetY;
	}

	/**
	 * An offset along the Y-axis expressed in pixels for event coordinates. Used when controllers are placed on
	 * specific elements that have such an offset as compared to the origin of the map. Event from such elements have
	 * X,Y coordinates relative from their own position, but need this extra offset so that we can still calculate the
	 * correct screen and world position.
	 * 
	 * @param offsetY
	 *            Set the actual offset value in pixels.
	 * @since 1.8.0
	 */
	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}

	// -------------------------------------------------------------------------
	// Helper functions on mouse events:
	// -------------------------------------------------------------------------

	protected WorldViewTransformer getTransformer() {
		return mapWidget.getMapModel().getMapView().getWorldViewTransformer();
	}

	// @extract-start AbstractGraphicsController, Extract from AbstractGraphicsController
	protected Coordinate getScreenPosition(MouseEvent<?> event) {
		return GwtEventUtil.getPosition(event, offsetX, offsetY);
	}

	protected Coordinate getWorldPosition(MouseEvent<?> event) {
		return getTransformer().viewToWorld(GwtEventUtil.getPosition(event, offsetX, offsetY));
	}

	protected Element getTarget(MouseEvent<?> event) {
		return GwtEventUtil.getTarget(event);
	}

	protected String getTargetId(MouseEvent<?> event) {
		return GwtEventUtil.getTargetId(event);
	}
	// @extract-end

}
