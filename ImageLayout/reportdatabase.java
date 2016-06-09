Thiet ke app giong hinh ve trong HeadFirst trang 442
###########################################################################
Android comes with SQLite classes:
The SQLite Helper: SQLiteOpenHelper class  enables you to create and manage databases.
The SQLite Database: The SQLiteDatabase class gives you access to the database.
Cursors: A Cursor lets you read from and write to the database.

###########################################################################
The SQLite Helper: SQLiteOpenHelper class  enables you to create and manage databases.
Some typical tasks that the SQLite helper can assist you with:
SQLite databases Create database Create table:
. Creating the database: When you first install
an app, the database file won’t exist. The SQLite helper will make sure the database file is created with the correct name and with the correct table structures installed.

. Keeping the database shipshape: The structure of the database will probably change over time, and the SQLite helper can be relied upon to convert an old version of a database into a shiny, spiffy new version, with all the latest database structures it needs.

. Getting access to the database: Our app shouldn’t
need to know all of the details about where
the database file is, so the SQLite helper can serve us with an easy- to-use database object whenever we need it.

###########################################################################
1. Create the SQLite helper
a. Specify the database
.First, we need to give the database a name. 
First, we need to give the database a name. By giving the database a name, we make sure that the database remains on the device when it’s closed. 
If we don’t, the database will only be created in memory, so once the database is closed, it will disappear.
-> Creating databases that are only held in memory can be useful when you’re testing your app.

.The second piece of information we need to provide is the version of the database. The database version needs to be an integer value, starting at 1. 
The SQLite helper uses this version number to determine whether the database needs to be upgraded.

class StarbuzzDatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_NAME = "starbuzz";
	private static final int DB_VERSION = 1;
	StarbuzzDatabaseHelper(Context context) {
		 super(context, DB_NAME, null, DB_VERSION);
	}

	...
 } 

 The constructor specifies details of the database, but the database doesn’t get created at that point. 
 The SQLite helper waits until the app needs to access the database, and the database gets created at that point.

You create a SQLite helper by writing a class that extends the SQLiteOpenHelper class. 
When you do this, you must override the onCreate() and onUpgrade() methods. These methods are mandatory.

The onCreate() method gets called when the database first gets created on the device. The method should include all the code needed to create the tables you need for your app.

The onUpgrade() method gets called when the database needs to be upgraded.
/*
Nói thêm vụ khi nào getWriteable và getReadable thì mới tạo database -> sử dụng asynctask
 */

2. Inside a SQLite database
The data inside a SQLite database is stored in tables. 
A table contains several rows, and each row is split into columns. 
A column contains a single piece of data, like a number of a piece of text.

Some columns can be specified as primary keys. A primary key uniquely identifies a single row. 
If you say that a column is a primary key, then the database won’t allow you to store rows with duplicate keys.

It’s an Android convention to call your primary key columns _id. 
Ignoring this convention will make it harder to get the data out of your database and into your user interface.

Storage classes and data-types
INTEGER Any integer type
TEXT Any character type
REAL Any floating-point number 
NUMERIC Booleans, dates, and date-times 
BLOB Binary Large Object
Unlike most database systems, you don’t need to specify the column size in SQLite. you can say very generally what kind of data you’re going to store, but you’re not forced to be specific about the size of data.

3. You create tables using Structured Query Language (SQL)
Every application that talks to SQLite needs to use a standard database language called Structured Query Language. 
SQL is used by almost every type of database. If you want to create the DRINK table, you will need to do it in SQL.

This is the SQL command to create the table:
CREATE TABLE DRINK (_id INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT,
DESCRIPTION TEXT, IMAGE_RESOURCE_ID INTEGER)

/*
dẫn thêm cấu trúc của SQL
 */

4. The onCreate() method is called when the database is created
// khi cần mới gọi hàm này
The SQLite helper is in charge of creating the SQLite database the first time it needs to be used. 
First, an empty database is created on the device, and then the SQLite helper onCreate() method is called.

The onCreate() method is passed a SQLiteDatabase object as a parameter. 
We can use this to run our SQL command with the method:
SQLiteDatabase.execSQL(String sql);

