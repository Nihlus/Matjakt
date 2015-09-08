package com.nihlus.matjakt;

import java.util.List;

/**
 * Created by Jarl on 2015-08-16.
 * <p/>
 * This class should be the interface between raw database data from OutpanAPI2 and price data from
 * our own cloud service, as well as containing additional data.
 * <p/>
 * Product-related data such as fairtrade status should be stored with OutpanAPI2,
 * while we store prices. Ingredient lists are still undecided, as they require language support.
 * <p/>
 * TODO: Contact OutpanAPI2 and discuss this.
 */
public class EANProduct
{
    private EAN EANCode;
    private String ProductName;
    private String ManufacturerName;
    private String ThumbnailPath;
    private List<String> Ingredients;

    private boolean Organic;
    private boolean Fairtrade;

    public EANProduct(EAN EANCode)
    {
        this.setEANCode(EANCode);
    }

    public String getProductName()
    {
        return ProductName;
    }

    public void setProductName(String productName)
    {
        ProductName = productName;
    }

    public String getManufacturerName()
    {
        return ManufacturerName;
    }

    public void setManufacturerName(String manufacturerName)
    {
        ManufacturerName = manufacturerName;
    }

    public EAN getEANCode()
    {
        return EANCode;
    }

    public void setEANCode(EAN EANCode)
    {
        this.EANCode = EANCode;
    }
}
