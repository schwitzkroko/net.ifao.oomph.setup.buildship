package dtdinfo.gui;


import ifaoplugin.Util;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import net.ifao.dialogs.swing.ConfimDialog;

import dtdinfo.DtdGenerator;


/**
 * Class DtdNewAgent
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DtdNewAgent
   extends JDialog
{
   /**
   * 
   */
   private static final long serialVersionUID = -3997672926198481372L;
   JPanel panel1 = new JPanel();
   BorderLayout borderLayout1 = new BorderLayout();
   JPanel jPanel1 = new JPanel();
   JPanel jPanel2 = new JPanel();
   JPanel jPanel3 = new JPanel();
   JButton jButtonCreate = new JButton();
   JButton jButtonExit = new JButton();
   BorderLayout borderLayout2 = new BorderLayout();
   JLabel jLabel1 = new JLabel();
   JComboBox jComboAgent = new JComboBox();
   JLabel jLabel2 = new JLabel();
   JComboBox jComboProvider = new JComboBox();
   TitledBorder titledBorder1;
   JLabel jLabel3 = new JLabel();
   JTextField jTextDir = new JTextField();
   private String _sBaseDir;

   private Frame _parentFrame;
   protected boolean created = false;

   /**
    * Constructor DtdNewAgent
    *
    * @param frame
    * @param psBaseDir
    */
   public DtdNewAgent(Frame frame, String psBaseDir)
   {
      super(frame, "New Agent", true);

      _parentFrame = frame;
      _sBaseDir = psBaseDir;

      if (!_sBaseDir.endsWith("\\")) {
         _sBaseDir += "\\";
      }

      init();

      try {
         jbInit();
         pack();
      }
      catch (Exception ex) {
         ex.printStackTrace();
      }

      if (frame != null) {
         Dimension screenSize = frame.getSize();
         Dimension frameSize = getSize();

         setLocation(frame.getX() + (screenSize.width - frameSize.width) / 2, frame.getY()
               + (screenSize.height - frameSize.height) / 2);
      }
   }

   /**
    * Method main
    *
    * @param args
    * @author Andreas Brod
    */
   public static void main(String[] args)
   {
      DtdNewAgent dtdNewAgent = new DtdNewAgent();

      dtdNewAgent.setVisible(true);
   }

   /**
    * Constructor DtdNewAgent
    */
   public DtdNewAgent()
   {
      this(null, "..\\..\\..\\");
   }

   /**
    * Method jbInit
    *
    * @throws Exception
    * @author Andreas Brod
    */
   private void jbInit()
      throws Exception
   {
      titledBorder1 = new TitledBorder("");

      panel1.setLayout(borderLayout1);
      jButtonCreate.setText("Create");
      jButtonCreate.addActionListener(new DtdNewAgent_jButtonCreate_actionAdapter(this));
      jButtonExit.setText("Exit");
      jButtonExit.addActionListener(new DtdNewAgent_jButtonExit_actionAdapter(this));
      jPanel2.setLayout(borderLayout2);
      jPanel1.setLayout(borderLayout5);
      jLabel1.setAlignmentX((float) 0.0);
      jLabel1.setAlignmentY((float) 0.5);
      jLabel1.setBorder(null);
      jLabel1.setHorizontalAlignment(SwingConstants.LEADING);
      jLabel1.setHorizontalTextPosition(SwingConstants.TRAILING);
      jLabel1.setText("Agent");
      jLabel1.setVerticalTextPosition(SwingConstants.CENTER);
      jLabel2.setText("Provider");
      jPanel1.setAlignmentX((float) 0.5);
      jPanel1.setAlignmentY((float) 0.5);
      jPanel1.setBorder(BorderFactory.createLoweredBevelBorder());
      jPanel1.setDebugGraphicsOptions(0);
      jLabel3.setText("Directory");
      jPanel5.setLayout(borderLayout3);
      jPanel4.setLayout(borderLayout4);
      borderLayout4.setHgap(5);
      borderLayout4.setVgap(5);
      borderLayout3.setHgap(5);
      borderLayout3.setVgap(5);
      borderLayout5.setHgap(5);
      borderLayout5.setVgap(5);
      borderLayout1.setHgap(5);
      borderLayout1.setVgap(5);
      jComboProvider.addActionListener(new DtdNewAgent_jComboProvider_actionAdapter(this));
      getContentPane().add(panel1);
      panel1.add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(jPanel4, BorderLayout.WEST);
      jPanel4.add(jLabel1, BorderLayout.NORTH);
      jPanel4.add(jLabel2, BorderLayout.CENTER);
      jPanel4.add(jLabel3, BorderLayout.SOUTH);
      jPanel1.add(jPanel5, BorderLayout.CENTER);
      jPanel5.add(jComboAgent, BorderLayout.NORTH);
      jPanel5.add(jTextDir, BorderLayout.SOUTH);
      jPanel5.add(jComboProvider, BorderLayout.CENTER);
      panel1.add(jPanel2, BorderLayout.SOUTH);
      jPanel2.add(jPanel3, BorderLayout.EAST);
      jPanel3.add(jButtonCreate, null);
      jPanel3.add(jButtonExit, null);
   }

   Hashtable<String, String> htProvider = new Hashtable<String, String>();
   JPanel jPanel4 = new JPanel();
   JPanel jPanel5 = new JPanel();
   BorderLayout borderLayout3 = new BorderLayout();
   BorderLayout borderLayout4 = new BorderLayout();
   BorderLayout borderLayout5 = new BorderLayout();

   /**
    * Method init
    *
    * @author Andreas Brod
    */
   private void init()
   {
      Vector<String> lstAgent = new Vector<String>();
      Vector<String> lstPro = new Vector<String>();

      if (_sBaseDir.length() > 0) {


         try {
            String sText = Util.loadFromFile(Util.getConfFile(_sBaseDir, "Agents.dtd"));
            String sTextXml = Util.loadFromFile(Util.getConfFile(_sBaseDir, "Agents.xml"));

            sText = sText.substring(sText.indexOf("ENTITY % EnumProviderType"));
            sText = sText.substring(sText.indexOf("("), sText.indexOf(">"));

            StringTokenizer st = new StringTokenizer(sText, " \"|<>()");

            while (st.hasMoreTokens()) {
               String sPro = st.nextToken();

               lstPro.add(sPro);

               String sDir = "comarctic";
               int iPro = sTextXml.indexOf("providerType=\"" + sPro + "\"");

               while ((iPro > 0) && (sDir.startsWith("comarctic"))) {
                  sDir =
                     sTextXml.substring(
                           sTextXml.lastIndexOf("net.ifao.arctic.agents.", iPro) + 16 + 7, iPro);
                  int iPro2 = sDir.lastIndexOf(".");
                  if (iPro2 > 0) {
                     sDir = sDir.substring(0, iPro2);
                     iPro = sTextXml.indexOf("providerType=\"" + sPro + "\"", iPro + 20);
                  } else {
                     iPro = -1;
                  }
               }

               if (sDir.startsWith("comarctic")) {
                  sDir = Util.replaceString(sPro.toLowerCase(), "_", ".");
               }

               if (sDir.indexOf(".") < 0) {
                  sDir += ".xml";
               }

               sDir = Util.replaceString(sDir, ".", "\\");

               htProvider.put(sPro, sDir);
            }

            // load Request
            sText = Util.loadFromFile(Util.getConfFile(_sBaseDir, "ArcticRequest.dtd"));
            sText = sText.substring(sText.indexOf("!ELEMENT Request"));
            sText = sText.substring(sText.indexOf("("), sText.indexOf(","));
            st = new StringTokenizer(sText, " \"|<>(),");

            while (st.hasMoreTokens()) {
               String sPro = st.nextToken();

               if (sTextXml.indexOf("requestType=\"" + sPro + "\"") < 0) {
                  sPro += "*";
               }

               lstAgent.add(sPro);
            }

         }
         catch (Exception ex) {
            ex.printStackTrace();
         }
      }

      set(jComboAgent, lstAgent);
      set(jComboProvider, lstPro);
      jComboProvider_actionPerformed(null);
   }

   /**
    * Method set
    *
    * @param box
    * @param list
    * @author Andreas Brod
    */
   private void set(JComboBox box, Vector<String> list)
   {
      box.removeAllItems();

      Object[] objs = list.toArray();

      Arrays.sort(objs);

      for (Object obj : objs) {
         box.addItem(obj);
      }

      if (objs.length > 0) {
         box.setSelectedIndex(0);
      }
   }

   /**
    * Method jButtonExit_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonCreate_actionPerformed(ActionEvent e)
   {
      try {
         String sReq = jComboAgent.getSelectedItem().toString();

         while (sReq.endsWith("*")) {
            sReq = sReq.substring(0, sReq.length() - 1);
         }

         String sREQ = DtdGenerator.getCamelCaseName(sReq);
         String sPackage =
            Util.replaceString("net\\ifao\\arctic\\agents\\" + jTextDir.getText(), "\\", ".");

         String sFileName =
            _sBaseDir + "src\\net\\ifao\\arctic\\agents\\" + jTextDir.getText() + "\\" + sReq
                  + ".java";
         String sInfo = Util.loadFromFile(sFileName);

         if (sInfo.length() > 0) {
            if (ConfimDialog.getBoolean(_parentFrame, "Do you want to overwrite "
                  + "existing agent " + jTextDir.getText() + "\\" + sReq + ".java")) {
               sInfo = "";
            }
         }

         if (sInfo.length() == 0) {
            String sText = "package " + sPackage + ";\n";

            sText += "\n";
            sText += "import " + sPackage + ".framework.*;\n";
            sText +=
               "import net.ifao.arctic.xml.arcticpnrelementinfos.types.EnumTransformActionType;\n";
            sText += "import net.ifao.arctic.xml.response.types.ResEnumErrorComponent;\n";
            sText += "\n";
            sText += "/**\n";
            sText += " * Class " + sReq + "\n";
            sText += " *\n";
            sText += " * <p>\n";
            sText += " * Copyright &copy; 2002, i:FAO\n";
            sText += " * @author _GENERATOR_\n";
            sText += " */\n";
            sText += "public class " + sReq + "\n";
            sText +=
               "    extends "
                     + DtdGenerator.getUnFormatedProvider(jComboProvider.getSelectedItem()
                           .toString()) + "AgentFramework\n";
            sText += "{\n";
            sText += "\n";
            sText += "    /**\n";
            sText +=
               "     * Method getErrorComponent has to be overwritten by the derived agent.\n";
            sText += "     * It will return the error component for exceptions\n";
            sText += "     *\n";
            sText += "     * @return one value of enumeration ResEnumErrorComponent\n";
            sText += "     * @author _GENERATOR_\n";
            sText += "     */\n";
            sText += "    @Override\n";
            sText += "    public ResEnumErrorComponent getErrorComponent()\n";
            sText += "    {\n";
            sText += "        return ResEnumErrorComponent.AGENT_" + sREQ + ";\n";
            sText += "    }\n";
            sText += "\n";
            sText += "    /**\n";
            sText += "     * Method getActionType has to be overwritten by the derived agent.\n";
            sText += "     * It will return the action type needed.\n";
            sText += "     *\n";
            sText += "     * @return one value of enumeration EnumTransformActionType\n";
            sText += "     * @author _GENERATOR_\n";
            sText += "     */\n";
            sText += "    @Override\n";
            sText += "    public EnumTransformActionType getActionType()\n";
            sText += "    {\n";
            sText += "        return EnumTransformActionType." + sREQ + ";\n";
            sText += "    }\n";
            sText += "\n";
            sText += "}\n";

            Util.writeToFile(sFileName, sText);

            updateAgentsXml(jComboProvider.getSelectedItem().toString(), sReq, sPackage);

            created = true;

            setVisible(false);
         }
      }
      catch (Exception ex) {}
   }

   /**
    * Method updateAgentsXml
    *
    * @param sProvider
    * @param sAgent
    * @param sPackage
    * @author Andreas Brod
    */
   private void updateAgentsXml(String sProvider, String sAgent, String sPackage)
   {
      String sAgents = Util.loadFromFile(Util.getConfFile(_sBaseDir, "Agents.xml"));

      if (sAgents.length() > 0) {
         int iPos = sAgents.indexOf("<AgentGroup requestType=\"" + sAgent + "\"");

         if (iPos < 0) {
            iPos = sAgents.lastIndexOf("</AgentGroup>");

            if ((iPos > 0)
                  && ConfimDialog.getBoolean(_parentFrame,
                        "Do you want to create a AgentGroup for " + sProvider
                              + " within Agents.xml")) {
               String sText = "</AgentGroup>\n";

               sText += "          <AgentGroup requestType=\"SystemStatus\">\n";
               sText +=
                  "                  <Agent className=\"" + sPackage + "." + sAgent
                        + "\" providerType=\"" + sProvider + "\"/>\n";
               sText += "          ";

               sAgents = sAgents.substring(0, iPos) + sText + sAgents.substring(iPos);

               Util.writeToFile(Util.getConfFile(_sBaseDir, "Agents.xml").getAbsolutePath(),
                     sAgents);
            }
         } else {
            String sOrig = sAgents.substring(iPos, sAgents.indexOf("</AgentGroup>", iPos));

            iPos = sOrig.indexOf("providerType=\"" + sProvider + "\"");

            if ((iPos < 0)
                  && ConfimDialog.getBoolean(_parentFrame, "Do you want to add a Agent-Entry for "
                        + sAgent + "." + sProvider + " within Agents.xml")) {
               String sNew =
                  sOrig + "       <Agent className=\"" + sPackage + "." + sAgent
                        + "\" providerType=\"" + sProvider + "\"/>\n"
                        + "               </AgentGroup>";

               sOrig += "</AgentGroup>";

               Util.writeToFile(Util.getConfFile(_sBaseDir, "Agents.xml").getAbsolutePath(),
                     Util.replaceString(sAgents, sOrig, sNew));
            } else if (ConfimDialog.getBoolean(_parentFrame,
                  "Do you want to replace the Agent-Entry for " + sAgent + "." + sProvider
                        + " within Agents.xml")) {
               int iStart = sOrig.lastIndexOf("<", iPos);
               int iEnd = sOrig.indexOf(">", iPos) + 1;
               String sNew =
                  sOrig.substring(0, iStart) + "<Agent className=\"" + sPackage + "." + sAgent
                        + "\" providerType=\"" + sProvider + "\"/>" + sOrig.substring(iEnd);

               Util.writeToFile(Util.getConfFile(_sBaseDir, "Agents.xml").getAbsolutePath(),
                     Util.replaceString(sAgents, sOrig, sNew));

            }
         }
      }
   }

   /**
    * Method jComboProvider_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jComboProvider_actionPerformed(ActionEvent e)
   {
      String sText = htProvider.get(jComboProvider.getSelectedItem());

      if (sText != null) {
         jTextDir.setText(sText);
      }
   }

   /**
    * Method jButtonExit_actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   void jButtonExit_actionPerformed(ActionEvent e)
   {

      setVisible(false);
   }
}


/**
 * Class DtdNewAgent_jButtonExit_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdNewAgent_jButtonExit_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdNewAgent adaptee;

   /**
    * Constructor DtdNewAgent_jButtonExit_actionAdapter
    *
    * @param adaptee
    */
   DtdNewAgent_jButtonExit_actionAdapter(DtdNewAgent adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonExit_actionPerformed(e);
   }
}


/**
 * Class DtdNewAgent_jComboProvider_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdNewAgent_jComboProvider_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdNewAgent adaptee;

   /**
    * Constructor DtdNewAgent_jComboProvider_actionAdapter
    *
    * @param adaptee
    */
   DtdNewAgent_jComboProvider_actionAdapter(DtdNewAgent adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jComboProvider_actionPerformed(e);
   }
}


/**
 * Class DtdNewAgent_jButtonCreate_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class DtdNewAgent_jButtonCreate_actionAdapter
   implements java.awt.event.ActionListener
{
   DtdNewAgent adaptee;

   /**
    * Constructor DtdNewAgent_jButtonCreate_actionAdapter
    *
    * @param adaptee
    */
   DtdNewAgent_jButtonCreate_actionAdapter(DtdNewAgent adaptee)
   {
      this.adaptee = adaptee;
   }

   /**
    * Method actionPerformed
    *
    * @param e
    * @author Andreas Brod
    */
   @Override
   public void actionPerformed(ActionEvent e)
   {
      adaptee.jButtonCreate_actionPerformed(e);
   }
}
