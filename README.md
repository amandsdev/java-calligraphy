Calligraphy
===========

Custom fonts in Android the easy way.

Are you fed up of Custom views to set fonts? Or traversing the ViewTree to find TextViews? Yeah me too.

![alt text](https://github.com/chrisjenx/Calligraphy/raw/master/screenshot.png "ScreenShot Of Font Samples")

##Getting started

[Download from Maven Central (.jar)](http://search.maven.org/remotecontent?filepath=uk/co/chrisjenx/calligraphy/0.7.2/calligraphy-0.7.2.jar)

__OR__

Include the dependency:

```groovy
dependencies {
    compile 'uk.co.chrisjenx:calligraphy:0.7.+'
}
```
__IMPORTANT:__ The Maven artifact group id is now `uk.co.chrisjenx` __NOT__ `uk.co.chrisjenx.calligraphy` (this changed in 0.7+)

Add your custom fonts to `assets/` all font definition is relative to this path.

Define your default font using `CalligraphyConfig`.

```java
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CalligraphyConfig.initDefault("fonts/Roboto-Regular.ttf");
    //....
}
```

###*Important*

Wrap the Activity Context:

```java
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(new CalligraphyContextWrapper(newBase));
}
```

You're good to go!


---
#### Custom font per TextView
Of course:

```xml
<TextView
    android:text="@string/hello_world"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:fontFamily="fonts/Roboto-Bold.ttf"/>
```

#### Custom font in styles
No problem:

```xml
<style name="TextViewCustomFont">
    <item name="android:fontFamily">fonts/RobotoCondensed-Regular.ttf</item>
</style>
```

#### Custom font defined in Theme
```xml
<style name="AppTheme" parent="android:Theme.Holo.Light.DarkActionBar">
    <item name="android:textViewStyle">@style/AppTheme.Widget.TextView</item>
</style>

<style name="AppTheme.Widget"/>

<style name="AppTheme.Widget.TextView" parent="android:Widget.Holo.Light.TextView">
    <item name="android:fontFamily">fonts/Roboto-ThinItalic.ttf</item>
</style>
```

#### Custom attribute
Defined your custom attribute name in your `attr.xml` (We don't ship calligraphy with one, this is so it can stay a jar).

```xml
<attr name="fontPath"/>
```

Wrap the Activity Context:

```java
@Override
protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(new CalligraphyContextWrapper(newBase, R.attr.fontPath));
}
```

Then define in one of the places listed above, e.g:

```xml
<TextView
    android:text="@string/hello_world"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    fontPath="fonts/Roboto-Bold.ttf"/>
```


#FAQ

## Why piggyback off of fontFamily attribute?
Means that the library can compile down to a jar instead of an aar, as it is not dependant on any resources.
(This may of course change in the future if we run into issues)

As of 0.7+ you are able to define your own custom attributeId.

#Colaborators

- [@mironov-nsk](https://github.com/mironov-nsk)
- [@Roman Zhilich](https://github.com/RomanZhilich)
- [@Smuldr](https://github.com/Smuldr)
