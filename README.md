# xSingleTouchView
common widget for Android

## how to use

edit your main build.gradle file

```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

add dependencies to your module build.gralde

```
compile 'com.github.xiaojinzi123:xSingleTouchView:v1.0.6'
```

## I will introduce the weiget

### xml

```
<com.move.xsingletouchview.XSingleTouchView
        android:id="@+id/x"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        app:control_size="4dp"
        app:control_size_scale="5"
        app:line_color="#0000FF"
        app:line_width="1px"
        app:src="@drawable/demo1"
        app:src_lb="@mipmap/ic_rotate"
        app:src_lt="@mipmap/ic_edit"
        app:src_rb="@mipmap/ic_scale"
        app:src_rt="@mipmap/ic_delete" />
```

### java code

```
XSingleTouchView x = (XSingleTouchView) findViewById(R.id.x);
        x.getXViewConfig().getCenterPoint().set(240,240);
        //x.getXViewConfig().setXXX......
        //x.getXViewConfig().setXXX......
        //x.getXViewConfig().setXXX......
        //x.getXViewConfig().setXXX......
        x.setOnDbClickListener(new XSingleTouchView.OnDbClickListener() {
            @Override
            public void onDbClick(View v) {
                Toast.makeText(MainAct.this, "双击", Toast.LENGTH_SHORT).show();
            }
        });
```

you can also use code getXViewConfig() to modify config

### result

<img src="./imgs/1.gif" width="300px" height="360px" />