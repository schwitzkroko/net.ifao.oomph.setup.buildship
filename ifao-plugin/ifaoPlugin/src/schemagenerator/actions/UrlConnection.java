package schemagenerator.actions;


import java.io.*;
import java.net.*;
import java.security.*;

import javax.net.ssl.*;


/** 
 * This class implements an UrlConnection (which can be used to request
 * follow up requests (http POST/GET) with cookies) 
 * 
 * <p> 
 * Copyright &copy; 2010, i:FAO 
 * 
 * @author brod 
 */
public class UrlConnection
{
   // this constant contains the all-trusting SSL context
   private static final SSLContext TRUST_ALL_CONTEXT;
   private static final TrustManager[] TRUST_ALL_MANAGER =
      new TrustManager[]{ new X509TrustManager()
      {
         @Override
         public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
         {}

         @Override
         public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
         {}

         @Override
         public java.security.cert.X509Certificate[] getAcceptedIssuers()
         {
            return null;
         }
      } };

   private static final HostnameVerifier ALL_OK_VERIFIER = new HostnameVerifier()
   {
      @Override
      public boolean verify(String urlHostName, SSLSession session)
      {
         return true;
      }
   };

   static {
      // create context using the all-trusting trust manager
      SSLContext context = null;
      try {
         // Now construct a SSLContext using this trustManager. We
         // specify a null KeyManager[], indicating that the default should be used.
         context = SSLContext.getInstance("SSL");
         context.init(null, TRUST_ALL_MANAGER, new java.security.SecureRandom());
      }
      catch (NoSuchAlgorithmException pException) {
         pException.printStackTrace();
      }
      catch (KeyManagementException pException) {
         pException.printStackTrace();
      }

      // use the created context for the constant
      TRUST_ALL_CONTEXT = context;
   }

   /** 
    * init a default CookieHandler (which accepts all cookies) 
    * 
    * @author brod 
    */
   static {
      CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
   }

   private URL _url;
   private String _sBaseArctic;

   /** 
    * The constructor for the UrlConnection requires 
    * 
    * @param psUrl default (start) URL 
    * @throws MalformedURLException 
    * 
    * @author brod 
    */
   public UrlConnection(String psUrl)
      throws MalformedURLException
   {
      _url = new URL(psUrl);
      _sBaseArctic = null;
   }


   /** 
    * The constructor for the UrlConnection requires 
    * 
    * @param psUrl default (start) URL 
    * @param psBaseArctic base directory of the arctic project; used to find arctic's keyStore
    * @throws MalformedURLException 
    * 
    * @author brod 
    */
   public UrlConnection(String psUrl, String psBaseArctic)
      throws MalformedURLException
   {
      _url = new URL(psUrl);
      _sBaseArctic = psBaseArctic.replace('\\', '/');
   }

   /** 
    * This method sends typically a http GET command, and
    * returns the result 
    * 
    * @return content of the http get command 
    * @throws IOException 
    * 
    * @author brod 
    */
   public String getContent()
      throws IOException
   {
      return getContent(null);
   }

   /** 
    * This method sends a http GET or POST command (dependent of the
    * htmlForm parameters) 
    * 
    * @param pHtmlForm HtmlForm (with parameters) which may be null 
    * @return content of the http response 
    * @throws IOException 
    * 
    * @author brod 
    */
   public String getContent(HtmlForm pHtmlForm)
      throws IOException
   {
      String sPost = null;
      // if there is a HtmlForm, react to the parameters  
      if (pHtmlForm != null) {
         // change the URL
         String action = pHtmlForm.getAction();
         String sNewUrl = _url.toExternalForm();
         if (action.startsWith("/")) {
            sNewUrl = sNewUrl.substring(0, sNewUrl.indexOf("/", 10)) + action;
         } else {
            sNewUrl = sNewUrl.substring(0, sNewUrl.lastIndexOf("/") + 1) + action;
         }
         // get the inputString (which may change the requestURL)
         String inputString = pHtmlForm.getInputString();
         if (inputString.length() > 0) {
            if (pHtmlForm.getMethod().equals("GET")) {
               if (action.contains("?")) {
                  sNewUrl += "&" + inputString;
               } else {
                  sNewUrl += "?" + inputString;
               }
            } else {
               sPost = inputString;
            }
         }
         // change the url
         _url = new URL(sNewUrl);
      }

      byte[] bytes = getBytes(sPost);
      return new String(bytes);
   }


   public byte[] getBytes(String sPost)
      throws IOException
   {
      // open the connetion
      URLConnection openConnection = _url.openConnection();
      if (_sBaseArctic == null) {
         useTrustAllContext(openConnection);
      } else {
         useArcticKeyStore(openConnection);
      }
      openConnection.setReadTimeout(30000);
      // in case of post
      if (sPost != null) {
         // open the connetion
         // write the post command to the outputString
         openConnection.setDoOutput(true);
         openConnection.getOutputStream().write(sPost.getBytes());
      }

      // read the response
      BufferedInputStream in = new BufferedInputStream(openConnection.getInputStream());
      byte[] b = new byte[4096];
      int count;
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      while ((count = in.read(b)) > 0) {
         out.write(b, 0, count);
      }
      in.close();
      // reset for next request (this may be changed in case of redirect)
      _url = openConnection.getURL();
      return out.toByteArray();
   }

