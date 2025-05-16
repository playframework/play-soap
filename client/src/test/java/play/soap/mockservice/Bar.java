/*
 * Copyright (C) from 2025 The Play Framework Contributors <https://github.com/playframework>, 2011-2025 Lightbend Inc. <https://www.lightbend.com>
 */
package play.soap.mockservice;

public class Bar {

  private Foo foo;
  private String name;

  public Bar() {}

  public Bar(Foo foo, String name) {
    this.foo = foo;
    this.name = name;
  }

  public Foo getFoo() {
    return foo;
  }

  public void setFoo(Foo foo) {
    this.foo = foo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Bar bar = (Bar) o;

    if (foo != null ? !foo.equals(bar.foo) : bar.foo != null) return false;
    if (name != null ? !name.equals(bar.name) : bar.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = foo != null ? foo.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
