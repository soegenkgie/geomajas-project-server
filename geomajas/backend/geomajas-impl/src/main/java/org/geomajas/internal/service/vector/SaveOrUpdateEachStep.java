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

package org.geomajas.internal.service.vector;

import org.geomajas.global.GeomajasException;
import org.geomajas.layer.feature.InternalFeature;
import org.geomajas.rendering.pipeline.PipelineContext;
import org.geomajas.rendering.pipeline.PipelineInfo;
import org.geomajas.rendering.pipeline.PipelineService;
import org.geomajas.rendering.pipeline.PipelineStep;
import org.geomajas.security.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Execute the vectorLayer.saveOrUpdateOne" pipeline for each of the features to saveOrUpdate.
 *
 * @author Joachim Van der Auwera
 */
public class SaveOrUpdateEachStep implements PipelineStep<SaveOrUpdateContainer, SaveOrUpdateContainer> {

	public static final String FEATURE_KEY = "feature";
	public static final String CRS_TRANSFORM_KEY = "crsTransform";
	public static final String FEATURE_DATA_OBJECT_KEY = "featureDataObject";
	public static final String LAYER_KEY = "layer";

	private String id;
	private String pipelineName;

	@Autowired
	private SecurityContext securityContext;

	@Autowired
	private PipelineService pipelineService;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setPipelineName(String pipelineName) {
		this.pipelineName = pipelineName;
	}

	public void execute(SaveOrUpdateContainer request, PipelineContext context,
			SaveOrUpdateContainer response) throws GeomajasException {
		SaveOrUpdateOneContainer oneContainer = new SaveOrUpdateOneContainer(request);
		PipelineInfo pipelineInfo = pipelineService.getPipeline(pipelineName, request.getLayerId());
		context.put(CRS_TRANSFORM_KEY, request.getMapToLayer());
		context.put(LAYER_KEY, request.getLayer());

		int count = request.getOldFeatures().size();
		for (int i = 0; i < count; i++) {
			oneContainer.setIndex(i);
			oneContainer.setOldFeature(request.getOldFeatures().get(i));
			InternalFeature newFeature = request.getNewFeatures().get(i);
			oneContainer.setNewFeature(newFeature);
			context.put(FEATURE_KEY, newFeature);

			pipelineService.execute(pipelineInfo, oneContainer, oneContainer, context);
		}
	}
	
}
