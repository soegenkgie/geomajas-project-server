/*
 * This is part of Geomajas, a GIS framework, http://www.geomajas.org/.
 *
 * Copyright 2008-2012 Geosparc nv, http://www.geosparc.com/, Belgium.
 *
 * The program is available in open source according to the GNU Affero
 * General Public License. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */
package org.geomajas.plugin.deskmanager.command.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geomajas.command.Command;
import org.geomajas.plugin.deskmanager.command.manager.dto.GetGeodeskResponse;
import org.geomajas.plugin.deskmanager.command.manager.dto.SaveBlueprintRequest;
import org.geomajas.plugin.deskmanager.command.manager.dto.SaveGeodeskRequest;
import org.geomajas.plugin.deskmanager.domain.Geodesk;
import org.geomajas.plugin.deskmanager.domain.MailAddress;
import org.geomajas.plugin.deskmanager.domain.security.Territory;
import org.geomajas.plugin.deskmanager.domain.security.dto.Role;
import org.geomajas.plugin.deskmanager.security.DeskmanagerSecurityContext;
import org.geomajas.plugin.deskmanager.service.common.DtoConverterService;
import org.geomajas.plugin.deskmanager.service.common.GeodeskService;
import org.geomajas.plugin.deskmanager.service.common.TerritoryService;
import org.geomajas.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Command that saves a geodesk from a given dto.
 * 
 * @author Oliver May
 * @author Kristof Heirwegh
 */
@Component(SaveGeodeskRequest.COMMAND)
@Transactional(rollbackFor = { Exception.class })
public class SaveGeodeskCommand implements Command<SaveGeodeskRequest, GetGeodeskResponse> {

	private final Logger log = LoggerFactory.getLogger(SaveGeodeskCommand.class);

	@Autowired
	private DtoConverterService dtoService;

	@Autowired
	private GeodeskService loketService;

	@Autowired
	private TerritoryService groupService;

	@Autowired
	private SecurityContext securityContext;

	/** {@inheritDoc} */
	public void execute(SaveGeodeskRequest request, GetGeodeskResponse response) throws Exception {
		try {
			if (request.getLoket() == null) {
				response.getErrorMessages().add("Geen loket opgegeven ??");
			} else if (request.getSaveBitmask() < 1) {
				response.getErrorMessages().add("Niets te bewaren ??");
			} else {
				Geodesk target = loketService.getLoketById(request.getLoket().getId());
				if (target == null) {
					response.getErrorMessages().add(
							"Geen loket gevonden met id: " + request.getLoket().getId() + " (Nieuw loket?)");
				} else {
					Geodesk source = dtoService.fromDto(request.getLoket());

					if ((SaveGeodeskRequest.SAVE_SETTINGS & request.getSaveBitmask()) > 0) {
						copySettings(source, target);
					}
					if ((SaveGeodeskRequest.SAVE_TERRITORIES & request.getSaveBitmask()) > 0) {
						copyGroups(source, target);
					}
					if ((SaveGeodeskRequest.SAVE_NOTIFICATIONS & request.getSaveBitmask()) > 0) {
						copyNotifications(source, target);
					}
					if ((SaveGeodeskRequest.SAVE_CLIENTWIDGETINFO & request.getSaveBitmask()) > 0) {
						copyWidgetInfo(source, target);
					}
					if ((SaveBlueprintRequest.SAVE_LAYERS & request.getSaveBitmask()) > 0) {
						copyLayers(source, target);
					}

					loketService.saveOrUpdateGeodesk(target);
					response.setGeodesk(dtoService.toDto(target, false));
				}
			}
		} catch (Exception e) {
			response.getErrorMessages().add("Fout bij opslaan loket: " + e.getMessage());
			log.error("fout bij opslaan loket.", e);
		}
	}

	/** {@inheritDoc} */
	public GetGeodeskResponse getEmptyCommandResponse() {
		return new GetGeodeskResponse();
	}

	// ----------------------------------------------------------

