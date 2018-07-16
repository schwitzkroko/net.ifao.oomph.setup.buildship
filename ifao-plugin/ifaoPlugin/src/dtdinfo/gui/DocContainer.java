package dtdinfo.gui;



import java.awt.*;

import javax.swing.text.*;


/**
 * Class DocContainer
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author Andreas Brod
 */
public class DocContainer
    extends DefaultStyledDocument
{
    private static Color brown = new Color(152, 48, 0);

    static SimpleAttributeSet attrBlue = new SimpleAttributeSet();
    static SimpleAttributeSet attrBrown = new SimpleAttributeSet();
    static SimpleAttributeSet attrBrown2 = new SimpleAttributeSet();
    static SimpleAttributeSet attrBlack = new SimpleAttributeSet();
    static SimpleAttributeSet attrGray = new SimpleAttributeSet();
    static SimpleAttributeSet attrGreen = new SimpleAttributeSet();
    static SimpleAttributeSet attrLog = new SimpleAttributeSet();

    static {
        StyleConstants.setForeground(attrBlue, Color.blue);
        StyleConstants.setForeground(attrBrown, brown);
        StyleConstants.setForeground(attrBrown2, brown);
        StyleConstants.setBold(attrBrown2, true);
        StyleConstants.setForeground(attrBlack, Color.black);
        StyleConstants.setBold(attrBlack, true);
        StyleConstants.setForeground(attrGray, Color.darkGray);
        StyleConstants.setForeground(attrGreen, new Color(0, 128, 0));

        StyleConstants.setFontFamily(attrBlue, "SansSerif");
        StyleConstants.setFontFamily(attrBrown, "SansSerif");
        StyleConstants.setFontFamily(attrBlack, "SansSerif");
        StyleConstants.setFontFamily(attrGray, "Monospaced");
        StyleConstants.setFontFamily(attrGreen, "SansSerif");

        StyleConstants.setFontFamily(attrLog, "Monospaced");
    }


    // int lastPos = 0;

    /**
     *    
     */
    private static final long serialVersionUID = 3191344006321019226L;

    /**
     * Method addString
     *
     * @param sText
     * @param set
     *
     * @throws BadLocationException
     * @author Andreas Brod
     */
    void addString(String sText, SimpleAttributeSet set)
        throws BadLocationException
    {
        int anz = sText.length();
        int offs = 0;

        while (anz > 0) {
            int read = (anz > 2000) ? 2000
                                    : anz;

            while ((read >= 2000) && (read <= 3000) && (read < anz)
                    && (sText.charAt(offs + read) != '<')) {
                read++;
            }

            String s1 = sText.substring(offs, offs + read);

            if (read >= 2000) {
                s1 += "\n";
            }

            insertString(getLength(), s1, set);

            offs += read;
            anz -= read;
        }

        // lastPos += sText.length();
    }

    /**
     * Method getXmlDocument
     *
     * @param sText
     *
     * @return
     * @author $author$
     */
    public static DocContainer getXmlDocument(String sText)
    {
        DocContainer doc = new DocContainer();
        SimpleAttributeSet iDefault = attrBlue;

        try {
            int iStart = 0;
            char[] chars = sText.toCharArray();
            char cString = ' ';
            SimpleAttributeSet styleOld = attrBlue;
            SimpleAttributeSet styleNew = attrBlue;
            boolean comment = false;
            boolean cdata = false;

            for (int i = 0; i < chars.length; i++) {
                styleNew = iDefault;

                if (comment) {
                    if ((chars[i] == '>') && (chars[i - 1] == '-')
                            && (chars[i - 2] == '-')) {
                        comment = false;
                        iDefault = attrGray;
                    }
                } else if (cdata) {
                    if ((chars[i] == ']') && (chars[i - 1] == ']')) {
                        cdata = false;
                    }
                } else if ((cString == ' ')
                           && ((chars[i] == '\"') || (chars[i] == '\''))) {
                    cString = chars[i];
                    styleNew = attrBlue;
                } else if ((cString != ' ') && (chars[i] == cString)) {
                    cString = ' ';
                    styleNew = attrBlue;
                } else if (cString != ' ') {
                    styleNew = attrBlack;
                } else {
                    switch (chars[i]) {

                    case '&':
                        if (i > iStart) {
                            doc.addString(sText.substring(iStart, i),
                                          styleOld);
                        }

                        iStart = i;

                        while ((i + 1 < chars.length) && (chars[i] != ';')) {
                            chars[i] = '?';

                            i++;
                        }

                        String s1 = sText.substring(iStart, i + 1);

                        if (s1.equals("&lt;")) {
                            s1 = "<";
                            iDefault = attrBrown;
                        } else if (s1.equals("&gt;")) {
                            s1 = ">";
                            iDefault = attrGray;
                        } else if (s1.equals("&amp;")) {
                            s1 = "&";
                            iDefault = attrGray;
                        } else if (s1.equals("&quot;")) {
                            s1 = "\"";
                            iDefault = attrGray;
                        } else if (s1.equals("&apos;")) {
                            s1 = "'";
                            iDefault = attrGray;
                        } else if (s1.equals("&nbsp;")) {
                            s1 = " ";
                            iDefault = attrGray;
                        }

                        styleNew = attrBlue;

                        doc.addString(s1, styleNew);

                        styleOld = styleNew;
                        iStart = i + 1;
                        break;

                    case '<':
                        if ((chars[i + 1] == '!') && (chars[i + 2] == '-')) {
                            comment = true;
                            iDefault = attrGreen;
                            styleNew = attrGreen;
                        } else if ((chars[i + 1] == '!')
                                   && (chars[i + 2] == '[')) {
                            cdata = true;
                            iDefault = attrLog;
                            styleNew = attrLog;
                        } else {
                            iDefault = attrBrown;
                            styleNew = attrBlue;
                        }
                        break;

                    case '>':
                        iDefault = attrGray;
                    case '=':
                    case '/':
                        styleNew = attrBlue;
                        break;

                    /*
                     * case ' ':
                     *    if (iDefault.equals(attrBrown2)){
                     *      iDefault = attrBrown;
                     *    }
                     * break;
                     */
                    }
                }

                if ((i > iStart) && (!styleOld.equals(styleNew))) {
                    doc.addString(sText.substring(iStart, i), styleOld);

                    iStart = i;
                }

                styleOld = styleNew;
            }

            doc.addString(sText.substring(iStart, chars.length), styleOld);

        } catch (Exception ex) {
            doc = new DocContainer();

            try {
                doc.addString(ex.getLocalizedMessage(), attrBlue);
            } catch (BadLocationException ex2) {}
        }

        return doc;
    }

}

