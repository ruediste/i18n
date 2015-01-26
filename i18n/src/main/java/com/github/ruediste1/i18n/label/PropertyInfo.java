package com.github.ruediste1.i18n.label;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.google.common.base.Objects;

public class PropertyInfo {
	private final String name;
	private final Class<?> declaringType;
	private final Method getter;
	private final Method setter;
	private final Field backingField;

	public PropertyInfo(String name, Class<?> declaringType, Method getter,
			Method setter, Field backingField) {
		super();
		this.name = name;
		this.declaringType = declaringType;
		this.getter = getter;
		this.setter = setter;
		this.backingField = backingField;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name, declaringType, getter, setter,
				backingField);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PropertyInfo other = (PropertyInfo) obj;
		return Objects.equal(name, other.name)
				&& Objects.equal(declaringType, other.declaringType)
				&& Objects.equal(getter, other.getter)
				&& Objects.equal(setter, other.setter)
				&& Objects.equal(backingField, other.backingField);
	}

	@Override
	public String toString() {
		return java.util.Objects.toString(declaringType) + "::" + name;
	}

	public String getName() {
		return name;
	}

	public Class<?> getDeclaringType() {
		return declaringType;
	}

	public Method getGetter() {
		return getter;
	}

	public Method getSetter() {
		return setter;
	}

	public Field getBackingField() {
		return backingField;
	}

	public PropertyInfo withGetter(Method getter) {
		return new PropertyInfo(name, declaringType, getter, setter,
				backingField);
	}

	public PropertyInfo withSetter(Method setter) {
		return new PropertyInfo(name, declaringType, getter, setter,
				backingField);
	}

	public PropertyInfo withBackingField(Field backingField) {
		return new PropertyInfo(name, declaringType, getter, setter,
				backingField);
	}

}