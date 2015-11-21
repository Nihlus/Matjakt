package com.nihlus.matjakt;

/**
 * Created by Jarl on 2015-08-16.
 * <p/>
 * Storage class for EAN codes, providing the raw code as well as additional compatibility data.
 */
public class EAN
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
}
