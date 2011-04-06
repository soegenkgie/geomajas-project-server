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

package org.geomajas.internal.layer.vector;

import org.geomajas.global.ExceptionCode;
import org.geomajas.global.GeomajasException;
import org.geomajas.security.GeomajasSecurityException;
import org.geomajas.layer.VectorLayer;
import org.geomajas.layer.feature.Attribute;
import org.geomajas.layer.feature.FeatureModel;
import org.geomajas.layer.feature.InternalFeature;
import org.geomajas.service.pipeline.PipelineCode;
import org.geomajas.service.pipeline.PipelineContext;
import org.opengis.filter.Filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Save the data from the feature data object (read from the context) into the layer.
 *
 * @author Joachim Van der Auwera
 */
public class SaveOrUpdateSaveStep extends AbstractSaveOrUpdateStep {

	public void execute(PipelineContext context, Object response) throws GeomajasException {
		InternalFeature newFeature = context.getOptional(PipelineCode.FEATURE_KEY, InternalFeature.class);
		Object feature = context.get(PipelineCode.FEATURE_DATA_OBJECT_KEY);
		String layerId = context.get(PipelineCode.LAYER_ID_KEY, String.class);
		VectorLayer layer = context.get(PipelineCode.LAYER_KEY, VectorLayer.class);
		FeatureModel featureModel = layer.getFeatureModel();
		Boolean isCreateObject = context.getOptional(PipelineCode.IS_CREATE_KEY, Boolean.class);
		boolean isCreate  = false;
		if (null != isCreateObject && isCreateObject) {
			isCreate = true;
		}

		// Assure only writable attributes are set
		Map<String, Attribute> requestAttributes = newFeature.getAttributes();
		Map<String, Attribute> filteredAttributes = new HashMap<String, Attribute>();
		if (null != requestAttributes) {
			for (Map.Entry<String, Attribute> entry : requestAttributes.entrySet()) {
				String key = entry.getKey();
				if (securityContext.isAttributeWritable(layerId, newFeature, key)) {
					filteredAttributes.put(key, entry.getValue());
				}
			}
		}
		featureModel.setAttributes(feature, filteredAttributes);

		if (newFeature.getGeometry() != null) {
			featureModel.setGeometry(feature, newFeature.getGeometry());
		}

		Filter securityFilter;
		if (isCreate) {
			securityFilter = getSecurityFilter(layer, securityContext.getCreateAuthorizedArea(layerId));
		} else {
			securityFilter = getSecurityFilter(layer, securityContext.getUpdateAuthorizedArea(layerId));
		}
		if (securityFilter.evaluate(feature)) {
			context.put(PipelineCode.FEATURE_DATA_OBJECT_KEY, layer.saveOrUpdate(feature));
			if (isCreate) {
				newFeature.setId(featureModel.getId(feature));
			}
		} else {
			if (isCreate) {
				throw new GeomajasSecurityException(ExceptionCode.FEATURE_CREATE_PROHIBITED,
						securityContext.getUserId());
			} else {
				throw new GeomajasSecurityException(ExceptionCode.FEATURE_UPDATE_PROHIBITED, newFeature.getId(),
						securityContext.getUserId());				
			}
		}
	}
}