example: use db to create a empty table
@Override
public void onCreate(SQLiteDatabase db){
SQLite databases
        db.execSQL("CREATE TABLE DRINK ("
		+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
		+ "NAME TEXT, "
		+ "DESCRIPTION TEXT, "
		+ "IMAGE_RESOURCE_ID INTEGER););
}

5. Insert data using the insert() method
If you need to prepopulate a SQLite table with data, you can use the SQLiteDatabase insert() method. 
This method allows you to insert data into the database, and returns the ID of the record once it’s been inserted. 
If the method is unable to insert the record, it returns a value of -1.

db.insert(String table, String nullColumnHack, ContentValues values);

The nullColumnHack String value is optional and most of the time you’ll want to set it to null like we did in the code above. 
It’s there in case the ContentValues object is empty and you want to insert an empty row into your table. 
SQLite won’t let you insert an empty row without you specifying the name of at least one column; the nullColumnHack parameter allows you to specify one of the columns.

We want to use it to insert a row of data into the DRINK table, so we’ll populate it with the name of each column in the DRINK table, and the value we want to go in each field:
ContentValues drinkValues = new ContentValues();
drinkValues.put("NAME", "Latte");
drinkValues.put("DESCRIPTION", "Espresso and steamed milk");
drinkValues.put("IMAGE_RESOURCE_ID", R.drawable.latte);
db.insert("DRINK", null, drinkValues);

6. Update records with the update() method
 This method allows you to update records in the database, and returns the number of records it’s updated.

public int update (String table, ContentValues values,
String whereClause, String[] whereArgs)

watch it: // update toàn bộ record có trong table
If you set the last two parameters of the update() method to null,
ALL records in the table will be updated.
As an example, the code
db.update("DRINK", drinkValues,
null, null);
// will update all records in the DRINK table.


As an example, here’s how you’d change the value of the DESCRIPTION column to “Tasty” where the name of the drink is “Latte”:
ContentValues drinkValues = new ContentValues(); 
drinkValues.put("DESCRIPTION", "Tasty"); 
db.update("DRINK",
drinkValues,
"NAME = ?",
new String[] {"Latte"});

Multiple conditions
If you want to apply multiple conditions to your query, you need to make sure you specify the conditions in the same order you specify the values. As an example, here’s how you’d update records from the DRINK table where the name of the drink is “Latte”, or the drink description is “Our best drip coffee”.
db.update("DRINK", drinkValues,
"NAME = ? OR DESCRIPTION = ?",
new String[] {"Latte", "Our best drip coffee"});

The condition values must be Strings, even if the column you’re applying the condition to doesn’t contain Strings. If this is the case, you need to convert your values to Strings. As an example, here’s how you’d return DRINK records where the _id is 1:
db.update("DRINK", drinkValues,
"_id = ?",
new String[] {Integer.toString(1)});

###########################################################################
Delete records with the delete() method
The SQLiteDatabase delete() method works in a similar way to the update() method you’ve just seen. It takes the following form:
public int delete (String table, String whereClause,
String[] whereArgs)

As an example, here’s how you’d delete all records from the database where the name of the drink is “Latte”:
db.delete("DRINK", "NAME = ?",
new String[] {"Latte"});

###########################################################################
So far, you’ve seen how to create a SQLite database that your app will be able to use to persist data. 
But what if you need to make changes to the database at some future stage?

When the user installs a new version of your app which includes a different version of the database. 
If the SQLite helper spots that the database that’s installed is out of date, it will call either the onUpgrade() or onDowngrade() method.

When you need to change an app’s database, there are two key scenarios you have to deal with.
The first scenario is that the user has never installed your app before, and doesn’t have the database installed on her device. 
In this case, the SQLite helper creates the database the first time the database needs to be accessed, and runs its onCreate() method.
The second scenario is where the user installs a new version of your app which includes a different version of the database. 
If the SQLite helper spots that the database that’s installed is out of date, it will call either the onUpgrade() or onDowngrade() method.

The SQLite helper can tell whether the SQLite database needs updating by looking at its version number. 

###########################################################################
Different between SharePreference and Database
The directory where an app’s databases are stored is only readable by the app itself.

private static final int DB_VERSION = 1;

When the database gets created, its version number gets set to the version number in the SQLite helper, and the SQLite helper onCreate() method gets called.

When you want to update the database, you change the version number in the SQLite helper code. 
To upgrade the database, specify a number that’s larger than you had before, and to downgrade your database, specify a number that’s lower:

private static final int DB_VERSION = 2;

When the user installs the latest version of the app on her device, the first time the app needs to use the database, the SQLite helper checks its version number against that of the database on the device.
If the version number in the SQLite helper code is higher than that of the database, it calls the SQLite helper onUpgrade() method. 

If the version number in the SQLite helper code is lower than that of the database, it calls the onDowngrade() method instead.

@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//Your code goes here }
//

The version numbers are important, as you can use them to say what database changes should be made depending on which version of the database the user already has.
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
if (oldVersion == 1) {
//Code to run if the database version is 1
}
}

