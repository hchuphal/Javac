/*******************************************************************************
 * Copyright (c) 2010 McGill University
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Barthelemy Dagenais (bart@cs.mcgill.ca)
 *******************************************************************************/
package ca.mcgill.cs.swevo.qualyzer.util;

import java.util.Collections;
import java.util.List;

/**
 * 
 */
public final class CollectionUtil
{

	private CollectionUtil()
	{

	}

	/**
	 * 
	 * @param <T>
	 * @param sortedList
	 * @param element
	 */
	public static <T extends Comparable<T>> void insertSorted(List<T> sortedList, T element)
	{
		int index = Collections.binarySearch(sortedList, element);
		sortedList.add(-index - 1, element);
	}

}
