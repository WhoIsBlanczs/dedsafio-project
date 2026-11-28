package net.minecraftforge.common.capabilities;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class CapabilityToken<T> {
   public Type getType() {
      return (Type)(this.getClass().getGenericSuperclass() instanceof ParameterizedType pt ? pt.getActualTypeArguments()[0] : this.getClass());
   }

   public String name() {
      Type t = this.getType();
      return t instanceof Class<?> c ? c.getName() : t.getTypeName();
   }
}
