/*
 * Copyright (c) 2009, Universität Hamburg
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package jadex.tools.gpmn.diagram.edit.commands;

import jadex.tools.gpmn.Goal;
import jadex.tools.gpmn.GpmnDiagram;
import jadex.tools.gpmn.GpmnFactory;
import jadex.tools.gpmn.diagram.providers.GpmnElementTypes;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gmf.runtime.common.core.command.CommandResult;
import org.eclipse.gmf.runtime.common.core.command.ICommand;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.emf.type.core.commands.EditElementCommand;
import org.eclipse.gmf.runtime.emf.type.core.requests.ConfigureRequest;
import org.eclipse.gmf.runtime.emf.type.core.requests.CreateElementRequest;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @generated
 */
public class GoalCreateCommand extends EditElementCommand
{
	
	/**
	 * @generated
	 */
	public GoalCreateCommand(CreateElementRequest req)
	{
		super(req.getLabel(), null, req);
	}
	
	/**
	 * FIXME: replace with setElementToEdit()
	 * @generated
	 */
	protected EObject getElementToEdit()
	{
		EObject container = ((CreateElementRequest) getRequest())
				.getContainer();
		if (container instanceof View)
		{
			container = ((View) container).getElement();
		}
		return container;
	}
	
	/**
	 * @generated
	 */
	public boolean canExecute()
	{
		return true;
		
	}
	
	/**
	 * @generated
	 */
	protected CommandResult doExecuteWithResult(IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		Goal newElement = GpmnFactory.eINSTANCE.createGoal();
		
		GpmnDiagram owner = (GpmnDiagram) getElementToEdit();
		owner.getGoals().add(newElement);
		
		GpmnElementTypes.init_Goal_2004(newElement);
		
		doConfigure(newElement, monitor, info);
		
		((CreateElementRequest) getRequest()).setNewElement(newElement);
		return CommandResult.newOKCommandResult(newElement);
	}
	
	/**
	 * @generated
	 */
	protected void doConfigure(Goal newElement, IProgressMonitor monitor,
			IAdaptable info) throws ExecutionException
	{
		IElementType elementType = ((CreateElementRequest) getRequest())
				.getElementType();
		ConfigureRequest configureRequest = new ConfigureRequest(
				getEditingDomain(), newElement, elementType);
		configureRequest.setClientContext(((CreateElementRequest) getRequest())
				.getClientContext());
		configureRequest.addParameters(getRequest().getParameters());
		ICommand configureCommand = elementType
				.getEditCommand(configureRequest);
		if (configureCommand != null && configureCommand.canExecute())
		{
			configureCommand.execute(monitor, info);
		}
	}
	
}