You can also use the version numbers to apply successive updates like this:
@Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
if (oldVersion == 1) {
//Code to run if the database version is 1
}
if (oldVersion < 3) {
// Code to run if the database version is 1 or 2 
// If the user has version 1 of the database, it will run both sets of code.
}

###########################################################################
The onDowngrade() method works in a similar way to the onUpgrade() method. Let’s take a look on the next page.
The onDowngrade() method isn’t used as often as the onUpgrade() method, as it’s used to revert your database to
a previous version. This can be useful if you release a version of your app that includes database changes, but you then discover that there are bugs. The onDowngrade() method allows you to pull the changes and set the database back to its previous version.

@Override
public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//Your code goes here 
if (oldVersion == 3) {
// Code to run if the database version is 3
}
}

###########################################################################
Get data from the database with a cursor
A cursor gives you access to database recordsets. You specify
what data you want access to, and the cursor brings back the records from the database. You then navigate through the records supplied by the cursor.

The SQLiteDatabase query() method lets you build SQL using a query builder
. Specify the table and columns
. Declare any conditions that restrict your selection
. Other stuff you can use queries for: If you expect your query to return several rows of data, you might find it useful to say what order you want the records to be in.  ...

public Cursor query(String table, String[] columns, //The table and columns you want to access.
	String selection,
String[] selectionArgs, // Use these if you want to apply conditions.
String groupBy, // Use these if you want to aggregate the data.
String having,
String orderBy) // Do you want the data in a particular order?

###########################################################################
Get a reference to the database
The query() method is defined in the SQLiteDatabase class, which means in order to call it we need to get a reference to your database. 

The SQLiteOpenHelper class implements a couple of methods that can help us with this: getReadableDatabase() and getWritableDatabase(). 
Each of these methods returns an object of type SQLiteDatabase, which gives us access to the database. 

getReadableDatabase() versus getWritableDatabase():
You’re probably thinking that getReadableDatabase() returns a read-only database object, and getWritableDatabase() returns one that’s writable. In fact, most of the time getReadableDatabase() and getWritableDatabase() both return a reference to the same database object. This database object can be used to read and write data to the database. 
So why is there a getReadableDatabase() method if it returns the same object as the getWriteableDatabase() method?

chụp hình trang 485
If you only need read data from a database, you’re best off using the getReadableDatabase() method. If you need to write to the database, use the getWritableDatabase() method instead.

###########################################################################
To read a record from a cursor, you first need to navigate to it
Whenever you need to retrieve values from a particular record in a cursor, you first need to navigate to that record. You need to do this irrespective of how many records are returned by the cursor.


There are four main methods you use to navigate through the records in a cursor. These methods are moveToFirst(), moveToLast(), moveToPrevious(), and moveToNext().
it returns true if it succeeds in moving to the  record, and false if it fails

. Getting cursor values
As an example, here’s the query we used to create our cursor:
Cursor cursor = db.query ("Drink",
new String[] {"NAME", "DESCRIPTION", "IMAGE_RESOURCE_ID"},
"_id = ?",
new String[] {Integer.toString(1)}, null, null,null);

The cursor has three columns: NAME, DESCRIPTION, and IMAGE_RESOURCE_ID. The first two columns, NAME and DESCRIPTION, contain data of type String. The third column, IMAGE_RESOURCE_ID, contains data of type int.
Suppose you wanted to get the value of the NAME column for the current record. NAME is the first column in the cursor, and contains String values. You’d therefore get the contents of the NAME column using the getString() method like this:
String name = cursor.getString(0);
Similarly, suppose you wanted to get the contents of the IMAGE_RESOURCE_ID column. This is the third column in the cursor, and contains int values, so you’d use the code:
int imageResource = cursor.getInt(2);

. Finally, close the cursor and database
Once you’ve finished retrieving values from the cursor, you need to close the cursor and the database in order to release their resources.
You do this by calling the cursor and database close() methods: 

