package ar.com.manflack.ada.domain.util.security;

import org.springframework.stereotype.Component;

@Component
public class SecurityUtil
{
	static byte[] salt = new byte[] { 0x7d, 0x60, 0x43, 0x5f, 0x02, (byte) 0xe9, (byte) 0xe0, (byte) 0xae };
	static String IV = "AAAAAAAAAAAAAAAA";



	/**
	 * Protects PAN, Track2, CVC (suitable for logs).
	 *
	 * <pre>
	 * "40000101010001" is converted to "400001____0001"
	 * "40000101010001=020128375" is converted to "400001____0001=0201_____"
	 * "40000101010001D020128375" is converted to "400001____0001D0201_____"
	 * "123" is converted to "___"
	 * </pre>
	 * 
	 * @param s
	 *            string to be protected
	 * @return 'protected' String
	 */
	public static String protect(String s)
	{
		StringBuilder sb = new StringBuilder();
		int len = s.length();
		int clear = len > 6 ? 6 : 0;
		int lastFourIndex = -1;
		if (clear > 0)
		{
			lastFourIndex = s.indexOf('=') - 4;
			if (lastFourIndex < 0)
				lastFourIndex = s.indexOf('^') - 4;
			if (lastFourIndex < 0 && s.indexOf('^') < 0)
				lastFourIndex = s.indexOf('D') - 4;
			if (lastFourIndex < 0)
				lastFourIndex = len - 4;
		}
		for (int i = 0; i < len; i++)
		{
			if (s.charAt(i) == '=' || s.charAt(i) == 'D' && s.indexOf('^') < 0)
				clear = 1; // use clear=5 to keep the expiration date
			else if (s.charAt(i) == '^')
			{
				lastFourIndex = 0;
				clear = len - i;
			}
			else if (i == lastFourIndex)
				clear = 4;
			sb.append(clear-- > 0 ? s.charAt(i) : '_');
		}
		s = sb.toString();
		try
		{
			// Addresses Track1 Truncation
			int charCount = s.replaceAll("[^\\^]", "").length();
			if (charCount == 2)
			{
				s = s.substring(0, s.lastIndexOf('^') + 1);
				s = padright(s, len, '_');
			}
		}
		catch (Exception e)
		{
			// cannot PAD - should never get here
		}
		return s;
	}

	/**
	 * pad to the right
	 * 
	 * @param s
	 *            - original string
	 * @param len
	 *            - desired len
	 * @param c
	 *            - padding char
	 * @return padded string
	 * @throws Exception
	 *             if String's length greater than pad length
	 */
	public static String padright(String s, int len, char c) throws Exception
	{
		s = s.trim();
		if (s.length() > len)
			throw new Exception("invalid len " + s.length() + "/" + len);
		StringBuilder d = new StringBuilder(len);
		int fill = len - s.length();
		d.append(s);
		while (fill-- > 0)
			d.append(c);
		return d.toString();
	}
}
