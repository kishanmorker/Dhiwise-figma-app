Identify and render figma UI dynamically in android at runtime.

This project covers below points:

1. Android Screen UI for inputting Figma Token and file data.
2. Asynchronously fetch figma UI json data from figma Api. And store it in the device storage.
3. Access figma json from the storage and Identify Android view elements like: EditText, Button,
   Text, and Image.
4. Generate json Hierarchy for the Android views and its properties.

Mobile screen UI consists:

- Two edit text for inputting Figma URL and figma Token.
- Two buttons:  Set defaults and Submit.
- Set defaults: which will set default figma url and Figma token in the EditText.
- Submit: It will call Figma Api and process api response to identify and generate json Hierarchy
  for Login and Signup screens.

**Notes:**
To extract Login and SignUp screen node JSON data from the main Figma response, I currently rely on
hardcoded Node IDs.
Login screen’s node ID: 1409:1516
SignUp screen’s node ID: 1409:1535

Output json Hierarchy for screens will be as list of below given custom object:


  {
  "type": "",
  "id": "",
  "name": "",
  "properties": {
  "hexColorCode": "",
  "width": 0.0,
  "height": 0.0,
  "background": [
  {
  "blendMode": "",
  "color": {
  "a": 1.0,
  "b": 0.0,
  "g": 0.0,
  "r": 0.0
  },
  "type": ""
  }
  ],
  "textStyle": {
  "fontFamily": "",
  "fontPostScriptName": "",
  "fontSize": 0.0,
  "fontWeight": 0,
  "letterSpacing": 0.0,
  "lineHeightPercent": 0.0,
  "lineHeightPx": 0.0,
  "lineHeightUnit": "",
  "textAlignHorizontal": "",
  "textAlignVertical": "",
  "textAutoResize": ""
  },
  }
  }
