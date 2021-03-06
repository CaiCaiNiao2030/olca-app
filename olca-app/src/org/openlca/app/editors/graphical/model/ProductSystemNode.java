package org.openlca.app.editors.graphical.model;

import java.util.List;

import org.openlca.app.editors.graphical.ProductSystemGraphEditor;
import org.openlca.app.editors.graphical.search.MutableProcessLinkSearchMap;
import org.openlca.core.model.ProductSystem;

public class ProductSystemNode extends Node {

	public final ProductSystemGraphEditor editor;
	public final MutableProcessLinkSearchMap linkSearch;

	public ProductSystemNode(ProductSystemGraphEditor editor) {
		this.linkSearch = new MutableProcessLinkSearchMap(editor.getSystemEditor().getModel().getProcessLinks());
		this.editor = editor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ProcessNode> getChildren() {
		return (List<ProcessNode>) super.getChildren();
	}

	public ProductSystem getProductSystem() {
		return editor.getSystemEditor().getModel();
	}

	public ProcessNode getProcessNode(long id) {
		for (ProcessNode node : getChildren())
			if (node.process.getId() == id)
				return node;
		return null;
	}

	@Override
	public String getName() {
		return getProductSystem().getName();
	}

	public void highlightMatchingExchanges(ExchangeNode toMatch) {
		long flowId = toMatch.exchange.getFlow().getId();
		for (ProcessNode node : getChildren()) {
			if (!node.isVisible() || node.isMinimized())
				continue;
			for (ExchangeNode inputNode : node.getInputs(flowId))
				highlightExchange(node, inputNode, toMatch);
			ExchangeNode outputNode = node.getOutput(flowId);
			highlightExchange(node, outputNode, toMatch);
		}
	}

	private void highlightExchange(ProcessNode node, ExchangeNode exchangeNode, ExchangeNode toMatch) {
		if (exchangeNode == null)
			return;
		if (toMatch.exchange.isInput() == exchangeNode.exchange.isInput())
			return;
		if (!toMatch.exchange.isInput() && node.hasIncoming(exchangeNode.exchange.getId()))
			return;
		exchangeNode.setHighlighted(true);
	}

	public void removeHighlighting() {
		for (ProcessNode node : getChildren()) {
			if (!node.isVisible() || node.isMinimized())
				continue;
			for (ExchangeNode exchangeNode : node.getChildren().get(0).getChildren())
				exchangeNode.setHighlighted(false);
		}
	}

	public void refreshChildren() {
		((ProductSystemPart) editPart).refreshChildren();
	}

}
