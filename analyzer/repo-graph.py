import matplotlib.pyplot as plt
import numpy as np

# Draws the plot showing how the number of Kotlin repos on GitHub was increasing until 2018-02-18.

data = np.array([(2011, 6), (2012, 14), (2013, 93), (2014, 240), (2015, 564), (2016, 2166), (2017, 8267), (2018, 39099),
                 (2019, 47753)]).reshape((9, 2))
plt.figure(1)
plt.scatter(data[:, 0], data[:, 1])
plt.plot(data[:, 0], data[:, 1])
plt.show()
