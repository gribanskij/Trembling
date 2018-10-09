# Trembling
Android app: JAVA - MVP - Google Map - Google AdMob

# How to setup local environment

1. Clone the repository
2. Trembling is using [Firebase](http://www.firebase.com). First use Android Studio - Tools - Firebase to connecting.  
3. Trembling is using [AdMob](https://www.google.ru/admob). Second create an account at AdMob and get а Key.  
4. Trembling is using [Google Map](https://cloud.google.com/maps-platform). Third create an account at Google Cloud platform and get a Key.
4. Create new api_google_key.xml res file (release and debug versions) and put Keys: 
@string/google_maps_key - your key for map (debug/release)
@string/ADMOB_APP_ID - your key for adMob (debug/release)
@string/BANNER_ID - your key for adMob block (debug/release)
