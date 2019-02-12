/*
 * Copyright (c) 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package net.ifao.oomph.buildshipimport.provider;


import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.oomph.resources.ResourcesFactory;
import org.eclipse.oomph.setup.provider.SetupTaskItemProvider;

import net.ifao.oomph.buildshipimport.BuildshipImportPackage;
import net.ifao.oomph.buildshipimport.BuildshipImportTask;


/**
 * This is the item provider adapter for a {@link net.ifao.oomph.buildshipimport.BuildshipImportTask} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class BuildshipImportTaskItemProvider
   extends SetupTaskItemProvider
{
   /**
    * This constructs an instance from a factory and a notifier.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   public BuildshipImportTaskItemProvider(AdapterFactory adapterFactory)
   {
      super(adapterFactory);
   }

   /**
    * This returns the property descriptors for the adapted class.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object)
   {
      if (itemPropertyDescriptors == null)
      {
         super.getPropertyDescriptors(object);

         addGradleTaskPropertyDescriptor(object);
      }
      return itemPropertyDescriptors;
   }

   /**
    * This adds a property descriptor for the Gradle Task feature.
    * <!-- begin-user-doc -->
    * <!-- end-user-doc -->
    * @generated
    */
   protected void addGradleTaskPropertyDescriptor(Object object)
   {
      itemPropertyDescriptors.add
         (createItemPropertyDescriptor
            (((ComposeableAdapterFactory)adapterFactory).getRootAdapterFactory(),
             getResourceLocator(),
             getString("_UI_BuildshipImportTask_gradleTask_feature"),
             getString("_UI_PropertyDescriptor_description", "_UI_BuildshipImportTask_gradleTask_feature", "_UI_BuildshipImportTask_type"),
             BuildshipImportPackage.Literals.BUILDSHIP_IMPORT_TASK__GRADLE_TASK,
             true,
             false,
             false,
             ItemPropertyDescriptor.GENERIC_VALUE_IMAGE,
             null,
             null));
   }

   /**
    * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
    * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
    * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Collection<? extends EStructuralFeature> getChildrenFeatures(Object object)
   {
      if (childrenFeatures == null)
      {
         super.getChildrenFeatures(object);
         childrenFeatures.add(BuildshipImportPackage.Literals.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS);
      }
      return childrenFeatures;
   }

   /**
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected EStructuralFeature getChildFeature(Object object, Object child)
   {
      // Check the type of the specified child object and return the proper feature to use for
      // adding (see {@link AddCommand}) it as a child.

      return super.getChildFeature(object, child);
   }

   /**
    * This returns BuildshipImportTask.gif.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public Object getImage(Object object)
   {
      return overlayImage(object, getResourceLocator().getImage("full/obj16/BuildshipImportTask"));
   }

   /**
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected boolean shouldComposeCreationImage()
   {
      return true;
   }

   /**
   * This returns the label text for the adapted class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
   @Override
   public String getText(Object object)
   {
      return getString("_UI_BuildshipImportTask_type");
   }

   /**
    * This handles model notifications by calling {@link #updateChildren} to update any cached
    * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public void notifyChanged(Notification notification)
   {
      updateChildren(notification);

      switch (notification.getFeatureID(BuildshipImportTask.class))
      {
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__GRADLE_TASK:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
            return;
         case BuildshipImportPackage.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS:
            fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
            return;
      }
      super.notifyChanged(notification);
   }

   /**
    * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
    * that can be created under this object.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object)
   {
      super.collectNewChildDescriptors(newChildDescriptors, object);

      newChildDescriptors.add
         (createChildParameter
            (BuildshipImportPackage.Literals.BUILDSHIP_IMPORT_TASK__SOURCE_LOCATORS,
             ResourcesFactory.eINSTANCE.createSourceLocator()));
   }

   /**
    * Return the resource locator for this item provider's resources.
    * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
    * @generated
    */
   @Override
   public ResourceLocator getResourceLocator()
   {
      return BuildshipImportEditPlugin.INSTANCE;
   }

}
