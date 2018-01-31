import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ANN 
{
	private JFrame ann_display = null;
	private final ANN_graphic graphics = new ANN_graphic(this);
	
	private static final String PATH = "src//resources//titanic.dat";
	private static final int TOTAL_NUM_OF_SAMPLES = 2200;
	private static final int NUM_OF_TRAINING_SAMPLES = 1800;
	private static final int OLD_WEIGHT = 0;
	private static final int NEW_WEIGHT = 1;
	private static final int TRAINING_LIMIT	= 20;
	private static final double LEARNING_RATE = 0.1;
	
	private ArrayList<Double> ticket_class = new ArrayList<Double>();
	private ArrayList<Double> age_class = new ArrayList<Double>();
	private ArrayList<Double> gender_class = new ArrayList<Double>();
	private ArrayList<Double> target_class = new ArrayList<Double>();

	public double input_layer[];
	public double hidden_layer[][][];
	public double output_layer[][][];
	
	private int training_count = 0;

	public ANN()
	{
		initiateLayers();
		setRandomWeights();
		// setCustomWeights();
		
		try {readData();} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
		
		initGraphics();
		train();
		classify();
	}

	private void initGraphics() 
	{
		ann_display = new JFrame();
		
		ann_display.setSize(1200, 500);
	    ann_display.setLocationRelativeTo(null);
	    ann_display.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    ann_display.setVisible(true);
		ann_display.add(graphics);	
	}

	private void initiateLayers() 
	{	
		// Column 1: Number of neurons in layer.
		// Column 2: Number of one-way-weights, per neuron in layer.
		// Column 3: A neuron can have two types of one_way-weights: previous-, 
		// and updated weight.
		
		input_layer =  new double[3];
		hidden_layer = new double[2][3][2];
		output_layer = new double[1][2][2];
	}

	@SuppressWarnings("unused")
	private void setCustomWeights()
	{
		hidden_layer[0][0][OLD_WEIGHT] = -1.9766086839899217;
		hidden_layer[0][1][OLD_WEIGHT] = -0.6703679422061039;
		hidden_layer[0][2][OLD_WEIGHT] = 1.2239430139586966;
		
		hidden_layer[1][0][OLD_WEIGHT] = -0.3481170978527557;
		hidden_layer[1][1][OLD_WEIGHT] = -0.9647975760224317;
		hidden_layer[1][2][OLD_WEIGHT] = 2.0258072222736394;
		
		output_layer[0][0][OLD_WEIGHT] = 1.6633567846203132;
		output_layer[0][1][OLD_WEIGHT] = -2.184401103737717;
	}
	
	private void setRandomWeights() 
	{
		int num_of_neurons;
		int num_of_weights;
		double rand_weight;
		Random rnd = new Random();
		
		System.out.print("Initializing weights randomly...\n\n");
		
		num_of_neurons = hidden_layer.length;
		num_of_weights = hidden_layer[0].length;
		
		for(int neuron = 0; neuron < num_of_neurons; neuron++)
		{
			for(int weight = 0; weight < num_of_weights; weight++)
			{
				rand_weight = 0.0;
				
				while(rand_weight == 0.0)
				{
					rand_weight = rnd.nextDouble();
				}
				
				if(rnd.nextBoolean())
				{
					hidden_layer[neuron][weight][OLD_WEIGHT] = -rand_weight;
				}
				else
				{
					hidden_layer[neuron][weight][OLD_WEIGHT] = rand_weight;
				}
			}
		}
		
		num_of_neurons = output_layer.length;
		num_of_weights = output_layer[0].length;
		
		for(int neuron = 0; neuron < num_of_neurons; neuron++)
		{
			for(int weight = 0; weight < num_of_weights; weight++)
			{
				rand_weight = 0.0;
				
				while(rand_weight == 0.0)
				{
					rand_weight = rnd.nextDouble();
				}
				
				if(rand_weight != 0 && rnd.nextBoolean())
				{
					output_layer[neuron][weight][OLD_WEIGHT] = -rand_weight;
				}
				else
				{
					output_layer[neuron][weight][OLD_WEIGHT] = rand_weight;
				}
			}
		}
		
		System.out.println("-INITIAL WEIGHTS-");
		System.out.println("");
		System.out.println("   * OUTPUT LAYER (Neuron 1):");
		System.out.println("     Weight 1 = " + output_layer[0][0][OLD_WEIGHT]);
		System.out.println("     Weight 2 = " + output_layer[0][1][OLD_WEIGHT]);
		System.out.println("");
		System.out.println("   * HIDDEN LAYER (Neuron 1):");
		System.out.println("     Weight 1 = " + hidden_layer[0][0][OLD_WEIGHT]);
		System.out.println("     Weight 2 = " + hidden_layer[0][1][OLD_WEIGHT]);
		System.out.println("     Weight 3 = " + hidden_layer[0][2][OLD_WEIGHT]);
		System.out.println("");
		System.out.println("   * HIDDEN LAYER (Neuron 2):");
		System.out.println("     Weight 1 = " + hidden_layer[1][0][OLD_WEIGHT]);
		System.out.println("     Weight 2 = " + hidden_layer[1][1][OLD_WEIGHT]);
		System.out.println("     Weight 3 = " + hidden_layer[1][2][OLD_WEIGHT]);
		System.out.println("");
	}
	
	@SuppressWarnings("resource")
	private void readData() throws IOException 
	{		
		/**Pointer to next character in file.*/
		FileReader fr = new FileReader(PATH);
		
		/**Integer representation of the character that was read.*/
		int ascii_code;
		
		/**The argument that is currently being built.*/
		String current_arg = "";
		
		/**A character representation of what currently is being read.*/
		char current_char;
		
		/**List containing all arguments, that has been read on the same line.*/
		ArrayList<String> args_read = new ArrayList<String>(); 
		
		double arg1 = 0.0;
		double arg2 = 0.0;
		double arg3 = 0.0;
		double arg4 = 0.0;
		
		while( (ascii_code = fr.read()) != -1)
		{
			//Reading whitespace or separator
			if(ascii_code == ' ' || 
			   ascii_code == '\r' || 
			   ascii_code == '\t' || 
			   ascii_code == 44)
			{
				if(current_arg.isEmpty())
				{
					continue;
				}
			
				// current string is now a complete argument, so we add it to 
				// the argument list
				args_read.add(current_arg);
				
				// reset current string
				current_arg = "";
				
				// go to next character
				continue;
			}
			
			// Reading newline...
			if(ascii_code == '\n')
			{
				args_read.add(current_arg);
				
				//reset current string
				current_arg = "";
				
				//add area...
				if(args_read.size() == 4)
				{
					// make sure the 1st argument can be interpreted as an 
					// integer and the next two as floats
					try 
					{			
						arg1 = Double.parseDouble( args_read.get(0) );
						arg2 = Double.parseDouble( args_read.get(1) );
						arg3 = Double.parseDouble( args_read.get(2) );
						arg4 = Double.parseDouble( args_read.get(3) );
						
						
						addPassenger(arg1, arg2, arg3, arg4);
						args_read.removeAll(args_read);
						continue;
					}
					
					// else the argument is not valid: move on to next line
					catch( Exception e ) 
					{	
						// reset arguments
						args_read.removeAll(args_read);
						
						// go to next line
						continue;
					}
				}
				else 
				{
					// reset arguments
					args_read.removeAll(args_read);
					
					// reset current string
					current_arg = "";
					
					// go to next line
					continue;
				}
			}
			
			// Numbers or '.' or '-'
			if( (ascii_code > 47 && ascii_code < 58) || 
				(ascii_code == 46) || 
				(ascii_code == 45) )
			{
				// convert ASCII character to string
				current_char = (char)ascii_code;
				
				//add character to the current argument
				current_arg = current_arg + Character.toString(current_char);
			}
			
			//go to next character
			continue;
		}
	}
	
	private void addPassenger(double ticket_arg, 
							  double age_arg, 
							  double gender_arg, 
							  double target_arg) 
	{	
		ticket_class.add(ticket_arg);
		age_class.add(age_arg);
		gender_class.add(gender_arg);
		target_class.add(target_arg);
	}

	private void train() 
	{
		int num_of_neurons;
		int inputs_per_neuron;
		int num_of_inputs;
		int outputIndex;
		double delta_weight;
		double outputs[] = new double[3];
		double deltaOut = 0.0;
		double deltaHidden = 0.0;
		double w, x, net, t, estimate;
		
		while(training_count != TRAINING_LIMIT)
		{
			for(int sample = 0; sample < NUM_OF_TRAINING_SAMPLES; sample++)
			{
				graphics.ticket_class = ticket_class.get(sample);
				graphics.age = age_class.get(sample);
				graphics.gender = gender_class.get(sample);
					
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				ann_display.repaint();
				
				
				input_layer[0] = ticket_class.get(sample);
				input_layer[1] = age_class.get(sample);
				input_layer[2] = gender_class.get(sample);
				
				//calculate hidden layer 
				
				num_of_neurons = 	hidden_layer.length;
				inputs_per_neuron = hidden_layer[0].length;
				num_of_inputs = 		input_layer.length;
				
				outputIndex = 0;
				net = 0.0;
				
				for(int neuron = 0; neuron < num_of_neurons; neuron++)
				{
					for(int input = 0; input < inputs_per_neuron; input++)
					{		
						x = input_layer[input];
						w = hidden_layer[neuron][input][OLD_WEIGHT];
						net = net + (x * w);
					}
					
					outputs[outputIndex] = sigmoid(net);
					outputIndex++;
				}
				
				//calculate output layer 
				
				num_of_inputs = hidden_layer.length;
				net = 0.0;
				estimate = 0.0;
				
				for(int inputIndex = 0; inputIndex < num_of_inputs; inputIndex++)
				{	
					x = outputs[inputIndex];
					w = output_layer[0][inputIndex][OLD_WEIGHT];
					net = net + (x * w);
				}
				
				estimate = sigmoid(net);
				outputs[2] = estimate;
				
				graphics.e = estimate;
				graphics.t = target_class.get(sample);
				
				if(target_class.get(sample) == -1)
				{
					t = 0.25;
				}
				else
				{
					t = 0.75;
				}
				
				num_of_inputs = hidden_layer.length;
				
				//update weights on output layer
				
				deltaOut = calcDeltaOut(t, estimate);
				
				for(int inputIndex = 0; inputIndex < num_of_inputs; inputIndex++)
				{
					delta_weight = LEARNING_RATE * deltaOut * outputs[inputIndex]; 
					
					output_layer[0][inputIndex][NEW_WEIGHT] = 
							(delta_weight + output_layer[0][inputIndex][OLD_WEIGHT]);
				}
				
				//update weights on hidden layer
				
				num_of_neurons = hidden_layer.length;
				inputs_per_neuron = input_layer.length;
				
 				for(int neuronIndex = 0; neuronIndex < num_of_neurons; neuronIndex++)
				{
 					estimate = 	outputs[neuronIndex];
					
					for(int inputIndex = 0; inputIndex < inputs_per_neuron; inputIndex++)
					{
						deltaHidden = calcDeltaHidden(
								estimate, 
								output_layer[0][neuronIndex][NEW_WEIGHT], 
								deltaOut);
						
						delta_weight = LEARNING_RATE * deltaHidden * input_layer[inputIndex];
						
						hidden_layer[neuronIndex][inputIndex][NEW_WEIGHT] = 
								(delta_weight + hidden_layer[neuronIndex][inputIndex][OLD_WEIGHT]);
					}
				}
				
				//set new weights to equal prev weights on all layers layer		
				
				//hidden layer
				num_of_neurons = hidden_layer.length;
				inputs_per_neuron = input_layer.length;
				
				for(int neuronIndex = 0; neuronIndex < num_of_neurons; neuronIndex++)
				{
					for(int inputIndex = 0; inputIndex < inputs_per_neuron; inputIndex++)
					{
						hidden_layer[neuronIndex][inputIndex][OLD_WEIGHT] = 
								hidden_layer[neuronIndex][inputIndex][NEW_WEIGHT];
					}
				}
				
				//output layer

				num_of_inputs = hidden_layer.length;
				
				for(int inputIndex = 0; inputIndex < num_of_inputs; inputIndex++)
				{
					output_layer[0][inputIndex][OLD_WEIGHT] = 
							output_layer[0][inputIndex][NEW_WEIGHT];
				}
				
			}//end of this sample
		
			training_count++;
			
		}//end of training session
		
		System.out.println("-TRAINING COMPLETE! CALCULATED WEIGHTS:\n");
		System.out.println("   - Learning rate multiplier: " + LEARNING_RATE);
		System.out.println("   - Number of training samples: " + 
						  (training_count * NUM_OF_TRAINING_SAMPLES) + "\n");
		
		System.out.println("   * OUTPUT LAYER (Neuron 1):");
		System.out.println("                   Weight 1 = " + output_layer[0][0][OLD_WEIGHT]);
		System.out.println("                   Weight 2 = " + output_layer[0][1][OLD_WEIGHT] + "\n");
		
		System.out.println("   * HIDDEN LAYER (Neuron 1):");
		System.out.println("                   Weight 1 = " + hidden_layer[0][0][OLD_WEIGHT]);
		System.out.println("                   Weight 2 = " + hidden_layer[0][1][OLD_WEIGHT]);
		System.out.println("                   Weight 3 = " + hidden_layer[0][2][OLD_WEIGHT] + "\n");
		
		System.out.println("   * HIDDEN LAYER (Neuron 2):");
		System.out.println("                   Weight 1 = " + hidden_layer[1][0][OLD_WEIGHT]);
		System.out.println("                   Weight 2 = " + hidden_layer[1][1][OLD_WEIGHT]);
		System.out.println("                   Weight 3 = " + hidden_layer[1][2][OLD_WEIGHT] + "\n");
	}
	
	@SuppressWarnings("unused")
	private void classify() 
	{
		System.out.println("CLASSIFICATION START... \n");
		int dead = 0;
		int live = 0;
		int correctGuess = 0;
		int incorrectGuess = 0;
		int targetDead = 0;
		int targetLive = 0;
		
		int numOfNeurons;
		int numOfWeights;
		int numOfInputs;
		double output[] = new double[3];
		double w, x, net, t, estimate;
		
		for(int sampleIndex = NUM_OF_TRAINING_SAMPLES; sampleIndex < TOTAL_NUM_OF_SAMPLES; sampleIndex++)
		{
//		for(int sampleIndex = 0; sampleIndex < TOTAL_NUM_OF_SAMPLES; sampleIndex++)
//		{
		
			graphics.ticket_class = ticket_class.get(sampleIndex);
			graphics.age = age_class.get(sampleIndex);
			graphics.gender = gender_class.get(sampleIndex);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ann_display.repaint();
			
			input_layer[0] = ticket_class.get(sampleIndex);
			input_layer[1] = age_class.get(sampleIndex);
			input_layer[2] = gender_class.get(sampleIndex);
			
			//calculate hidden layer 
			
			numOfNeurons = hidden_layer.length;
			numOfInputs = input_layer.length;
			net = 0.0;
			
			for(int neuronIndex = 0; neuronIndex < numOfNeurons; neuronIndex++)
			{
				for(int inputIndex = 0; inputIndex < numOfInputs; inputIndex++)
				{		
					x = input_layer[inputIndex];
					w = hidden_layer[neuronIndex][inputIndex][OLD_WEIGHT];
					net = net + (x * w);
				}
				output[neuronIndex] = sigmoid(net);

			}
			
			//calculate output layer 
			
			numOfNeurons = output_layer.length;
			numOfInputs = hidden_layer.length;
			net = 0.0;
			estimate = 0.0;
			
			for(int inputIndex = 0; inputIndex < numOfInputs; inputIndex++)
			{	
				x = output[inputIndex];
				w = output_layer[0][inputIndex][OLD_WEIGHT];
				net = net + (x * w);
			}
			
			estimate = sigmoid(net);
			output[2] = estimate;
			
			graphics.e = estimate;
			graphics.t = target_class.get(sampleIndex);
			
			if(target_class.get(sampleIndex) == -1)
			{
				targetDead++;
				
				if(estimate < 0.5)
				{
					System.out.print("[" + sampleIndex + "]" + " E: DEAD" + " T: DEAD OK!\n");
					correctGuess++;
					graphics.estimate_correct = 1;
					dead++;
				}
				else
				{
					System.out.print("[" + sampleIndex + "]" + " E: LIVE" + " T: DEAD \n");
					graphics.estimate_correct = -1;
					incorrectGuess++;
					live++;
				}
			}
			else
			{
				targetLive++;
				
				if(estimate >= 0.5)
				{
					System.out.print("[" + sampleIndex + "]" + " E: LIVE" + " T: LIVE OK!\n");
					graphics.estimate_correct = 1;
					correctGuess++;
					live++;
				}
				else
				{
					System.out.print("[" + sampleIndex + "]" + " E: DEAD" + " T: LIVE \n");
					graphics.estimate_correct = -1;
					incorrectGuess++;
					dead++;
				}
			}
		}//end of classification samples	
		
		System.out.println("");
		System.out.println("CLASSIFICATION COMPLETED \n");
	
		System.out.print("   * Total number of classifications: " + (targetDead + targetLive) + "\n");
		
		float total = ( (float)targetDead ) + ( (float) targetLive );
		float percentageTotal = ( ( ( correctGuess )/total ) * 100 );
		
		float percentageDeadCorrect   = 0.0f;
		float percentageLiveCorrect   = 0.0f;
		float percentageDeadIncorrect = 0.0f;
		float percentageLiveIncorrect = 0.0f;
		
		System.out.print("   * Classification correctness (total):  " + String.format("%.3f", percentageTotal) + " % \n\n");
		
		if(dead > targetDead)
		{
			percentageDeadIncorrect = ( ((float)dead - (float)targetDead)/dead );
			percentageDeadCorrect = ( 1 - percentageDeadIncorrect );
		}
		else
		{
			percentageDeadCorrect = ( (float)dead/(float)targetDead);
			percentageDeadIncorrect = ( 1 - percentageDeadCorrect );
		}
		
		if(live > targetLive)
		{
			percentageLiveIncorrect = ( ((float)dead - (float)targetLive)/live );
			percentageLiveCorrect = ( 1 - percentageLiveIncorrect );
		}
		else
		{
			percentageLiveCorrect = ( (float)live/(float)targetLive);
			percentageLiveIncorrect = ( 1 - percentageLiveCorrect );
		}
		
		System.out.print("   * Classification correctness (living): " + 
						 String.format("%.5f", percentageLiveCorrect * 100) + " % \n");
		
		System.out.print("   * Error (living)                     : " + 
						 String.format("%.5f", percentageLiveIncorrect * 100) + " % \n\n");

		System.out.print("   * Classification correctness (dying) : " + 
				 		 String.format("%.5f", percentageDeadCorrect * 100) + " % \n");
		
		System.out.print("   * Error (dying)                      : " + 
				 		 String.format("%.5f", percentageDeadIncorrect * 100) + " % \n\n");
					
		System.out.print("   * TARGET [dead]:     " + targetDead + "        ");
		System.out.print("TARGET [live]:     " + targetLive + "\n");
		System.out.print("   * ESTIMATION [dead]: " + dead +  "        ");
		System.out.print("ESTIMATION [live]: " + live + "\n");
	}

	private double calcDeltaOut(double target, double output)
	{ 
		double error = (target - output);
		double delta = error * output * (1 - output);
		
		return delta;
	}
	
	private double calcDeltaHidden(double output, double updatedWeightOnOutput, double deltaOut)
	{ 
		double change = (updatedWeightOnOutput * deltaOut);
		double delta = output * (1 - output) * change;
		
		return delta;
	}
	
	private double sigmoid(double net) 
	{
		double numerator = 1.0;
		double denominator = ( 1 + Math.pow( Math.E,(-1.0 * net)) );
	    
		return numerator/denominator;
	}
}