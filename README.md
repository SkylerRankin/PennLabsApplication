# PennLabsApplication
### Submission to the PennLabs Android Challenge by Skyler Rankin
This application features two tabs, the first of which allows users to view information about various food trucks, and the second of which allows users to search for buildings located on Penn's campus.

##### Activities & Fragments
- MainActivity.java
    
    This activity houses the tab layout and view pager that displays the app's two tabs. The only thing that this file really needs to do is specify the TabAdapter, which gives the tab titles and the fragments for each tab. The actual content of the tabs is specified in the corresponding fragments FoodTruckFragment and BuildingsFragment. This section of the layout does have the toolbar however, and the nested fragments do not.
- AllMapActivity.java

    This activity shows a large map with markers for each of the food truck locations. Initially, the user's location is shown, but each location is clickable to show the name of that particular truck. The function onMapReady() is where the map fragment is configured with markers and what not, so it's important that this function is called after there is a marker to place. Thus, a variable called temp_map is stored, just so that at any time, onMapReady can be called, and it can be passed that temporary map variable and the markers can be placed. This class also performs the permission actions that many of the other activities perform. This consists of checking if location permissions have been granted. Since the permission is added to the Manifest.xml file, the application should automatically ask for the necessary permissions. When the user makes a decision, the onRequestPermissionsResult() callback is called. In this function, if the permission was granted, then the map is reloaded using the temporary map variable, but if not, then the user is notificed with a toast that says the functionality may not be totally workin. 
- BuildingActivity

  This activity shows the name of a particular building along with a map of its location relative to the user's location. Since the building activity is specific to a particular building, the name that the user searched for is passed as an extra with the intent that starts the actvity. With this name, the activity can make a GET request to the provided API with the name as the query parameter. When the data is returned, then the layout can be populated accordingly. If no match was found, then a special message is displayed. Since the API call contains lots of information and potentially downloading large images, it is done asynchronously in a separate thread via the AsyncTask<> class. Extending this class gives access to three methods; the doInBackground() method is where the main, time consuming work is done and where the UI is not touched at all. The onPostExecute() method is called after the background tasks are completed. This method does access the UI, and thus it takes the result of the background tasks and fills in the title and map with that information.
- FoodTruckActivity

  This activity shows information about a particular food truck, along with a map of its location relative to the user's location. In terms of code, this activity does not do anything new. It employs the same method of getting location permission of the other classes, and displays the map based on coordinates that come from the FoodTruckData class. The class does contain a static string called NAME which is used to make sure that when passing values into the intent when connecting to this activity, the keys for the values are consistent between the activities.
- FoodTruckFragment

  This fragment is used in the view pager of the main activity, and holds a recycler view that lists all of the food trucks, along with their information and rating. The recycler view does most of the work for this class, but the spinner does have some logic behind it. When a selection is made, the spinner tells the FoodTruckData class to sort the data based on the selection. Afterwards, the adapter that converts the data into views for the recycler view is refreshed. For the sorting based on location, gathering the user's location is not done immediately; a location object is passed to a callback. Thus, while the other sorting methods can just sort and then refresh the adapter, the location sort must wait until the location is actually recieved, then sort, and then update the adapter to see the changes.
- BuildingsFragment
  
  This fragment is used in the view pager of the main activity, and provides a text input region so that buildings can be searched for. The fragments adds a textChangedListener to the text input field so that when a user types enter, the new line is removed, and the Search button is clicked, triggering the BuildingActivity. The fragment also contains a list view that displays the recently searched terms. Whenever the user searches a building name, that query is added to the RecentSearchesData class. Then, when the fragment is returned to, that search along with the other 9 most recent searches are displayed. Since they are just text views, an array adapter is used to convert the array of search queries to list of text views. A click listener is added such that when an item in the list is clicked, the fragment will perform the same logic as if the search button has been clicked.

##### Services
All maps are rendered using the Google maps API. Foodtruck and building locations are displayed using the provided coordinates, but the user location is gathered using the FusedLocationProvider from the Google location API. In all cases, the location services utilize either the "Fine" or the "Coarse" location service, whichever the user has given permission for.

When loading information about buildings, the PennLabs API is used. This was completed using a thread separate from the UI in case it ends up taking a long time. Thus, the BuildingActivity executes the task, and when complete, the task takes the resulting JSON to populate the layout with the title of the building and its location on the map.

##### Other Java Files

- FoodTruckData
  
  This class holds all of the information about the food trucks in a static array so that the recyclerview can easily pull information from it. This class also exposes methods to sort the array of food trucks based on either their names, their distance from the user's location, or their rating.
- FoodTruckListAdapter
  
  This adapter specifies how to build each item in the recycler view's list. It takes a position as an integer, get the relevant information from the FoodTruckData class, and then fills out the specific layout that is displayed as the view is scrolled.

- RecentSearchesData

    This class holds the array of recently searched buildings. Ideally, it would write to a SQLite database to persist the searches, but for now it just holds them while the app is running. 

##### Other Resource files
The strings.xml file holds the strings that are statically set in the application. The styles.xml holds the themes for the appbars as well as the color scheme for the stars/rating bars.
