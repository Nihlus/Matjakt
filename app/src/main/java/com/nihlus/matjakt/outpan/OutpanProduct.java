package com.nihlus.matjakt.outpan;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.nihlus.matjakt.constants.Constants;
import com.nihlus.matjakt.database.containers.EAN;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Jarl on 2015-08-21.
 *
 * Represents a response object from the Outpan database.
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class OutpanProduct implements Parcelable
{
    public EAN ean;
    public URL outpanURL;
    public String name;
    public HashMap<String, String> attributes;
    public ArrayList<String> images;
    public ArrayList<String> videos;
    public ArrayList<String> categories;

    public OutpanProduct(JSONObject InJSON)
    {
        try
        {
            if (!isErrorMessage(InJSON))
            {
                this.ean = new EAN(InJSON.getString(Constants.PRODUCTKEY_EAN));
                this.outpanURL = new URL(InJSON.getString(Constants.PRODUCTKEY_OUTPANURL));
                this.name = InJSON.getString(Constants.PRODUCTKEY_NAME);
                this.attributes = getAttributesFromJSON(InJSON.getJSONObject(Constants.PRODUCTKEY_ATTRIBUTES));
                this.images = getStringListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_IMAGES));
                this.videos = getStringListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_VIDEOS));
                this.categories = getStringListFromJSON(InJSON.getJSONArray(Constants.PRODUCTKEY_CATEGORIES));
            }
            else
            {
                throw new IllegalArgumentException("The provided product was an error message.");
            }
        }
        catch (JSONException jex)
        {
            //TODO: Create global exception handler
            jex.printStackTrace();
        }
        catch (MalformedURLException mex)
        {
            //TODO: Create global exception handler
            mex.printStackTrace();
        }
    }

    // Constructor for deserialization when doing parcellation and packaging data to send to the database
    public OutpanProduct(EAN InEAN, Bundle InBundle)
    {
        this.ean = InEAN;
        this.attributes = new HashMap<>();
        this.categories = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.categories = new ArrayList<>();

        if (InBundle.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_BRAND_ATTRIBUTE, InBundle.getString(Constants.PRODUCT_BRAND_ATTRIBUTE));
        }

        if (InBundle.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_TITLE_ATTRIBUTE, InBundle.getString(Constants.PRODUCT_TITLE_ATTRIBUTE));
        }

        if (InBundle.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_AMOUNT_ATTRIBUTE, InBundle.getString(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
        }

        if (InBundle.containsKey(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE, String.valueOf(InBundle.getBoolean(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE)));
        }

        if (InBundle.containsKey(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE, InBundle.getString(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE));
        }

        if (InBundle.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_ORGANIC_ATTRIBUTE, String.valueOf(InBundle.getBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
        }

        if (InBundle.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
        {
            this.attributes.put(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, String.valueOf(InBundle.getBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));
        }

        if (InBundle.containsKey(Constants.PRODUCT_IMAGES_LIST))
        {
            this.images = InBundle.getStringArrayList(Constants.PRODUCT_IMAGES_LIST);
        }

        if (InBundle.containsKey(Constants.PRODUCT_VIDEOS_LIST))
        {
            this.videos = InBundle.getStringArrayList(Constants.PRODUCT_VIDEOS_LIST);
        }

        if (InBundle.containsKey(Constants.PRODUCT_CATEGORIES_LIST))
        {
            this.categories = InBundle.getStringArrayList(Constants.PRODUCT_CATEGORIES_LIST);
        }
    }

    public String getCompositeProductTitle()
    {
        return attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE) + " " +
                attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE) + " " +
                attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE);
    }

    public String getWeightUnit()
    {
        if (attributes.containsKey(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE))
        {
            return attributes.get(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE);
        }
        else
        {
            return null;
        }
    }

    private Bundle getBundle()
    {
        Bundle productData = new Bundle();

        if (name != null && !name.isEmpty())
        {
            productData.putString(Constants.PRODUCT_NAME, name);
        }

        if (ean != null)
        {
            productData.putParcelable(Constants.PRODUCT_EAN, ean);
        }

        if (attributes != null)
        {
            if (attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
            {
                productData.putString(Constants.PRODUCT_BRAND_ATTRIBUTE, attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE));
            }

            if (attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
            {
                productData.putString(Constants.PRODUCT_TITLE_ATTRIBUTE, attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE));
            }

            if (attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
            {
                productData.putString(Constants.PRODUCT_AMOUNT_ATTRIBUTE, attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE));
            }

            if (attributes.containsKey(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE))
            {
                productData.putBoolean(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE, Boolean.valueOf(attributes.get(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE)));
            }

            if (attributes.containsKey(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE))
            {
                productData.putString(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE, attributes.get(Constants.PRODUCT_BYWEIGHT_UNIT_ATTRIBUTE));
            }

            if (attributes.containsKey(Constants.PRODUCT_ORGANIC_ATTRIBUTE))
            {
                productData.putBoolean(Constants.PRODUCT_ORGANIC_ATTRIBUTE, Boolean.valueOf(attributes.get(Constants.PRODUCT_ORGANIC_ATTRIBUTE)));
            }

            if (attributes.containsKey(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE))
            {
                productData.putBoolean(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE, Boolean.valueOf(attributes.get(Constants.PRODUCT_FAIRTRADE_ATTRIBUTE)));
            }
        }

        if (images != null)
        {
            productData.putStringArrayList(Constants.PRODUCT_IMAGES_LIST, images);
        }

        if (videos != null)
        {
            productData.putStringArrayList(Constants.PRODUCT_VIDEOS_LIST, videos);
        }

        if (categories != null)
        {
            productData.putStringArrayList(Constants.PRODUCT_CATEGORIES_LIST, categories);
        }

        return productData;
    }

    // TODO: Maybe merge with isValid() ?
    public boolean isMissingRequiredAttributes()
    {
        boolean isMissingRequiredAttributes = false;

        if (!isValid())
        {
            isMissingRequiredAttributes = true;
        }

        if (attributes == null)
        {
            return false;
        }

        if (!attributes.containsKey(Constants.PRODUCT_BRAND_ATTRIBUTE))
        {
            isMissingRequiredAttributes = true;
        }

        if (!attributes.containsKey(Constants.PRODUCT_TITLE_ATTRIBUTE))
        {
            isMissingRequiredAttributes = true;
        }

        if (!attributes.containsKey(Constants.PRODUCT_AMOUNT_ATTRIBUTE))
        {
            isMissingRequiredAttributes = true;
        }

        return isMissingRequiredAttributes;
    }

    // TODO: Make this a proper, complete validation
    public boolean isValid()
    {
        //if name is empty or says null, return false
        boolean nameIsEmpty = name.isEmpty();
        boolean nameContainsNull = name.equals("null");

        return !(nameIsEmpty || nameContainsNull);
    }

    private boolean isErrorMessage(JSONObject InJSON)
    {
        return InJSON.has("error");
    }

    public boolean isSoldByWeight()
    {
        if (attributes.containsKey(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE))
        {
            return Boolean.valueOf(attributes.get(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE));
        }
        else
        {
            return false;
        }
    }

    private static HashMap<String, String> getAttributesFromJSON(JSONObject InJSON)
    {
        HashMap<String, String> outMap = new HashMap<>();

        if (InJSON != null)
        {
            Iterator<?> attributes = InJSON.keys();

            while (attributes.hasNext())
            {
                try
                {
                    String attributeKey = (String)attributes.next();
                    String attributeValue = InJSON.getString(attributeKey);

                    outMap.put(attributeKey, attributeValue);
                }
                catch (JSONException jex)
                {
                    //TODO: Create global exception handler
                    jex.printStackTrace();
                }
            }

        }

        return outMap;
    }

    private static ArrayList<String> getStringListFromJSON(JSONArray InJSON)
    {
        ArrayList<String> outList = new ArrayList<>();

        if (InJSON != null)
        {
            try
            {
                for (int i = 0; i < InJSON.length(); ++i)
                {
                    outList.add(InJSON.getString(i));
                }
            }
            catch (JSONException jex)
            {
                //TODO: Create global exception handler
                jex.printStackTrace();
            }
        }

        return outList;
    }

    // Begin Parcelable interface
    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        Bundle productBundle = getBundle();
        out.writeBundle(productBundle);
    }

    public static final Parcelable.Creator<OutpanProduct> CREATOR = new Parcelable.Creator<OutpanProduct>()
    {
        public OutpanProduct createFromParcel(Parcel in)
        {
            Bundle inBundle = in.readBundle();

            inBundle.setClassLoader(EAN.class.getClassLoader());
            EAN productEAN = inBundle.getParcelable(Constants.PRODUCT_EAN);

            return new OutpanProduct(productEAN, inBundle);
        }

        public OutpanProduct[] newArray(int size)
        {
            return new OutpanProduct[size];
        }
    };

    // End Parcelable interface
}
