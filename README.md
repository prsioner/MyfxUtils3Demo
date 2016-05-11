# MyfxUtils3Demo
xUtils是一款实用的Android 开源框架
一.基本介绍

1.xUtils主要有四大模块：

<1.DbUtils模块：   

       android中的orm框架，一行代码就可以进行增删改查；
       支持事务，默认关闭；
       可通过注解自定义表名，列名，外键，唯一性约束，NOT NULL约束，CHECK约束等（需要混淆的时候请注解 表名和列名）；
       支持绑定外键，保存实体时外键关联实体自动保存或更新；
       自动加载外键关联实体，支持延时加载；
       支持链式表达查询，更直观的查询语义.      
<2.xUtils中的IOC框架

    android中的ioc框架，完全注解方式就可以进行UI，资源和事件绑定；
    新的事件绑定方式，使用混淆工具混淆后仍可正常工作；
    目前支持常用的20种事件绑定，参见ViewCommonEventListener类和包com.lidroid.xutils.view.annotation.event。

<3.HttpUtils模块：

        支持同步，异步方式的请求；
        支持大文件上传，上传大文件不会oom；
        支持GET，POST，PUT，MOVE，COPY，DELETE，HEAD，OPTIONS，TRACE，CONNECT请求；
        下载支持301/302重定向，支持设置是否根据Content-Disposition重命名下载的文件；
        返回文本内容的请求(默认只启用了GET请求)支持缓存，可设置默认过期时间和针对当前请求的过期时间。

<4.BitmapUtils模块:

        加载bitmap的时候无需考虑bitmap加载过程中出现的oom和android容器快速滑动时候出现的图片错位等现象；
        支持加载网络图片和本地图片；
        内存管理使用lru算法，更好的管理bitmap内存；
        可配置线程加载线程数量，缓存大小，缓存路径，加载显示动画等...

混淆时注意事项：
    添加Android默认混淆配置${sdk.dir}/tools/proguard/proguard-android.txt
    不要混淆xUtils中的注解类型，添加混淆配置：-keep class * extends java.lang.annotation.Annotation { *; }
    对使用DbUtils模块持久化的实体类不要混淆，或者注解所有表和列名称@Table(name="xxx")，@Id(column="xxx")，@Column(column="xxx"),@Foreign(column="xxx",foreign="xxx")；


二.xUtils在android studio 中的使用方法

    1.下载xUtils ：https://github.com/wyouflf/xUtils3/tree/master 

      2.把整个 xutils复制到app同级目录下

      3.setting.gradle 中

include ':app',':xutils'

      4.app 内的build.gradle 添加对xutils的依赖

dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:23.2.1'
    compile project(':xutils')
}

        5.将xutils文件夹下的build.gradle中的版本与最低版本调整到与创建工程时app中的一致

        6.添加如下代码到工程下的build.gradle中

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.0.0'
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.2'
        classpath 'com.github.dcendents:android-maven-gradle-plugin:1.3'

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

之后点同步就好了。
三.各个模块的使用
1.xUtils中的IOC框架

    使用xUtils的第一步就是必须创建自己的Application类继承自Application 在onCreate()中添加如下

     x.Ext.init(this);
     x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

MainActivity的代码是这样的

