/**
 *  OutpanError.java
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

import org.json.JSONException;
import org.json.JSONObject;

public class OutpanError
{
    public OutpanErrorType ErrorType;
    public String Message;

    public OutpanError(JSONObject json)
    {
        try
        {
            JSONObject errorObject = json.getJSONObject("error");

            this.ErrorType = OutpanErrorType.getEnumForValue(errorObject.getInt("code"));
            this.Message = errorObject.getString("message");
        }
        catch (JSONException jex)
        {
            jex.printStackTrace();
        }
    }

    enum OutpanErrorType
    {
        Unknown,
        AuthenticationRequired(1000),
        AuthenticationFailed(1001),
        GTINRequired(1002),
        InvalidGTIN(1003),
        EndpointNotFound(1005),
        NameAlreadyExists(1008),
        PostFieldNameRequired(1009),
        PostFieldValueRequired(1010),
        AttributeAlreadyExists(1011);


        private int value;

        OutpanErrorType()
        {
            this.value = -1;
        }

        OutpanErrorType(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        public static OutpanErrorType getEnumForValue(int value)
        {
            switch (value)
            {
                case 1000:
                {
                    return OutpanErrorType.AuthenticationRequired;
                }
                case 1001:
                {
                    return OutpanErrorType.AuthenticationFailed;
                }
                case 1002:
                {
                    return OutpanErrorType.GTINRequired;
                }
                case 1003:
                {
                    return OutpanErrorType.InvalidGTIN;
                }
                case 1005:
                {
                    return OutpanErrorType.EndpointNotFound;
                }
                case 1008:
                {
                    return OutpanErrorType.NameAlreadyExists;
                }
                case 1009:
                {
                    return OutpanErrorType.PostFieldNameRequired;
                }
                case 1010:
                {
                    return OutpanErrorType.PostFieldValueRequired;
                }
                case 1011:
                {
                    return OutpanErrorType.AttributeAlreadyExists;
                }
                default:
                {
                    return OutpanErrorType.Unknown;
                }
            }
        }
    }
}