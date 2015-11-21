package com.nihlus.matjakt.Outpan;


import com.nihlus.matjakt.Constants.Constants;
import com.nihlus.matjakt.EAN;

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

import java.io.File;
import java.io.IOException;

import javax.net.ssl.SSLContext;

public class OutpanAPI2
{
    private final String APIKey;
    public OutpanAPI2(String InAPIKey)
    {
        this.APIKey = InAPIKey;
    }

    //TODO: Stub
    public OutpanProduct getProduct(EAN InEAN)
    {
        return null;
    }

}
