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

package org.geomajas.internal.rendering.pipeline;

import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.rendering.pipeline.PipelineContext;
import org.geomajas.rendering.pipeline.PipelineInfo;
import org.geomajas.rendering.pipeline.PipelineService;
import org.geomajas.rendering.pipeline.PipelineStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Service which is allows "executing" a pipeline.
 *
 * @param <REQUEST> type of request object for the pipeline
 * @param <RESPONSE> type of response object for the pipeline
 *
 * @author Joachim Van der Auwera
 */
@Component
public class PipelineServiceImpl<REQUEST, RESPONSE> implements PipelineService<REQUEST, RESPONSE> {

	@Autowired
	private ApplicationContext applicationContext;

	/** @inheritDoc */
	public void execute(PipelineInfo<REQUEST, RESPONSE> pipeline, REQUEST request, RESPONSE response)
			throws GeomajasException {
		execute(pipeline, request, response, createContext());
	}

	/** @inheritDoc */
	public void execute(PipelineInfo<REQUEST, RESPONSE> pipeline, REQUEST request, RESPONSE response,
			PipelineContext context) throws GeomajasException {
		for (PipelineStep<REQUEST, RESPONSE> step : pipeline.getPipeline()) {
			if (context.isFinished()) {
				break;
			}
			step.execute(request, context, response);
		}
	}

	/** @inheritDoc */
	public PipelineInfo<REQUEST, RESPONSE> getPipeline(String key, String layerId) throws GeomajasException {
		PipelineInfo<REQUEST, RESPONSE> generalPipeline = null;
		String specificId = key + "." + layerId;
		Collection<PipelineInfo> pipelines = applicationContext.getBeansOfType(PipelineInfo.class).values();
		for (PipelineInfo pipeline : pipelines) {
			String id = pipeline.getId();
			if (id.equals(specificId)) {
				return pipeline;
			}
			if (id.equals(key)) {
				generalPipeline = pipeline;
			}
		}
		if (null == generalPipeline) {
			throw new GeomajasException(ExceptionCode.PIPELINE_UNKNOWN, key, layerId);
		}
		return generalPipeline;
	}

	/** @inheritDoc */
	public PipelineContext createContext() {
		return new PipelineContextImpl();
	}
}
