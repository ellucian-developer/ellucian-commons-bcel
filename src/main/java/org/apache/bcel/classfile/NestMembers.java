/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.bcel.Const;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class is derived from <em>Attribute</em> and records the classes and interfaces that are authorized to claim
 * membership in the nest hosted by the current class or interface. There may be at most one NestMembers attribute in a
 * ClassFile structure.
 *
 * @see Attribute
 */
public final class NestMembers extends Attribute {

    private int[] classes;

    /**
     * Construct object from input stream.
     *
     * @param name_index Index in constant pool
     * @param length Content length in bytes
     * @param input Input stream
     * @param constant_pool Array of constants
     * @throws IOException if an I/O error occurs.
     */
    NestMembers(final int name_index, final int length, final DataInput input, final ConstantPool constant_pool) throws IOException {
        this(name_index, length, (int[]) null, constant_pool);
        final int number_of_classes = input.readUnsignedShort();
        classes = new int[number_of_classes];
        for (int i = 0; i < number_of_classes; i++) {
            classes[i] = input.readUnsignedShort();
        }
    }

    /**
     * @param name_index Index in constant pool
     * @param length Content length in bytes
     * @param classes Table of indices in constant pool
     * @param constant_pool Array of constants
     */
    public NestMembers(final int name_index, final int length, final int[] classes, final ConstantPool constant_pool) {
        super(Const.ATTR_NEST_MEMBERS, name_index, length, constant_pool);
        this.classes = classes != null ? classes : ArrayUtils.EMPTY_INT_ARRAY;
    }

    /**
     * Initialize from another object. Note that both objects use the same references (shallow copy). Use copy() for a
     * physical copy.
     */
    public NestMembers(final NestMembers c) {
        this(c.getNameIndex(), c.getLength(), c.getClasses(), c.getConstantPool());
    }

    /**
     * Called by objects that are traversing the nodes of the tree implicitely defined by the contents of a Java class.
     * I.e., the hierarchy of methods, fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
    @Override
    public void accept(final Visitor v) {
        v.visitNestMembers(this);
    }

    /**
     * @return deep copy of this attribute
     */
    @Override
    public Attribute copy(final ConstantPool constantPool) {
        final NestMembers c = (NestMembers) clone();
        if (classes != null) {
            c.classes = new int[classes.length];
            System.arraycopy(classes, 0, c.classes, 0, classes.length);
        }
        c.setConstantPool(constantPool);
        return c;
    }

    /**
     * Dump NestMembers attribute to file stream in binary format.
     *
     * @param file Output file stream
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void dump(final DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(classes.length);
        for (final int index : classes) {
            file.writeShort(index);
        }
    }

    /**
     * @return array of indices into constant pool of class names.
     */
    public int[] getClasses() {
        return classes;
    }

    /**
     * @return string array of class names
     */
    public String[] getClassNames() {
        final String[] names = new String[classes.length];
        for (int i = 0; i < classes.length; i++) {
            names[i] = super.getConstantPool().getConstantString(classes[i], Const.CONSTANT_Class).replace('/', '.');
        }
        return names;
    }

    /**
     * @return Length of classes table.
     */
    public int getNumberClasses() {
        return classes == null ? 0 : classes.length;
    }

    /**
     * @param classes the list of class indexes Also redefines number_of_classes according to table length.
     */
    public void setClasses(final int[] classes) {
        this.classes = classes != null ? classes : ArrayUtils.EMPTY_INT_ARRAY;
    }

    /**
     * @return String representation, i.e., a list of classes.
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("NestMembers(");
        buf.append(classes.length);
        buf.append("):\n");
        for (final int index : classes) {
            final String className = super.getConstantPool().getConstantString(index, Const.CONSTANT_Class);
            buf.append("  ").append(Utility.compactClassName(className, false)).append("\n");
        }
        return buf.substring(0, buf.length() - 1); // remove the last newline
    }
}
