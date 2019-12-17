/*******************************************************************************
 * Copyright (c) 2011 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.util;

import java.util.Map;

/**
 * 
 */
public final class ParserUtil
{

	private ParserUtil()
	{
		
	}
	
	/**
	 * 
	 * @param c
	 * @param s
	 * @return
	 */
	public static boolean equal(char c, String s)
	{
		return s.charAt(0) == c;
	}

	/**
	 * 
	 * @param c
	 * @param array
	 * @return
	 */
	public static boolean in(char c, String[] array)
	{
		String s = String.valueOf(c);
		return in(s, array);
	}

	/**
	 * 
	 * @param s
	 * @param array
	 * @return
	 */
	public static boolean in(String s, String[] array)
	{
		for (String element : array)
		{
			if (s.equals(element))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param <K>
	 * @param <V>
	 * @param map
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static <K, V> V getDefault(Map<K, V> map, K key, V defaultValue)
	{
		if (map.containsKey(key))
		{
			return map.get(key);
		}
		else
		{
			return defaultValue;
		}
	}
}
