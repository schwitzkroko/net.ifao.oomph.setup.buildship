package schemagenerator.gui;


import ifaoplugin.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.regex.Pattern;
import java.util.zip.*;

import net.ifao.xml.XmlObject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import schemagenerator.Generator;
import schemagenerator.correctors.AmadeusWsdlZipCorrector;


public class SwtAmadeusWsdlImport
   extends SwtBase
{

   private Text textAreaSabre = null;
   Table table = null;
   private Button btnImportSchemasFrom;
   HashSet<String> hsSelected = new HashSet<>();
   File amadeusWSZip = null;
   File importZipFile = null;

   public SwtAmadeusWsdlImport(Composite parent, int style)
   {
      super(parent, style);
      initialize();
   }

   private void initialize()
   {
      GridData gridData = new GridData();
      gridData.grabExcessHorizontalSpace = true;
      gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      GridLayout gridLayout = new GridLayout();
      this.setLayout(gridLayout);
      setSize(new Point(461, 327));
      textAreaSabre = new Text(this, SWT.MULTI | SWT.WRAP);
      textAreaSabre
            .setText("Within this page, you may add specific schema files from a zip file, which is provided by amadeus (https://webservices.amadeus.com/extranet/download_page.do).\r\nYou should first click on the 'Import' button, where you may select a zip file. This file will be compared with the file 'com\\amadeus\\xml\\amadeusWSschemas.zip' and the differences will be listed. \r\nAfter this you may select, which requests should be updated within the 'amadeusWSschemas.zip' file. Press start to copy these (selected) requests to the file.");
      textAreaSabre.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
      textAreaSabre.setLayoutData(gridData);
      GridData gridData2 = new GridData();
      gridData2.grabExcessHorizontalSpace = true;
      gridData2.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
      gridData2.grabExcessVerticalSpace = true;
      table = new Table(this, SWT.FULL_SELECTION);
      table.setHeaderVisible(true);
      table.setLayoutData(gridData2);
      table.setLinesVisible(true);

      TableColumn tableColumn = new TableColumn(table, SWT.NONE);
      tableColumn.setText("Selected");
      TableColumn tableColumn1 = new TableColumn(table, SWT.NONE);
      tableColumn1.setText("Schema");
      TableColumn tableColumn2 = new TableColumn(table, SWT.NONE);
      tableColumn2.setText("Installed version");
      TableColumn tableColumn3 = new TableColumn(table, SWT.NONE);
      tableColumn3.setText("New Version");
      table.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {
         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            TableItem[] selection = table.getSelection();
            if (selection.length > 0) {
               String sVersionNew = selection[0].getText(2) + " ";
               sVersionNew = sVersionNew.substring(0, sVersionNew.indexOf(" ")).trim();
               String sWsdl = selection[0].getText(3);
               int iEnd = sWsdl.indexOf("RQ.wsdl");
               int iStart = sWsdl.lastIndexOf("LS") + 2;
               if (iEnd > 0 && iStart < iEnd) {
                  if (sVersionNew.equals("1.0.1")) {
                     sVersionNew = "";
                  }
                  sWsdl = sWsdl.substring(0, iStart) + sVersionNew + sWsdl.substring(iEnd);
               }
               //               _swtSabre.setWsdlFile(sWsdl);
            }
         }
      });
      createComposite();
      GridData gd_btnImportSchemasFrom = new GridData();
      gd_btnImportSchemasFrom.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
      btnImportSchemasFrom = new Button(this, SWT.NONE);
      btnImportSchemasFrom.setText("Import schemas from amadeus zip");
      btnImportSchemasFrom.setLayoutData(gd_btnImportSchemasFrom);
      btnImportSchemasFrom.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter()
      {

         @Override
         public void widgetSelected(org.eclipse.swt.events.SelectionEvent e)
         {
            FileDialog fileDialog = new FileDialog(getShell(), SWT.MULTI);

            String baseDir = Util.getProviderDataPath(getBaseDir(), "com/amadeus/xml");
            fileDialog.setFilterPath(baseDir);

            fileDialog.setFilterExtensions(new String[]{ "*.zip" });
            fileDialog.setFilterNames(new String[]{ "ZIP-File" });

            String firstFile = fileDialog.open();
            if (firstFile != null && firstFile.endsWith(".zip")) {

               final Hashtable<String, String[]> htRequests = new Hashtable<>();
               amadeusWSZip = new File(baseDir, "amadeusWSschemas.zip");
               importZip(amadeusWSZip, htRequests, 0);
               importZipFile = new File(firstFile);
               importZip(importZipFile, htRequests, 1);

               getDisplay().syncExec(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     table.clearAll();
                     hsSelected.clear();
                     String[] keys = htRequests.keySet().toArray(new String[0]);
                     Arrays.sort(keys);
                     for (final String sKey : keys) {
                        final String[] strings = htRequests.get(sKey);
                        TableItem tableItem = new TableItem(table, SWT.NONE);
                        if (strings[1].length() == 0 || strings[1].equals(strings[0])) {
                           tableItem.setText(1, "");
                        } else {
                           final Button check = new Button(table, SWT.CHECK);
                           check.setData(Integer.valueOf(0));
                           check.addSelectionListener(new SelectionAdapter()
                           {
                              @Override
                              public void widgetSelected(SelectionEvent selectionEvent)
                              {
                                 if (check.getSelection()) {
                                    hsSelected.add(strings[2]);
                                 } else {
                                    hsSelected.remove(strings[2]);
                                 }
                              }
                           });
                           TableEditor tbl_editor = new TableEditor(table);
                           tbl_editor.grabHorizontal = true;
                           tbl_editor.minimumHeight = check.getSize().x;
                           tbl_editor.minimumWidth = check.getSize().y;
                           tbl_editor.setEditor(check, tableItem, 0);
                        }
                        tableItem.setText(1, sKey);
                        tableItem.setText(2, strings[0]);
                        tableItem.setText(3, strings[1]);
                        checkColors(tableItem);
                     }
                     TableColumn[] columns = table.getColumns();
                     for (TableColumn column : columns) {
                        column.pack();
                     }
                  }
               });


            }
         }


      });
   }


   private void mergeFiles(File pImportZipFile, File pAmadeusWSZip)
   {
      try {
         ByteArrayOutputStream newFile = new ByteArrayOutputStream();
         ZipOutputStream zipOutputStream = new ZipOutputStream(newFile);

         // 1. copy 'old' file entries (except selected ones)
         copyFile(pAmadeusWSZip, true, zipOutputStream);
         // 2. copy 'new' file entries
         copyFile(pImportZipFile, false, zipOutputStream);

         // final close the main stream
         zipOutputStream.close();

         // copy/overwrite the file
         OutputStream out = new BufferedOutputStream(new FileOutputStream(pAmadeusWSZip));
         copyStream(new ByteArrayInputStream(newFile.toByteArray()), out);
         out.close();
         info("The file "
               + pAmadeusWSZip.getName()
               + " is now up to date.\nYou may now start the 'AmadeusWsdl' import process (previous tab)");
      }
      catch (Exception e) {
         errorMsg(e);
      }
   }

   private void copyFile(File pFile, boolean pbAddNew, ZipOutputStream zipOutputStream)
      throws IOException, IOException
   {
      ZipFile zipFile = new ZipFile(pFile);
      Enumeration<? extends ZipEntry> entries = zipFile.entries();
      Pattern compile = Pattern.compile("([A-Za-z]+_[A-Za-z]+)_([0-9]+)_([0-9]+)_.*[.]xsd");
      HashSet<String> hsNames = new HashSet<String>();
      for (String name : hsSelected) {
         Matcher matcher = compile.matcher(name);
         if (matcher.find()) {
            hsNames.add(matcher.group(1));
         }
      }
      while (entries.hasMoreElements()) {
         ZipEntry zipEntry = entries.nextElement();
         String name = zipEntry.getName();
         Matcher matcher = compile.matcher(name);
         boolean bAdd = pbAddNew;
         if (matcher.find()) {
            String sName = matcher.group(1);
            if (pbAddNew) {
               bAdd = !hsNames.contains(sName);
            } else {
               bAdd = hsSelected.contains(name);
            }
         }
         if (bAdd) {
            ZipEntry e = new ZipEntry(zipEntry.getName());
            e.setComment(zipEntry.getComment());
            e.setTime(zipEntry.getTime());
            zipOutputStream.putNextEntry(e);
            InputStream in = zipFile.getInputStream(zipEntry);
            copyStream(in, zipOutputStream);
            zipOutputStream.closeEntry();
         }
      }
      zipFile.close();

   }

   private static void copyStream(InputStream in, OutputStream out)
      throws IOException
   {
      int count;
      byte[] b = new byte[4096];
      while ((count = in.read(b)) > 0) {
         out.write(b, 0, count);
      }
   }

   void importZip(File file, Hashtable<String, String[]> htRequests, int i)
   {
      if (file.exists()) {
         try {
            ZipFile zipFile = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            Pattern compile = Pattern.compile("([A-Za-z]+_[A-Za-z]+)_([0-9]+)_([0-9]+)_.*[.]xsd");
            while (entries.hasMoreElements()) {
               ZipEntry zipEntry = entries.nextElement();
               String name = zipEntry.getName();

               Matcher matcher = compile.matcher(name);
               if (matcher.find()) {
                  String sVersion = getVersionAsString(matcher.group(2) + "." + matcher.group(3));
                  String sName = matcher.group(1);
                  String[] strings = htRequests.get(sName);
                  if (strings == null) {
                     strings = new String[3];
                     for (int j = 0; j < strings.length; j++) {
                        strings[j] = "";
                     }
                     strings[i] = sVersion;
                     htRequests.put(sName, strings);
                     if (i == 1) {
                        strings[2] = name;
                     }
                  } else {
                     if (sVersion.compareTo(strings[i]) > 0) {
                        strings[i] = sVersion;
                        if (i == 1) {
                           strings[2] = name;
                        }
                     }
                  }
               }
            }
            zipFile.close();
         }
         catch (Exception e) {
            errorMsg(e);
         }
      }
   }

   @Override
   public void start(Generator generator)
   {
      try {
         if (importZipFile != null) {
            if (hsSelected.size() > 0) {
               if (getBoolean("Do you really want to import " + hsSelected.size()
                     + " schema files from " + importZipFile.getName())) {
                  mergeFiles(importZipFile, amadeusWSZip);

                  AmadeusWsdlZipCorrector amadeusWsdlZipCorrector =
                     new AmadeusWsdlZipCorrector(importZipFile, amadeusWSZip);
                  amadeusWsdlZipCorrector.checkWsdlFiles();
                  String sCorrectionSummary = amadeusWsdlZipCorrector.getCorrectionSummary();
                  if (sCorrectionSummary.length() > 0) {
                     info("INFO: Within the WSDL files, the following elements are modified:\n"
                           + sCorrectionSummary);
                  }
               }
            }

         }
      }
      catch (Exception ex) {
         errorMsg(ex);
      }
   }

   static void checkColors(TableItem tableItem)
   {
      int f1 = getVersion(tableItem.getText(2));
      int f2 = getVersion(tableItem.getText(3));
      int diff = f2 - f1;
      if (diff > 200) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 255, 200, 200));
      } else if (diff > 100) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 255, 255, 200));
      } else if (diff == 0) {
         tableItem.setBackground(new Color(tableItem.getDisplay(), 200, 255, 200));
      }
   }

   private static int getVersion(String text)
   {
      StringTokenizer st = new StringTokenizer(text, ". ()_");
      int i = 0;
      for (int j = 0; j < 2; j++) {
         i = i * 100;
         try {
            i += Integer.parseInt(st.nextToken());
         }
         catch (Exception ex) {
            // invalid number
         }
      }
      return i;
   }

   private static String getVersionAsString(String text)
   {
      StringTokenizer st = new StringTokenizer(text, ". ()_");
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < 2; j++) {
         if (j > 0) {
            sb.append(".");
         }
         String nextToken = st.nextToken();
         if (nextToken.length() < 2) {
            nextToken = "0" + nextToken;
         }
         sb.append(nextToken);
      }
      return sb.toString();
   }

   @Override
   public void loadValuesFrom(XmlObject settings)
   {
      // not supported
   }

   @Override
   public void saveValuesTo(XmlObject settings)
   {
      // not supported
   }

   /**
    * This method initializes composite
    *
    */
   private void createComposite()
   {
      // not supported
   }


}
