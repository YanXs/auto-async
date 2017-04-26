# auto-async
auto-async是一个基于java APT自动生成异步接口和Facade抽象实现类的库

## Download
```xml
<dependency>
    <groupId>net.vakilla</groupId>
    <artifactId>auto-async</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 使用方法

* Example 1: 生成异步接口

```java
@AutoAsync
public interface SimpleService {

    String echo(String message);

    @Asyncable
    List<Complex> complex(Map<String, List<Date>> map);
}
```
自动生成接口

```java
@AutoGenerated("net.vakilla.auto.async.processor.AutoAsyncProcessor")
public interface Unified_SimpleService extends SimpleService {
  ListenableFuture<List<Complex>> async_complex(Map<String, List<Date>> map);
}
```

* Example 2: 生成异步接口和抽象实现类

```java
@AutoAsync(generateFacade = true)
public interface SimpleService {

    String echo(String message);

    @Asyncable
    List<Complex> complex(Map<String, List<Date>> map);
}
```
自动生成接口

```java
@AutoGenerated(
    generateFacade = true,
    value = "net.vakilla.auto.async.processor.AutoAsyncProcessor"
)
public interface Unified_SimpleService extends SimpleService {
  ListenableFuture<List<Complex>> async_complex(Map<String, List<Date>> map);
}
```
自动生成抽象实现类

```java
@AutoGenerated("net.vakilla.auto.async.processor.AutoGeneratedProcessor")
public abstract class Facade_Unified_SimpleService implements Unified_SimpleService {
  public ListenableFuture<List<Complex>> async_complex(Map<String, List<Date>> map) {
    return null;
  }
}
```


## 依赖
```xml
<dependency>
    <groupId>com.google.auto.service</groupId>
    <artifactId>auto-service</artifactId>
    <version>1.0-rc3</version>
</dependency>

<dependency>
    <groupId>com.squareup</groupId>
    <artifactId>javapoet</artifactId>
    <version>1.7.0</version>
</dependency>
```

## 注意事项
* 生成的异步接口基于guava的ListenableFuture
* 生成代码使用javapoet库，没有检查方法返回类型，如果方法标记了@Asyncable就会扫描生成异步方法，即使原本的返回类型是Future。
所以，使用时注意不要在返回类型是Future的方法上添加@Asyncable注解