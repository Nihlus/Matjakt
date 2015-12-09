package com.nihlus.matjakt.database.containers;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Storage class for EAN codes, providing the raw code as well as additional compatibility data.
 */
public class EAN implements Parcelable
{
    // Remember to update the parcelable interface if you add more members here.
    private final String rawEAN;

    public enum EANType
    {
        EAN14,
        EAN13,
        EAN12,
        EAN8
    }

    public EAN(String InRawEAN)
    {
        this.rawEAN = InRawEAN;
    }

    public String getCode()
    {
        return rawEAN;
    }

    private EANType getType()
    {
        int eanLength = rawEAN.length();
        if (eanLength == 14)
        {
            return EANType.EAN14;
        }
        else if (eanLength == 13)
        {
            return EANType.EAN13;
        }
        else if (eanLength == 12)
        {
            return EANType.EAN12;
        }
        else if (eanLength == 8)
        {
            return EANType.EAN8;
        }
        else
        {
            return null;
        }
    }

    public boolean isInternalCode()
    {
        String eanType = this.rawEAN.substring(0, 2);
        return eanType.startsWith("2");
    }

    private boolean mayHaveEmbeddedWeight()
    {
        return rawEAN.startsWith("23");
    }

    private boolean mayHaveEmbeddedPrice()
    {
        return rawEAN.startsWith("20");
    }

    public double getEmbeddedWeight()
    {
        // Only allow this for EAN13 products
        if (getType() == EANType.EAN13 && mayHaveEmbeddedWeight())
        {
            String weightString = this.rawEAN.substring(8, 12);
            DecimalFormat df = new DecimalFormat("0.000");

            return Double.parseDouble(df.format(weightString));
        }

        return 0;
    }

    public double getEmbeddedPrice()
    {
        if (getType() == EANType.EAN13 && mayHaveEmbeddedPrice())
        {
            String weightString = this.rawEAN.substring(8, 12);
            DecimalFormat df = new DecimalFormat("#0.00");

            return Double.parseDouble(df.format(weightString));
        }

        return 0;
    }

    public EAN getEmbeddedPriceEAN()
    {
        // Only allow this for EAN13 products
        if (getType() == EANType.EAN13 && isInternalCode())
        {
            // Get the country code + product number
            String strippedEAN = this.rawEAN.substring(0, 8);

            // Zero out the weight
            String baseEAN = strippedEAN + "0000";

            // Recalculate the checksum and return the final code
            return new EAN(baseEAN + getChecksum(baseEAN));
        }

        return null;
    }

    // TODO: Optimize
    private static int getChecksum(String inEAN)
    {
        // Implementation of the mod10 algorithm
        ArrayList<Character> group1 = new ArrayList<>();
        ArrayList<Character> group2 = new ArrayList<>();


        // Split the raw code into two groups of every other number
        int i = 0;
        for (char c : inEAN.toCharArray())
        {
            if (i % 2 == 0)
            {
                // Add to group 2
                group2.add(c);
            }
            else
            {
                // Add to group 1
                group1.add(c);
            }
            ++i;
        }

        // Sum the groups
        int sumGroup1 = 0;
        int sumGroup2 = 0;
        for (char c : group1)
        {
            int actualNumber = Character.getNumericValue(c);
            sumGroup1 += actualNumber;
        }

        for (char c : group2)
        {
            int actualNumber = Character.getNumericValue(c);
            sumGroup2 += actualNumber;
        }

        // Multiply group 1 by three
        int multGroup1 = sumGroup1 * 3;

        // Add the multiplied group with the second group
        int finalSum = multGroup1 + sumGroup2;

        // Return the integer needed to reach the next multiple of 10
        if (finalSum % 10 == 0)
        {
            return 0;
        }
        else
        {
            return 10 - (finalSum % 10);
        }
    }

    // Begin Parcelable interface
    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(rawEAN);
    }

    public static final Parcelable.Creator<EAN> CREATOR = new Parcelable.Creator<EAN>()
    {
        public EAN createFromParcel(Parcel in)
        {
            return new EAN(in.readString());
        }

        public EAN[] newArray(int size)
        {
            return new EAN[size];
        }
    };

    // End Parcelable interface
}
