/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.geomajas.sld.editor.client.view;

import java.util.LinkedHashMap;
import java.util.logging.Logger;

import org.geomajas.sld.client.presenter.CreateSldDialogPresenterWidget.MyView;
import org.geomajas.sld.client.presenter.event.CreateSldPopupCreateEvent;
import org.geomajas.sld.client.presenter.event.CreateSldPopupCreateEvent.CreateSldPopupCreateHandler;
import org.geomajas.sld.editor.client.GeometryType;
import org.geomajas.sld.editor.client.i18n.SldEditorMessages;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.PopupViewCloseHandler;
import com.gwtplatform.mvp.client.PopupViewImpl;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;


/**
 * The view implementation for
 * {@link com.gwtplatform.CreateSldDialogPresenterWidget.tab.client.presenter.GlobalDialogPresenterWidget}.
 * 
 * @author Philippe Beaudoin
 */
public class CreateSldDialogView extends PopupViewImpl implements MyView {
	
	private Logger log = Logger.getLogger(CreateSldDialogView.class.getName());

	private SldEditorMessages editorMessages;

	private Window winModal;
	
	private  SelectItem typeOfGeomItem;
	
	private TextItem nameOfSldItem;
	
	private EventBus eventBus;

	@Inject
	public CreateSldDialogView(final EventBus eventBus, final SldEditorMessages editorMessages) {
		super(eventBus);
		this.eventBus = eventBus;
		this.editorMessages = editorMessages;
		log.info("User clicked on 'Add new SLD' button");
		winModal = new Window();
		winModal.setWidth(360);
		winModal.setHeight(150);
		winModal.setTitle("Voeg SLD toe");
		winModal.setLayoutTopMargin(10);
		winModal.setShowMinimizeButton(false);
		winModal.setIsModal(true);
		winModal.setShowModalMask(true); // darken all other elements on the screen when a modal dialog is showing.
		winModal.setShowCloseButton(false);
		winModal.centerInPage();

		final DynamicForm addSldForm = new DynamicForm();

		nameOfSldItem = new TextItem("NameOfSld", editorMessages.nameSld());

		nameOfSldItem.setWidth(200);
		nameOfSldItem.addChangedHandler(new ChangedHandler() {

			public void onChanged(ChangedEvent event) {
				if (!nameOfSldItem.validate()) {
					return;
				}
			}
		});

		nameOfSldItem.setRequired(true);
		nameOfSldItem.setRequiredMessage(editorMessages.nameSldCanNotBeEmpty());

		typeOfGeomItem = new SelectItem();
		typeOfGeomItem.setTitle(editorMessages.geometryTitle());

		final LinkedHashMap<String, String> typeOfGeomList = new LinkedHashMap<String, String>();
		typeOfGeomList.put(GeometryType.POINT.value(), editorMessages.pointTitle());
		typeOfGeomList.put(GeometryType.LINE.value(), editorMessages.lineTitle());
		typeOfGeomList.put(GeometryType.POLYGON.value(), editorMessages.polygonTitle());
		typeOfGeomItem.setValueMap(typeOfGeomList);
		typeOfGeomItem.setDefaultValue(GeometryType.POINT.value());
		typeOfGeomItem.setRequired(true);

		addSldForm.setItems(nameOfSldItem, typeOfGeomItem);
		winModal.addItem(addSldForm);

		HLayout toolStrip = new HLayout(10/* membersMargin */);
		toolStrip.setPadding(10);
		toolStrip.setAlign(Alignment.RIGHT);
		toolStrip.setHeight(30); /* fixed size for buttons strip for dialogue to add an SLD */

		final IButton createButton = new IButton();
		// @todo FIX createButton.setIcon(WidgetLayout.iconCreate);
		// createButton.setShowDisabledIcon(false);
		createButton.setPrompt(editorMessages.createButtonTooltip());

		// TODO: validate form first
		createButton.setTitle(editorMessages.createButtonTitle());
		createButton.setShowDisabledIcon(false);
		createButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				if (addSldForm.validate()) {
					CreateSldPopupCreateEvent.fire(CreateSldDialogView.this);
				}
			}

		});


		final IButton cancelButton = new IButton();
		// @todo FIX cancelButton.setIcon(WidgetLayout.iconCancel);
		cancelButton.setShowDisabledIcon(false);
		cancelButton.setTitle(editorMessages.cancelButtonTitle());
		cancelButton.setTooltip(editorMessages.createSldCancelButtonTitle());

		toolStrip.addMember(createButton);

		cancelButton.addClickHandler(new ClickHandler() {

			public void onClick(com.smartgwt.client.widgets.events.ClickEvent event) {
				winModal.hide();
			}

		});

		toolStrip.addMember(cancelButton);
		winModal.addItem(toolStrip);
	}


	public HandlerRegistration addCreateSldPopupCreateHandler(CreateSldPopupCreateHandler handler) {
		return eventBus.addHandler(CreateSldPopupCreateEvent.getType(), handler);
	}



	public void fireEvent(GwtEvent<?> event) {
		eventBus.fireEvent(event);
	}



	public GeometryType getGeometryType() {
		return GeometryType.fromValue(typeOfGeomItem.getValueAsString());
	}



	public String getName() {
		return nameOfSldItem.getValueAsString();
	}



	@Override
	public void setCloseHandler(final PopupViewCloseHandler popupViewCloseHandler) {
		winModal.addCloseClickHandler(new CloseClickHandler() {
			
			public void onCloseClick(CloseClientEvent event) {
				popupViewCloseHandler.onClose();
			}
		});
	}


	@Override
	public void center() {
		winModal.centerInPage();
	}



	@Override
	public void hide() {
		winModal.hide();
	}



	@Override
	public void setPosition(int left, int top) {
		winModal.setLeft(left);
		winModal.setTop(top);
	}



	@Override
	public void show() {
		winModal.show();
	}



	public Widget asWidget() {
		return winModal;
	}

}