## ProNotes: Privacy policy

Welcome to the ProNotes app for Android

This is an open source Android app developed by Brett Tech Networking The source code is available on GitHub, the app is also available on Google Play.

As an avid Android user myself, I take privacy very seriously.
I know how irritating it is when apps collect your data without your knowledge.

I hereby state, to the best of my knowledge and belief, that I have not programmed this app to collect any personally identifiable information. All data (app preferences (like theme, etc.) and notes) created by you (the user) is stored on your device only, and can be simply erased by clearing the app's data or uninstalling it. 
In the case that you choose to use our backup feature you may contact us to request your data be erased on our end, we will provide proof of deletion after. We will in the future allow you to control the backup data yourself.

### Explanation of permissions requested in the app

The list of permissions required by the app can be found in the `AndroidManifest.xml` file:

https://github.com/Brett-Tech-Networking/ProNotes/blob/main/app/src/main/AndroidManifest.xml
<br/>

| Permission | Why it is required |
| :---: | --- |
| `android.permission.WRITE_EXTERNAL_STORAGE` | This is required to store notes on the device localy for access within the app |
| `android.permission.READ_EXTERNAL_STORAGE` | This is required to read the stored notes on the device within the app. |
| `android.permission.READ_MEDIA_IMAGES` | This is required so the user may access there device gallery in order to insert images within our application |
| `android.permission.FOREGROUND_SERVICE` | This allows us to use tools such as push notifications |

 <hr style="border:1px solid gray">

If you find any security vulnerability that has been inadvertently caused by me, or have any question regarding how the app protectes your privacy, please send me an email and I will surely try to fix it/help you.

Yours sincerely,  
Brett Hudson  
Brett Tech Networking  
android-app@brett-techrepair.com
