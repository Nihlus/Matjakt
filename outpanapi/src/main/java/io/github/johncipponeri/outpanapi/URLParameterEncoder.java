package io.github.johncipponeri.outpanapi;

/**
 * https://stackoverflow.com/questions/724043/http-url-address-encoding-in-java/4605816#4605816
 */
public class URLParameterEncoder
{
    public static String encode(String input)
    {
        StringBuilder resultStr = new StringBuilder();
        for (char ch : input.toCharArray())
        {
            if (isUnsafe(ch))
            {
                resultStr.append('%');
                resultStr.append(toHex(ch / 16));
                resultStr.append(toHex(ch % 16));
            }
            else
            {
                resultStr.append(ch);
            }
        }
        return resultStr.toString();
    }

    private static char toHex(int ch)
    {
        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
    }

    private static boolean isUnsafe(char ch)
    {
        if (ch > 128 || ch < 0)
        {
            return true;
        }
        return " %$&+,/:;=?@<>#%".indexOf(ch) >= 0;
    }

}
