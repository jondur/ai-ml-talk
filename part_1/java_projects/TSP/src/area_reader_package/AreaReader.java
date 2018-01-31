package area_reader_package;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import ga_package.Area;

/**Reads a file ('areas.txt') in the resources folder.*/
public class AreaReader 
{
	/**File to read.*/
	private String path = "src//resources//areas.txt";
	
	/**A linked list containing all cities.*/
	ArrayList<Area> allAreas = new ArrayList<Area>();

	public AreaReader(){}
	
	@SuppressWarnings("resource")
	/**Create cities and roads from the read data in the file.*/
	public ArrayList<Area> readAreas() throws IOException
	{
		/**Pointer to next character in file.*/
		FileReader fr = new FileReader(path);
		
		/**Integer representation of the character that was read.*/
		int asciiCode;
		
		/**The argument that is currently being built.*/
		String currentArg = "";
		
		/**A character representation of what currently is being read.*/
		char currentChar;
		
		/**List containing all arguments, that has been read on the same line.*/
		ArrayList<String> argsRead = new ArrayList<String>(); 
		
		int arg1 = 0;
		float arg2 = 0;
		float arg3 = 0;
				
		while( (asciiCode = fr.read()) != - 1)
		{
			//Reading whitespace...
			if(asciiCode == ' ' || asciiCode == '\r' || asciiCode == '\t')
			{
				if(currentArg.isEmpty())
				{
					continue;
				}
			
				//current string is now a complete argument, so we add it to the argument list
				argsRead.add(currentArg);
				
				//reset current string
				currentArg = "";
				
				//go to next character
				continue;
			}
			
			//Reading newline...
			if(asciiCode == '\n')
			{
				argsRead.add(currentArg);
				
				//reset current string
				currentArg = "";
				
				int numOfArgs = argsRead.size();
				
				//add area...
				if(numOfArgs == 3)
				{
					//make sure the 1st argument can be interpreted as an integer
					//and the next two as floats
					try 
					{
						arg1 = Integer.parseInt( argsRead.get(0) );
						arg2 = Float.parseFloat( argsRead.get(1) );
						arg3 = Float.parseFloat( argsRead.get(2) );
						addArea(arg1, arg2, arg3);
						argsRead.removeAll(argsRead);
						continue;
					}
					//else the argument is not valid: move on to next line
					catch( Exception e ) 
					{
						//reset arguments
						argsRead.removeAll(argsRead);
						
						//go to next line
						continue;
					}
				}
				else 
				{
					//reset arguments
					argsRead.removeAll(argsRead);
					
					//reset current string
					currentArg = "";
					
					//go to next line
					continue;
				}
			}
			
			//Numbers or '.'
			if( (asciiCode > 47 && asciiCode < 58) || (asciiCode == 46) )
			{
				//convert ASCII character to string
				currentChar = (char)asciiCode;
				
				//add character to the current argument
				currentArg = currentArg + Character.toString(currentChar);
			}
			
			//go to next character
			continue;
		}
		
		return allAreas;
	}

	/**Creates new Area.
	 * @param id Area id.
	 * @param posX X coordinate of area.
	 * @param posY X coordinate of area.*/
	private void addArea(int id, float posX, float posY) 
	{	
		Area a = new Area();
		a.id = id;
		a.x = posX;
		a.y = posY;
		allAreas.add(a);
	}
}