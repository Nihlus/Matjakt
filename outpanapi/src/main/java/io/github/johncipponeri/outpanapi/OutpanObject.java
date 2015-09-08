package io.github.johncipponeri.outpanapi;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class OutpanObject
{

    public ErrorCode errorCode;
    public String
            gtin,
            outpan_url,
            name;
    public HashMap<String, String>
            attributes;
    public ArrayList<String>
            images,
            videos;
    public boolean hasError;

    public OutpanObject()
    {
        this.errorCode = ErrorCode.INVALID_ERROR_CODE;
        this.gtin = "";
        this.outpan_url = "";
        this.name = "";

        this.attributes = new HashMap<String, String>();
        this.images = new ArrayList<String>();
        this.videos = new ArrayList<String>();

        this.hasError = false;
    }

    public OutpanObject(JSONObject json)
    {
        this();

        try
        {
            JSONObject errorObject = json.getJSONObject("error");

            if (errorObject != null)
            {
                this.errorCode = ErrorCode.tryParse(json.getString("code"));
                this.hasError = true;
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            this.gtin = json.getString("gtin");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        try
        {
            this.outpan_url = json.getString("outpan_url");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }


        if (!json.isNull("name"))
        {
            try
            {
                this.name = json.getString("name");
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        //Outpan encodes empty attributes as an empty array for some reason, so an extra check is needed.
        if (!json.isNull("attributes") && isObject(json, "attributes"))
        {
            JSONObject attrObject;
            try
            {
                attrObject = json.getJSONObject("attributes");

                if (attrObject != null)
                {
                    String[] attributes = getNames(attrObject);

                    for (int a = 0; a < attributes.length; a++)
                    {
                        this.attributes.put(attributes[a], attrObject.getString(attributes[a]));
                    }
                }
            } catch (JSONException e)
            {
                Log.w("OUTPAN", "Attributes was not a valid JSON object. Probably empty?");
            }
        }

        if (!json.isNull("images"))
        {
            try
            {
                JSONArray images = json.getJSONArray("images");
                for (int i = 0; i < images.length(); i++)
                {
                    this.images.add(images.getString(i));
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        if (!json.isNull("videos"))
        {
            try
            {
                JSONArray videos = json.getJSONArray("videos");
                for (int i = 0; i < videos.length(); i++)
                {
                    this.videos.add(videos.getString(i));
                }
            } catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
    }

    private static String[] getNames(JSONObject jo)
    {
        int length = jo.length();
        if (length == 0)
        {
            return null;
        }

        Iterator iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext())
        {
            names[i] = (String) iterator.next();
            i += 1;
        }

        return names;
    }

    private boolean isObject(JSONObject json, String elementName)
    {
        try
        {
            JSONObject object = json.getJSONObject(elementName);
        } catch (JSONException e)
        {
            Log.d("OUTPANAPI", "Exception caught: Element was an array.");
            return false;
        }

        //we made it past the catch, so it was an object
        return true;
    }

    public enum ErrorCode
    {
        API_KEY_REQUIRED("110"),
        INVALID_API_KEY("120"),
        BARCODE_REQUIRED("200"),
        INVALID_GTIN_BARCODE("210"),
        IMAGE_FILE_REQUIRED("300"),
        MINUTE_RATE_EXCEEDED("900"),
        DAILY_RATE_EXCEEDED("910"),
        INVALID_ERROR_CODE("-1");

        private String errorValue;

        ErrorCode(String errorValue)
        {
            this.errorValue = errorValue;
        }

        public static ErrorCode tryParse(String inCode)
        {
            switch (inCode)
            {
                case "110":
                {
                    return ErrorCode.API_KEY_REQUIRED;
                }
                case "120":
                {
                    return ErrorCode.INVALID_API_KEY;
                }
                case "200":
                {
                    return ErrorCode.BARCODE_REQUIRED;
                }
                case "210":
                {
                    return ErrorCode.INVALID_GTIN_BARCODE;
                }
                case "300":
                {
                    return ErrorCode.IMAGE_FILE_REQUIRED;
                }
                case "900":
                {
                    return ErrorCode.MINUTE_RATE_EXCEEDED;
                }
                case "910":
                {
                    return ErrorCode.DAILY_RATE_EXCEEDED;
                }
                default:
                {
                    return ErrorCode.INVALID_ERROR_CODE;
                }
            }
        }

        public String getErrorCode()
        {
            return errorValue;
        }
    }
}