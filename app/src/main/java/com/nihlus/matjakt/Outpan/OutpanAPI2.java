package com.nihlus.matjakt.Outpan;


import com.nihlus.matjakt.Constants.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;

import javax.net.ssl.SSLContext;

public class OutpanAPI2
{
    public OutpanAPI2()
    {

    }

    public static boolean UploadImage(String[] filePaths, String ean) throws IOException
    {
        String requestURL = Constants.OutpanBaseURL + ean + OutpanEndpoints.IMAGES;
        try
        {
            //set up basic authentication
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(Constants.OutpanAPIKey);
            CredentialsProvider provider = new BasicCredentialsProvider();
            provider.setCredentials(AuthScope.ANY, credentials);

            for (String filePath : filePaths)
            {
                File file = new File(filePath);

                SSLContext sslContext = SSLContexts.createSystemDefault();
                SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext);

                CloseableHttpClient httpClient = HttpClients.custom()
                        .setDefaultCredentialsProvider(provider)
                        .setSSLSocketFactory(sslConnectionSocketFactory)
                        .build();

                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.addBinaryBody(Constants.OutpanFileUploadField, file, ContentType.create(Constants.WEBFORMAT_JPEG), file.getName());


                HttpEntity entity = builder.build();
                HttpPost post = new HttpPost(requestURL);
                post.setEntity(entity);
                HttpResponse response = httpClient.execute(post);

                String content = EntityUtils.toString(response.getEntity(), Constants.UTF8);
                httpClient.close();

                if (response.getStatusLine().getStatusCode() == 400)
                {
                    return false;
                }
            }
        } catch (ClientProtocolException cex)
        {
            cex.printStackTrace();
            return false;
        }

        return true;
    }
}
