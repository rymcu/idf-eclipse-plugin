/*******************************************************************************
 * Copyright 2018-2019 Espressif Systems (Shanghai) PTE LTD. All rights reserved.
 * Use is subject to license terms.
 *******************************************************************************/
package com.espressif.idf.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.espressif.idf.core.build.IDFBuildConfigurationProvider;
import com.espressif.idf.core.logging.Logger;

/**
 * @author Kondal Kolipaka <kondal.kolipaka@espressif.com>
 *
 */
public class EclipseUtil
{

	/**
	 * Returns the IResource from the current selection
	 *
	 * @return the IResource, or <code>null</code>.
	 */
	public static IResource getSelectionResource()
	{
		ISelectionService selectionService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = selectionService != null ? selectionService.getSelection() : StructuredSelection.EMPTY;

		if (selection instanceof IStructuredSelection && !selection.isEmpty())
		{
			Object element = ((IStructuredSelection) selection).getFirstElement();
			if (element instanceof org.eclipse.core.resources.IResource)
			{
				return ((IResource) element);
			}
			if (element instanceof IAdaptable)
			{
				return ((IAdaptable) element).getAdapter(IResource.class);
			}
		}
		return null;
	}

	/**
	 * Selected project from the Project explorer view
	 * 
	 * @return
	 */
	public static IProject getSelectedProjectInExplorer()
	{
		return getSelectedProject(IPageLayout.ID_PROJECT_EXPLORER);
	}

	/**
	 * @return
	 */
	public static IProject getSelectedIDFProjectInExplorer()
	{
		IProject project = getSelectedProject(IPageLayout.ID_PROJECT_EXPLORER);
		try
		{
			if (project != null && project.getActiveBuildConfig() != null
					&& project.getActiveBuildConfig().getName().startsWith(IDFBuildConfigurationProvider.ID))
			{
				return project;
			}
		}
		catch (CoreException e)
		{
			Logger.log(e);
		}

		return null;
	}
	
	/**
	 * @param viewID
	 * @return
	 */
	private static IProject getSelectedProject(String viewID)
	{
		ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service.getSelection(viewID);
		if (structured instanceof IStructuredSelection)
		{
			Object selectedObject = ((IStructuredSelection) structured).getFirstElement();
			if (selectedObject instanceof IAdaptable)
			{
				IResource resource = (IResource) ((IAdaptable) selectedObject).getAdapter(IResource.class);
				if (resource != null)
				{
					return resource.getProject();
				}
			}
		}
		return null;
	}

	public static Shell getShell()
	{
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
		{
			IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (windows.length > 0)
			{
				return windows[0].getShell();
			}
		}
		else
		{
			return window.getShell();
		}
		return null;
	}

}
