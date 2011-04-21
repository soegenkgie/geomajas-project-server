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

package org.geomajas.puregwt.client.map;

import javax.annotation.PostConstruct;

import junit.framework.Assert;

import org.geomajas.configuration.client.ClientMapInfo;
import org.geomajas.geometry.Coordinate;
import org.geomajas.puregwt.client.map.event.EventBus;
import org.geomajas.puregwt.client.map.event.EventBusImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Unit test that checks if the ViewPortImpl positions correctly.
 * 
 * @author Pieter De Graef
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/org/geomajas/spring/geomajasContext.xml", "viewPortContext.xml",
		"mapViewPortBeans.xml", "mapBeansNoResolutions.xml", "layerViewPortBeans.xml" })
@DirtiesContext
public class ViewPortPositionTest {

	@Autowired
	@Qualifier(value = "mapViewPortBeans")
	private ClientMapInfo mapInfo;

	private EventBus eventBus;

	private ViewPort viewPort;

	@PostConstruct
	public void initialize() {
		eventBus = new EventBusImpl();
		viewPort = new ViewPortImpl(eventBus);
		viewPort.setMapSize(200, 200);
		viewPort.initialize(mapInfo);
	}

	@Before
	public void prepareTest() {
		viewPort.applyBounds(viewPort.getMaximumBounds());
	}

	@Test
	public void testOnMinimumScale() {
		Assert.assertEquals(0.0, viewPort.getPosition().getX());
		Assert.assertEquals(0.0, viewPort.getPosition().getY());

		viewPort.applyPosition(new Coordinate(10,10));
		Assert.assertEquals(0.0, viewPort.getPosition().getX());
		Assert.assertEquals(0.0, viewPort.getPosition().getY());
	}

	@Test
	public void testOnAverageScale() {
		Assert.assertEquals(0.0, viewPort.getPosition().getX());
		Assert.assertEquals(0.0, viewPort.getPosition().getY());

		viewPort.applyScale(2.0); // Width now becomes 100, so max center = (50,50).
		Assert.assertEquals(0.0, viewPort.getPosition().getX());
		Assert.assertEquals(0.0, viewPort.getPosition().getY());

		viewPort.applyPosition(new Coordinate(10,10));
		Assert.assertEquals(10.0, viewPort.getPosition().getX());
		Assert.assertEquals(10.0, viewPort.getPosition().getY());

		viewPort.applyPosition(new Coordinate(1000,1000));
		Assert.assertEquals(50.0, viewPort.getPosition().getX());
		Assert.assertEquals(50.0, viewPort.getPosition().getY());
	}
}