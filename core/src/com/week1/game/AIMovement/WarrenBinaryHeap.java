package com.week1.game.AIMovement;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.badlogic.gdx.utils.StringBuilder;

public class WarrenBinaryHeap<T extends WarrenBinaryHeap.Node> {
    public int size;
    private WarrenBinaryHeap.Node[] nodes;
    private final boolean isMaxHeap;

    public WarrenBinaryHeap() {
        this(16, false);
    }

    public WarrenBinaryHeap(int capacity, boolean isMaxHeap) {
        this.isMaxHeap = isMaxHeap;
        this.nodes = new WarrenBinaryHeap.Node[capacity];
    }

    public T add(T node) {
        if (this.size == this.nodes.length) {
            WarrenBinaryHeap.Node[] newNodes = new WarrenBinaryHeap.Node[this.size << 1];
            System.arraycopy(this.nodes, 0, newNodes, 0, this.size);
            this.nodes = newNodes;
        }

        node.index = this.size;
        this.nodes[this.size] = node;
        this.up(this.size++);
        return node;
    }

    public T add(T node, float value) {
        node.value = value;
        return this.add(node);
    }

    public boolean contains(T node, boolean identity) {
        if (node == null) {
            throw new IllegalArgumentException("node cannot be null.");
        } else {
            WarrenBinaryHeap.Node[] var3;
            int var4;
            int var5;
            WarrenBinaryHeap.Node n;
            if (identity) {
                var3 = this.nodes;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    n = var3[var5];
                    if (n == node) {
                        return true;
                    }
                }
            } else {
                var3 = this.nodes;
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    n = var3[var5];
                    if (n.equals(node)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public T peek() {
        if (this.size == 0) {
            throw new IllegalStateException("The heap is empty.");
        } else {
            return (T) this.nodes[0];
        }
    }

    public T pop() {
        return this.remove(0);
    }

    public T remove(T node) {
        return this.remove(node.index);
    }

    private T remove(int index) {
        WarrenBinaryHeap.Node[] nodes = this.nodes;
        WarrenBinaryHeap.Node removed = nodes[index];
        nodes[index] = nodes[--this.size];
        nodes[this.size] = null;
        if (this.size > 0 && index < this.size) {
            this.down(index);
        }

        return (T) removed;
    }

    public boolean notEmpty() {
        return this.size > 0;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public void clear() {
        WarrenBinaryHeap.Node[] nodes = this.nodes;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            nodes[i] = null;
        }

        this.size = 0;
    }

    public void setValue(T node, float value) {
        float oldValue = node.value;
        node.value = value;
        if (value < oldValue ^ this.isMaxHeap) {
            this.up(node.index);
        } else {
            this.down(node.index);
        }

    }

    private void up(int index) {
        WarrenBinaryHeap.Node[] nodes = this.nodes;
        WarrenBinaryHeap.Node node = nodes[index];

        int parentIndex;
        if (node == null){
            return;
        }
        for(float value = node.value; index > 0; index = parentIndex) {
            parentIndex = index - 1 >> 1;
            WarrenBinaryHeap.Node parent = nodes[parentIndex];
            if (parent == null){
                return;
            }
            if (value < parent.value == this.isMaxHeap) {
                break;
            }

            nodes[index] = parent;
            parent.index = index;
        }

        nodes[index] = node;
        node.index = index;
    }

    private void down(int index) {
        WarrenBinaryHeap.Node[] nodes = this.nodes;
        int size = this.size;
        WarrenBinaryHeap.Node node = nodes[index];
        if (node == null){
            return;
        }
        float value = node.value;

        while(true) {
            int leftIndex = 1 + (index << 1);
            if (leftIndex >= size) {
                break;
            }

            int rightIndex = leftIndex + 1;
            WarrenBinaryHeap.Node leftNode = nodes[leftIndex];
            if (leftNode == null){
                return;
            }
            float leftValue = leftNode.value;
            WarrenBinaryHeap.Node rightNode;
            float rightValue;
            if (rightIndex >= size) {
                rightNode = null;
                rightValue = this.isMaxHeap ? -3.4028235E38F : 3.4028235E38F;
            } else {
                rightNode = nodes[rightIndex];
                if(rightNode == null){
                    return;
                }
                rightValue = rightNode.value;
            }

            if (leftValue < rightValue ^ this.isMaxHeap) {
                if (leftValue == value || leftValue > value ^ this.isMaxHeap) {
                    break;
                }

                nodes[index] = leftNode;
                leftNode.index = index;
                index = leftIndex;
            } else {
                if (rightValue == value || rightValue > value ^ this.isMaxHeap) {
                    break;
                }

                nodes[index] = rightNode;
                rightNode.index = index;
                index = rightIndex;
            }
        }

        nodes[index] = node;
        node.index = index;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof WarrenBinaryHeap)) {
            return false;
        } else {
            WarrenBinaryHeap other = (WarrenBinaryHeap)obj;
            if (other.size != this.size) {
                return false;
            } else {
                WarrenBinaryHeap.Node[] nodes1 = this.nodes;
                WarrenBinaryHeap.Node[] nodes2 = other.nodes;
                int i = 0;

                for(int n = this.size; i < n; ++i) {
                    if (nodes1[i].value != nodes2[i].value) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    public int hashCode() {
        int h = 1;
        int i = 0;

        for(int n = this.size; i < n; ++i) {
            h = h * 31 + Float.floatToIntBits(this.nodes[i].value);
        }

        return h;
    }

    public String toString() {
        if (this.size == 0) {
            return "[]";
        } else {
            WarrenBinaryHeap.Node[] nodes = this.nodes;
            StringBuilder buffer = new StringBuilder(32);
            buffer.append('[');
            buffer.append(nodes[0].value);

            for(int i = 1; i < this.size; ++i) {
                buffer.append(", ");
                if (nodes[i] == null){
                    buffer.append(']');
                    return buffer.toString();
                }
                buffer.append(nodes[i].value);
            }

            buffer.append(']');
            return buffer.toString();
        }
    }

    public static class Node {
        float value;
        int index;

        public Node(float value) {
            this.value = value;
        }

        public float getValue() {
            return this.value;
        }

        public String toString() {
            return Float.toString(this.value);
        }
    }
}
