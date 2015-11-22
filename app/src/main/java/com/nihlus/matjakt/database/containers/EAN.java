package com.nihlus.matjakt.database.containers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Storage class for EAN codes, providing the raw code as well as additional compatibility data.
 */
public class EAN implements Parcelable
{
    private final String RawEAN;

    public EAN(String InRawEAN)
    {
        this.RawEAN = InRawEAN;
    }

    public String getCode()
    {
        return RawEAN;
    }

    // Begin Parcelable interface
    public int describeContents()
    {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(RawEAN);
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
}
