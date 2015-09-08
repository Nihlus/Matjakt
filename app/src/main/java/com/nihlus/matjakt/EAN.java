package com.nihlus.matjakt;

/**
 * Created by Jarl on 2015-08-16.
 * <p/>
 * Storage class for EAN codes, providing the raw code as well as additional compatibility data.
 */
public class EAN
{
    private String Type;
    private String RawEAN;

    public EAN(String EANString, String Type)
    {
        this.RawEAN = EANString;
        this.Type = Type;
    }

    public EAN()
    {
        this.RawEAN = "";
        this.Type = "";
    }

    public String getType()
    {
        return Type;
    }

    public String getCode()
    {
        return RawEAN;
    }
}
