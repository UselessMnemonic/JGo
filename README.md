# JGo
Welcome to JGo, my pet project to bring Go to the JVM! This repo is a running history of my
research and progress. I'm inspired by Kotlin, which shares a lot of similarities to Go and serves
as a proof that many of their similar constructs can work with Java. In the future, an adaptation
of this project will also have its basis in Kotlin.

## Mapping Go to Java
### Calling Conventions
In Java, primitives (like `int`, `double`, and `ref`) are passed into functions by their
values alone; this makes them pass-by-value. Objects themselves (meaning the data comprising an
object) are always passed by their references, so objects in Java are _always_ pass-by-reference.

In Go (much like in C), all data is by-default pass-by-value. The data comprising a `struct`, for
instance, is copied entirely when passed into a function. There are some exceptions with constructs
like maps and slices; still, any value can be pass-by-reference by using a pointer to that value.

#### Java Conventions
Java objects can emulate pass-by-value via cloning, so that a new object is constructed when a
distinct copy is needed. Such clones must be deep copies, but this can only be practically
guaranteed when a Go type is translated to Java.

It is not so trivial, however, to emulate pass-by-reference in Java. Pointers in Go can be made from
most types, wherever data may be legally addressed. Java requires some mechanism by which fields,
indexed memory, and local variables alike can be made accessible by a reference. In some cases,
first-order references to an object can be satisfied by its reference value allowing us to use Java
objects normally.

#### The Reference Class
We introduce a new class called `Reference` that will work for all orders of references. It is
declared as follows:
```java
public class Reference<T> {
    T get();
    set(T referent);
}
```
It contains constructors for referencing the fields of `structs`, indexed values of `slices` and
`arrays`, or independent values.

### Structs
TODO

### Functions
TODO

#### Multiple Return
TODO

### Method Sets
A Go type may have a method set associated with it. The method set of an interface type is its
interface. The method set of any other type `T` consists of all methods declared with receiver type
`T`. The method set of type `*T` also contains the method set of type `T`.

A Java class representing `T` can have instance methods that reflect those in `T`. Instance methods
can be defined on `T` or `*T`, so long as (1) `T` itself is not a pointer type and (2) those methods
are defined in the same package as `T`. Therefore, `Reference` instances cannot have methods, nor
can base types like `int` or `complex`.

### Interfaces
In Go, interface type specifies a method set, called its _interface_. Any type that implements all
the methods of an interface is said to implement that interface. All types implicitly implement the
empty interface `interface{}`, whose method set is empty.
```golang
type I interface {
    M()
}
```
Any type that has a method `M()` automatically implements interface `I`. Thus, with the following
declaration, a type `T` will implement `I`:
```golang
func (i T) M() {...}
```
This allows an `T` to be passed into a variable of type `I`, like so:
```golang
var v I = i
```
The corresponding Java interface will be declared as follows:
```java
public interface I {
    void M();
}
```
and any Go types that implement `I` will automatically have an `implements I` clause in their
corresponding Java classes.

### Visibility
#### Identifiers
In Go, identifiers are always visible inside the packages in which they are declared. However, they
are only visible to other packages if those identifiers begin with capital letters. Take the
following example:
```golang
type Address struct {  
    City  string
    State string
    zip   int
}
func (a Address) String() String
func (a Address) transform()
```
The corresponding Java type appears as such:
```java
public class Address {
    public  String City;
    public  String State;
    int     zip;
    
    public  String String();
    void    transform();
}
```
Therefore, `transform()` and `zip` are visible for the package in which `Address` is defined, but
are not visible anywhere else.

#### Packages
Go packages become Java packages, so a Go package `package mypkg` on the path `xyz/` becomes
`xyz.mypkg`. Individual Go files become Java class files, so a file like `example.go` becomes
`ExampleGo.java` and its class is fully qualified as `xyz.mypkg.ExampleGo`.

Functions defined in a Go file become static methods in its corresponding Java class. If the
following is in `example.go`:
```golang
func Square(x int) int
func sqrt(x float) float
```
then the following becomes part of `ExampleGo.java`
```java
public class ExampleGo {
    public static int Square(int x);
    static int sqrt(float x);
}
```

## Defer, Panic, and Recover
A defer statement pushes a function call onto a list. The list of saved calls is executed after the
surrounding function returns. Defer is commonly used to simplify functions that perform various
clean-up actions. The behavior of defer statements is straightforward and predictable. There are
three simple rules:

1. A deferred function's arguments are evaluated when the defer statement is evaluated.
2. Deferred function calls are executed in Last In First Out order after the surrounding function
   returns.
3. Deferred functions may read and assign to the returning function's named return values.

Defer statements are not available to Java users, since the JVM does not offer any such facility.
They are automatically generated and hence hidden from normal operation.

#### Panic
It is customary in Java to handle errors by throwing exceptions, but Go prefers to make errors part
of function returns. However, some functions that do encounter errors invoke the `panic()` function,
a built-in function that stops the ordinary flow of control and begins panicking.

When a function `F` calls panic, execution of `F` stops, any deferred functions in `F` are executed
normally, and then `F` returns to its caller. To the caller, F now behaves like a call to `panic().`
The process continues up the stack until all functions in the current goroutine have returned, at
which point the program crashes.

Panics translate into runtime exceptions in Java, avoiding the need to declare them in method
declarations. This allows errors to propagate through threads and eventually the JVM, much like
panics.

#### Recover
`recover()` is a built-in function that regains control of a panicking goroutine. Recover is only
useful inside deferred functions. During normal execution, a call to recover will return `nil` and
have no other effect. If the current goroutine is panicking, a call to `recover()` will capture the
value given to `panic()` and resume normal execution.

Recovery is not a facility exposed to Java users, like `defer` and `panic()` before it. Instead,
exceptions caused by panics must be caught by the user using Java, allowing for traditional error
handling. Internally, `recover()` translates into a `try-catch` block that captures the exception.

## Concurrency
### Channels
TODO

#### Buffered Channels
TODO

#### Range, Close, and Select
TODO

### Goroutines
TODO

### Mutexes
TODO