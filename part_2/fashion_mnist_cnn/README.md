# Small CNN demo
This is a small demo of a vision classification task using convolutional neural networks (CNNs). The task is to correctly classify images containing fashion items from one of ten different classes. The data used in the demo comes from the [Fashion-MNIST dataset](https://github.com/zalandoresearch/fashion-mnist) dataset, where 60,000 images are used for training and 10,000 for testing and evaluation.

The demo is written in Python 3, and the CNN classifier is implemented using [Keras](https://keras.io/), which is a high-level neural networks API running on top of popular deep learning frameworks such as [Tensorflow](https://www.tensorflow.org/), [Theano](http://deeplearning.net/software/theano/) and [CNTK](https://www.microsoft.com/en-us/cognitive-toolkit/). Keras offers a more high-level approach to neural network modeling, but the demo could have been implemented directly in Tensorflow just as well.

The neural network architecture used here is just one of virtually infinite ways of solving the task, and experimentation is encouraged. The current configuration takes ~2 hours to train using an i5 @ 2.4 GHz and ~2 minutes on a Geforce GTX 1070, a mainstream consumer GPU. If no GPU is available, training can be sped up using GPU equipped cloud VM. Instructions for setting up a VM on Azure, training the model and downloading the results are [available here](azure_vm.md).

To use this demo (assuming Python 3 is installed), make sure you have the following dependencies installed:
```
tensorflow
keras
matplotlib

```
Training is then done by running `train.py`, which will download the dataset when run the first time. After completing training, the model is saved and can be used for evaluation at a later stage. Evaluation after training is done by running `evaluation.py`.
