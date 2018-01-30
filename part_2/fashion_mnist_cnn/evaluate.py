import keras
from keras.datasets import fashion_mnist
from keras.models import load_model
from keras import backend as K


# Data dimensions. Images are 28x28 pixels, and the number of classes is 10.
image_rows, image_columns = 28, 28
number_of_classes = 10


# Load test dataset from Fashion MNIST and prepare it.
_, (x_test, y_test) = fashion_mnist.load_data()
y_test = keras.utils.to_categorical(y_test, number_of_classes)

if K.image_data_format() == 'channel_first':
    x_test = x_test.reshape(x_test.shape[0], 1, image_rows, image_columns)
    input_shape = (1, image_rows, image_columns)
else:
    x_test = x_test.reshape(x_test.shape[0], image_rows, image_columns, 1)
    input_shape = (image_rows, image_columns, 1)

# Normalize data ranges.
x_test = x_test.astype('float32') / 255


# Load saved model and evaluate it using the test dataset.
try:
    model = load_model('model.h5')
    print('Evaluating model on test dataset...')
    score = model.evaluate(x_test, y_test, verbose = 0)
    print('Test loss:', score[0])
    print('Test accuracy:', score[1])
except OSError:
    print('Cannot find model.h5! Re-run training to generate it.')
