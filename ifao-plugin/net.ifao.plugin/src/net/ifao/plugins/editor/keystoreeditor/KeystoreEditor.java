package net.ifao.plugins.editor.keystoreeditor;


import java.io.*;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.*;
import org.eclipse.jface.window.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;


/**
 * Class KeystoreEditor
 *
 * <p>
 * Copyright &copy; 2006, i:FAO Group GmbH
 * @author kaufmann
 */
public class KeystoreEditor
   extends EditorPart
{

   public static final String ID = "net.ifao.plugins.editor.keystoreeditor.KeystoreEditor"; // Needs to be whatever is mentioned in plugin.xml
   private Composite top = null;
   private Label keys_label = null;
   private List keys_list = null;
   private Label certificates_label = null;
   private List certificates_list = null;
   private Composite actionButtons_composite = null;
   private Button add_button = null;
   private Button delete_button = null;
   private Label lists_separatorlabel = null;
   private Label verticalseparatorlabel = null;

   private KeyStore _keystore = null; //  @jve:decl-index=0:
   private KeyStore _truststore = null; //  @jve:decl-index=0:
   private String _keystoreFileName = null; //  @jve:decl-index=0:
   private String _truststoreFileName = null;
   private String _keystoreFullFileName = null; //  @jve:decl-index=0:
   private String _truststoreFullFileName = null;
   private boolean _bIsDirty = false;

   private static final char[] IFAOKEYSTOREPASSWORD = { 'a', 'r', 'c', 't', '1', 'c' };
   private static final char[] IFAOTRUSTSTOREPASSWORD = { '1', '1', '1', '1', '1', '1' };

   /**
    * Method doSave
    * overrides @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
    *
    * @param pMonitor
    *
    * @author kaufmann
    */
   @Override
   public void doSave(IProgressMonitor pMonitor)
   {
      try {
         FileOutputStream out = new FileOutputStream(_keystoreFullFileName, false);


         char[] password = null;
         if (_keystoreFileName.toLowerCase().equals("ifaokey.jks")) {
            password = IFAOKEYSTOREPASSWORD;
         }

         _keystore.store(out, password);
         out.close();

         if (!_keystoreFileName.equals(_truststoreFileName)) {
            out = new FileOutputStream(_truststoreFullFileName, false);

            password = null;
            if (_truststoreFileName.toLowerCase().equals("ifaotrust.jks")) {
               password = IFAOTRUSTSTOREPASSWORD;
            }

            _truststore.store(out, password);
            out.close();
         }
         _bIsDirty = false;
         firePropertyChange(PROP_DIRTY);
         setContentDescription("");
      }
      catch (FileNotFoundException pException) {
         setContentDescription("Keystore could not be saved: " + pException.getMessage());
         showExecption(pException);
      }
      catch (KeyStoreException pException) {
         setContentDescription("Keystore could not be saved: " + pException.getMessage());
         showExecption(pException);
      }
      catch (NoSuchAlgorithmException pException) {
         setContentDescription("Keystore could not be saved: " + pException.getMessage());
         showExecption(pException);
      }
      catch (CertificateException pException) {
         setContentDescription("Keystore could not be saved: " + pException.getMessage());
         showExecption(pException);
      }
      catch (IOException pException) {
         setContentDescription("Keystore could not be saved: " + pException.getMessage());
         showExecption(pException);
      }

   }


   /**
    * Method doSaveAs
    * overrides @see org.eclipse.ui.part.EditorPart#doSaveAs()
    * not supported
    *
    * @author kaufmann
    */
   @Override
   public void doSaveAs()
   {}

   /**
    * Method init
    * overrides @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
    *
    * @param pSite
    * @param pInput
    * @throws PartInitException
    *
    * @author kaufmann
    */
   @Override
   public void init(IEditorSite pSite, IEditorInput pInput)
      throws PartInitException
   {
      if (!(pInput instanceof IFileEditorInput))
         throw new PartInitException("Invalid Input: Must be IFileEditorInput");
      setSite(pSite);
      setInput(pInput);
      readKeystores((IFileEditorInput) pInput);
   }

   /**
    * Method readKeystores
    *
    *
    * @author kaufmann
    * @param pInput 
    */
   private void readKeystores(IFileEditorInput pInput)
   {
      String sFileName = pInput.getFile().getName();
      if (sFileName.toLowerCase().endsWith("key.jks")) {
         _keystoreFileName = sFileName;
         _keystoreFullFileName = pInput.getFile().getLocation().toOSString();
         _truststoreFileName = _keystoreFileName.replaceAll("(?i)key.jks$", "Trust.jks");
         _truststoreFullFileName = _keystoreFullFileName.replaceAll("(?i)key.jks$", "Trust.jks");
         if (!(new File(_truststoreFullFileName)).exists()) {
            _truststoreFileName = _keystoreFileName;
            _truststoreFullFileName = _keystoreFullFileName;
         }
      } else if (sFileName.toLowerCase().endsWith("trust.jks")) {
         _truststoreFileName = sFileName;
         _truststoreFullFileName = pInput.getFile().getLocation().toOSString();
         _keystoreFileName = _truststoreFileName.replaceAll("(?i)trust.jks$", "Key.jks");
         _keystoreFullFileName = _truststoreFullFileName.replaceAll("(?i)trust.jks$", "Key.jks");
         if (!(new File(_keystoreFullFileName)).exists()) {
            _keystoreFileName = _truststoreFileName;
            _keystoreFullFileName = _truststoreFullFileName;
         }
      }

      try {
         FileInputStream keystoreInputStream = new FileInputStream(_keystoreFullFileName);

         _keystore = KeyStore.getInstance("JKS");
         char[] passwordKeystore = null;

         if (_keystoreFileName.toLowerCase().equals("ifaokey.jks")) {
            passwordKeystore = IFAOKEYSTOREPASSWORD;
         }

         _keystore.load(keystoreInputStream, passwordKeystore);
         keystoreInputStream.close();

         if (!_keystoreFileName.equals(_truststoreFileName)) {
            FileInputStream truststoreInputStream = new FileInputStream(_truststoreFullFileName);

            _truststore = KeyStore.getInstance("JKS");
            char[] passwordTruststore = null;

            if (_truststoreFileName.toLowerCase().equals("ifaotrust.jks")) {
               passwordTruststore = IFAOTRUSTSTOREPASSWORD;
            }

            _truststore.load(truststoreInputStream, passwordTruststore);
            truststoreInputStream.close();
         } else {
            _truststore = _keystore;
         }
         setContentDescription("");
      }
      catch (FileNotFoundException pException) {
         setContentDescription("Keystore could not be read: " + pException.getMessage());
         showExecption(pException);
      }
      catch (KeyStoreException pException) {
         setContentDescription("Keystore could not be read: " + pException.getMessage());
         showExecption(pException);
      }
      catch (NoSuchAlgorithmException pException) {
         setContentDescription("Keystore could not be read: " + pException.getMessage());
         showExecption(pException);
      }
      catch (CertificateException pException) {
         setContentDescription("Keystore could not be read: " + pException.getMessage());
         showExecption(pException);
      }
      catch (IOException pException) {
         setContentDescription("Keystore could not be read: " + pException.getMessage());
         showExecption(pException);
      }
   }

   /**
    * Method getTitleToolTip
    * overrides @see org.eclipse.ui.part.EditorPart#getTitleToolTip()
    *
    * @return
    *
    * @author kaufmann
    */
   @Override
   public String getTitleToolTip()
   {
      StringBuilder sbToolTip = new StringBuilder();
      if (_keystoreFileName != null) {
         sbToolTip.append(_keystoreFileName);
         if (_truststoreFileName != null) {
            if (!_keystoreFileName.equals(_truststoreFileName)) {
               sbToolTip.append(" / ").append(_truststoreFileName);
            }
         }
      }
      return sbToolTip.toString();
   }

   /**
    * Method isDirty
    * overrides @see org.eclipse.ui.part.EditorPart#isDirty()
    *
    * @return
    *
    * @author kaufmann
    */
   @Override
   public boolean isDirty()
   {
      return _bIsDirty;
   }

   /**
    * Method isSaveAsAllowed
    * overrides @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
    *
    * @return
    *
    * @author kaufmann
    */
   @Override
   public boolean isSaveAsAllowed()
   {
      return false;
   }

   /**
    * Method createPartControl
    * overrides @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    *
    * @param parent
    *
    * @author kaufmann
    */
   @Override
   public void createPartControl(Composite parent)
   {
      GridData gridData31 = new GridData();
      gridData31.horizontalAlignment = GridData.BEGINNING;
      gridData31.grabExcessVerticalSpace = false;
      gridData31.verticalSpan = 5;
      gridData31.verticalAlignment = GridData.FILL;
      GridData gridData21 = new GridData();
      gridData21.horizontalIndent = 3;
      GridData gridData11 = new GridData();
      gridData11.grabExcessHorizontalSpace = true;
      gridData11.verticalAlignment = GridData.FILL;
      gridData11.grabExcessVerticalSpace = true;
      gridData11.horizontalAlignment = GridData.FILL;
      GridData gridData1 = new GridData();
      gridData1.grabExcessVerticalSpace = false;
      gridData1.horizontalAlignment = GridData.BEGINNING;
      gridData1.verticalAlignment = GridData.END;
      gridData1.horizontalIndent = 3;
      gridData1.heightHint = -1;
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.verticalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.horizontalAlignment = GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      gridLayout.numColumns = 3;
      gridLayout.verticalSpacing = 5;
      gridLayout.horizontalSpacing = 5;
      top = new Composite(parent, SWT.NONE);
      top.setLayout(gridLayout);
      keys_label = new Label(top, SWT.NONE);
      keys_label.setText("Keys:");
      keys_label.setLayoutData(gridData21);
      verticalseparatorlabel = new Label(top, SWT.VERTICAL | SWT.SEPARATOR | SWT.SHADOW_IN);
      verticalseparatorlabel.setText("");
      verticalseparatorlabel.setLayoutData(gridData31);
      createActionButtons_composite();
      keys_list = new List(top, SWT.H_SCROLL | SWT.V_SCROLL);
      keys_list.setLayoutData(gridData);
      keys_list.addFocusListener(new org.eclipse.swt.events.FocusAdapter()
      {
         @Override
         public void focusGained(org.eclipse.swt.events.FocusEvent e)
         {
            certificates_list.deselectAll();
         }
      });
      lists_separatorlabel = new Label(top, SWT.HORIZONTAL | SWT.SEPARATOR | SWT.SHADOW_NONE);
      lists_separatorlabel.setText("");
      certificates_label = new Label(top, SWT.NONE);
      certificates_label.setText("Certificates:");
      certificates_label.setLayoutData(gridData1);
      certificates_list = new List(top, SWT.H_SCROLL | SWT.V_SCROLL);
      certificates_list.setLayoutData(gridData11);
      certificates_list.addFocusListener(new org.eclipse.swt.events.FocusAdapter()
      {
         @Override
         public void focusGained(org.eclipse.swt.events.FocusEvent e)
         {
            keys_list.deselectAll();
         }
      });

      if (_keystore != null) {
         updateKeyList();
      }
      if (_truststore != null) {
         updateTrustList();
      }
   }

   /**
    * Method updateTrustList
    *
    *
    * @author kaufmann
    */
   private void updateTrustList()
   {
      certificates_list.removeAll();
      java.util.List<String> sortedList = new ArrayList<String>();

      try {
         Enumeration<String> aliases = _truststore.aliases();
         while (aliases.hasMoreElements()) {
            String sAlias = aliases.nextElement();
            if (_truststore.isCertificateEntry(sAlias)) {
               String sStringToAdd = sAlias;
               Certificate certificate = _truststore.getCertificate(sAlias);
               if (certificate instanceof X509Certificate) {
                  X509Certificate x509Cert = (X509Certificate) certificate;
                  sStringToAdd +=
                     "   (" + new SimpleDateFormat("yyyy-MM-dd").format(x509Cert.getNotBefore())
                           + " - "
                           + new SimpleDateFormat("yyyy-MM-dd").format(x509Cert.getNotAfter())
                           + ")";
               }

               sortedList.add(sStringToAdd);
            }
         }
         if (sortedList.size() > 0) {
            Collections.sort(sortedList);
            for (String sAlias : sortedList) {
               certificates_list.add(sAlias);
            }
         }
      }
      catch (KeyStoreException pException) {
         showExecption(pException);
      }
   }


   /**
    * Method updateKeyList
    *
    *
    * @author kaufmann
    */
   private void updateKeyList()
   {
      keys_list.removeAll();
      java.util.List<String> sortedList = new ArrayList<String>();

      try {
         Enumeration<String> aliases = _keystore.aliases();
         while (aliases.hasMoreElements()) {
            String sAlias = aliases.nextElement();
            if (_keystore.isKeyEntry(sAlias)) {
               String sStringToAdd = sAlias;
               Certificate certificate = _keystore.getCertificate(sAlias);
               if (certificate instanceof X509Certificate) {
                  X509Certificate x509Cert = (X509Certificate) certificate;
                  sStringToAdd +=
                     "   (" + new SimpleDateFormat("yyyy-MM-dd").format(x509Cert.getNotBefore())
                           + " - "
                           + new SimpleDateFormat("yyyy-MM-dd").format(x509Cert.getNotAfter())
                           + ")";
               }
               sortedList.add(sStringToAdd);
            }
         }
         if (sortedList.size() > 0) {
            Collections.sort(sortedList);
            for (String sAlias : sortedList) {
               keys_list.add(sAlias);
            }
         }
      }
      catch (KeyStoreException pException) {
         showExecption(pException);
      }
   }


   /**
    * Method setFocus
    * overrides @see org.eclipse.ui.part.WorkbenchPart#setFocus()
    *
    *
    * @author kaufmann
    */
   @Override
   public void setFocus()
   {}

   /**
    * This method initializes ActionButtons_composite	
    *
    */
   private void createActionButtons_composite()
   {
      GridData gridData5 = new GridData();
      gridData5.horizontalAlignment = GridData.FILL;
      gridData5.verticalAlignment = GridData.CENTER;
      GridData gridData4 = new GridData();
      gridData4.heightHint = -1;
      gridData4.horizontalAlignment = GridData.FILL;
      gridData4.verticalAlignment = GridData.CENTER;
      gridData4.grabExcessVerticalSpace = false;
      GridLayout gridLayout1 = new GridLayout();
      gridLayout1.verticalSpacing = 5;
      gridLayout1.marginHeight = 5;
      GridData gridData2 = new GridData();
      gridData2.horizontalAlignment = GridData.BEGINNING;
      gridData2.verticalAlignment = GridData.FILL;
      gridData2.horizontalIndent = 5;
      gridData2.verticalSpan = 5;
      gridData2.grabExcessVerticalSpace = false;
      actionButtons_composite = new Composite(top, SWT.NONE);
      actionButtons_composite.setLayoutData(gridData2);
      actionButtons_composite.setLayout(gridLayout1);
      add_button = new Button(actionButtons_composite, SWT.NONE);
      add_button.setText("Add...");
      add_button.setLayoutData(gridData5);
      add_button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            doAddKeyOrCertificate();
         }
      });
      delete_button = new Button(actionButtons_composite, SWT.NONE);
      delete_button.setText("Delete...");
      delete_button.setLayoutData(gridData4);
      delete_button.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            doDeleteAlias();
         }
      });
   }


   /**
    * Method doAddKeyOrCertificate
    *
    *
    * @author kaufmann
    */
   private void doAddKeyOrCertificate()
   {
      FileDialog addDialog = new FileDialog(add_button.getShell(), SWT.MULTI);
      addDialog.setText("Add key or certificate");
      addDialog.setFilterExtensions(new String[]{ "*.p12;*.cer;*.pem;*.crt", "*.p12", "*.cer;*.pem;*.crt",
            "*.*" });
      addDialog.setFilterNames(new String[]{ "Keys and Certificates (*.p12, *.cer, *.pem, *.crt)",
            "Keys (*.p12)", "Certificates (*.cer, *.pem, *.crt)", "All files (*.*)" });
      addDialog.setFilterPath(((IFileEditorInput) getEditorInput()).getFile().getLocation()
            .toOSString());
      addDialog.open();
      String[] fileNames = addDialog.getFileNames();
      if (fileNames.length > 0) {
         String sPath = addDialog.getFilterPath() + "\\";
         for (String sFile : fileNames) {
            if (sFile.toLowerCase().endsWith(".p12")) {
               importKey(sPath + sFile);
            } else {
               importTrust(sPath, sFile);
            }
         }
      }
   }


   /**
    * Method importTrust
    *
    * @param psPath, String psFile
    *
    * @author kaufmann
    */
   private void importTrust(String psPath, String psFile)
   {
      String sAlias = psFile.replaceAll("(?i).(cer|pem|crt)$", "");
      System.out.println("\nalias: " + sAlias);
      boolean bAdd = true;
      try {
         FileInputStream in = new FileInputStream(psPath + psFile);
         if (_truststore.containsAlias(sAlias)) {
            bAdd = allowOverwrite(sAlias, false, _truststore);
         }
         if (bAdd) {
            X509Certificate x509certificate =
               (X509Certificate) CertificateFactory.getInstance("X509").generateCertificate(in);
            if (x509certificate.getSubjectDN().equals(x509certificate.getIssuerDN())) {
               x509certificate.verify(x509certificate.getPublicKey());
            }
            _truststore.setCertificateEntry(sAlias, x509certificate);
            _bIsDirty = true;
            firePropertyChange(PROP_DIRTY);
            updateTrustList();
         }
      }
      catch (InvalidKeyException pException) {
        showExecption(pException);
      }
      catch (FileNotFoundException pException) {
         showExecption(pException);
      }
      catch (KeyStoreException pException) {
         showExecption(pException);
      }
      catch (CertificateException pException) {
         showExecption(pException);
      }
      catch (NoSuchAlgorithmException pException) {
         showExecption(pException);
      }
      catch (NoSuchProviderException pException) {
         showExecption(pException);
      }
      catch (SignatureException pException) {
         showExecption(pException);
      }
   }

   /**
    * Method importKey
    *
    * @param psFile
    *
    * @author kaufmann
    */
   private void importKey(String psFile)
   {
      try {
         FileInputStream in = new FileInputStream(psFile);
         KeyStore ksin = KeyStore.getInstance("PKCS12");
         // ask for the password
         InputDialog pwdInput =
            new InputDialog(getEditorSite().getShell(), "Password for key",
                  "Please enter the password for the key", null, null);
         int iReturnCode = pwdInput.open();
         char[] pwin = "nvs".toCharArray(); // default, if nothing entered or cancel has been pressed
         if (iReturnCode == Window.OK) {
            String sPwd = pwdInput.getValue();
            if (sPwd != null && sPwd.length() > 0) {
               pwin = sPwd.toCharArray();
            }
         }

         ksin.load(in, pwin);
         in.close();
         Enumeration en = ksin.aliases();

         while (en.hasMoreElements()) {
            boolean bAdd = true;
            String sAlias = (String) en.nextElement();
            boolean bIsKey = ksin.isKeyEntry(sAlias);
            KeyStore store = bIsKey ? _keystore : _truststore;

            if (store.containsAlias(sAlias)) {
               bAdd = allowOverwrite(sAlias, bIsKey, store);
            }
            if (bAdd) {
               if (bIsKey) {
                  System.out.println("importing key " + sAlias);
                  _keystore.setKeyEntry(sAlias, ksin.getKey(sAlias, pwin), IFAOKEYSTOREPASSWORD,
                        ksin.getCertificateChain(sAlias));
                  _bIsDirty = true;
                  firePropertyChange(PROP_DIRTY);
                  updateKeyList();
               } else {
                  System.out.println("importing certificate " + sAlias);
                  _truststore.setCertificateEntry(sAlias, ksin.getCertificate(sAlias));
                  _bIsDirty = true;
                  firePropertyChange(PROP_DIRTY);
                  updateTrustList();
               }
            }
         }
      }
      catch (KeyStoreException pException) {
         showExecption(pException);
      }
      catch (FileNotFoundException pException) {
         showExecption(pException);
      }
      catch (NoSuchAlgorithmException pException) {
         showExecption(pException);
      }
      catch (CertificateException pException) {
         showExecption(pException);
      }
      catch (IOException pException) {
         showExecption(pException);
      }
      catch (UnrecoverableKeyException pException) {
         showExecption(pException);
      }
   }

   /**
    * Shows the execption in a dialog box; the first 15 stacktrace elements will be shown.
    *
    * @param pException exception
    *
    * @author kaufmann
    */
   private void showExecption(Exception pException)
   {
      StackTraceElement[] stackTrace = pException.getStackTrace();
      StringBuilder message = new StringBuilder();
      message.append("The following Exception has happened:\n");
      message.append(pException.getClass().getName()).append(": ").append(pException.getMessage())
            .append("\n");
      for (int i = 0; i < stackTrace.length && i < 15; i++) {
         message.append("  at ").append(stackTrace[i].toString()).append("\n");
      }
      if (stackTrace.length > 15) {
         message.append("  ...\n");
      }
      // show the exception at the console, too
      pException.printStackTrace();
      // show the message box
      new MessageDialog(getEditorSite().getShell(), "ERROR", null, message.toString(),
            MessageDialog.ERROR, new String[]{ "OK" }, 0).open();
   }

   /**
    * Method allowOverwrite
    *
    * @param sAlias
    * @param bIsKey
    * @param store
    * @return
    * @throws KeyStoreException
    *
    * @author kaufmann
    */
   private boolean allowOverwrite(String sAlias, boolean bIsKey, KeyStore store)
      throws KeyStoreException
   {
      boolean bAdd = true;
      System.out.println("store already contains " + sAlias);
      MessageBox yesNo =
         new MessageBox(add_button.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
      yesNo.setText("Are you sure?");
      yesNo.setMessage((bIsKey ? "Key" : "Certificate") + " \"" + sAlias
            + "\" already exists.\nDo you really want to overwrite it?");
      if (yesNo.open() == SWT.YES) {
         store.deleteEntry(sAlias);
      } else {
         bAdd = false;
      }
      return bAdd;
   }


   /**
    * Method doDeleteAlias
    *
    *
    * @author kaufmann
    */
   private void doDeleteAlias()
   {
      String sAlias = null;
      boolean bIsKey = false;
      KeyStore store = null;
      if (keys_list.getSelectionCount() > 0) {
         sAlias = keys_list.getItem(keys_list.getSelectionIndex());
         bIsKey = true;
         store = _keystore;
      }
      if (certificates_list.getSelectionCount() > 0) {
         sAlias = certificates_list.getItem(certificates_list.getSelectionIndex());
         store = _truststore;
      }
      if (sAlias != null) {
         MessageBox yesNo =
            new MessageBox(delete_button.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
         yesNo.setText("Are you sure?");
         yesNo.setMessage("Do you really want to delete " + (bIsKey ? "key" : "certificate")
               + " \"" + sAlias + "\" from the keystore?");
         if (yesNo.open() == SWT.YES) {
            try {
               Pattern p =
                  Pattern
                        .compile("^(.*?) +\\([0-9]{4}-[0-9]{2}-[0-9]{2} - [0-9]{4}-[0-9]{2}-[0-9]{2}\\)$");
               Matcher m = p.matcher(sAlias);
               if (m.matches()) {
                  sAlias = m.group(1);
               }
               System.out.println(sAlias + " will be deleted");
               store.deleteEntry(sAlias);
               _bIsDirty = true;
               firePropertyChange(PROP_DIRTY);
            }
            catch (KeyStoreException pException) {
               showExecption(pException);
            }
            if (bIsKey) {
               updateKeyList();
            } else {
               updateTrustList();
            }
         }
      }
   }


} //  @jve:decl-index=0:visual-constraint="10,10,459,371"
