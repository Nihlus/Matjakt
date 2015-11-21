package com.nihlus.matjakt.containers;

/**
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