   /**
    * Creates a SSL context which uses the arctic keyStore and sets the SSL factory for the 
    * connection passed
    *
    * @param pOpenConnection URLConnection for which the arctic SSL factory should be used
    *
    * @author kaufmann
    */
   private void useArcticKeyStore(URLConnection pOpenConnection)
   {
      if (pOpenConnection instanceof HttpsURLConnection) {

         try {
            // Call getKeyManagers to get suitable key managers
            KeyManager[] keyManagers = getKeyManagers();

            // Call getTrustManagers to get suitable trust managers
            TrustManager[] trustManagers = getTrustManagers();

            // Now construct a SSLContext using these KeyManagers. We
            // specify a null SecureRandom, indicating that the default should be used.
            SSLContext context = SSLContext.getInstance("SSL");
            context.init(keyManagers, trustManagers, null);

            // connect the socket factory using the arctic key manager to the connection
            ((HttpsURLConnection) pOpenConnection).setSSLSocketFactory(context.getSocketFactory());

            System.out.println(">>> setting arctic SSL Factory suceeded");
         }
         catch (KeyManagementException pException) {
            System.out.println("Failed to set the SSL Factory:\n" + pException.toString());
         }
         catch (NoSuchAlgorithmException pException) {
            System.out.println("Failed to set the SSL Factory:\n" + pException.toString());
         }
         catch (GeneralSecurityException pException) {
            System.out.println("Failed to set the SSL Factory:\n" + pException.toString());
         }
      }
   }

   /** 
   * Method getKeyManagers gets the keymanagers using the arctic key store 
   * 
   * @return array of keymanagers 
   * @throws GeneralSecurityException 
   * 
   * @author kaufmann 
   */
   protected KeyManager[] getKeyManagers()
      throws GeneralSecurityException
   {
      // Get the default KeyManagerFactory and initialize it with the KeyStore
      KeyManagerFactory keyManagerFactory =
         KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); // "SunX509"
      keyManagerFactory.init(getArcticKeyStore(), "arct1c".toCharArray());

      // Finally return the KeyManagers of the factory
      return keyManagerFactory.getKeyManagers();
   }

   /**
    * Loads the arctic keystore into a KeyStore object
    * 
    * @return arctic keystore
    *
    * @author kaufmann
    */
   private KeyStore getArcticKeyStore()
   {
      String sFullFileName = _sBaseArctic + "/dtd/ssl/ifaoKey.jks";
      if (!new File(sFullFileName).exists()) {
         sFullFileName = _sBaseArctic + "/conf/ssl/ifaoKey.jks";
      }

      InputStream inputStream = null;
      KeyStore keyStore = null;
      try {
         keyStore = KeyStore.getInstance("JKS");
         inputStream = new FileInputStream(new File(sFullFileName));
         keyStore.load(inputStream, "arct1c".toCharArray());
      }
      catch (Exception pException) {
         pException.printStackTrace();
      }
      finally {
         if (inputStream != null) {
            try {
               inputStream.close();
            }
            catch (IOException pException) {
               pException.printStackTrace();
            }
         }
      }
      return keyStore;
   }


   /** 
   * Method getTrustManagers gets the 'all-trusting' trustmanagers; this is not the way SSL was 
   * intended to be used but there must have been a reason for this... 
   * 
   * @return array of TrustManagers 
   * @throws GeneralSecurityException 
   * 
   * @author kaufmann 
   */
   protected TrustManager[] getTrustManagers()
      throws GeneralSecurityException
   {
      return TRUST_ALL_MANAGER;
   }


   /**
    * Checks, if the connection passed is a Https connection and in this case assigns the 
    * all-trusting trust manager to this connection 
    *
    * @param pConnection connection which should use the all-trusting trust manager for SSL
    *
    * @author kaufmann
    */
   public static void useTrustAllContext(URLConnection pConnection)
   {
      if (pConnection instanceof HttpsURLConnection && TRUST_ALL_CONTEXT != null) {
         // connect the socket factory using the trust-all manager to the connection
         ((HttpsURLConnection) pConnection).setSSLSocketFactory(TRUST_ALL_CONTEXT
               .getSocketFactory());
         ((HttpsURLConnection) pConnection).setHostnameVerifier(ALL_OK_VERIFIER);
      }
   }

   /** 
    * This method changes the Url 
    * 
    * @param psUrl new URL 
    * @throws MalformedURLException 
    * 
    * @author brod 
    */
   public void setUrl(String psUrl)
      throws MalformedURLException
   {
      _url = new URL(psUrl);
   }
}
