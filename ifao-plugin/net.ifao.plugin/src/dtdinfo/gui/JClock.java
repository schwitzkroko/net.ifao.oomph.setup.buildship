package dtdinfo.gui;


import javax.swing.*;

import java.awt.*;


/**
 * Class JClock
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
class JClock
    extends JPanel
{

    /**
	 * 
	 */
	private static final long serialVersionUID = -6948261436709611586L;
	long start = 0;

    /**
     * Constructor JClock
     */
    public JClock()
    {
        try {
            jbInit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        start = System.currentTimeMillis();


    }

    /**
     * Method jbInit
     *
     * @throws Exception
     * @author $author$
     */
    void jbInit()
        throws Exception
    {
        Dimension d = new Dimension(60, 60);

        setMinimumSize(d);
        setPreferredSize(d);
        setSize(d);

    }

    /**
     * Method paintComponent
     *
     * @param g
     * @author Andreas Brod
     */
    @Override
   public void paintComponent(Graphics g)
    {
        double now1 = (System.currentTimeMillis() - start) / 30000.0;
        double nowPi = now1 * Math.PI;
        int s = (int) (Math.sin(nowPi) * 20);
        int c = (int) (Math.cos(nowPi) * 20);
        int mx = getWidth() / 2;
        int my = getHeight() / 2;

        super.paintComponent(g);

        g.setColor(Color.black);
        g.drawOval(mx - 20, my - 20, 40, 40);

        // int arc = ((int)(now1*180) % 360);
        // g.fillArc(mx - 20, my - 20, 40, 40,90,-arc);
        g.drawLine(mx, my, mx + s, my - c);

        mx--;
        my--;

        g.setColor(Color.white);
        g.drawOval(mx - 20, my - 20, 40, 40);
        g.drawLine(mx, my, mx + s, my - c);
    }
}
