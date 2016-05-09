import numpy as np
import math
import random

def sigmoidal(x):
	return 1 / (1 + math.exp(-x))

def main():
	#number of hidden nodes
	h = 5
	alpha = 0.2
	error = 0
	prevError = 0

	#opening the file
	f = open('mushroom-training.txt', 'r').readlines()
	f2 = open('mushroom-testing.txt', 'r').readlines()
	f3 = open('test-data.txt', 'w')

	#matrix weight
	i_weight = np.random.random((h, 100))
	o_weight = np.random.random((1, h))

	#bias stuff
	b1 = np.random.random((1, h))
	b2 = 1
	a = 0
	runnum = 0
	sameError = 0

	while(True):
		#split input values into lines
		l = f[a]
		p = l.replace(' ','').split(',')
		d = map(int, p)
		t = d.pop(0)

		y0 = np.zeros(100)
		for i in range(0, 100):
			y0[i] = d.pop(0)

		#hidden layer
		y1 = np.zeros(h)
		x1 = np.zeros(h)

		#output layer
		y2 = 0
		x2 = 0

		for i in range(0, h):
			for j in range(0, 100):
				y1[i] = y1[i] + y0[j] * i_weight[i][j]

		for i in range(0, h):
			y1[i] = y1[i] + b1[0][i]
			x1[i] = y1[i]
			y1[i] = sigmoidal(y1[i])

		for i in range(0, h):
			y2 = y2 + y1[i] * o_weight[0][i]

		y2 = y2 + b2
		x2 = y2
		y2 = sigmoidal(x2)

		#error = ((t - y2)**2) / 2
		# print "Run number: " + str(runnum)
		# print "Actual output: " + str(t) + " My Output: " + str(y2)
		# print "Average Error: " + str(error) + "\n"
			
		delta2 = sigmoidal(x2) * (1 - sigmoidal(x2)) * (y2 - t)
		delta1 = 0

		for i in range(0, h):
			delta1 = delta1 + sigmoidal(y1[i]) * (1 - sigmoidal(y1[i])) * o_weight[0][i] * delta2

		#updating w1, w2, b1, b2		
		for i in range(0, h):
			for j in range(0, 100):
				i_weight[i][j] = i_weight[i][j] - alpha * delta1 * y0[j]

		for i in range(0, h):
			o_weight[0][i] = o_weight[0][i] - alpha * delta2 * y1[i]
			b1[0][i] = b1[0][i] - alpha * delta1

		b2 = b2 - alpha * delta2
		a = a + 1
		if a >= len(f):
			a = 0
		runnum = runnum + 1

		#testing the weights and bias with the test data
		if runnum % 100 == 0:
			error = 0
			#alpha = alpha * 0.9
			for a2 in range(0, len(f2)):
				l2 = f2[a2]
				p2 = l2.replace(' ','').split(',')
				d2 = map(int, p2)
				t2 = d2.pop(0)
		
				y0 = np.zeros(100)
				for i in range(0, 100):
					y0[i] = d2.pop(0)

				#hidden layer
				y1 = np.zeros(h)
				x1 = np.zeros(h)

				#output layer
				y2 = 0
				x2 = 0

				for i in range(0, h):
					for j in range(0, 100):
						y1[i] = y1[i] + y0[j] * i_weight[i][j]

				for i in range(0, h):
					y1[i] = y1[i] + b1[0][i]
					x1[i] = y1[i]
					y1[i] = sigmoidal(y1[i])

				for i in range(0, h):
					y2 = y2 + y1[i] * o_weight[0][i]

				y2 = y2 + b2
				x2 = y2
				y2 = sigmoidal(x2)

				error = error + ((t2 - y2)**2) / 2
				
			error = error / len(f2)
			print "Run number: " + str(runnum)
			print "Actual output: " + str(t2) + " My Output: " + str(y2)
			print "Average Error: " + str(error) + "\n"
			# f3.write(str(runnum))
			# f3.write(": ")
			# f3.write(str(error))
			# f3.write("\n")
			if abs(prevError - error) < 0.001:
				sameError = sameError + 1
				if sameError >= 5:
					break
			else:
				sameError = 0
			prevError = error
main()