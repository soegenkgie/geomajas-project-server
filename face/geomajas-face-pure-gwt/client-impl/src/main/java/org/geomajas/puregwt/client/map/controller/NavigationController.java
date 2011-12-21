/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2011 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.geomajas.puregwt.client.map.controller;

import org.geomajas.geometry.Bbox;
import org.geomajas.geometry.Coordinate;
import org.geomajas.gwt.client.map.RenderSpace;
import org.geomajas.puregwt.client.GeomajasGinjector;
import org.geomajas.puregwt.client.map.MapPresenter;
import org.geomajas.puregwt.client.map.ViewPort;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseWheelEvent;

/**
 * Generic navigation map controller. This controller allows for panning and zooming on the map in many different ways.
 * Options are the following:
 * <ul>
 * <li><b>Panning</b>: Drag the map to pan.</li>
 * <li><b>Double click</b>: Double clicking on some location will see the map zoom in to that location.</li>
 * <li><b>Zoom to rectangle</b>: By holding shift or ctrl and dragging at the same time, a rectangle will appear on the
 * map. On mouse up, the map will than zoom to that rectangle.</li>
 * <li></li>
 * </ul>
 * For zooming using the mouse wheel there are 2 options, defined by the ScrollZoomType enum. These options are the
 * following:
 * <ul>
 * <li><b>ScrollZoomType.ZOOM_CENTER</b>: Zoom in/out so that the current center of the map remains.</li>
 * <li><b>ScrollZoomType.ZOOM_POSITION</b>: Zoom in/out so that the mouse world position remains the same. Can be great
 * for many subsequent double clicks, to make sure you keep zooming to the same location (wherever the mouse points to).
 * </li>
 * </ul>
 * 
 * @author Pieter De Graef
 */
public class NavigationController extends AbstractMapController {

	protected static final GeomajasGinjector INJECTOR = GWT.create(GeomajasGinjector.class);

	/** Zooming types on mouse wheel scroll. */
	public static enum ScrollZoomType {
		/** When scroll zooming, retain the center of the map position. */
		ZOOM_CENTER,

		/** When scroll zooming, retain the mouse position. */
		ZOOM_POSITION
	}

	private final ZoomToRectangleController zoomToRectangleController;

	protected Coordinate dragOrigin;

	protected Coordinate lastClickPosition;

	protected boolean zooming;

	protected boolean dragging;

	private ScrollZoomType scrollZoomType = ScrollZoomType.ZOOM_POSITION;

	// ------------------------------------------------------------------------
	// Constructors:
	// ------------------------------------------------------------------------

	public NavigationController() {
		super();
		zoomToRectangleController = new ZoomToRectangleController();
	}

	// ------------------------------------------------------------------------
	// MapController implementation:
	// ------------------------------------------------------------------------

	public void onActivate(MapPresenter mapPresenter) {
		super.onActivate(mapPresenter);
		zoomToRectangleController.onActivate(mapPresenter);
	}

	public void onMouseDown(MouseDownEvent event) {
		if (event.isControlKeyDown() || event.isShiftKeyDown()) {
			zooming = true;
			zoomToRectangleController.onMouseDown(event);
		} else if (event.getNativeButton() != NativeEvent.BUTTON_RIGHT) {
			dragging = true;
			dragOrigin = getScreenPosition(event);
			mapPresenter.setCursor("move");
		}
		lastClickPosition = getWorldPosition(event);
	}

	public void onMouseUp(MouseUpEvent event) {
		if (zooming) {
			zoomToRectangleController.onMouseUp(event);
			zooming = false;
		} else if (dragging) {
			stopPanning(event);
		}
	}

	public void onMouseMove(MouseMoveEvent event) {
		if (zooming) {
			zoomToRectangleController.onMouseMove(event);
		} else if (dragging) {
			updateView(event);
		}
	}

	public void onMouseOut(MouseOutEvent event) {
		if (zooming) {
			zoomToRectangleController.onMouseOut(event);
		} else {
			stopPanning(null);
		}
	}

	public void onDoubleClick(DoubleClickEvent event) {
		// Zoom in on the event location:
		Bbox bounds = mapPresenter.getViewPort().getBounds();
		double x = lastClickPosition.getX() - (bounds.getWidth() / 4);
		double y = lastClickPosition.getY() - (bounds.getHeight() / 4);
		Bbox newBounds = new Bbox(x, y, bounds.getWidth() / 2, bounds.getHeight() / 2);
		mapPresenter.getViewPort().applyBounds(newBounds);
	}

	public void onMouseWheel(MouseWheelEvent event) {
		ViewPort viewPort = mapPresenter.getViewPort();
		if (event.isNorth()) {
			if (scrollZoomType == ScrollZoomType.ZOOM_POSITION) {
				int index = viewPort.getZoomStrategy().getZoomStepIndex(viewPort.getScale());
				viewPort.applyScale(viewPort.getZoomStrategy().getZoomStepScale(index - 1), viewPort.transform(
						new Coordinate(event.getX(), event.getY()), RenderSpace.SCREEN, RenderSpace.WORLD));
			} else {
				int index = viewPort.getZoomStrategy().getZoomStepIndex(viewPort.getScale());
				viewPort.applyScale(viewPort.getZoomStrategy().getZoomStepScale(index - 1));
			}
		} else {
			if (scrollZoomType == ScrollZoomType.ZOOM_POSITION) {
				int index = viewPort.getZoomStrategy().getZoomStepIndex(viewPort.getScale());
				viewPort.applyScale(viewPort.getZoomStrategy().getZoomStepScale(index + 1), viewPort.transform(
						new Coordinate(event.getX(), event.getY()), RenderSpace.SCREEN, RenderSpace.WORLD));
			} else {
				int index = viewPort.getZoomStrategy().getZoomStepIndex(viewPort.getScale());
				viewPort.applyScale(viewPort.getZoomStrategy().getZoomStepScale(index + 1));
			}
		}
	}

	// ------------------------------------------------------------------------
	// Getters and setters:
	// ------------------------------------------------------------------------

	public ScrollZoomType getScrollZoomType() {
		return scrollZoomType;
	}

	public void setScrollZoomType(ScrollZoomType scrollZoomType) {
		this.scrollZoomType = scrollZoomType;
	}

	// ------------------------------------------------------------------------
	// Private methods:
	// ------------------------------------------------------------------------

	protected void stopPanning(MouseUpEvent event) {
		dragging = false;
		mapPresenter.setCursor("default");
		if (null != event) {
			updateView(event);
		}
	}

	protected void updateView(MouseEvent<?> event) {
		Coordinate end = getScreenPosition(event);
		Coordinate beginWorld = mapPresenter.getViewPort().transform(dragOrigin, RenderSpace.SCREEN, RenderSpace.WORLD);
		Coordinate endWorld = mapPresenter.getViewPort().transform(end, RenderSpace.SCREEN, RenderSpace.WORLD);

		double x = mapPresenter.getViewPort().getPosition().getX() + beginWorld.getX() - endWorld.getX();
		double y = mapPresenter.getViewPort().getPosition().getY() + beginWorld.getY() - endWorld.getY();
		mapPresenter.getViewPort().applyPosition(new Coordinate(x, y));
		dragOrigin = end;
	}
}