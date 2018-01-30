import http.client, urllib.request, urllib.parse, urllib.error, base64, requests, json
from io import BytesIO
from PIL import Image, ImageDraw

# Based on Azure Face API quickstart for Python: https://docs.microsoft.com/en-us/azure/cognitive-services/face/quickstarts/python

# Setup api access. Use access keys and url as given in the Azure portal
# when setting up Face API.
face_api_key = ''
url_base = 'https://northeurope.api.cognitive.microsoft.com/face/v1.0'


# Start crafting request. Determine which face attributes to analyze.
headers = {
    'Content-Type': 'application/json',
    'Ocp-Apim-Subscription-Key': face_api_key,
}
params = {
    'returnFaceId': 'true',
    'returnFaceLandmarks': 'false',
    'returnFaceAttributes': 'age,gender,headPose,smile,facialHair,glasses,emotion,hair,makeup,occlusion,accessories,blur,exposure,noise',
}

# Url to jpeg image to analyze. Change this to test on another image.
body = {'url': 'https://ak4.picdn.net/shutterstock/videos/2173574/thumb/1.jpg?i10c=img.resize(height:72)'}

def get_rectangle(face_rect):
    left = face_rect['left']
    top = face_rect['top']
    bottom = left + face_rect['height']
    right = top + face_rect['width']
    return ((left, top), (bottom, right))

try:
    # Send Face API request and parse response
    response = requests.request('POST', url_base + '/detect', json = body,
                                data = None, headers = headers, params = params)
    faces = json.loads(response.text)

    # Download and draw image
    img_tmp = requests.get(body['url'])
    img = Image.open(BytesIO(img_tmp.content))
    draw = ImageDraw.Draw(img)

    # Draw face rectangles for every face identified
    for face in faces:
        draw.rectangle(get_rectangle(face['faceRectangle']))
    img.show()

    # Print response
    print (json.dumps(faces, sort_keys=True, indent=2))

except Exception as e:
    print('Error:')
    print(e)
