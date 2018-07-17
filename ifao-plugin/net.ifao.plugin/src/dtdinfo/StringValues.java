package dtdinfo;


/**
 * Class StringValues
 *
 * <p>
 * Copyright &copy; 2002, i:FAO, AG.
 * @author $author$
 */
public class StringValues
{
    String s;

    /**
     * Constructor StringValues
     *
     * @param ps
     */
    public StringValues(String ps)
    {
        s = ps;
    }

    /**
     * Method getNext
     *
     * @return
     * @author $author$
     */
    public String getNext()
    {
        String sRet = "";

        if (hasNext()) {
            int i1 = s.indexOf("</td>");
            int i2 = s.indexOf("<td");

            if ((i2 > 0) && (i2 < i1)) {
                i1 = i2;
            }

            sRet = s.substring(0, i1);
            s = s.substring(s.indexOf(">", i2) + 1);
        }

        return sRet;
    }

    /**
     * Method hasNext
     *
     * @return
     * @author $author$
     */
    public boolean hasNext()
    {
        return s.indexOf("</td>") > 0;
    }
}

