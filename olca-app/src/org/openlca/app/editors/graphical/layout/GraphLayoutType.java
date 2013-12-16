package org.openlca.app.editors.graphical.layout;

import org.openlca.app.Messages;


public enum GraphLayoutType {

	MINIMAL_TREE_LAYOUT(Messages.Systems_GraphLayoutType_MinimalTree),

	TREE_LAYOUT(Messages.Systems_GraphLayoutType_Tree);

	private String displayName;

	private GraphLayoutType(final String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

}
