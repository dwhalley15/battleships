## App module
This is the final Android application module. It contains all
Android specific code and uses the logic module for the game. To get started you will have to create
a new activity for the app (preferably with the new activity wizzard).

**Note** Do not forget to mark an activity as main by adding into the activity tag the following:
```xml
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />

        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
```