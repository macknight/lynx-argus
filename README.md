lynx-argus
==========
百眼巨人

该项目搭建了一个android非UI层代码的动态加载框架，配合对应server端api接口实现各个service的动态加载。
项目为分离view和model，在工程项目上将一个android项目拆分成3个module：biz，service，lib。其相互依赖关系为：biz->service->lib。
note：
biz层为业务层代码实现层，主要负责UI相关、流程跳转等；
service层为服务层，为biz层各业务模块提供所需的数据服务；
lib层为静态库层，为整个项目提供最为基础且稳定的功能（如http）支持。

动态加载框架的搭建使得service层的灵活性大大提升，当app出现非ui层bug，业务数据修改，service功能改进等时，传统的开发框架下，开发者
只能通过整个APP的升级来实现，这无疑是一种代价非常大的升级方式。service层动态升级的方式使得这些工作转移到幕后，用户在打开app后，
在对用户体验基本无损害的情况下完成相关升级。


实现原理：

Dalvik虚拟机如同其他Java虚拟机一样，在运行程序时首先需要将对应的类加载到内存中。而在Java标准的虚拟机中，类加载可以从class文件中
读取，也可以是其他形式的二进制流。因此，我们常常利用这一点，在程序运行时手动加载Class，从而达到代码动态加载执行的目的。然而Dalvik
虚拟机毕竟不算是标准的Java虚拟机，因此在类加载机制上，它们有相同的地方，也有不同之处。

Dalvik虚拟机类加载机制
DexClassLoader和PathClassLoader都属于符合双亲委派模型的类加载器（因为它们没有重载loadClass方法）。也就是说，它们在加载一个类之前，
会去检查自己以及自己以上的类加载器是否已经加载了这个类。如果已经加载过了，就会直接将之返回，而不会重复加载。
DexClassLoader和PathClassLoader其实都是通过DexFile这个类来实现类加载的。Dalvik虚拟机识别的是dex文件，而不是class文件。因此，我们
供类加载的文件也只能是dex文件，或者包含有dex文件的.apk或.jar文件。DexFile在加载类时，具体是调用成员方法loadClass或者loadClassBinaryName。
其中loadClassBinaryName需要将包含包名的类名中的”.”转换为”/”。 PathClassLoader是通过构造函数new DexFile(path)来产生DexFile对象的，
而DexClassLoader则是通过其静态方法loadDex（path, outpath, 0）得到DexFile对象。这两者的区别在于DexClassLoader需要提供一个可写的
outpath路径，用来释放.apk包或者.jar包中的dex文件。换个说法来说，就是PathClassLoader不能主动从zip包中释放出dex，因此只支持直接操作dex
格式文件，或者已经安装的apk（因为已经安装的apk在cache中存在缓存的dex文件）。而DexClassLoader可以支持.apk、.jar和.dex文件，并且会在指
定的outpath路径释放出dex文件。PathClassLoader在加载类时调用的是DexFile的loadClassBinaryName，而DexClassLoader调用的是loadClass。
因此，在使用PathClassLoader时类全名需要用”/”替换”.”。

在这里我们利用DexClassLoader来实现
DexClassLoader cl = new DexClassLoader(dexFile.getAbsolutePath(), dexOut.getAbsolutePath(), null, context.getClassLoader());

