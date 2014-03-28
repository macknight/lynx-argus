lynx-argus
==========
百眼巨人

该项目搭建了一个android非UI层代码的动态加载框架，配合对应server端api接口实现各个service的动态加载。
项目为分离view和model，在工程项目上将一个android项目拆分成3个module：biz，service，lib。其相互依赖关系为：biz->service->lib。
note：
biz层为业务层代码实现层，主要负责UI相关、流程跳转等；
service层为服务层，为biz层各业务模块提供所需的数据服务；
lib层为静态库层，为整个项目提供最为基础且稳定的功能（如http）支持。

动态加载框架的搭建使得service层的灵活性大大提升，当app出现业务数据修改，service功能改进甚至是一些bug的修复等时，传统的开发框架下，开发者
只能通过整个APP的升级来实现，这无疑是一种代价非常大，对用户侵入性极大的软件升级方式。动态加载的方式使得这些工作转移到幕后，用户在打开app后，
在对用户体验基本无损害的情况下完成相关升级。


实现原理：

Dalvik虚拟机如同其他Java虚拟机一样，在运行程序时首先需要将对应的类加载到内存中。而在Java标准的虚拟机中，类加载可以从class文件中读取，也可
以是其他形式的二进制流。因此，我们常常利用这一点，在程序运行时手动加载Class，从而达到代码动态加载执行的目的。利用DexClassLoader来实现
DexClassLoader dcl = new DexClassLoader(moduleLoader.srcPath(), moduleLoader.dexDir(), null, super.getClassLoader());