	/**
	 * Helper method to copy all geodesk settings.
	 */
	private void copySettings(Geodesk source, Geodesk target) throws Exception {
		target.setActive(source.isActive());
		target.setLimitToCreatorTerritory(source.isLimitToCreatorTerritory());
		target.setLimitToUserTerritory(source.isLimitToUserTerritory());
		target.setName(source.getName());
		target.setPublic(source.isPublic());
		if (Role.ADMINISTRATOR.equals(((DeskmanagerSecurityContext) securityContext).getRole())) {
			target.setGeodeskId(source.getGeodeskId());
		}
	}

	/**
	 * Helper method to copy widget info.
	 */
	private void copyWidgetInfo(Geodesk source, Geodesk target) throws Exception {
		target.getApplicationClientWidgetInfos().clear();
		target.getApplicationClientWidgetInfos().putAll(source.getApplicationClientWidgetInfos());
		
		target.getMainMapClientWidgetInfos().clear();
		target.getMainMapClientWidgetInfos().putAll(source.getMainMapClientWidgetInfos());

		target.getOverviewMapClientWidgetInfos().clear();
		target.getOverviewMapClientWidgetInfos().putAll(source.getOverviewMapClientWidgetInfos());
		
	}

	/**
	 * Helper method to copy groups.
	 */
	private void copyGroups(Geodesk source, Geodesk target) throws Exception {
		List<Territory> toDelete = new ArrayList<Territory>();
		List<Territory> toAdd = new ArrayList<Territory>();
		for (Territory g : target.getTerritories()) {
			if (!source.getTerritories().contains(g)) {
				toDelete.add(g);
			}
		}
		if (toDelete.size() > 0) {
			target.getTerritories().removeAll(toDelete);
		}
		for (Territory g : source.getTerritories()) {
			if (!target.getTerritories().contains(g)) {
				toAdd.add(g);
			}
		}
		for (Territory discon : toAdd) {
			Territory conn = groupService.getById(discon.getId());
			if (conn != null) {
				target.getTerritories().add(conn);
			} else {
				log.warn("Territory not found !? (id: " + discon.getId());
			}
		}
	}

	/**
	 * Helper method to copy all notifications.
	 * @deprecated don't use notifications as such, use clientwidgetinfo.
	 */
	@Deprecated
	private void copyNotifications(Geodesk source, Geodesk target) throws Exception {
		List<MailAddress> toDelete = new ArrayList<MailAddress>();
		List<MailAddress> toAdd = new ArrayList<MailAddress>();
		Map<Long, MailAddress> sourcemap = toMap(source.getMailAddresses());

		// remove deleted records
		for (MailAddress m : target.getMailAddresses()) {
			if (!source.getMailAddresses().contains(m)) {
				toDelete.add(m);
			}
		}
		if (toDelete.size() > 0) {
			target.getMailAddresses().removeAll(toDelete);
		}

		// update existing
		for (MailAddress upd : target.getMailAddresses()) {
			MailAddress src = sourcemap.get(upd.getId());
			if (src != null) {
				upd.setEmail(src.getEmail());
				upd.setName(src.getName());
			} else {
				log.warn("MailAddress not found !? (id: " + upd.getId() + ")");
			}
		}

		// add new
		for (MailAddress m : source.getMailAddresses()) {
			if (!target.getMailAddresses().contains(m)) {
				toAdd.add(m);
			}
		}
		if (toAdd.size() > 0) {
			target.getMailAddresses().addAll(toAdd);
		}
	}

	/**
	 * Helper method to copy all mail addresses.
	 * @deprecated don't use notifications as such, use clientwidgetinfo.
	 */
	@Deprecated
	private Map<Long, MailAddress> toMap(List<MailAddress> list) {
		Map<Long, MailAddress> res = new HashMap<Long, MailAddress>();
		for (MailAddress m : list) {
			if (m.getId() != null) {
				res.put(m.getId(), m);
			}
		}

		return res;
	}

	/**
	 * Helper method to copy all layers.
	 */
	private void copyLayers(Geodesk source, Geodesk target) throws Exception {
		target.getMainMapLayers().clear();
		target.getMainMapLayers().addAll(source.getMainMapLayers());

		target.getOverviewMapLayers().clear();
		target.getOverviewMapLayers().addAll(source.getOverviewMapLayers());
	}
	
}