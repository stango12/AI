
/**
 * Do NOT edit implemented methods.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Class Decision Tree
 */
public class DecisionTree {
	/**
	 * Class Attribute
	 */
	public static class Attribute {
		/**
		 * Name of the attribute
		 */
		private final String name;

		/**
		 * Index of the column in the instances that corresponds to value of
		 * this attribute. Leftmost column has an index of zero.
		 */
		private final int columnIndex;

		/**
		 * All possible values that the attribute may take.
		 */
		private final String[] possibleAttrValues;

		/**
		 * Constructor.
		 *
		 * @param attributeInfo
		 *            String representation of all the fields of this class.
		 */
		public Attribute(final String attributeInfo) {
			final String[] fields = attributeInfo.split("\\s+");
			columnIndex = Integer.parseInt(fields[0]);
			name = fields[1];
			possibleAttrValues = Arrays.copyOfRange(fields, 2, fields.length);
		}
	}

	/**
	 * Class Instance
	 */
	private static class Instance {
		/**
		 * Label of an instance
		 */
		private final Label label;

		/**
		 * Features of an instance. These are values of all the attributes for
		 * this instance.
		 */
		private final String[] features;

		/**
		 * Constructor.
		 *
		 * @param featuresAndLabel
		 *            String representation of the fields of this class.
		 */
		public Instance(final String featuresAndLabel) {
			final String[] fields = featuresAndLabel.split("\\s+");
			features = Arrays.copyOfRange(fields, 0, fields.length - 1);
			label = Label.valueOf(fields[fields.length - 1]);
		}

		/**
		 * Get value of an attribute in this instance. May use while creating
		 * splits on an attribute-value pair.
		 *
		 * @param attr
		 *            Attribute
		 * @return Value
		 */
		public final String getValueForAttribute(final Attribute attr) {
			return features[attr.columnIndex];
		}
	}

	/**
	 * Possible Labels of an instance
	 */
	private static enum Label {
		YES, NO
	}

	/**
	 * @param args
	 *            Paths to input files.
	 */
	public static void main(final String[] args) {
		final List<Attribute> attributeInfo = readAttributes(args[0]);
		final List<Instance> trainingData = readInstances(args[1]);
		
//		for(Attribute a : attributeInfo)
//		{
//			System.out.println(a.columnIndex + ": " + a.name);
//			for(int i = 0; i < a.possibleAttrValues.length; i++)
//				System.out.print(a.possibleAttrValues[i] + ", ");
//			System.out.println();
//		}
//		
//		
//		for(Instance i : trainingData)
//		{
//			System.out.println(i.label + ":");
//			for(int l = 0; l < i.features.length; l++)
//				System.out.print(i.features[l] + ", ");
//			System.out.println();
//		}
		
		final DecisionTree tree = new DecisionTree(attributeInfo, trainingData);
		if(args.length > 3) {
			if(args[3].equalsIgnoreCase("prune")){
				// Only for bonus credit.
				System.out.println("Pruning not implemented"); // Comment this out if you do implement!
				/*
				 * Add code to call your pruning method(s) here as appropriate.
				 * For example: tree.prune(arguments...);
				 */
			}
		}
		tree.print();
		System.out.println("\n");
		final List<Instance> testData = readInstances(args[2]);
		for (final Instance testInstance : testData) {
			System.out.println(tree.classify(testInstance));
		}
		System.out.println("\n");
		System.out.println("Training error = " + tree.computeError(trainingData) + " , Test error = "
				+ tree.computeError(testData));
	}

	/**
	 * To parse the attribute info file.
	 *
	 * @param attrInfoPath
	 *            file path
	 * @return List of attributes (objects of Class Attribute)
	 */
	public static List<Attribute> readAttributes(final String attrInfoPath) {
		final List<Attribute> attributes = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(attrInfoPath));

			while ((currentLine = br.readLine()) != null) {
				final Attribute attribute = new Attribute(currentLine);
				attributes.add(attribute);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return attributes;
	}

