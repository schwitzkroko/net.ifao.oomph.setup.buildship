package net.ifao.plugins.editor.dtdinfo;

import org.eclipse.swt.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.custom.*;
import java.util.Hashtable;
import org.eclipse.jface.resource.*;

/** 
 * The class DtdInfoAdapter was automatically genereated with Xml2Swt
 * <p>
 * Date: 30.November 2006<br>
 * sourceXml:
 * <pre>
 *   <font color='blue'>&lt;<font color='#983000'>SWT</font> <font color='#983000'>xmlns:xsi</font><font color='blue'>="</font><font color='black'>http://www.w3.org/2001/XMLSchema-instance</font><font color='blue'>"</font> <font color='#983000'>xsi:noNamespaceSchemaLocation</font><font color='blue'>="</font><font color='black'>..\..\Swt.xsd</font><font color='blue'>"</font> <font color='#983000'>package</font><font color='blue'>="</font><font color='black'>net.ifao.plugins.editor.dtdinfo</font><font color='blue'>"</font>&gt;
 *     <font color='blue'>&lt;<font color='#983000'>Shell</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DtdInfo</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font> <font color='#983000'>modal</font><font color='blue'>="</font><font color='black'>true</font><font color='blue'>"</font>&gt;
 *       <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_LIGHT_SHADOW</font><font color='blue'>"</font>&gt;
 *         <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Title</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Title:</font><font color='blue'>"</font> <font color='#983000'>font</font><font color='blue'>="</font><font color='black'>HEADER_FONT</font><font color='blue'>"</font> /&gt;</font>
 *         <font color='blue'>&lt;<font color='#983000'>SashForm</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Sash</font><font color='blue'>"</font> <font color='#983000'>sashDirection</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>TabFolder</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ReqRes</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *               <font color='blue'>&lt;<font color='#983000'>TabItem</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Request</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Tree</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Req</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *               &lt;/<font color='#983000'>TabItem</font>&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>TabItem</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Response</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Tree</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Res</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *               &lt;/<font color='#983000'>TabItem</font>&gt;</font>
 *             &lt;/<font color='#983000'>TabFolder</font>&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *           <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_WIDGET_BACKGROUND</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font>&gt;
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailName</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Name</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailDescription</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Description</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_OUT</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>type</font><font color='blue'>="</font><font color='black'>CHECK</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Madatory ?</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *             <font color='blue'>&lt;<font color='#983000'>Group</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>Type</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *               <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *                 <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>DisplayPnrElements</font><font color='blue'>"</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DisplayPnrElements</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>3</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *                     <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Transform Rules</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>AdditionalDocument1</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Additional Document</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>NotSupported1</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>not supported</font><font color='blue'>"</font> /&gt;</font>
 *                   &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TransformRules1</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Provider Ref</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ProviderRef1</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Date (User)</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DateUser1</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *               &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *               <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font>&gt;
 *                 <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>HidePnrElements</font><font color='blue'>"</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>HidePnrElements</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>PnrElementsActive</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Pnr Elements</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>3</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *                     <font color='blue'>&lt;<font color='#983000'>Combo</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>PnrName</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Combo</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>PnrAttr</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>UsePnrElement</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Use</font><font color='blue'>"</font> /&gt;</font>
 *                   &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>SelfDefined</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Create self defined method</font><font color='blue'>"</font> <font color='#983000'>type</font><font color='blue'>="</font><font color='black'>CHECK</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>2</font><font color='blue'>"</font>&gt;
 *                     <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailPnr</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DetailPnr2</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Detail ...</font><font color='blue'>"</font> /&gt;</font>
 *                   &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>numColumns</font><font color='blue'>="</font><font color='black'>3</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font>&gt;
 *                     <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Transform Rules</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>AdditionalDocument2</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Additional Document</font><font color='blue'>"</font> /&gt;</font>
 *                     <font color='blue'>&lt;<font color='#983000'>Button</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>NotSupported2</font><font color='blue'>"</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>not supported</font><font color='blue'>"</font> /&gt;</font>
 *                   &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>TransformRules2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Provider Ref</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>ProviderRef2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>FULL</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *                 <font color='blue'>&lt;<font color='#983000'>Composite</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>border</font><font color='blue'>="</font><font color='black'>SHADOW_ETCHED_IN</font><font color='blue'>"</font> <font color='#983000'>background</font><font color='blue'>="</font><font color='black'>COLOR_LIST_BACKGROUND</font><font color='blue'>"</font>&gt;
 *                   <font color='blue'>&lt;<font color='#983000'>Label</font> <font color='#983000'>text</font><font color='blue'>="</font><font color='black'>Date (User)</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> /&gt;</font>
 *                   <font color='blue'>&lt;<font color='#983000'>Text</font> <font color='#983000'>name</font><font color='blue'>="</font><font color='black'>DateUser2</font><font color='blue'>"</font> <font color='#983000'>fill</font><font color='blue'>="</font><font color='black'>HORIZONTAL</font><font color='blue'>"</font> <font color='#983000'>enabled</font><font color='blue'>="</font><font color='black'>false</font><font color='blue'>"</font> /&gt;</font>
 *                 &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *               &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *             &lt;/<font color='#983000'>Group</font>&gt;</font>
 *           &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *         &lt;/<font color='#983000'>SashForm</font>&gt;</font>
 *       &lt;/<font color='#983000'>Composite</font>&gt;</font>
 *     &lt;/<font color='#983000'>Shell</font>&gt;</font>
 *   &lt;/<font color='#983000'>SWT</font>&gt;</font>
 * 
 * </pre>
 * <p> 
 * Copyright &copy; 2006, i:FAO
 * 
 * @author generator
 */

public abstract class DtdInfoAdapter
{
  private Shell sShell = null;
  private Class _abstractUIPlugin;

  /**
   * Constructor for DtdInfoAdapter
   * 
   * @param pAbstractUIPlugin InterfaceClass for AbstractUIPlugin
   **/
  public DtdInfoAdapter(Class pAbstractUIPlugin) {
    _abstractUIPlugin = pAbstractUIPlugin;
    sShell = new Shell(Display.getCurrent(), SWT.APPLICATION_MODAL | SWT.TITLE | SWT.CLOSE | SWT.RESIZE);
    sShell.setText("DtdInfo");
    GridLayout gridLayout = new GridLayout();
    gridLayout.horizontalSpacing = 0;
    gridLayout.marginWidth = 0;
    gridLayout.marginHeight = 0;
    gridLayout.verticalSpacing = 0;
    sShell.setLayout(gridLayout);
  }

