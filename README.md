# SlideView
仿微信左滑listView 出现删除按钮

> 原作者应该为任玉刚，这是《Android高手开发进阶》书中的一个例子，当然也有专门的博客，地址在这[高仿微信对话列表滑动删除效果](http://blog.csdn.net/singwhatiwanna/article/details/17515543)

### 修改
原作者是通过重写ListView和ItemView来实现功能的，感觉以后使用的话不方便，所以把所有的逻辑都放在了SlideView中。

### 使用
将原有view加入到SlideView中就可以了
```
SlideView slideView = new SlideView(this);
slideView.setContentView(view);
```

###微信效果图
![](image/display-1.jpg)
### Demo效果图
![](image/display-2.jpg)

### TODO
- 抽取成library
- 滑动菜单定制
