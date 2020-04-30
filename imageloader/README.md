
这是一个自定义功能很高的图片加载框架PhotoLoader第一版
可能会有一些小的bug，喜欢的铁汁麻烦给个好评。

使用方法:
```
  //Application中
  PhotoLoader.init(getApplicationContext());
  
  
  //业务逻辑中
  PhotoLoader.getInstance().load(Object src, View view);
  
  其中View为ImageView，也可以自定义，
  若View不是ImageView，那么请实现Chain接口并重写invoke方法，自定义自己的实现方法。
  
  重写后调用PhotoLoader.getInstance().addImageLoader(Chain chain);
  将自定义实现类加入链式调用头部，后续加载时会优先进入自定义Chain实现类中
  
  //若要暴露Drawable， Bitmap对象，请调用PhotoLoader.getInstance().apply(Object src, SimpleTarget simpleTarget);
  SimpleTarget为内部实现接口，可以直接重写接口，也可以自定义实现接口
  实现方法可参考BlurSimpleTarget
  
  //进阶用法
  //可以和activity的生命周期绑定，用以实现activity销毁时自动销毁子线程任务。
  PhotoLoader.with(LifeCycleOwner owner).load(Object src, View view);
  PhotoLoader.with(LifeCycleOwner owner).apply(Object src, SimpleTarget simpleTarget);
  
```
