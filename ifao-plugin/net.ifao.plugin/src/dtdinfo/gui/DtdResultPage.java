package dtdinfo.gui;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Class DtdResultPage
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
public class DtdResultPage
    extends JDialog
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 9134646331751213160L;
	JPanel panel1 = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JScrollPane jScrollPane1 = new JScrollPane();
    JTextArea jTextArea1 = new JTextArea();
    JPanel jPanel1 = new JPanel();
    JPanel jPanel2 = new JPanel();
    JButton jClose = new JButton();
    BorderLayout borderLayout2 = new BorderLayout();

    /**
     * Constructor DtdResultPage
     *
     * @param frame
     * @param title
     * @param modal
     */
    private DtdResultPage(Frame frame, String title, boolean modal)
    {
        super(frame, title, modal);

        try {
            jbInit();
            pack();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setLocation(frame.getLocation());
        setSize(frame.getSize());
    }

    /**
     * Method show
     *
     * @param frame
     * @param title
     * @param modal
     * @param sText
     * @author $author$
     */
    public static void show(Frame frame, String title, boolean modal,
                            String sText)
    {
        DtdResultPage page = new DtdResultPage(frame, title, modal);

        page.setResult(sText);
        page.setVisible(true);
    }

    /**
     * Method setResult
     *
     * @param sText
     * @author $author$
     */
    private void setResult(String sText)
    {
        jTextArea1.setText(sText);
    }

    /**
     * Constructor DtdResultPage
     */
    private DtdResultPage()
    {
        this(null, "", false);
    }

    /**
     * Method jbInit
     *
     * @throws Exception
     * @author $author$
     */
    private void jbInit()
        throws Exception
    {
        panel1.setLayout(borderLayout1);
        jTextArea1.setText("jTextArea1");
        jClose.setText("Close");
        jClose.addActionListener(
            new DtdResultPage_jClose_actionAdapter(this));
        jPanel1.setLayout(borderLayout2);
        getContentPane().add(panel1);
        panel1.add(jScrollPane1, BorderLayout.CENTER);
        panel1.add(jPanel1, BorderLayout.SOUTH);
        jPanel1.add(jPanel2, BorderLayout.EAST);
        jPanel2.add(jClose, null);
        jScrollPane1.getViewport().add(jTextArea1, null);
    }

    /**
     * Method jClose_actionPerformed
     *
     * @param e
     * @author $author$
     */
    void jClose_actionPerformed(ActionEvent e)
    {
        this.setVisible(false);
    }
}

/**
 * Class DtdResultPage_jClose_actionAdapter
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
class DtdResultPage_jClose_actionAdapter
    implements java.awt.event.ActionListener
{
    DtdResultPage adaptee;

    /**
     * Constructor DtdResultPage_jClose_actionAdapter
     *
     * @param adaptee
     */
    DtdResultPage_jClose_actionAdapter(DtdResultPage adaptee)
    {
        this.adaptee = adaptee;
    }

    /**
     * Method actionPerformed
     *
     * @param e
     * @author $author$
     */
    public void actionPerformed(ActionEvent e)
    {
        adaptee.jClose_actionPerformed(e);
    }
}
