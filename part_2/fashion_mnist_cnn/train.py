import time
import keras
import matplotlib.pyplot as plt
from keras.datasets import fashion_mnist
from keras.models import Sequential
from keras.layers import Dense, Dropout, Flatten, Conv2D, MaxPooling2D
from keras import backend as K

# Configurable parameters for training.
batch_size = 128
epochs = 20

# Set to <1 to use subset of data for speeding up debugging.
sample_fraction = 1

# Data dimensions. Images are 28x28 pixels, and the number of classes is 10.
image_rows, image_columns = 28, 28
number_of_classes = 10

# Load Fashion MNIST dataset using built in Keras function. Will download it if necessary.
(x_train, y_train), (x_test, y_test) = fashion_mnist.load_data()
sample_size_train = round(sample_fraction * x_train.shape[0])
sample_size_test = round(sample_fraction * x_test.shape[0])
x_train, y_train = x_train[:sample_size_train], y_train[:sample_size_train]
x_test, y_test = x_test[:sample_size_test], y_test[:sample_size_test]

if K.image_data_format() == 'channel_first':
    x_train = x_train.reshape(x_train.shape[0], 1, image_rows, image_columns)
    x_test = x_test.reshape(x_test.shape[0], 1, image_rows, image_columns)
    input_shape = (1, image_rows, image_columns)
else:
    x_train = x_train.reshape(x_train.shape[0], image_rows, image_columns, 1)
    x_test = x_test.reshape(x_test.shape[0], image_rows, image_columns, 1)
    input_shape = (image_rows, image_columns, 1)

# Normalize feature data ranges.
x_train = x_train.astype('float32') / 255
x_test = x_test.astype('float32') / 255

# Converts class labels to one-hot encoding, i.e. if we have 3 classes (labeled
# 0, 1, 2), a class label of 1 is converted to a one-hot vector [0, 1, 0].
y_train = keras.utils.to_categorical(y_train, number_of_classes)
y_test = keras.utils.to_categorical(y_test, number_of_classes)


# Build convolutional neural network model.
model = Sequential()
model.add(Conv2D(32, kernel_size = (3, 3), activation = 'relu', input_shape = input_shape))
model.add(Conv2D(32, (3, 3), padding = 'same', activation = 'relu'))
model.add(MaxPooling2D(pool_size = (2, 2)))
model.add(Dropout(0.25))
model.add(Conv2D(64, (3, 3), activation = 'relu', padding = 'same'))
model.add(Conv2D(64, (3, 3), activation = 'relu', padding = 'same'))
model.add(MaxPooling2D(pool_size = (2, 2)))
model.add(Dropout(0.5))
model.add(Conv2D(128, (3, 3), activation = 'relu', padding = 'same'))
model.add(Conv2D(128, (3, 3), activation = 'relu', padding = 'same'))
model.add(MaxPooling2D(pool_size = (2, 2)))
model.add(Dropout(0.5))
model.add(Flatten())
model.add(Dense(128, activation = 'relu'))
model.add(Dense(128, activation = 'relu'))
model.add(Dropout(0.5))
model.add(Dense(number_of_classes, activation = 'softmax'))

# Print summary of network layout and number of parameters
model.summary()

# Compile model. Use Categorical cross entropy as loss function, and Adam
# optimizer.
model.compile(loss = keras.losses.categorical_crossentropy,
              optimizer = keras.optimizers.Adam(),
              metrics = ['accuracy'])

# Fit mode using parameters and data defined.
start_time = time.time()
history = model.fit(x_train, y_train,
              batch_size = batch_size,
              epochs = epochs,
              verbose = 1,
              validation_data = (x_test, y_test))
print('Training time:', round(time.time() - start_time), 'seconds')

# Save model for later use.
model.save('model.h5')

# Print and plot results.
score = model.evaluate(x_test, y_test, verbose = 0)
print('Test loss:', score[0])
print('Test accuracy:', score[1])

accuracy = history.history['acc']
validation_accuracy = history.history['val_acc']
loss = history.history['loss']
validation_loss = history.history['val_loss']
epochs_x = range(len(accuracy))

plt.subplot(121)
plt.plot(epochs_x, accuracy, 'bo', label='Training accuracy')
plt.plot(epochs_x, validation_accuracy, 'b', label='Testing accuracy')
plt.title('Training and testing accuracy')
plt.legend()

plt.subplot(122)
plt.plot(epochs_x, loss, 'bo', label='Training loss')
plt.plot(epochs_x, validation_loss, 'b', label='Testing loss')
plt.title('Training and testing loss')
plt.legend()
plt.show()
