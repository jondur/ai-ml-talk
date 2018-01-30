# Azure Face API demo
Small demo written in Python showcasing the [Azure Face API](https://azure.microsoft.com/en-us/services/cognitive-services/face/). Requires pillow, which can be installed e.g. using pip:
```
pip install pillow
```
Before running the demo, a Face API account needs to be set up. This is done by following the instructions below.
1. Login to the Azure portal (if necessary, sign-up for an account for which you will be given a trial period of 30 days and $200 in credits).
2. Go to "Create a resource" (marked by a plus sign) to the top left.
3. Search for Face API in the search field, and click on it.
4. Read the text and click "Create".
5. Fill in a name for the resource, and appropriate location. Pick the lower price tier (F0) and choose to create a new resource group with your choice of name.
6. Accept the terms, and also click to pin to dashboard before clicking "Create".
7. After a while you should receive notification that the resource has been created, at which point you can navigate to it using the dashboard.
8. If everything went smoothly, you should be able to pick up your API keys by clicking the quick start link, or "Keys" under the "Resource Management" menu. Copy any of the keys and paste it in the appropriate place in the Python source code.
9. Make sure the location in the url_base variable in the Python source code file is in agreement with the location you chose during setup.

At this point you should be able to run the demo. Face API will analyze an image and return the results. The input image can be changed by changing the body variable in the source code.

After completing the demo, remember to tear down the Face API resource to avoid unpleasant surprises. This is done by following the instructions below.
1. Login to the Azure portal.
2. Navigate to your Face API resource.
3. Click "Overview" in the left side menu.
4. Click "Delete". Confirm.
5. Navigate to "Resource groups" in the far left side menu.
6. Click on the resource group corresponding to your Face API resource.
7. Click "Delete".
8. Confirm by typing the resource group name in the field.
9. Verify that the Face API resource and resource group have been deleted.
