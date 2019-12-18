package ca.mcgill.cs.swevo.qualyzer.editors;

import org.eclipse.jface.text.source.Annotation;

import ca.mcgill.cs.swevo.qualyzer.model.CodeEntry;
import ca.mcgill.cs.swevo.qualyzer.model.Fragment;

public class CodeAnnotation extends Annotation {
	
	private Fragment fFragment;
	
	public CodeAnnotation() {
		
	}
	
	public CodeAnnotation(Fragment fragment, String fragmentType) {
		super(fragmentType, false, buildCodeString(fragment));
		fFragment = fragment;
	}
	
	/**
	 * Returns the text containing all the code names.
	 * @return
	 */
	private static String buildCodeString(Fragment fragment)
	{
		String toReturn = ""; //$NON-NLS-1$
		for(CodeEntry entry : fragment.getCodeEntries())
		{
			toReturn += entry.getCode().getCodeName() + ", "; //$NON-NLS-1$
		}
		
		return toReturn.isEmpty() ? null : toReturn.substring(0, toReturn.length() - 2);
	}

	/**
	 * Get the text fragment associated with this annotation.
	 * @return
	 */
	public Fragment getFragment()
	{
		return fFragment;
	}

	/**
	 * Update the fragment that is associated with this Annotation.
	 * @param fragment
	 */
	public void setFragment(Fragment fragment)
	{
		fFragment = fragment;
		setText(buildCodeString(fragment));
	}
	
	/**
	 * Updates the text field to match the Code Entries in the Fragment.
	 */
	public void updateText()
	{
		setText(buildCodeString(fFragment));
	}

}
