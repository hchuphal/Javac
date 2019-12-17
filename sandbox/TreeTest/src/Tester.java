/**
 * @author Jonathan Faubert
 *
 */
public class Tester
{
	public static void main(String[] args)
	{
		Project project = new Project("Project");
				
		Code code = new Code("a");
		project.getCodes().add(code);
		
		code = new Code("b");
		project.getCodes().add(code);
		code.getParents().add("g");
		code.getParents().add("a");


		code = new Code("c");
		project.getCodes().add(code);
		code.getParents().add("d");
		code.getParents().add("a");
		

		code = new Code("d");
		project.getCodes().add(code);

		code = new Code("e");
		project.getCodes().add(code);
		code.getParents().add("g/b");
		code.getParents().add("d");
		

		code = new Code("f");
		project.getCodes().add(code);
		code.getParents().add("g/b/e");
		code.getParents().add("a/c");
	
		
		code = new Code("g");
		project.getCodes().add(code);

		code = new Code("h");
		project.getCodes().add(code);
		code.getParents().add("g");

		TreeModel model = TreeModel.getTreeModel(project);
		
		System.out.println("The model:");
		System.out.println(model);
		
		System.out.println("The codes before update:");
		for(Code lCode : project.getCodes())
		{
			System.out.println(lCode);
			lCode.getParents().clear();
		}
		
		System.out.println("The codes after update:");
		model.updatePaths();
		for(Code lCode : project.getCodes())
		{
			System.out.println(lCode);
		}
		

		
	}
}
