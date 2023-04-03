package cn.oahttp;

import android.app.Application;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.OkHttpClient;


public class ClientFactory {

    public static Application application;


    public static OkHttpClient getClient() {
        return Client.okHttpClient;
    }

    public static void setClient(OkHttpClient okHttpClient) {
        Client.okHttpClient = okHttpClient;
    }

    private static class Client {
        volatile static OkHttpClient okHttpClient=initClient();

        private static OkHttpClient initClient() {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder.build();
        }

    }

    public static void callAllRequest() {
        getClient().dispatcher().cancelAll();

    }

    public static void callRequestByTag(String tag) {
        List<Call> calls = getClient().dispatcher().runningCalls();
        for (int i = 0; i < calls.size(); i++) {
            Call call = calls.get(i);
            String tagInRq = call.request().tag() + "";
            if (tag.equals(tagInRq)) {
                call.cancel();
            }
        }
    }


    public static Application getApplication() {
        return application;
    }

    public static void setApplication(Application application) {
        ClientFactory.application = application;
    }



}
