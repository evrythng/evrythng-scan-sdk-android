# Setting up EVT Android SDK to your own project.

 ### Gradle 

Step 1: In your project's main directory look for the build.gradle file and copy this block of code below

```gradle
maven {
  url  "https://dl.bintray.com/imfreeincmobiledev/evt-mobile-sdk-test/"
}
```
your project's build.gradle 
```gradle
{
	....

	allprojects {
	    repositories {
	        jcenter()
	        /** 
	        * Place the block of code here in your build.gradle file
	        */
	    }
	}

}
```
- Note: This will be a temporary setup because we'll distribute this package in jcenter and mavenCentral for the official deployment.


Step 2: In your app module look for the build.gradle and look for this section in the file.

```gradle
dependencies {
    ...
}
```

Include this line: ``` compile 'com.evrythng.android.sdk:library:0.0.6' ```

Step 3: In your AndroidManifest.xml

Include this two permissions:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.CAMERA" />
```

# How to:

#### Create instance of EVTApiClient

```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
```

### Authentication
#### Create an anonymous user
Asynchronous Call
```java
/** Asynchronous Call */
EVTApiClient client = new EVTApiClient("APP_API_KEY");
client.auth().createAnonymousUser().execute(new ServiceCallback<User>() {

    @Override public void onResponse(User user) {

    }

    @OVerride public void onFailure(APIError error) {

    }
});
```
Synchronous Call
```java
 /** Note: Don't forget to call this on a non-ui blocking thread. */
 EVTApiClient client = new EVTApiClient("APP_API_KEY");
 try {
        User user = client.auth().createAnonymousUser().execute();
 }
 catch(APIException e) {

 }
```
#### Create a user
Asynchronous Call
```java
User user = new User();
user.setEmail("emai@gmail.com");
user.setPassword("yourpassword");
user.setFirstName("firstName");
user.setLastName("lastName");

EVTApiClient client = new EVTApiClient("APP_API_KEY");
client.auth().createUser(user).execute(ServiceCallback<User>() {

    @Override
    public void onResponse(User user) {
       //code to handle activation
    }

    @Override
    public void onFailure(APIError error) {

    }
});
```
Synchronous call
```java

EVTApiClient client = new EVTApiClient("APP_API_KEY");
try {

    User user = new User();
    user.setEmail("emai@gmail.com");
    user.setPassword("yourpassword");
    user.setFirstName("firstName");
    user.setLastName("lastName");

    user = client.auth().createUser(user).execute();
    //activate user based on response
}
catch(APIException e) {

}

```

#### Activate User

Asynchronous Call

```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
client.auth().validateUser(user.getUserId(), user.getActivationCode()).execute(ServiceCallback<User>() {
    @Override
    public void onResponse(User user) {
        //code to handle activation
    }

    @Override
    public void onFailure(APIError error) {

    }
		});
```
Synchronous Call
```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
// (Asynchronous Call) ** assumed that you already had the
// user object returned by the EVT Server. */
try {
    User user = client.auth().validateUser(user.getUserId(), user.getActivationCode()).execute();
}
catch(APIException e) {

}
```

##### Login user
Asynchronous Call
```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
client.auth().useCredentials("email", "password").execute(ServiceCallback<User>() {

    @Override
    public void onResponse(User user) {

    }

    @Override
    public void onFailure(APIError error) {

    }

});
```

Synchronous Call
```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
try {
    User user = client.auth().useCredentials("email", "password").execute();
} catch(APIException e) {

}
```
##### Logout user

Asynchronous Call
```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
client.auth().logoutUser("USER_API_KEY").execute(ServiceCallback<User>() {

    @Override
    public void onResponse(User user) {

    }

    @Override
    public void onFailure(APIError error) {

    }

});
```

Synchronous Call
```java
EVTApiClient client = new EVTApiClient("APP_API_KEY");
try {
    User user = client.auth().logoutUser("USER_API_KEY").execute();
} catch(APIException e) {

}

```

### Scanning

#### Using the SDK's built-in Scanning Camera.

```java
EVTApiClient client = new EVTApiClient("API_KEY");
client.scan().launchScannerCamera(<activity instance>);

/** Always include this to your activity where the launchScannerCamera()
 *  is called. This handles the camera response when there is a
 *  successful scan or closed the camera without scanning anything.
 **/

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    //code that parses the response from the Scanner Camera
    IntentResult intentResult = client.scan()
        .parseScannerResponse(requestCode, resultCode, data);

    if(intentResult != null) {
        client.scan().useIntentResult(intentResult).execute(mServiceCallback);
    }
}
```

##### Using the indentify method. Identify manually if the scanned value is a thng/product.

Asynchronous Call
```java
EVTApiClient client = new EVTApiClient("API_KEY");
client.scan().setMethod(ScanMethod.EAN_13).useIdentify("2323232132").execute(new ServiceCallback<List<ScanResponse>>() {
    @Override
    public void onResponse(List<ScanResponse> response) {

    }

    @Override
    public void onFailure(APIError error) {

    }
});
```
Synchronous Call

```java
EVTApiClient client = new EVTApiClient("API_KEY");
try {
    List<ScanResponse> response = client.scan().setMethod(ScanMethod.EAN_13).useIdentify("32332323").execute();
}
catch(APIException e) {

}
```

##### Using the usePhoto method. Scan a photo for a barcode

Asynchronous Call
```java
EVTApiClient client = new EVTApiClient("API_KEY");
client.scan().setMethod(ScanMethod.EAN_13).usePhoto("imagePath").execute(new ServiceCallback<List<ScanResponse>>() {
    @Override
    public void onResponse(List<ScanResponse> response) {

    }

    @Override
    public void onFailure(APIError error) {

    }
});
```
Synchronous Call

```java
EVTApiClient client = new EVTApiClient("API_KEY");
try {
    List<ScanResponse> response = client.scan().setMethod(ScanMethod.EAN_13).usePhoto("imagePath").execute();
}
catch(APIException e) {

}
```

