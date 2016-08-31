package solr.search.util;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Wangtc
 * Date: 2016/8/30
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    private static Logger logger = Logger.getLogger(Util.class);
    private static int TIMEOUT = 10000;
    public static final String getUrlString(String urlString) {
        return getUrlString(urlString, "UTF-8", (String[][])null, false);
    }

    public static final String getUrlString(String urlString, String defaultEncoding) {
        return getUrlString(urlString, defaultEncoding, (String[][])null, false);
    }

    public static final String getUrlString(String urlString, String defaultEncoding, String[][] headers) {
        return getUrlString(urlString, defaultEncoding, headers, false);
    }

    public static final String getUrlString(String urlString, String defaultEncoding, String[][] headers, boolean gzip) {
        Object in = null;
        HttpURLConnection con = null;

        String var19;
        try {
            URL e = new URL(urlString);
            con = (HttpURLConnection)e.openConnection();
            con.setReadTimeout(TIMEOUT);
            con.setConnectTimeout(TIMEOUT);
            if(gzip && Math.random() < 0.3D) {
                con.setRequestProperty("Accept-Encoding", "gzip");
            }

            if(headers != null) {
                for(int var53 = 0; var53 < headers.length; ++var53) {
                    if(headers[var53] != null) {
                        con.setRequestProperty(headers[var53][0], headers[var53][1]);
                    }
                }
            }

            long var54 = System.currentTimeMillis();
            con.connect();
            int code = con.getResponseCode();
            int length = con.getContentLength();
            if(length >= 0) {
                logger.info(MessageFormat.format("URL:{0},START:{1},COST:{2},CODE:{3},CONTENT-LENGTH:{4}",urlString, var54, (int)(System.currentTimeMillis() - var54), code, length));
            }

            String encoding2 = con.getHeaderField("Content-Type");
            if(encoding2 != null) {
                int index;
                if((index = encoding2.indexOf("charset=")) > 0) {
                    encoding2 = encoding2.substring(index + "charset=".length()).replace('\"', ' ').replace('\'', ' ').trim();
                } else {
                    encoding2 = defaultEncoding;
                }
            }

            if(code != 404) {
                in = new BufferedInputStream(con.getInputStream());
            }

            if(in == null) {
                return null;
            }

            String contentencoding = con.getHeaderField("Content-Encoding");
            if(gzip && "gzip".equals(contentencoding)) {
                System.out.println("gzipped");
                in = new GZIPInputStream((InputStream)in);
            }

            ByteArrayOutputStream urlData = new ByteArrayOutputStream();
            byte[] buf2 = new byte[1024];

            int n;
            while((n = ((InputStream)in).read(buf2)) >= 0) {
                if(urlData.size() > 2097152) {
                    System.out.println("***error geturl too long " + urlString);
                    if(length < 0) {
                        logger.info(MessageFormat.format("URL:{0},START:{1},COST:{2},CODE:{3},URLDATA:{4}", urlString, var54, (int) (System.currentTimeMillis() - var54), code, urlData.size()));
                    }

                    return null;
                }

                urlData.write(buf2, 0, n);
            }

            if(length < 0) {
                logger.info(MessageFormat.format("URL:{0},START:{1},COST:{2},CODE:{3},URLDATA:{4}", urlString, var54, (int) (System.currentTimeMillis() - var54), code, urlData.size()));
            }

            if(encoding2 == null) {
                var19 = urlData.toString();
                return var19;
            }

            try {
                var19 = urlData.toString(encoding2);
                return var19;
            } catch (UnsupportedEncodingException var48) {
                System.out.println("UnsupportedEncodingException detected: " + var48.getMessage());
                var19 = urlData.toString();
            }
        } catch (SocketTimeoutException var49) {
            logger.error(urlString + " timeout", var49);
            var49.printStackTrace();
            return null;
        } catch (MalformedURLException var50) {
            logger.error(urlString + " MalformedURL", var50);
            var50.printStackTrace();
            return null;
        } catch (Exception var51) {
            logger.error(urlString + " other error", var51);
            var51.printStackTrace();
            if(con != null) {
                try {
                    InputStream e1 = con.getErrorStream();
                    if(e1 != null) {
                        e1.close();
                        e1 = null;
                        return null;
                    }
                } catch (Exception var47) {
                    var47.printStackTrace();
                }

                return null;
            }

            return null;
        } finally {
            if(in != null) {
                try {
                    ((InputStream)in).close();
                } catch (Exception var46) {
                    var46.printStackTrace();
                }

                in = null;
            }

            if(con != null) {
                try {
                    con.getInputStream().close();
                } catch (Exception var45) {
                    var45.printStackTrace();
                }

                con = null;
            }

        }

        return var19;
    }

    public static final String unescapeXML(String s) {
        return s.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&apos;", "\'").replaceAll("&quot;", "\"").replaceAll("&amp;", "&");
    }
}
