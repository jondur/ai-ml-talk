# Azure GPU VM instructions
Training deep learning models can be very computationally costly. Using a GPU can cut training times to a fraction of the time taken using even a modern CPU. If it is not possible to acquire a GPU card, an alternative can be to use GPU-based cloud services during training. The main benefit is that the resources needed can be leased for only the training duration.

Here, instructions for provisioning a GPU-equipped virtual machine (VM) on [Microsoft Azure](https://azure.microsoft.com) are presented. Note that the basic principles here carry over to other cloud services as well, such as Amazon AWS and Google Cloud.

Depending on your operating system, make sure you have SSH and SCP clients. On Mac and Linux systems, you can use the built-in services accessible through the terminal. On Windows, [PuTTY](https://www.putty.org) for SSH and [WinSCP](https://winscp.net) are recommended. Then follow the instructions below.

1. Visit the Azure website and sign up for a free trial account. These are provided with $200 in credits which can be used for 30 days. After that, the account is converted to a pay-as-you-go account, so make sure to de-allocate any resources you have provisioned to avoid unpleasant surprises.
2. Login to the [Azure portal](https://portal.azure.com) using your trial account.
3. Using the menu bar to the left, click the new resource button towards the top, indicated by a plus sign.
4. Use the search bar to look for "Deep Learning Virtual Machine", and click it.
5. Read through the text and click "Create".
6. On the next page, fill in a name, username and password for your VM. Pick Linux as the OS (Windows is possible, but these instructions assume Linux), and create a new resource group with some name of your choice. Choose a location for your VM. Click "OK".
7. On the next page, click the "Virtual machine size" tab to see which VM types are available to you. It is recommended that you pick the cheapest out of the recommended types (NC6 at time of writing). Click "OK".
8. Review the setup and click "OK". Read through the text on the next page and click "Create".

After this, the VM will be provisioned. This may take ~10 minutes. After receiving notification that the VM has been successfully provisioned, follow the next steps.

1. Navigate to the newly created VM in the portal.
2. Find and copy the external IP connected to the VM.
3. Use an SSH client to connect the VM through the external IP.
4. When logged in to the VM, clone this repository using `git clone https://github.com/jondur/FIX.git`.
5. Navigate to the CNN demo, and run `train.py`. After the training is completed, the model will be saved as `model.h5`.
6. The model may now be evaluated on the VM, or the model can be downloaded for evaluation on a local computer.
7. To download the model from the VM, use an SCP client. On Linux or Mac systems this is done by modifying and running `scp username@external_vm_ip:/path/to/model.h5 /path/on/local/computer/to/download/to.h5`. On Windows, using WinSCP, use the GUI to navigate to the model file and download it.
8. Place the downloaded model file as `model.h5` in the same folder as the code for the demo and run `evaluate.py` to evaluate the performance using the downloaded model.

After completing training, be sure to remove any allocated VM resources to avoid getting bill for it later on. To remove, folow the instructions below:

1. In the Azure portal, navigate to the VM. Click remove close to the top and confirm.
2. When receiving notification that the VM has been removed, navigate to the resource group tab using the menu bar to the left.
3. Click on the resource group created during the VM provisioning.
4. Click "Remove resource group".
5. Confirm by entering the exact name of the resource group and confirm.
6. Wait for notification that the resource group has been deleted.
7. Use the left side meny bar and navigate to "All resources". Verify that the list contains no resources connected to the VM.
