/**
 *  OutpanProduct.java
 *
 *  Author:
 *       Jarl Gullberg <jarl.gullberg@gmail.com>
 *
 *  Copyright (c) 2016 Jarl Gullberg
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
 * Represents a product from the Outpan database.
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class OutpanProduct implements Parcelable
{
    public EAN ean;
    public URL outpanURL;
    public String name;
    public HashMap<String, String> attributes = new HashMap<>();
    public ArrayList<String> images = new ArrayList<>();
    public ArrayList<String> videos = new ArrayList<>();
    public ArrayList<String> categories = new ArrayList<>();

    /**
     * Creates a new Outpan product from a retrieved JSON object. Used for initializing products
     * retrieved from the database.
     *
     * This constructor will fail if the input object contains an explicit error field.
     *
     * @param InJSON The JSON to be turned into a product.
     */
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

    /**
     * Creates a new Outpan product from an EAN and a bundle of product data. Used for deserialization
     * when creating parcels, and for packaging up data to be sent to the database.
     *
     * @param InEAN     The EAN of the product
     * @param InBundle  The data to be contained in the product, such as brand or name.
     */
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

    /**
     * Gets the composite title in the form of "Brand Title Amount". Used as a shorthand to be
     * displayed to the user.
     *
     * @return The composite title.
     */
    public String getCompositeProductTitle()
    {
        return attributes.get(Constants.PRODUCT_BRAND_ATTRIBUTE) + " " +
                attributes.get(Constants.PRODUCT_TITLE_ATTRIBUTE) + " " +
                attributes.get(Constants.PRODUCT_AMOUNT_ATTRIBUTE);
    }

    /**
     * Gets the unit of the product's by-weight price. Usually g (grams) or kg (kilograms)
     *
     * @return The by-weight price unit.
     */
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

    /**
     * Gets a bundle containing the actual data of the product, as well as the name and EAN.
     * Used for parcel serialization and deserialization.
     *
     * @return A bundle with all of the product's data in it.
     */
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


    /**
     * Checks if the database returned an error instead of a valid JSON object.
     *
     * @param InJSON The JSON to check.
     * @return Whether or not the JSON contains an error message.
     */
    private boolean isErrorMessage(JSONObject InJSON)
    {
        return InJSON.has("error");
    }

    /**
     * Checks if the product is being sold by weight instead of having a fixed price.
     *
     * @return Whether or not the product is sold by weight.
     */
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

    /**
     * Sets whether or not the product is sold by weight.
     *
     * @param InIsSoldByWeight If the product is sold by weight or not.
     */
    public void setIsSoldByWeight(boolean InIsSoldByWeight)
    {
        attributes.put(Constants.PRODUCT_BYWEIGHT_ATTRIBUTE, String.valueOf(InIsSoldByWeight));
    }

    /**
     * Helper function that retrieves all Outpan attributes from a given JSON object (usually the
     * attributes sub-object.
     *
     * @param InJSON The JSON to be turned into a HashMap
     * @return A HashMap representing the product attributes.
     */
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

    /**
     * Helper function that turns a JSON array into an ArrayList. Used for transforming JSON arrays
     * into Outpan lists of videos, images, and categories.
     *
     * @param InJSON The JSON to be turned into an ArrayList.
     * @return An ArrayList containing all the values in the JSON array.
     */
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
