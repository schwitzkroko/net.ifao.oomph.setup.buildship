/*
 * Copyright (c) 2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package net.ifao.oomph.setup.buildship;

import org.eclipse.oomph.util.OomphPlugin;

import org.eclipse.emf.common.util.ResourceLocator;

/**
 * @author Eike Stepper
 */
public final class BuildshipImportPlugin extends OomphPlugin
{
  public static final BuildshipImportPlugin INSTANCE = new BuildshipImportPlugin();

  private static Implementation plugin;

  public BuildshipImportPlugin()
  {
    super(new ResourceLocator[] {});
  }

  @Override
  public ResourceLocator getPluginResourceLocator()
  {
    return plugin;
  }

  /**
   * @author Eike Stepper
   */
  public static class Implementation extends EclipsePlugin
  {
    public Implementation()
    {
      plugin = this;
    }
  }
}