  /**
   * Method initAdapter should be called within child constructor and 
   * initalizes the GUI with it's componenets
   * 
   **/
  protected void initAdapter() {
    Color backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    sShell.setBackground(backgound);
    createComposite1(sShell, backgound);
  }
  /**
   * Method show displays this DtdInfo and waits
   * until finished.
   * 
   **/
  public void show() {
    Display display = Display.getDefault();
    sShell.pack();
    sShell.open();
    while (!sShell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    sShell.dispose();
    
  }
  
  /**
   * Method close, closes this shell.
   * 
   **/
  public void close() {
    if (!sShell.isDisposed()) {
       sShell.close();
       sShell.dispose();
    }
  }

   // get hashtable for Images
   private Hashtable<String, Image> htImages = new Hashtable<String, Image>();

  /**
   * Method getIcon returns an image, which has to be located
   * whithin the /icons/ directory
   * 
   * @param sName The name of the Icon
   * @return The related Image
   * 
   **/
   public Image getIcon(String sName)
   {
      Image image = htImages.get(sName);
      if (image == null) {
         Class[] classes = { String.class };
         Object[] args = { "/icons/" + sName };
         ImageDescriptor descriptor;
         try {
            descriptor = (ImageDescriptor) _abstractUIPlugin.getMethod("getImageDescriptor",
                  classes).invoke(_abstractUIPlugin, args);
         }
         catch (Exception e) {
            descriptor = null;
         }
         if (descriptor == null) {
            image = new Image(sShell.getDisplay(), "icons/" + sName);
         } else {
            image = descriptor.createImage();
         }
         htImages.put(sName, image);
      }
      return image;
   }
  
  /**
   * Method createComposite1 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite1(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW);
    value.setBackground(backgound);
    createCLabelTitle(value, backgound);
    createSashFormSash(value, backgound);
    
    return value;
  } // finished createComposite1
  
  // +---+---+---+---+---+
  // | T | i | t | l | e |
  // +---+---+---+---+---+
  
  /**
   * Method initCLabelTitle initalizes Title.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTitle Title of type CLabel
   **/
  protected void initCLabelTitle(CLabel pTitle) {}
  
  private CLabel _CLabelTitle = null; // private member
  /**
   * Method getCLabel_CLabelTitle returns the _CLabelTitle-object
   * 
   * @return _CLabelTitle-object of type CLabel
   **/
  public CLabel getCLabelTitle() {
    return _CLabelTitle;
  }
  
  /**
   * Method createCLabelTitle creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelTitle(Composite parent, Color backgound) {
    _CLabelTitle = new CLabel(parent, SWT.NONE);
    _CLabelTitle.setText("Title:");
    _CLabelTitle.setFont(JFaceResources.getFontRegistry().get(JFaceResources.HEADER_FONT));
    _CLabelTitle.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelTitle(_CLabelTitle);
    
    return _CLabelTitle;
  } // finished createCLabelTitle
  
  // +---+---+---+---+
  // | S | a | s | h |
  // +---+---+---+---+
  
  /**
   * Method initSashFormSash initalizes Sash.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pSash Sash of type SashForm
   **/
  protected void initSashFormSash(SashForm pSash) {}
  
  private SashForm _SashFormSash = null; // private member
  /**
   * Method getSashForm_SashFormSash returns the _SashFormSash-object
   * 
   * @return _SashFormSash-object of type SashForm
   **/
  public SashForm getSashFormSash() {
    return _SashFormSash;
  }
  
  /**
   * Method createSashFormSash creates a SashForm and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created SashForm-object
   **/
  private SashForm createSashFormSash(Composite parent, Color backgound) {
    _SashFormSash = new SashForm(parent, SWT.NONE | SWT.HORIZONTAL);
    _SashFormSash.SASH_WIDTH=3;
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _SashFormSash.setLayoutData(gridData);
    _SashFormSash.setBackground(backgound);
    // call the Init method (which could be overridden
    initSashFormSash(_SashFormSash);
    createComposite2(_SashFormSash, backgound);
    createComposite5(_SashFormSash, backgound);
    
    return _SashFormSash;
  } // finished createSashFormSash
  
  /**
   * Method createComposite2 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite2(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    value.setBackground(backgound);
    createTabFolderReqRes(value, backgound);
    
    return value;
  } // finished createComposite2
  
  // +---+---+---+---+---+---+
  // | R | e | q | R | e | s |
  // +---+---+---+---+---+---+
  
  /**
   * Method initTabFolderReqRes initalizes ReqRes.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pReqRes ReqRes of type TabFolder
   **/
  protected void initTabFolderReqRes(TabFolder pReqRes) {}
  
  private TabFolder _TabFolderReqRes = null; // private member
  /**
   * Method getTabFolder_TabFolderReqRes returns the _TabFolderReqRes-object
   * 
   * @return _TabFolderReqRes-object of type TabFolder
   **/
  public TabFolder getTabFolderReqRes() {
    return _TabFolderReqRes;
  }
  
  /**
   * Method createTabFolderReqRes creates a TabFolder and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created TabFolder-object
   **/
  private TabFolder createTabFolderReqRes(Composite parent, Color backgound) {
    _TabFolderReqRes = new TabFolder(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _TabFolderReqRes.setLayoutData(gridData);
    _TabFolderReqRes.setBackground(backgound);
    // call the Init method (which could be overridden
    initTabFolderReqRes(_TabFolderReqRes);
    
    // add a Selection-Listener for this click
    _TabFolderReqRes.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickTabFolderReqRes(_TabFolderReqRes);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    createTabItem1(_TabFolderReqRes, backgound);
    createTabItem2(_TabFolderReqRes, backgound);
    
    return _TabFolderReqRes;
  } // finished createTabFolderReqRes
  protected abstract void clickTabFolderReqRes(TabFolder pTabFolderReqRes);
  
  /**
   * Method createTabItem1 creates a TabItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created TabItem-object
   **/
  private TabItem createTabItem1(Composite parent, Color backgound) {
    TabItem value = new TabItem((TabFolder)parent, SWT.NONE);
    value.setText("Request");
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setControl(createComposite3(parent, backgound));
    
    return value;
  } // finished createTabItem1
  
  /**
   * Method createComposite3 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite3(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    value.setBackground(backgound);
    createTreeReq(value, backgound);
    
    return value;
  } // finished createComposite3
  
  // +---+---+---+
  // | R | e | q |
  // +---+---+---+
  
  /**
   * Method initTreeReq initalizes Req.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pReq Req of type Tree
   **/
  protected void initTreeReq(Tree pReq) {}
  
  private Tree _TreeReq = null; // private member
  /**
   * Method getTree_TreeReq returns the _TreeReq-object
   * 
   * @return _TreeReq-object of type Tree
   **/
  public Tree getTreeReq() {
    return _TreeReq;
  }
  
  /**
   * Method createTreeReq creates a Tree and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Tree-object
   **/
  private Tree createTreeReq(Composite parent, Color backgound) {
    _TreeReq = new Tree(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _TreeReq.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    _TreeReq.setBackground(backgound);
    // call the Init method (which could be overridden
    initTreeReq(_TreeReq);
    
    // add a Tree-Listener for this treeCollapsed treeExpanded
    _TreeReq.addTreeListener(new org.eclipse.swt.events.TreeListener()
    {
       public void treeCollapsed(org.eclipse.swt.events.TreeEvent e)
       {
         treeCollapsedTreeReq(e);
       }
       public void treeExpanded(org.eclipse.swt.events.TreeEvent e)
       {
         treeExpandedTreeReq(e);
       }
    });
    
    // add a Key-Listener for this keyPressed
    _TreeReq.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTreeReq(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Selection-Listener for this click
    _TreeReq.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickTreeReq(_TreeReq);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _TreeReq;
  } // finished createTreeReq
  protected void treeCollapsedTreeReq(org.eclipse.swt.events.TreeEvent e) {}
  protected void treeExpandedTreeReq(org.eclipse.swt.events.TreeEvent e) {}
  protected void keyPressedTreeReq(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL)  && e.keyCode == 'a') {
      getTreeReq().selectAll();
   }
  }
  protected abstract void clickTreeReq(Tree pTreeReq);
  
  /**
   * Method createTabItem2 creates a TabItem and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created TabItem-object
   **/
  private TabItem createTabItem2(Composite parent, Color backgound) {
    TabItem value = new TabItem((TabFolder)parent, SWT.NONE);
    value.setText("Response");
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setControl(createComposite4(parent, backgound));
    
    return value;
  } // finished createTabItem2
  
  /**
   * Method createComposite4 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite4(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    value.setBackground(backgound);
    createTreeRes(value, backgound);
    
    return value;
  } // finished createComposite4
  
  // +---+---+---+
  // | R | e | s |
  // +---+---+---+
  
  /**
   * Method initTreeRes initalizes Res.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pRes Res of type Tree
   **/
  protected void initTreeRes(Tree pRes) {}
  
  private Tree _TreeRes = null; // private member
  /**
   * Method getTree_TreeRes returns the _TreeRes-object
   * 
   * @return _TreeRes-object of type Tree
   **/
  public Tree getTreeRes() {
    return _TreeRes;
  }
  
  /**
   * Method createTreeRes creates a Tree and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Tree-object
   **/
  private Tree createTreeRes(Composite parent, Color backgound) {
    _TreeRes = new Tree(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _TreeRes.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    _TreeRes.setBackground(backgound);
    // call the Init method (which could be overridden
    initTreeRes(_TreeRes);
    
    // add a Tree-Listener for this treeCollapsed treeExpanded
    _TreeRes.addTreeListener(new org.eclipse.swt.events.TreeListener()
    {
       public void treeCollapsed(org.eclipse.swt.events.TreeEvent e)
       {
         treeCollapsedTreeRes(e);
       }
       public void treeExpanded(org.eclipse.swt.events.TreeEvent e)
       {
         treeExpandedTreeRes(e);
       }
    });
    
    // add a Key-Listener for this keyPressed
    _TreeRes.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTreeRes(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Selection-Listener for this click
    _TreeRes.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickTreeRes(_TreeRes);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _TreeRes;
  } // finished createTreeRes
  protected void treeCollapsedTreeRes(org.eclipse.swt.events.TreeEvent e) {}
  protected void treeExpandedTreeRes(org.eclipse.swt.events.TreeEvent e) {}
  protected void keyPressedTreeRes(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL)  && e.keyCode == 'a') {
      getTreeRes().selectAll();
   }
  }
  protected abstract void clickTreeRes(Tree pTreeRes);
  
  /**
   * Method createComposite5 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite5(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
    value.setBackground(backgound);
    createCLabelDetailName(value, backgound);
    createCLabelDetailDescription(value, backgound);
    createButton1(value, backgound);
    createGroupType(value, backgound);
    
    return value;
  } // finished createComposite5
  
  // +---+---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | N | a | m | e |
  // +---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initCLabelDetailName initalizes DetailName.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailName DetailName of type CLabel
   **/
  protected void initCLabelDetailName(CLabel pDetailName) {}
  
  private CLabel _CLabelDetailName = null; // private member
  /**
   * Method getCLabel_CLabelDetailName returns the _CLabelDetailName-object
   * 
   * @return _CLabelDetailName-object of type CLabel
   **/
  public CLabel getCLabelDetailName() {
    return _CLabelDetailName;
  }
  
  /**
   * Method createCLabelDetailName creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelDetailName(Composite parent, Color backgound) {
    _CLabelDetailName = new CLabel(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _CLabelDetailName.setLayoutData(gridData);
    _CLabelDetailName.setText("Name");
    _CLabelDetailName.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelDetailName(_CLabelDetailName);
    
    return _CLabelDetailName;
  } // finished createCLabelDetailName
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | D | e | s | c | r | i | p | t | i | o | n |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initCLabelDetailDescription initalizes DetailDescription.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailDescription DetailDescription of type CLabel
   **/
  protected void initCLabelDetailDescription(CLabel pDetailDescription) {}
  
  private CLabel _CLabelDetailDescription = null; // private member
  /**
   * Method getCLabel_CLabelDetailDescription returns the _CLabelDetailDescription-object
   * 
   * @return _CLabelDetailDescription-object of type CLabel
   **/
  public CLabel getCLabelDetailDescription() {
    return _CLabelDetailDescription;
  }
  
  /**
   * Method createCLabelDetailDescription creates a CLabel and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created CLabel-object
   **/
  private CLabel createCLabelDetailDescription(Composite parent, Color backgound) {
    _CLabelDetailDescription = new CLabel(parent, SWT.SHADOW_ETCHED_OUT | SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _CLabelDetailDescription.setLayoutData(gridData);
    _CLabelDetailDescription.setText("Description");
    _CLabelDetailDescription.setBackground(backgound);
    // call the Init method (which could be overridden
    initCLabelDetailDescription(_CLabelDetailDescription);
    
    return _CLabelDetailDescription;
  } // finished createCLabelDetailDescription
  
  /**
   * Method createButton1 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButton1(Composite parent, Color backgound) {
    Button value = new Button(parent, SWT.NONE | SWT.CHECK);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Madatory ?");
    value.setBackground(backgound);
    
    return value;
  } // finished createButton1
  
  // +---+---+---+---+
  // | T | y | p | e |
  // +---+---+---+---+
  
  /**
   * Method initGroupType initalizes Type.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pType Type of type Group
   **/
  protected void initGroupType(Group pType) {}
  
  private Group _GroupType = null; // private member
  /**
   * Method getGroup_GroupType returns the _GroupType-object
   * 
   * @return _GroupType-object of type Group
   **/
  public Group getGroupType() {
    return _GroupType;
  }
  
  /**
   * Method createGroupType creates a Group and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Group-object
   **/
  private Group createGroupType(Composite parent, Color backgound) {
    _GroupType = new Group(parent, SWT.NONE);
    // create a StackLayout for the Composite
    _GroupType.setLayout(_GroupTypeLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _GroupType.setLayoutData(gridData);
    _GroupType.setBackground(backgound);
    // call the Init method (which could be overridden
    initGroupType(_GroupType);
    createComposite6(_GroupType, backgound);
    createComposite7(_GroupType, backgound);
    createComposite12(_GroupType, backgound);
    
    return _GroupType;
  } // finished createGroupType

  StackLayout _GroupTypeLayout = new StackLayout();


  public void setGroupTypePage(int piPage)
  {
     if (_GroupType == null)
         return;
     Control[] children = _GroupType.getChildren();
     if (piPage >= 0 && piPage < children.length) {
        _GroupTypeLayout.topControl = children[piPage];
        _GroupType.layout();
     }
  }
  
  /**
   * Method createComposite6 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite6(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    
    return value;
  } // finished createComposite6
  
  /**
   * Method createComposite7 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite7(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createButtonDisplayPnrElements(value, backgound);
    createComposite8(value, backgound);
    createComposite10(value, backgound);
    createComposite11(value, backgound);
    
    return value;
  } // finished createComposite7
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | D | i | s | p | l | a | y | P | n | r | E | l | e | m | e | n | t | s |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonDisplayPnrElements initalizes DisplayPnrElements.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDisplayPnrElements DisplayPnrElements of type Button
   **/
  protected void initButtonDisplayPnrElements(Button pDisplayPnrElements) {}
  
  private Button _ButtonDisplayPnrElements = null; // private member
  /**
   * Method getButton_ButtonDisplayPnrElements returns the _ButtonDisplayPnrElements-object
   * 
   * @return _ButtonDisplayPnrElements-object of type Button
   **/
  public Button getButtonDisplayPnrElements() {
    return _ButtonDisplayPnrElements;
  }
  
  /**
   * Method createButtonDisplayPnrElements creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonDisplayPnrElements(Composite parent, Color backgound) {
    _ButtonDisplayPnrElements = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonDisplayPnrElements.setLayoutData(gridData);
    _ButtonDisplayPnrElements.setText("DisplayPnrElements");
    _ButtonDisplayPnrElements.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonDisplayPnrElements(_ButtonDisplayPnrElements);
    
    // add a Selection-Listener for this click
    _ButtonDisplayPnrElements.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonDisplayPnrElements(_ButtonDisplayPnrElements);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonDisplayPnrElements;
  } // finished createButtonDisplayPnrElements
  protected abstract void clickButtonDisplayPnrElements(Button pButtonDisplayPnrElements);
  
  /**
   * Method createComposite8 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite8(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createComposite9(value, backgound);
    createTextTransformRules1(value, backgound);
    
    return value;
  } // finished createComposite8
  
  /**
   * Method createComposite9 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite9(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createLabel1(value, backgound);
    createButtonAdditionalDocument1(value, backgound);
    createButtonNotSupported1(value, backgound);
    
    return value;
  } // finished createComposite9
  
  /**
   * Method createLabel1 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel1(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setText("Transform Rules");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel1
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | A | d | d | i | t | i | o | n | a | l | D | o | c | u | m | e | n | t | 1 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonAdditionalDocument1 initalizes AdditionalDocument1.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pAdditionalDocument1 AdditionalDocument1 of type Button
   **/
  protected void initButtonAdditionalDocument1(Button pAdditionalDocument1) {}
  
  private Button _ButtonAdditionalDocument1 = null; // private member
  /**
   * Method getButton_ButtonAdditionalDocument1 returns the _ButtonAdditionalDocument1-object
   * 
   * @return _ButtonAdditionalDocument1-object of type Button
   **/
  public Button getButtonAdditionalDocument1() {
    return _ButtonAdditionalDocument1;
  }
  
  /**
   * Method createButtonAdditionalDocument1 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonAdditionalDocument1(Composite parent, Color backgound) {
    _ButtonAdditionalDocument1 = new Button(parent, SWT.NONE);
    _ButtonAdditionalDocument1.setText("Additional Document");
    _ButtonAdditionalDocument1.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonAdditionalDocument1(_ButtonAdditionalDocument1);
    
    // add a Selection-Listener for this click
    _ButtonAdditionalDocument1.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonAdditionalDocument1(_ButtonAdditionalDocument1);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonAdditionalDocument1;
  } // finished createButtonAdditionalDocument1
  protected abstract void clickButtonAdditionalDocument1(Button pButtonAdditionalDocument1);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | N | o | t | S | u | p | p | o | r | t | e | d | 1 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonNotSupported1 initalizes NotSupported1.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pNotSupported1 NotSupported1 of type Button
   **/
  protected void initButtonNotSupported1(Button pNotSupported1) {}
  
  private Button _ButtonNotSupported1 = null; // private member
  /**
   * Method getButton_ButtonNotSupported1 returns the _ButtonNotSupported1-object
   * 
   * @return _ButtonNotSupported1-object of type Button
   **/
  public Button getButtonNotSupported1() {
    return _ButtonNotSupported1;
  }
  
  /**
   * Method createButtonNotSupported1 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonNotSupported1(Composite parent, Color backgound) {
    _ButtonNotSupported1 = new Button(parent, SWT.NONE);
    _ButtonNotSupported1.setText("not supported");
    _ButtonNotSupported1.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonNotSupported1(_ButtonNotSupported1);
    
    // add a Selection-Listener for this click
    _ButtonNotSupported1.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonNotSupported1(_ButtonNotSupported1);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonNotSupported1;
  } // finished createButtonNotSupported1
  protected abstract void clickButtonNotSupported1(Button pButtonNotSupported1);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | T | r | a | n | s | f | o | r | m | R | u | l | e | s | 1 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextTransformRules1 initalizes TransformRules1.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTransformRules1 TransformRules1 of type Text
   **/
  protected void initTextTransformRules1(Text pTransformRules1) {}
  
  private Text _TextTransformRules1 = null; // private member
  /**
   * Method getText_TextTransformRules1 returns the _TextTransformRules1-object
   * 
   * @return _TextTransformRules1-object of type Text
   **/
  public Text getTextTransformRules1() {
    return _TextTransformRules1;
  }
  
  /**
   * Method createTextTransformRules1 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextTransformRules1(Composite parent, Color backgound) {
    _TextTransformRules1 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextTransformRules1.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextTransformRules1(_TextTransformRules1);
    
    // add a Key-Listener for this keyPressed
    _TextTransformRules1.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextTransformRules1(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextTransformRules1.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextTransformRules1(_TextTransformRules1);
       }
    });
    
    return _TextTransformRules1;
  } // finished createTextTransformRules1
  protected void keyPressedTextTransformRules1(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextTransformRules1().selectAll();
   }
  }
  protected abstract void modifyTextTransformRules1(Text pTextTransformRules1);
  
  /**
   * Method createComposite10 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite10(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createLabel2(value, backgound);
    createTextProviderRef1(value, backgound);
    
    return value;
  } // finished createComposite10
  
  /**
   * Method createLabel2 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel2(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Provider Ref");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel2
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  // | P | r | o | v | i | d | e | r | R | e | f | 1 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextProviderRef1 initalizes ProviderRef1.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pProviderRef1 ProviderRef1 of type Text
   **/
  protected void initTextProviderRef1(Text pProviderRef1) {}
  
  private Text _TextProviderRef1 = null; // private member
  /**
   * Method getText_TextProviderRef1 returns the _TextProviderRef1-object
   * 
   * @return _TextProviderRef1-object of type Text
   **/
  public Text getTextProviderRef1() {
    return _TextProviderRef1;
  }
  
  /**
   * Method createTextProviderRef1 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextProviderRef1(Composite parent, Color backgound) {
    _TextProviderRef1 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextProviderRef1.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextProviderRef1(_TextProviderRef1);
    
    // add a Key-Listener for this keyPressed
    _TextProviderRef1.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextProviderRef1(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextProviderRef1.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextProviderRef1(_TextProviderRef1);
       }
    });
    
    return _TextProviderRef1;
  } // finished createTextProviderRef1
  protected void keyPressedTextProviderRef1(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextProviderRef1().selectAll();
   }
  }
  protected abstract void modifyTextProviderRef1(Text pTextProviderRef1);
  
  /**
   * Method createComposite11 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite11(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createLabel3(value, backgound);
    createTextDateUser1(value, backgound);
    
    return value;
  } // finished createComposite11
  
  /**
   * Method createLabel3 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel3(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Date (User)");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel3
  
  // +---+---+---+---+---+---+---+---+---+
  // | D | a | t | e | U | s | e | r | 1 |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextDateUser1 initalizes DateUser1.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDateUser1 DateUser1 of type Text
   **/
  protected void initTextDateUser1(Text pDateUser1) {}
  
  private Text _TextDateUser1 = null; // private member
  /**
   * Method getText_TextDateUser1 returns the _TextDateUser1-object
   * 
   * @return _TextDateUser1-object of type Text
   **/
  public Text getTextDateUser1() {
    return _TextDateUser1;
  }
  
  /**
   * Method createTextDateUser1 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextDateUser1(Composite parent, Color backgound) {
    _TextDateUser1 = new Text(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _TextDateUser1.setLayoutData(gridData);
    _TextDateUser1.setEnabled(false);
    // call the Init method (which could be overridden
    initTextDateUser1(_TextDateUser1);
    
    // add a Key-Listener for this keyPressed
    _TextDateUser1.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextDateUser1(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextDateUser1.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextDateUser1(_TextDateUser1);
       }
    });
    
    return _TextDateUser1;
  } // finished createTextDateUser1
  protected void keyPressedTextDateUser1(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDateUser1().selectAll();
   }
  }
  protected abstract void modifyTextDateUser1(Text pTextDateUser1);
  
  /**
   * Method createComposite12 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite12(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createButtonHidePnrElements(value, backgound);
    createCompositePnrElementsActive(value, backgound);
    createComposite15(value, backgound);
    createComposite17(value, backgound);
    createComposite18(value, backgound);
    
    return value;
  } // finished createComposite12
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | H | i | d | e | P | n | r | E | l | e | m | e | n | t | s |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonHidePnrElements initalizes HidePnrElements.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pHidePnrElements HidePnrElements of type Button
   **/
  protected void initButtonHidePnrElements(Button pHidePnrElements) {}
  
  private Button _ButtonHidePnrElements = null; // private member
  /**
   * Method getButton_ButtonHidePnrElements returns the _ButtonHidePnrElements-object
   * 
   * @return _ButtonHidePnrElements-object of type Button
   **/
  public Button getButtonHidePnrElements() {
    return _ButtonHidePnrElements;
  }
  
  /**
   * Method createButtonHidePnrElements creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonHidePnrElements(Composite parent, Color backgound) {
    _ButtonHidePnrElements = new Button(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonHidePnrElements.setLayoutData(gridData);
    _ButtonHidePnrElements.setText("HidePnrElements");
    _ButtonHidePnrElements.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonHidePnrElements(_ButtonHidePnrElements);
    
    // add a Selection-Listener for this click
    _ButtonHidePnrElements.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonHidePnrElements(_ButtonHidePnrElements);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonHidePnrElements;
  } // finished createButtonHidePnrElements
  protected abstract void clickButtonHidePnrElements(Button pButtonHidePnrElements);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | P | n | r | E | l | e | m | e | n | t | s | A | c | t | i | v | e |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initCompositePnrElementsActive initalizes PnrElementsActive.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pPnrElementsActive PnrElementsActive of type Composite
   **/
  protected void initCompositePnrElementsActive(Composite pPnrElementsActive) {}
  
  private Composite _CompositePnrElementsActive = null; // private member
  /**
   * Method getComposite_CompositePnrElementsActive returns the _CompositePnrElementsActive-object
   * 
   * @return _CompositePnrElementsActive-object of type Composite
   **/
  public Composite getCompositePnrElementsActive() {
    return _CompositePnrElementsActive;
  }
  
  /**
   * Method createCompositePnrElementsActive creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createCompositePnrElementsActive(Composite parent, Color backgound) {
    _CompositePnrElementsActive = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    _CompositePnrElementsActive.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    _CompositePnrElementsActive.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    _CompositePnrElementsActive.setBackground(backgound);
    // call the Init method (which could be overridden
    initCompositePnrElementsActive(_CompositePnrElementsActive);
    createLabel4(_CompositePnrElementsActive, backgound);
    createComposite13(_CompositePnrElementsActive, backgound);
    createButtonSelfDefined(_CompositePnrElementsActive, backgound);
    createComposite14(_CompositePnrElementsActive, backgound);
    
    return _CompositePnrElementsActive;
  } // finished createCompositePnrElementsActive
  
  /**
   * Method createLabel4 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel4(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Pnr Elements");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel4
  
  /**
   * Method createComposite13 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite13(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createComboPnrName(value, backgound);
    createComboPnrAttr(value, backgound);
    createButtonUsePnrElement(value, backgound);
    
    return value;
  } // finished createComposite13
  
  // +---+---+---+---+---+---+---+
  // | P | n | r | N | a | m | e |
  // +---+---+---+---+---+---+---+
  
  /**
   * Method initComboPnrName initalizes PnrName.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pPnrName PnrName of type Combo
   **/
  protected void initComboPnrName(Combo pPnrName) {}
  
  private Combo _ComboPnrName = null; // private member
  /**
   * Method getCombo_ComboPnrName returns the _ComboPnrName-object
   * 
   * @return _ComboPnrName-object of type Combo
   **/
  public Combo getComboPnrName() {
    return _ComboPnrName;
  }
  
  /**
   * Method createComboPnrName creates a Combo and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Combo-object
   **/
  private Combo createComboPnrName(Composite parent, Color backgound) {
    _ComboPnrName = new Combo(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ComboPnrName.setLayoutData(gridData);
    _ComboPnrName.setBackground(backgound);
    // call the Init method (which could be overridden
    initComboPnrName(_ComboPnrName);
    
    // add a Key-Listener for this keyPressed
    _ComboPnrName.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedComboPnrName(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Selection-Listener for this click
    _ComboPnrName.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickComboPnrName(_ComboPnrName);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ComboPnrName;
  } // finished createComboPnrName
  protected void keyPressedComboPnrName(org.eclipse.swt.events.KeyEvent e) {}
  protected abstract void clickComboPnrName(Combo pComboPnrName);
  
  // +---+---+---+---+---+---+---+
  // | P | n | r | A | t | t | r |
  // +---+---+---+---+---+---+---+
  
  /**
   * Method initComboPnrAttr initalizes PnrAttr.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pPnrAttr PnrAttr of type Combo
   **/
  protected void initComboPnrAttr(Combo pPnrAttr) {}
  
  private Combo _ComboPnrAttr = null; // private member
  /**
   * Method getCombo_ComboPnrAttr returns the _ComboPnrAttr-object
   * 
   * @return _ComboPnrAttr-object of type Combo
   **/
  public Combo getComboPnrAttr() {
    return _ComboPnrAttr;
  }
  
  /**
   * Method createComboPnrAttr creates a Combo and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Combo-object
   **/
  private Combo createComboPnrAttr(Composite parent, Color backgound) {
    _ComboPnrAttr = new Combo(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ComboPnrAttr.setLayoutData(gridData);
    _ComboPnrAttr.setBackground(backgound);
    // call the Init method (which could be overridden
    initComboPnrAttr(_ComboPnrAttr);
    
    // add a Key-Listener for this keyPressed
    _ComboPnrAttr.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedComboPnrAttr(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Selection-Listener for this click
    _ComboPnrAttr.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickComboPnrAttr(_ComboPnrAttr);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ComboPnrAttr;
  } // finished createComboPnrAttr
  protected void keyPressedComboPnrAttr(org.eclipse.swt.events.KeyEvent e) {}
  protected abstract void clickComboPnrAttr(Combo pComboPnrAttr);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | U | s | e | P | n | r | E | l | e | m | e | n | t |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonUsePnrElement initalizes UsePnrElement.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pUsePnrElement UsePnrElement of type Button
   **/
  protected void initButtonUsePnrElement(Button pUsePnrElement) {}
  
  private Button _ButtonUsePnrElement = null; // private member
  /**
   * Method getButton_ButtonUsePnrElement returns the _ButtonUsePnrElement-object
   * 
   * @return _ButtonUsePnrElement-object of type Button
   **/
  public Button getButtonUsePnrElement() {
    return _ButtonUsePnrElement;
  }
  
  /**
   * Method createButtonUsePnrElement creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonUsePnrElement(Composite parent, Color backgound) {
    _ButtonUsePnrElement = new Button(parent, SWT.NONE);
    _ButtonUsePnrElement.setText("Use");
    _ButtonUsePnrElement.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonUsePnrElement(_ButtonUsePnrElement);
    
    // add a Selection-Listener for this click
    _ButtonUsePnrElement.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonUsePnrElement(_ButtonUsePnrElement);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonUsePnrElement;
  } // finished createButtonUsePnrElement
  protected abstract void clickButtonUsePnrElement(Button pButtonUsePnrElement);
  
  // +---+---+---+---+---+---+---+---+---+---+---+
  // | S | e | l | f | D | e | f | i | n | e | d |
  // +---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonSelfDefined initalizes SelfDefined.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pSelfDefined SelfDefined of type Button
   **/
  protected void initButtonSelfDefined(Button pSelfDefined) {}
  
  private Button _ButtonSelfDefined = null; // private member
  /**
   * Method getButton_ButtonSelfDefined returns the _ButtonSelfDefined-object
   * 
   * @return _ButtonSelfDefined-object of type Button
   **/
  public Button getButtonSelfDefined() {
    return _ButtonSelfDefined;
  }
  
  /**
   * Method createButtonSelfDefined creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonSelfDefined(Composite parent, Color backgound) {
    _ButtonSelfDefined = new Button(parent, SWT.NONE | SWT.CHECK);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _ButtonSelfDefined.setLayoutData(gridData);
    _ButtonSelfDefined.setText("Create self defined method");
    _ButtonSelfDefined.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonSelfDefined(_ButtonSelfDefined);
    
    // add a Selection-Listener for this click
    _ButtonSelfDefined.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonSelfDefined(_ButtonSelfDefined);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonSelfDefined;
  } // finished createButtonSelfDefined
  protected abstract void clickButtonSelfDefined(Button pButtonSelfDefined);
  
  /**
   * Method createComposite14 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite14(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 2;
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createTextDetailPnr(value, backgound);
    createButtonDetailPnr2(value, backgound);
    
    return value;
  } // finished createComposite14
  
  // +---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | P | n | r |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextDetailPnr initalizes DetailPnr.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailPnr DetailPnr of type Text
   **/
  protected void initTextDetailPnr(Text pDetailPnr) {}
  
  private Text _TextDetailPnr = null; // private member
  /**
   * Method getText_TextDetailPnr returns the _TextDetailPnr-object
   * 
   * @return _TextDetailPnr-object of type Text
   **/
  public Text getTextDetailPnr() {
    return _TextDetailPnr;
  }
  
  /**
   * Method createTextDetailPnr creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextDetailPnr(Composite parent, Color backgound) {
    _TextDetailPnr = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextDetailPnr.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextDetailPnr(_TextDetailPnr);
    
    // add a Key-Listener for this keyPressed
    _TextDetailPnr.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextDetailPnr(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextDetailPnr.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextDetailPnr(_TextDetailPnr);
       }
    });
    
    return _TextDetailPnr;
  } // finished createTextDetailPnr
  protected void keyPressedTextDetailPnr(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDetailPnr().selectAll();
   }
  }
  protected abstract void modifyTextDetailPnr(Text pTextDetailPnr);
  
  // +---+---+---+---+---+---+---+---+---+---+
  // | D | e | t | a | i | l | P | n | r | 2 |
  // +---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonDetailPnr2 initalizes DetailPnr2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDetailPnr2 DetailPnr2 of type Button
   **/
  protected void initButtonDetailPnr2(Button pDetailPnr2) {}
  
  private Button _ButtonDetailPnr2 = null; // private member
  /**
   * Method getButton_ButtonDetailPnr2 returns the _ButtonDetailPnr2-object
   * 
   * @return _ButtonDetailPnr2-object of type Button
   **/
  public Button getButtonDetailPnr2() {
    return _ButtonDetailPnr2;
  }
  
  /**
   * Method createButtonDetailPnr2 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonDetailPnr2(Composite parent, Color backgound) {
    _ButtonDetailPnr2 = new Button(parent, SWT.NONE);
    _ButtonDetailPnr2.setText("Detail ...");
    _ButtonDetailPnr2.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonDetailPnr2(_ButtonDetailPnr2);
    
    // add a Selection-Listener for this click
    _ButtonDetailPnr2.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonDetailPnr2(_ButtonDetailPnr2);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonDetailPnr2;
  } // finished createButtonDetailPnr2
  protected abstract void clickButtonDetailPnr2(Button pButtonDetailPnr2);
  
  /**
   * Method createComposite15 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite15(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createComposite16(value, backgound);
    createTextTransformRules2(value, backgound);
    
    return value;
  } // finished createComposite15
  
  /**
   * Method createComposite16 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite16(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.NONE);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setBackground(backgound);
    createLabel5(value, backgound);
    createButtonAdditionalDocument2(value, backgound);
    createButtonNotSupported2(value, backgound);
    
    return value;
  } // finished createComposite16
  
  /**
   * Method createLabel5 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel5(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    value.setText("Transform Rules");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel5
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | A | d | d | i | t | i | o | n | a | l | D | o | c | u | m | e | n | t | 2 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonAdditionalDocument2 initalizes AdditionalDocument2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pAdditionalDocument2 AdditionalDocument2 of type Button
   **/
  protected void initButtonAdditionalDocument2(Button pAdditionalDocument2) {}
  
  private Button _ButtonAdditionalDocument2 = null; // private member
  /**
   * Method getButton_ButtonAdditionalDocument2 returns the _ButtonAdditionalDocument2-object
   * 
   * @return _ButtonAdditionalDocument2-object of type Button
   **/
  public Button getButtonAdditionalDocument2() {
    return _ButtonAdditionalDocument2;
  }
  
  /**
   * Method createButtonAdditionalDocument2 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonAdditionalDocument2(Composite parent, Color backgound) {
    _ButtonAdditionalDocument2 = new Button(parent, SWT.NONE);
    _ButtonAdditionalDocument2.setText("Additional Document");
    _ButtonAdditionalDocument2.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonAdditionalDocument2(_ButtonAdditionalDocument2);
    
    // add a Selection-Listener for this click
    _ButtonAdditionalDocument2.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonAdditionalDocument2(_ButtonAdditionalDocument2);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonAdditionalDocument2;
  } // finished createButtonAdditionalDocument2
  protected abstract void clickButtonAdditionalDocument2(Button pButtonAdditionalDocument2);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | N | o | t | S | u | p | p | o | r | t | e | d | 2 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initButtonNotSupported2 initalizes NotSupported2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pNotSupported2 NotSupported2 of type Button
   **/
  protected void initButtonNotSupported2(Button pNotSupported2) {}
  
  private Button _ButtonNotSupported2 = null; // private member
  /**
   * Method getButton_ButtonNotSupported2 returns the _ButtonNotSupported2-object
   * 
   * @return _ButtonNotSupported2-object of type Button
   **/
  public Button getButtonNotSupported2() {
    return _ButtonNotSupported2;
  }
  
  /**
   * Method createButtonNotSupported2 creates a Button and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Button-object
   **/
  private Button createButtonNotSupported2(Composite parent, Color backgound) {
    _ButtonNotSupported2 = new Button(parent, SWT.NONE);
    _ButtonNotSupported2.setText("not supported");
    _ButtonNotSupported2.setBackground(backgound);
    // call the Init method (which could be overridden
    initButtonNotSupported2(_ButtonNotSupported2);
    
    // add a Selection-Listener for this click
    _ButtonNotSupported2.addSelectionListener(new org.eclipse.swt.events.SelectionListener()
    {
       public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
       {
         clickButtonNotSupported2(_ButtonNotSupported2);
       }
       public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e)
       {
       }
    });
    
    return _ButtonNotSupported2;
  } // finished createButtonNotSupported2
  protected abstract void clickButtonNotSupported2(Button pButtonNotSupported2);
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  // | T | r | a | n | s | f | o | r | m | R | u | l | e | s | 2 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextTransformRules2 initalizes TransformRules2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pTransformRules2 TransformRules2 of type Text
   **/
  protected void initTextTransformRules2(Text pTransformRules2) {}
  
  private Text _TextTransformRules2 = null; // private member
  /**
   * Method getText_TextTransformRules2 returns the _TextTransformRules2-object
   * 
   * @return _TextTransformRules2-object of type Text
   **/
  public Text getTextTransformRules2() {
    return _TextTransformRules2;
  }
  
  /**
   * Method createTextTransformRules2 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextTransformRules2(Composite parent, Color backgound) {
    _TextTransformRules2 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextTransformRules2.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextTransformRules2(_TextTransformRules2);
    
    // add a Key-Listener for this keyPressed
    _TextTransformRules2.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextTransformRules2(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextTransformRules2.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextTransformRules2(_TextTransformRules2);
       }
    });
    
    return _TextTransformRules2;
  } // finished createTextTransformRules2
  protected void keyPressedTextTransformRules2(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextTransformRules2().selectAll();
   }
  }
  protected abstract void modifyTextTransformRules2(Text pTextTransformRules2);
  
  /**
   * Method createComposite17 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite17(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createLabel6(value, backgound);
    createTextProviderRef2(value, backgound);
    
    return value;
  } // finished createComposite17
  
  /**
   * Method createLabel6 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel6(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Provider Ref");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel6
  
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  // | P | r | o | v | i | d | e | r | R | e | f | 2 |
  // +---+---+---+---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextProviderRef2 initalizes ProviderRef2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pProviderRef2 ProviderRef2 of type Text
   **/
  protected void initTextProviderRef2(Text pProviderRef2) {}
  
  private Text _TextProviderRef2 = null; // private member
  /**
   * Method getText_TextProviderRef2 returns the _TextProviderRef2-object
   * 
   * @return _TextProviderRef2-object of type Text
   **/
  public Text getTextProviderRef2() {
    return _TextProviderRef2;
  }
  
  /**
   * Method createTextProviderRef2 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextProviderRef2(Composite parent, Color backgound) {
    _TextProviderRef2 = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.minimumHeight = 60;
    _TextProviderRef2.setLayoutData(gridData);
    // call the Init method (which could be overridden
    initTextProviderRef2(_TextProviderRef2);
    
    // add a Key-Listener for this keyPressed
    _TextProviderRef2.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextProviderRef2(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextProviderRef2.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextProviderRef2(_TextProviderRef2);
       }
    });
    
    return _TextProviderRef2;
  } // finished createTextProviderRef2
  protected void keyPressedTextProviderRef2(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextProviderRef2().selectAll();
   }
  }
  protected abstract void modifyTextProviderRef2(Text pTextProviderRef2);
  
  /**
   * Method createComposite18 creates a Composite and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Composite-object
   **/
  private Composite createComposite18(Composite parent, Color backgound) {
    Composite value = new Composite(parent, SWT.SHADOW_ETCHED_IN | SWT.BORDER);
    // create a GridLayout for the Composite
    GridLayout gridLayout = new GridLayout();
    value.setLayout(gridLayout);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    // reset Background
    backgound = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND);
    value.setBackground(backgound);
    createLabel7(value, backgound);
    createTextDateUser2(value, backgound);
    
    return value;
  } // finished createComposite18
  
  /**
   * Method createLabel7 creates a Label and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Label-object
   **/
  private Label createLabel7(Composite parent, Color backgound) {
    Label value = new Label(parent, SWT.NONE);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    value.setLayoutData(gridData);
    value.setText("Date (User)");
    value.setBackground(backgound);
    
    return value;
  } // finished createLabel7
  
  // +---+---+---+---+---+---+---+---+---+
  // | D | a | t | e | U | s | e | r | 2 |
  // +---+---+---+---+---+---+---+---+---+
  
  /**
   * Method initTextDateUser2 initalizes DateUser2.
   * This method may/should be overwritten if neccessary.
   * 
   * @param pDateUser2 DateUser2 of type Text
   **/
  protected void initTextDateUser2(Text pDateUser2) {}
  
  private Text _TextDateUser2 = null; // private member
  /**
   * Method getText_TextDateUser2 returns the _TextDateUser2-object
   * 
   * @return _TextDateUser2-object of type Text
   **/
  public Text getTextDateUser2() {
    return _TextDateUser2;
  }
  
  /**
   * Method createTextDateUser2 creates a Text and adds this
   * to the parent-object
   * 
   * @param parent The parent Composite
   * @param backgound The default background-color
   * @return The created Text-object
   **/
  private Text createTextDateUser2(Composite parent, Color backgound) {
    _TextDateUser2 = new Text(parent, SWT.BORDER);
    // create GridData which will be used for the layout
    GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessVerticalSpace = false;
    gridData.verticalAlignment = GridData.BEGINNING;
    _TextDateUser2.setLayoutData(gridData);
    _TextDateUser2.setEnabled(false);
    // call the Init method (which could be overridden
    initTextDateUser2(_TextDateUser2);
    
    // add a Key-Listener for this keyPressed
    _TextDateUser2.addKeyListener(new org.eclipse.swt.events.KeyListener()
    {
       public void keyPressed(org.eclipse.swt.events.KeyEvent e)
       {
         keyPressedTextDateUser2(e);
       }
       public void keyReleased(org.eclipse.swt.events.KeyEvent e)
       {
       }
    });
    
    // add a Modify-Listener for this modify
    _TextDateUser2.addModifyListener(new org.eclipse.swt.events.ModifyListener()
    {
       public void modifyText(org.eclipse.swt.events.ModifyEvent e)
       {
         modifyTextDateUser2(_TextDateUser2);
       }
    });
    
    return _TextDateUser2;
  } // finished createTextDateUser2
  protected void keyPressedTextDateUser2(org.eclipse.swt.events.KeyEvent e) {
   if ((e.stateMask == SWT.CTRL) && e.keyCode == 'a') {
      getTextDateUser2().selectAll();
   }
  }
  protected abstract void modifyTextDateUser2(Text pTextDateUser2);
}