cursor.close();
db.close();
###########################################################################
# xem về database leak -> singleton -> khỏi close cursor và database

// trong gist
// vấn đề close database và close cursor
###########################################################################
Notice position in recyclerview and _id in database
chụp hình table trang 448 dể thấy _id bắt đầu bằng 1 chứ ko phải bằng 0 / nhưng index trong recyclerview lại bắt đầu bằng 0

###########################################################################
# recyclerView vs cursorAdapter
How do we replace the datas in the RecyclerView?
We can use query() to get all datas in database and after that, we pass to RecyclerView.Adapter

That would work, but can you think of a reason why that might be a bad idea?

. xem lại cách kết hợp recyclerview với cursorAdapter
For our very small database, there’s no real problem in reading all of the data from the database and storing it in an array in memory. 
But if you have an app that stores a very large amount of data, then it’s going to take some time to read it all out of the database. It may also take a lot of memory to store it all in an array some place.

When the list is first displayed, it will be sized to fit the screen. Let’s say it has space to show five items.
A CursorAdapter is given a cursor when it’s constructed, and it will ask the cursor for data only when it needs it.
Even though the database table contains a lot of rows, the cursor only needs to read the first five.

The user scrolls the list.
As the user scrolls the list, the CursorAdapter asks the cursor to read a few more
rows from the database. If the user scrolls the list just a little, and uncovers one new item, the cursor will read one more row from the database.

-> So a CursorAdapter is a lot efficient.  It only reads the data it needs. That means it’s faster and takes up less memory, and speed and memory are both important things to keep in mind.
save state recyclerView
http://stackoverflow.com/questions/28236390/recyclerview-store-restore-state-between-activities

###########################################################################
tìm hiểu về how to refresh recycler
changeCursor not happen here

Cursors don’t automatically refresh
If the user chooses a new favorite drink by navigating through the app to DrinkActivity, the new favorite drink isn’t automatically displayed in the favorites list view in TopLevelActivity. 
This is because cursors retrieve data when the cursor gets created.

// fix lại theo fragment của ta
In our case, the cursor is created in the activity onCreate() method, so it gets its data when the activity is created. When the user navigates through the other activities, TopLevelActivity is stopped, not destroyed and re-created.

// Change the cursor with changeCursor()
 The changeCursor() method replaces the cursor currently used by a cursor adapter to a new one, and closes the old cursor.

 public void changeCursor(Cursor newCursor)

###########################################################################
AsyncTask
Databases can make your app go in sloooo-moooo....


Think about what your app has to do when it opens a database. It first needs to go searching through the flash to find the database file. If the database file isn’t there, it needs to go create a blank database. Then
it needs to run all of the SQL commands to create tables inside the database and any initial data it needs. Finally, it needs to fire off some queries to get the data out of there.
That takes time. For a tiny database like the one used in the Starbuzz app, it’s not a lot of time. But as a database gets bigger and bigger, that time will increase and increase.

Because it’s the main event thread that runs your event methods. If you just drop your database code into the onCreate() method (as we did in the Starbuzz app) then the main event thread will be busy talking to the database, instead of rushing off to look for any events from the screen or other apps. If your database code takes a long time, users will feel like they’re being ignored.
So the trick is to move your database code off the main event thread and run it in a custom thread in the background.

When you use databases in your app, it’s a good idea to run database code in a background thread, and update views with the database
data in the main event thread.

The AsyncTask class lets you perform operations in the background. When they’ve finished running, it then allows you to update views in the main event thread. If the task is repetitive, you can even use it to publish the progress of the task while it’s running.

You create an AsyncTask by extending the AsyncTask class, and implementing its doInBackground() method. The
code in this method runs in a background thread, so it’s the
perfect place for you to put database code. The AsyncTask
class also has an onPreExecute() method that runs before doInBackground(), and an onPostExecute() method that runs afterward. There’s an onProgressUpdate() method if you need to publish task progress.

private class MyAsyncTask extends AsyncTask<Params, Progress, Result>
protected void onPreExecute() {
//Code to run before executing the task
}
protected Result doInBackground(Params... params) { //Code that you want to run in a background thread
}
protected void onProgressUpdate(Progress... values) {
//Code that you want to run to publish the progress of your task
}
protected void onPostExecute(Result result) {
//Code that you want to run when the task is complete
} }

=> In an ideal world, all of your database code should run in the background. 