	/**
	 * To parse the training data (instances)
	 *
	 * @param trainDataPath
	 *            file path
	 * @return List of Instances.
	 */
	public static List<Instance> readInstances(final String trainDataPath) {
		final List<Instance> instances = new ArrayList<>();
		BufferedReader br = null;

		try {
			String currentLine;

			br = new BufferedReader(new FileReader(trainDataPath));
			br.readLine();

			while ((currentLine = br.readLine()) != null) {
				final Instance instance = new Instance(currentLine);
				instances.add(instance);
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}
		return instances;
	}

	/**
	 * The attribute which is the root of this tree.
	 */
	private final Attribute rootAttribute;

	/**
	 * True if this tree is a leaf.
	 */
	private final Boolean isLeaf;

	/**
	 * The label to be output if this tree is a leaf; Set to Null if the
	 * 'isleaf' flag is false.
	 */
	private final Label leafVal;

	/**
	 * List of the children trees sorted in the same order as the corresponding
	 * values of the root attribute.
	 */
	private final List<DecisionTree> children;

	/**
	 * Constructor. Builds the tree given the following parameters.
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 */
	public DecisionTree(final List<Attribute> attributeList, final List<Instance> instanceList) {
		isLeaf = shouldThisBeLeaf(instanceList, attributeList);
		if (isLeaf) {
			leafVal = computeLeafLabel(instanceList);
			rootAttribute = null;
			children = null;
			return;
		}
		leafVal = null;
		rootAttribute = computeBestAttribute(attributeList, instanceList);
		final List<Attribute> remAttributeList = getRemainingAttributes(attributeList, rootAttribute);
		children = new ArrayList<>();
		for (final String possibleVal : rootAttribute.possibleAttrValues) {
			children.add(new DecisionTree(remAttributeList,
				generateSplitForAttrVal(instanceList, rootAttribute, possibleVal)));
		}
	}

	/**
	 * Classify an instance. May also be used when evaluating performance on test data.
	 *
	 * @param instance
	 *            Instance to be classified.
	 * @return Label output
	 */
	public Label classify(final Instance instance) {
		// TODO Implement this!
		if(children == null || children.size() == 0	)
		{
			return leafVal;
		}
		else
		{
			for(int i = 0 ; i < children.size(); i++)
			{
				if(children.get(i) != null && rootAttribute != null)
					for(int j = 0; j < rootAttribute.possibleAttrValues.length; ++j){
						if(rootAttribute.possibleAttrValues[j].equals(instance.getValueForAttribute(rootAttribute))){
							return children.get(j).classify(instance);
						}

					}

			}
			return null;
		}
	}

	/**
	 * Computes the best attribute (least entropy)
	 *
	 * @param attributeList
	 *            List of attributes
	 * @param instanceList
	 *            List of instances
	 * @return The best attribute
	 */
	private Attribute computeBestAttribute(final List<Attribute> attributeList, final List<Instance> instanceList) {
		// TODO Implement this!
		
		//calculating total entropy
		int[] count = {0, 0};
		double totalCount = instanceList.size();
//		
//		for(int i = 0; i < instanceList.size(); i++)
//		{
//			if(instanceList.get(i).label == Label.YES)
//				count[0]++;
//			else
//				count[1]++;
//		}
//		
//		double totalEntropy = calculateEntropy(count[0], count[1]);
//		//System.out.println(totalEntropy + ", " + count[0] + ", " + count[1]);
		
		//minimum entropy means maximum info gain, so we're finding the smallest entropy
		double minEntropy = 1;
		Attribute minEntropyAttribute = attributeList.get(0);
		double currentEntropy;
		for(int i = 0; i < attributeList.size(); i++)
		{
			currentEntropy = 0;
			for(int j = 0; j < attributeList.get(i).possibleAttrValues.length; j++)
			{
				//clearing the count matrix
				count[0] = 0;
				count[1] = 0;
				for(int k = 0; k < totalCount; k++)
				{
					if(instanceList.get(k).features[attributeList.get(i).columnIndex].equals(attributeList.get(i).possibleAttrValues[j]))
					{
						if(instanceList.get(k).label == Label.YES)
							count[0]++;
						else
							count[1]++;
					}
				}
//				System.out.println(count[0] + ", " + count[1]);
				currentEntropy += ((count[0] + count[1]) / totalCount) * calculateEntropy(count[0], count[1]);
			}
			
			if(currentEntropy < minEntropy)
			{
				minEntropy = currentEntropy;
				minEntropyAttribute = attributeList.get(i);
			}
		}
		
		return minEntropyAttribute;
		
	}

	/**
	 * Evaluate performance of this tree.
	 * 
	 * @param trainingData
	 * @return
	 */
	private double computeError(final List<Instance> trainingData) {
		// TODO Implement this!
		double total = 0;
		for(int i = 0; i < trainingData.size(); ++i){
			if(trainingData.get(i).label != classify(trainingData.get(i)))
				++total;
		}
		return total / ((double) trainingData.size());
	}

	/**
	 * computes the label to be output at a leaf (which minimizes error on
	 * training data). If the given split is empty, you can assign any label for
	 * this leaf.
	 *
	 * @param instanceList
	 *            List of instances
	 * @return computed label
	 */
	private Label computeLeafLabel(final List<Instance> instanceList) {
		// TODO Implement this!
		if(instanceList.size() < 1)
			return Label.YES;
		return instanceList.get(0).label;
	}
	
	/**
	 * Split the data on an attribute-value pair.
	 * 
	 * @param instanceList
	 *            List of instances
	 * @param splitAttribute
	 *            Attribute to split on
	 * @param splitVal
	 *            Value to split on
	 * @return List of instances that constitute the said split (i.e. have the
	 *         given value for the given attribute)
	 */
	private List<Instance> generateSplitForAttrVal(final List<Instance> instanceList, final Attribute splitAttribute,
			final String splitVal) {
		// TODO Implement this!
		List<Instance> splitVals = new ArrayList<>();
		for(int i = 0; i < instanceList.size(); i++)
		{
			if(instanceList.get(i).features[splitAttribute.columnIndex].equals(splitVal))
				splitVals.add(instanceList.get(i));
		}
		return splitVals;
	}

	/**
	 * @param attributeList
	 *            List of candidate attributes at this subtree
	 * @param rootAttribute
	 *            Attribute chosen as the root
	 * @return List of remaining attributes
	 */
	private List<Attribute> getRemainingAttributes(final List<Attribute> attributeList, final Attribute rootAttribute) {
		// TODO implement this!
		attributeList.remove(rootAttribute);
		return attributeList;
	}

	/**
	 * Print a representation of this tree.
	 */
	public void print() {
		print(0);
	}

	/**
	 * Print relative to a calling super-tree.
	 *
	 * @param rootDepth
	 *            Depth of the root of this tree in the super-tree.
	 */
	private void print(final int rootDepth) {
		if (!isLeaf) {
			final Iterator<DecisionTree> itr = children.iterator();
			for (final String possibleAttrVal : rootAttribute.possibleAttrValues) {
				printIndent(rootDepth);
				System.out.println(rootAttribute.name + " = " + possibleAttrVal + " :");
				itr.next().print(rootDepth + 1);
			}
		} else {
			printIndent(rootDepth);
			System.out.println(leafVal);
		}
	}

	/**
	 * For formatted printing.
	 *
	 * @param n
	 *            Indent
	 */
	private void printIndent(final int n) {
		for (int i = 0; i < n; i++)
			System.out.print("\t");
	}

	/**
	 * Determine if this is simply a leaf, as a function of the given
	 * parameters.
	 *
	 * @param instanceList
	 *            List of instances
	 * @param attributeList
	 *            List of attributes
	 * @return True iff this tree should be a leaf.
	 */
	private boolean shouldThisBeLeaf(final List<Instance> instanceList, final List<Attribute> attributeList) {
		// TODO Implement this!
		for(int i = 0; i < instanceList.size() - 1; i++)
		{
			if(instanceList.get(i).label != instanceList.get(i + 1).label)
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determine the entropy for two values
	 * @param x The first value
	 * @param y The second value
	 * @return The calculated entropy
	 */
	private double calculateEntropy(int x, int y)
	{
		double val1 = x / (double)(x + y);
		double val2 = 1 - val1;
		
		if(val1 == 0)
			val1 = 1;
		if(val2 == 0)
			val2 = 1;
		return -val1 * Math.log(val1) / Math.log(2) - val2 * Math.log(val2) / Math.log(2);
	}
}