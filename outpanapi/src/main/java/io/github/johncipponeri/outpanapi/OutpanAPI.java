package io.github.johncipponeri.outpanapi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import io.github.johncipponeri.outpanapi.android.AndroidBase64;

public class OutpanAPI
{
    private String api_key;

    public OutpanAPI(String api_key)
    {
        this.api_key = api_key;
    }

    private JSONObject executeGet(String barcode)
    {
        return executeGet(barcode, "");
    }

    private JSONObject executeGet(String barcode, String endpoint)
    {
        JSONObject jsonResult = new JSONObject();

        try
        {
            URL url = new URL("https://api.outpan.com/v1/products/" + barcode + endpoint);
            URLConnection uc = url.openConnection();

            String key = api_key + ":";
            String basicAuth = "Basic " + new String(AndroidBase64.encode(key.getBytes(), AndroidBase64.NO_WRAP));

            uc.setRequestProperty("Authorization", basicAuth);

            InputStream in = uc.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();

            while ((numCharsRead = isr.read(charArray)) > 0)
            {
                sb.append(charArray, 0, numCharsRead);
            }

            jsonResult = new JSONObject(sb.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonResult;
    }

    private JSONObject executePost(String barcode, String dataPath, String endpoint)
    {
        JSONObject jsonResult = new JSONObject();

        try
        {
            URL url = new URL("https://api.outpan.com/v1/products/" + barcode + endpoint);
            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestMethod("POST");

            String key = api_key + ":";
            String basicAuth = "Basic " + new String(AndroidBase64.encode(key.getBytes(), AndroidBase64.NO_WRAP));

            uc.setRequestProperty("Authorization", basicAuth);

            InputStream in = uc.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();

            while ((numCharsRead = isr.read(charArray)) > 0)
            {
                sb.append(charArray, 0, numCharsRead);
            }

            jsonResult = new JSONObject(sb.toString());
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        return jsonResult;
    }

    private void executeEditGet(String barcode, EditIntent intent, String... values)
    {
        URL url = null;
        try
        {
            switch (intent)
            {
                case EDIT_PRODUCT_NAME:
                {
                    if (values.length == 1 && !values[0].isEmpty())
                    {
                        url = new URL("http://www.outpan.com/api/edit-name.php?apikey=" + URLParameterEncoder.encode(api_key) +
                                "&barcode=" + URLParameterEncoder.encode(barcode) +
                                "&name=" + URLParameterEncoder.encode(values[0]));

                    }
                    else
                    {
                        throw new IllegalArgumentException("Editing a product title requires a valid title.");
                    }
                    break;
                }
                case EDIT_PRODUCT_ATTRIBUTE:
                {
                    if (values.length == 2 && !values[0].isEmpty())
                    {
                        url = new URL("http://www.outpan.com/api/edit-attr.php?apikey=" + URLParameterEncoder.encode(api_key) +
                                "&barcode=" + URLParameterEncoder.encode(barcode) +
                                "&attr_name=" + URLParameterEncoder.encode(values[0]) +
                                "&attr_val=" + URLParameterEncoder.encode(values[1]));
                    }
                    else
                    {
                        throw new IllegalArgumentException("Editing a product attribute requires both a key and a value.");
                    }
                    break;
                }
            }
            URLConnection uc = url.openConnection();


            //if the content length here is more than 0, we have an error
            //TODO: Investigate if this is needed to complete the request
            if (uc.getContentLength() > 0)
            {
                InputStream in = uc.getInputStream();
                InputStreamReader isr = new InputStreamReader(in);

                int numCharsRead;
                char[] charArray = new char[1024];
                StringBuffer sb = new StringBuffer();

                while ((numCharsRead = isr.read(charArray)) > 0)
                {
                    sb.append(charArray, 0, numCharsRead);
                }

                JSONObject jsonResult = new JSONObject(sb.toString());
                OutpanObject outpanObject = new OutpanObject(jsonResult);
            }
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public OutpanObject getProduct(String barcode)
    {
        return new OutpanObject(executeGet(barcode));
    }

    public OutpanObject getProductName(String barcode)
    {
        return new OutpanObject(executeGet(barcode, "/name"));
    }

    public OutpanObject getProductAttributes(String barcode)
    {
        return new OutpanObject(executeGet(barcode, "/attributes"));
    }

    public OutpanObject getProductImages(String barcode)
    {
        return new OutpanObject(executeGet(barcode, "/images"));
    }

    public OutpanObject getProductVideos(String barcode)
    {
        return new OutpanObject(executeGet(barcode, "/videos"));
    }

    //Unofficial API, does not return an object.
    public void setProductName(String barcode, String newName)
    {
        executeEditGet(barcode, EditIntent.EDIT_PRODUCT_NAME, newName);
    }

    //Unofficial API, does not return an object.
    public void setProductAttribute(String barcode, String attributeKey, String attributeValue)
    {
        executeEditGet(barcode, EditIntent.EDIT_PRODUCT_ATTRIBUTE, attributeKey, attributeValue);
    }

    private enum EditIntent
    {
        EDIT_PRODUCT_NAME,
        EDIT_PRODUCT_ATTRIBUTE;
    }
}