# iPid Demo - A mobile application demo project for iPid

![Home Page](https://media.discordapp.net/attachments/778069978595196929/871827679278669925/Splash_Screen.png?width=279&height=589)

## Features

- Registration of users
- Sign up of users using 3rd party applications (i.e Google)
- Login and logout of users
- Fingerprint authentication on login
- Conversion of currencies
- Manage user profile
- Manage bank accounts
- Request for banking details from other users
- Request for payment from other users
- View details of request for payment transactions

### Testing
This mobile application is recommended to run in the following resolutions:

1. 1440 x 3040 _(e.g. Pixel 4 XL)_
2. 1440 x 3200 _(e.g. Xiaomi Mi 11)_
3. 1080 x 2400 _(e.g. S20 FE)_
4. 1080 x 2340 _(e.g. Oppo Find X)_
5. 1080 x 1920 _(e.g. Xiaomi Mi 3)_

For other resolutions not listed above, there will possibly be minor UI issues in terms of alignment, padding and other similar UI issues.

#### Test Data

##### Pay and Get Paid screen flows

In order to test the Pay and Get Paid screen flows, user needs to register accurately the full name and contact number of the users they wanted to send. 
These users registered in the database should have the same name and contact number to the users saved in the device's contact list. 
The contact numbers saved on the device needs to have the country code same as the registered contact number in the system.

The validation for the phone numbers do not include spaces.
For example, **+65 8432 4458** saved in the application and **+6584324458** saved on the test device will pass the validation, both phone numbers are technically the same.

The following users are pre-populated in the database and can be used for testing. Kindly add these users on your device's contact list for testing.

![Test data](https://media.discordapp.net/attachments/778069978595196929/876487750449254420/Screenshot_1629041306.png?width=270&height=585)

Email addresses:

1. adrien.baery@gmail.com
2. antoine.wong@gmail.com
3. amie.lee@gmail.com
4. david.tan@gmail.com
5. john.sy@gmail.com

`Password: ipiddemo0701`

There are currently four (4) currencies intially saved in the database.

1. Australia (AUD)
2. China (CNY)
3. India (INR)
4. Singapore (SGD)

The following currency conversions are also pre-populated in the database.

![Conversion](https://media.discordapp.net/attachments/718843842232844380/861523064989417482/Conversion.PNG)

## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

The software tools listed below are required to run the development setup.

1. [Android Studio](https://developer.android.com/studio/archive) - Latest Arctic Fox version (2020.3.1)
2. [Git](https://git-scm.com/download/win) - Latest version (2.32.0)
3. [SQLite browser](https://sqlitebrowser.org/blog/version-3-12-2-released/) - Latest version (3.12.2)

### Setup

1. Clone the repo in Android Studio by importing from VCS.   
`git clone https://bitbucket.org/ifis-demo/ipiddemo.git`   
![Get from VCS](https://media.discordapp.net/attachments/778069978595196929/863975410777456721/unknown.png?width=743&height=586)   
![Git clone](https://media.discordapp.net/attachments/778069978595196929/863975744967671818/unknown.png?width=774&height=586)   
2. If there is a popup indicating that the path `sdk.dir=C\:\\Users\\ifis-Nish\\AppData\\Local\\Android\\Sdk` doesn't exist, click **Yes**.   
3. After the above popup, you'll be seeing that the **build.gradle** file has been changed, the path is updated in `local.properties`.   
![Setup popups](https://media.discordapp.net/attachments/778069978595196929/863978141365305384/unknown.png?width=1003&height=587)   
3. Build the project. `Build > Rebuild Project`   
4. Create a virtual device/emulator under AVD Manager.   
![Click AVD Manager](https://media.discordapp.net/attachments/778069978595196929/863978588411002910/unknown.png?width=1003&height=443)   
![Create device](https://media.discordapp.net/attachments/778069978595196929/863978815238569994/unknown.png?width=1080&height=586)   
![Select hardware](https://media.discordapp.net/attachments/778069978595196929/863979001571180565/unknown.png?width=744&height=586)   
5. Click **Next** until **Finish**.   
6. Select the virtual device/emulator previously created.   
![Select device](https://media.discordapp.net/attachments/778069978595196929/863980093524541460/unknown.png?width=998&height=586)   
7. Run the application in the selected virtual device.   
![Running on device](https://media.discordapp.net/attachments/778069978595196929/871829079811301436/unknown.png?width=660&height=616)   
8. Navigate to **Contacts** application in the virtual device and add the Google account below. This is to automatically import the pre-populated contacts in the application database.   
- Username: `ipiddemo@gmail.com`   
- Password: `ipiddemo0701`   

### Database using Room API

This mobile application is using Android's Room persistence library in saving information in the database. It provides an abstraction layer over SQLite to allow more robust database access while harnessing the full power of SQLite.

#### Benefits

1. Compile-time verification of SQL queries. each `@Query` and `@Entity` is checked at the compile time, that preserves your app from crash issues at runtime and not only it checks the only syntax, but also missing tables.
2. Boilerplate code
3. Easily integrated with other Architecture components (like LiveData)

#### How to view the database

1. Install the latest version of [DB Browser for SQLite](https://sqlitebrowser.org/).
2. In Android Studio, after running the application go to ```View > Tool Windows > Device File Explorer```.
3. On the right panel of Android Studio, navigate to the ```Device File Explorer``` tab.
4. Click on ```data > data > com.ipid.demo```.
5. Expand the directory and right click on the databases folder and select ```Save As```. There will be three (3) DB files saved in this directory.
6. Open DB Browser for SQLite. If SQLite does not show from your list of applications, go to your Program Files directory. _(e.g. C:\Program Files\DB Browser for SQLite)_
7. Open the saved database (file without extension) in DB Browser for SQLite.

You will be seeing the screen below once you have successfully saved and opened the database of the application.

![Conversion](https://media.discordapp.net/attachments/718843842232844380/861794063740305411/unknown.png?width=946&height=587)

### Entity Relationship Diagram (ERD)

![ERD](https://cdn.discordapp.com/attachments/778069978595196929/864025755859025920/unknown.png)

## How to install application in device

Please refer to this [video](https://gifs.com/gif/r2RjOk) to know how to install the [APK](https://bitbucket.org/ifis-demo/ipiddemo/src/master/iPid_Demo_v1.apk) on your device.

## How to setup Fingerprint Sensor on the Android Studio Emulator

Please refer to this [video](https://codinginflow.com/tutorials/android/use-fingerprint-sensor-on-android-studio-emulator) to setup the fingerprint sensor on the emulator. Once you have setup the fingerprint authentication on the emulator, run the application and navigate to login screen. 

## Contact

Steven Gan - [steven.gan@ifis.com.sg](steven.gan@ifis.com.sg)

Keith John Omiping - [keith.omiping@ifis.com.sg](keith.omiping@ifis.com.sg)

Project Link: [https://bitbucket.org/ifis-demo/ipiddemo](https://bitbucket.org/ifis-demo/ipiddemo)
