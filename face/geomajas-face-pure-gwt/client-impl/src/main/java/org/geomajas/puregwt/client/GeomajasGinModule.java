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

package org.geomajas.puregwt.client;

import org.geomajas.puregwt.client.map.LayersModel;
import org.geomajas.puregwt.client.map.LayersModelImpl;
import org.geomajas.puregwt.client.map.MapPresenter;
import org.geomajas.puregwt.client.map.MapPresenterImpl;
import org.geomajas.puregwt.client.map.MapPresenterImpl.MapWidget;
import org.geomajas.puregwt.client.map.ViewPort;
import org.geomajas.puregwt.client.map.ViewPortImpl;
import org.geomajas.puregwt.client.map.event.EventBus;
import org.geomajas.puregwt.client.map.event.EventBusImpl;
import org.geomajas.puregwt.client.map.feature.FeatureSearch;
import org.geomajas.puregwt.client.map.gfx.GfxUtil;
import org.geomajas.puregwt.client.spatial.BboxService;
import org.geomajas.puregwt.client.spatial.BboxServiceImpl;
import org.geomajas.puregwt.client.spatial.GeometryService;
import org.geomajas.puregwt.client.spatial.GeometryServiceImpl;
import org.geomajas.puregwt.client.widget.MapWidgetImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

/**
 * Gin binding module. All bindings defined in here are used by the GeomajasGinjector.
 * 
 * @author Jan De Moerloose
 * @author Pieter De Graef
 */
public class GeomajasGinModule extends AbstractGinModule {

	protected void configure() {
		// Spatial services:
		bind(BboxService.class).to(BboxServiceImpl.class).in(Singleton.class);
		bind(GeometryService.class).to(GeometryServiceImpl.class).in(Singleton.class);

		// Map related interfaces:
		bind(MapPresenter.class).to(MapPresenterImpl.class);
		bind(LayersModel.class).to(LayersModelImpl.class);
		bind(ViewPort.class).to(ViewPortImpl.class);
		bind(EventBus.class).to(EventBusImpl.class);
		bind(MapWidget.class).to(MapWidgetImpl.class);

		// Other:
		bind(FeatureSearch.class);
		bind(GfxUtil.class);
	}
}