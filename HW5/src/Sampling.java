
public class Sampling 
{
	private static int[] count = new int[128];
	private static boolean a,b,c,d,e,f,g;
	private static int spot;
	private static int partBCount;
	private static int partCCount1;
	private static int partCCount2;
	
	public Sampling()
	{
		for(int i = 0; i < count.length; i++)
			count[i] = 0;
		partBCount = 0;
		partCCount1 = 0;
		partCCount2 = 0;
	}
	
	public static void main(String[] args)
	{
		doRunsNetwork1(100000);
		//System.out.println(partA());
		//System.out.println(partB());
		System.out.println(partC());
	}
	
	public static void doRunsNetwork1(int n)
	{
		for(int i = 0; i < n; i++)
		{
			spot = 0;
			//variable a
			double value = Math.random();
			if(value <= 0.6)
				a = true;
			else
			{
				a = false;
				spot += Math.pow(2,6);
			}
			
			//variable b
			value = Math.random();
			if(value <= 0.3)
				b = true;
			else
			{
				b = false;
				spot += Math.pow(2,5);
			}
			
			//variable c
			value = Math.random();
			if(a)
			{
				if(value <= 0.8)
					c = true;
				else
				{
					c = false;
					spot += Math.pow(2,4);
				}
			}
			else
			{
				if(value <= 0.7)
					c = true;
				else
				{
					c = false;
					spot += Math.pow(2,4);
				}
			}
			
			//variable d
			value = Math.random();
			if(a && b)
			{
				if(value <= 0.4)
					d = true;
				else
				{
					d = false;
					spot += Math.pow(2,3);
				}
			}
			else if(a && !b)
			{
				if(value <= 0.2)
					d = true;
				else
				{
					d = false;
					spot += Math.pow(2,3);
				}
			}
			else if(!a && b)
			{
				if(value <= 0.1)
					d = true;
				else
				{
					d = false;
					spot += Math.pow(2,3);
				}
			}
			else
			{
				if(value <= 0.8)
					d = true;
				else
				{
					d = false;
					spot += Math.pow(2,3);
				}
			}
			
			//variable e
			value = Math.random();
			if(c)
			{
				if(value <= 0.8)
					e = true;
				else
				{
					e = false;
					spot += Math.pow(2,2);
				}
			}
			else
			{
				if(value <= 0.5)
					e = true;
				else
				{
					e = false;
					spot += Math.pow(2,2);
				}
			}
			
			//variable f
			value = Math.random();
			if(c && d)
			{
				if(value <= 0.7)
					f = true;
				else
				{
					f = false;
					spot += 2;
				}
			}
			else if(c && !d)
			{
				if(value <= 0.6)
					f = true;
				else
				{
					f = false;
					spot += 2;
				}
			}
			else if(!c && d)
			{
				if(value <= 0.3)
					f = true;
				else
				{
					f = false;
					spot += 2;
				}
			}
			else
			{
				if(value <= 0.4)
					f = true;
				else
				{
					f = false;
					spot += 2;
				}
			}
			
			//variable g
			value = Math.random();
			if(d)
			{
				if(value <= 0.3)
					g = true;
				else
				{
					g = false;
					spot += 1;
				}
			}
			else
			{
				if(value <= 0.2)
					g = true;
				else
				{
					g = false;
					spot += 1;
				}
			}
			
			//used for partA
			count[spot]++;
			
			//count to know how many have E as true for part B
			if(!b)
				partBCount++;
			
			//count to find P(F = T A = F B = T)
			if(f && !a && b)
				partCCount1++;
			
			//count to find P(A = F B = T)
			if(!a && b)
				partCCount2++;
		}
	}
	
	public static int partA()
	{
		return count[41];
	}
	
	public static int partB()
	{
		return partBCount;
	}
	
	public static double partC()
	{
		return (double)partCCount1 / partCCount2;
	}
}
