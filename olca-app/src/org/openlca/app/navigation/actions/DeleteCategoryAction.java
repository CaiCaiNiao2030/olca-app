/*******************************************************************************
 * Copyright (c) 2007 - 2010 GreenDeltaTC. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Mozilla
 * Public License v1.1 which accompanies this distribution, and is available at
 * http://www.openlca.org/uploads/media/MPL-1.1.html
 * 
 * Contributors: GreenDeltaTC - initial API and implementation
 * www.greendeltatc.com tel.: +49 30 4849 6030 mail: gdtc@greendeltatc.com
 ******************************************************************************/
package org.openlca.app.navigation.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.openlca.app.Messages;
import org.openlca.app.db.Database;
import org.openlca.app.navigation.CategoryElement;
import org.openlca.app.navigation.INavigationElement;
import org.openlca.app.navigation.Navigator;
import org.openlca.app.resources.ImageType;
import org.openlca.app.util.Error;
import org.openlca.app.util.Question;
import org.openlca.core.database.BaseDao;
import org.openlca.core.model.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delete a category via the navigation tree.
 */
public class DeleteCategoryAction extends Action implements INavigationAction {

	private CategoryElement categoryElement;

	public DeleteCategoryAction() {
		setText(Messages.NavigationView_RemoveCategoryText);
		setImageDescriptor(ImageType.DELETE_ICON.getDescriptor());
	}

	@Override
	public boolean accept(INavigationElement<?> element) {
		if (!(element instanceof CategoryElement))
			return false;
		categoryElement = (CategoryElement) element;
		return true;
	}

	public boolean accept(List<INavigationElement<?>> elements) {
		return false;
	}

	@Override
	public void run() {
		if (categoryElement == null)
			return;
		if (categoryElement.getChildren().size() != 0) {
			Error.showBox("The category is not empty.");
			return;
		}
		boolean b = Question.ask("Delete",
				"Do you really want to delete the selected category?");
		if (!b)
			return;
		delete();
	}

	private void delete() {
		Category category = categoryElement.getContent();
		try {
			BaseDao<Category> dao = Database.get().createDao(Category.class);
			Category parent = category.getParentCategory();
			if (parent != null) {
				parent.getChildCategories().remove(category);
				category.setParentCategory(null);
				dao.update(parent);
			}
			dao.delete(category);
			// we have to refresh the category starting from it's root
			// otherwise the object model is out of sync.
			INavigationElement<?> element = Navigator.findElement(category
					.getModelType());
			Navigator.refresh(element);
		} catch (Exception e) {
			Logger log = LoggerFactory.getLogger(getClass());
			log.error("failed to delete category " + category, e);
		}
	}

}