@ContentView(value = R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.ioc_btn)
    private Button ioc_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
    }
        @Event(value = R.id.ioc_btn, type = View.OnClickListener.class)
        private void onButtonClick (View v){
            switch (v.getId()) {
                case R.id.ioc_btn:
                    Toast.makeText(this, "IOC test", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

}

注意点：

1.使用IOC必须全部为私有，不然无效
2.所有用到IOC成员变量，使用的时候，必须在x.view().inject(this)后，如果写在前面，那么程序会崩溃。
2.使用xUtils3 加载网络图片

可以直接

x.image().bind(xutil_imv,"http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg");

也可以给图片设置参数

ImageOptions imageOptions = new ImageOptions.Builder()
       .setSize(DensityUtil.dip2px(120),DensityUtil.dip2px(120))//图片大小
        .setRadius(DensityUtil.dip2px(5))//圆角半径
        .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
       .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
       .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
        .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
        .build();
x.image().bind(xutil_imv,"http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",imageOptions);

注意点：

1.加载网络图片要访问网络权限

2.也可以将第2个参数设置为图片文件路径，那么将从SD卡中加载图片

3.xUtils3 的数据库操作

1.在MyApplication实例化一个数据库

//自定义了一个数据库文件的存储路径android.os.Environment.getExternalStorageDirectory().getPath() + "/tempImage/";
daoConfig = new DbManager.DaoConfig()
        .setDbName("myUtils_db")
        .setDbVersion(1)
        .setDbDir(new File(android.os.Environment
                .getExternalStorageDirectory().getPath() + "/tempImage/"))
        .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {

            }
        });

并提供对完的getDaoConfig()方法返回daoConfig 对象

2.MainActivity中根据daoConfig实例化一个dbManager就可以进行数据库的操作了

DbManager db = x.getDb(((MyApplication)getApplicationContext()).getDaoConfig());
try {
    List<xUtilsPerson> lyjPersons=db.selector(xUtilsPerson.class).findAll();
    for (int i=0;i<lyjPersons.size();i++){
        Log.e("MainActivity","LYJPerson"+i+".name="+lyjPersons.get(i).getName());
        Log.e("MainActivity","LYJPerson"+i+".name="+lyjPersons.get(i).getAge());
    }
} catch (DbException e) {
    e.printStackTrace();
}

4.xUtils 的网络请求

如下代码：

//xUtils的异步网络任务
RequestParams params = new RequestParams("http://blog.csdn.net/mobile/experts.html");
// 默认缓存存活时间, 单位:毫秒.(如果服务没有返回有效的max-age或Expires)
params.setCacheMaxAge(1000 * 60);
params.addQueryStringParameter("wd", "xUtils");
x.http().get(params, new Callback.CacheCallback<String>() {

    private boolean hasError = false;
    private String result = null;

    @Override
    public boolean onCache(String result) {
        // 得到缓存数据, 缓存过期后不会进入这个方法.
        // 如果服务端没有返回过期时间, 参考params.setCacheMaxAge(maxAge)方法.
        // * 客户端会根据服务端返回的 header 中 max-age 或 expires 来确定本地缓存是否给 onCache 方法.
        //   如果服务端没有返回 max-age 或 expires, 那么缓存将一直保存, 除非这里自己定义了返回false的
        //   逻辑, 那么xUtils将请求新数据, 来覆盖它.
        //
        // * 如果信任该缓存返回 true, 将不再请求网络;
        //   返回 false 继续请求网络, 但会在请求头中加上ETag, Last-Modified等信息,
        //   如果服务端返回304, 则表示数据没有更新, 不继续加载数据.
        //
        this.result = result;
        return false; // true: 信任缓存数据, 不在发起网络请求; false不信任缓存数据.
    }

    @Override
    public void onSuccess(String result) {
        // 注意: 如果服务返回304 或 onCache 选择了信任缓存, 这时result为null.
        if (result != null) {
            this.result = result;

        }
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
        hasError = true;
        Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
        if (ex instanceof HttpException) { // 网络错误
            HttpException httpEx = (HttpException) ex;
            int responseCode = httpEx.getCode();
            String responseMsg = httpEx.getMessage();
            String errorResult = httpEx.getResult();
            // ...
        } else { // 其他错误
            // ...
        }
    }

    @Override
    public void onCancelled(CancelledException cex) {
        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFinished() {
        if (!hasError && result != null) {
            // 成功获取数据
            Toast.makeText(x.app(), result, Toast.LENGTH_LONG).show();

        }
    }
});

直接返回了整个html 文件。


参考：http://www.tuicool.com/articles/jQnMRjB
